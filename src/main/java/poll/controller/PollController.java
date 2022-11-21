// The REST controller that handles HTTP requests.
// Lives in sub-package controller, marked with the @RestController annotation
// for auto-configuration; the @CrossOrigin annotation enables CORS.

package poll.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import poll.exception.Error;
import poll.model.Candidate;
import poll.model.Member;
import poll.service.PollService;

import java.util.Map;

@RestController
@CrossOrigin
public class PollController {

    // Endpoints
    private final String ROOT_PATH = "/birds";
    private final String ROOT_PATH_UNRESTRICTED = ROOT_PATH + "-enriched";
    private final String VOTE_PATH = ROOT_PATH + "/vote";
    private final String REMOVE_BIRD_PATH = ROOT_PATH + "/remove";
    private final String POLL_PATH = "/poll";

    // The PollController depends on the PollService, so it needs to keep a reference to it.
    private final PollService pollService;

    /**
     * This constructor will set up the PollService that this controller will manipulate.
     *
     * @param pollService The Poll Service object that will compute requests.
     */
    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    /**
     * This method will get all the candidates with the voteCount attribute redacted.
     *
     * @return The list of candidates without the attribute voteCount.
     */
    @GetMapping(ROOT_PATH)
    public MappingJacksonValue getAllCandidates() {
        // Change the filter to apply voteCount if not admin otherwise none!
        // If a normal member is making the request revoke voteCount attribute, otherwise show all attributes.
        String filter = "voteCount";
        return getMappingJacksonValue(filter, pollService.getAllCandidates());
    }

    /**
     * This method will get all the candidates fully populated if authenticated, otherwise it will provide
     * a redacted version without the voteCount attribute.
     *
     * @param authentication The authentication object to verify an administrator.
     * @return The list of all candidates, or all redacted candidates if not authenticated.
     */
    @GetMapping(ROOT_PATH_UNRESTRICTED)
    public MappingJacksonValue getAllCandidates(Authentication authentication) {
        // If a normal member is making the request revoke voteCount attribute, otherwise show all attributes.
        String filter = "voteCount";
        if (authentication != null && authentication.isAuthenticated()) {
            filter = null;
        }
        return getMappingJacksonValue(filter, pollService.getAllCandidates());
    }

    /**
     * This method will set the status of the poll.
     *
     * @param status The boolean value, indicating if the poll is open or not.
     * @return The response object to indicate the status of the request.
     */
    @PostMapping(POLL_PATH)
    public ResponseEntity<Void> setPollStatus(@RequestBody boolean status) {
        pollService.setPollOpen(status);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * This method will get the vote for a specified membershipId.
     *
     * @param membershipId The identifier of the member to get the vote for.
     * @return The redacted version (no voteCount attribute) of the associated candidate.
     */
    @GetMapping(VOTE_PATH + "/{membershipId}")
    public MappingJacksonValue getVote(@PathVariable String membershipId) {
        // Guard Clauses
        validateMember(pollService.getMember(membershipId), false);

        Candidate candidate = pollService.getMember(membershipId).getCandidateVotedFor();
        if (candidate == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Error.NO_CANDIDATE_VOTE.toString());
        }

        return getMappingJacksonValue("voteCount", candidate);
    }

    /**
     * This method will store a vote for a specified member and candidate in the poll service.
     *
     * @param signedVote The JSON value with the Candidate commonName as the key, and the Member object as the value.
     * @return The response object to indicate the status of the request.
     */
    @PutMapping(VOTE_PATH)
    public ResponseEntity<Void> makeVote(@RequestBody Map<String, Member> signedVote) {
        // Separate the data into parsable variables.
        String commonName = signedVote.keySet().stream().findFirst().orElse(null);
        Member member = signedVote.values().stream().findFirst().orElse(null);

        // Guard Clauses
        validateMember(member, true);
        if (!pollService.hasCandidate(commonName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Error.CANDIDATE_NON_EXISTENT.toString());
        }

        // Gather objects
        Member voter = pollService.getMember(member.getMembershipId());
        Candidate lastVote = voter.getCandidateVotedFor();
        Candidate newVote = pollService.getCandidate(commonName);

        // If the votes are the same, don't modify.
        if (newVote.equals(lastVote)) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }

        pollService.makeVote(voter, lastVote, newVote);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * This method will retract a vote for a specified member.
     *
     * @param membershipId The identifier of the member to retract the vote for.
     * @return The response object to indicate the status of the request.
     */
    @DeleteMapping(VOTE_PATH)
    public ResponseEntity<Void> retractVote(@RequestBody String membershipId) {
        // Guard Clauses
        validateMember(pollService.getMember(membershipId), false);

        // Gather objects
        Member voter = pollService.getMember(membershipId);
        Candidate lastVote = voter.getCandidateVotedFor();

        // If there is no vote to retract, don't modify.
        if (lastVote == null) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }

        pollService.retractVote(voter, lastVote);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method will remove a candidate from the poll service.
     *
     * @param commonName The common name of the Candidate object to remove.
     * @return The response object to indicate the status of the request.
     */
    @DeleteMapping(REMOVE_BIRD_PATH)
    public ResponseEntity<Void> removeCandidate(@RequestBody String commonName) {
        // Guard Clauses
        if (!isStringValid(commonName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Error.INVALID_CANDIDATE.toString());
        }
        if (!pollService.hasCandidate(commonName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Error.CANDIDATE_NON_EXISTENT.toString());
        }

        pollService.removeCandidate(commonName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method will check if a string is valid, i.e. it is not null,
     * and it is not empty regardless of whitespace.
     *
     * @param string The string to test.
     * @return The boolean value true if it is valid, otherwise it will return false.
     */
    public boolean isStringValid(String string) {
        return string != null && !string.trim().isEmpty();
    }

    /**
     * This method will validate a given member and throw an error if appropriate. No need to return anything here,
     * as if it isn't valid, a ResponseStatusException will be thrown.
     *
     * @param member The member to validate.
     * @param addMember If the member is valid, but is not stored in the database, this boolean value determines if it
     *                  should be added into the poll service.
     */
    private void validateMember(Member member, boolean addMember) {
        if (!pollService.isPollOpen()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, Error.POLL_CLOSED.toString());
        }
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Error.MEMBER_NON_EXISTENT.toString());
        }
        if (!isStringValid(member.getMembershipId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Error.INVALID_MEMBER.toString());
        }
        if (!pollService.hasMember(member.getMembershipId())) {
            if (addMember) {
                pollService.addMember(member);
                return;
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Error.MEMBER_NON_EXISTENT.toString());
        }
    }

    /**
     * This method will filter out a specified filter from a given data object.
     *
     * @param filter The filter attribute to apply.
     * @param data The data to be filtered.
     * @return The filtered object as a MappingJacksonValue.
     */
    private MappingJacksonValue getMappingJacksonValue(String filter, Object data) {
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept(filter);
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("candidateFilter", simpleBeanPropertyFilter);

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(data);
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

}

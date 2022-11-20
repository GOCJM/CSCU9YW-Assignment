// The REST controller that handles HTTP requests.
// Lives in sub-package controller, marked with the @RestController annotation
// for auto-configuration; the @CrossOrigin annotation enables CORS.

package poll.controller;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import poll.exception.Error;
import poll.model.Candidate;
import poll.model.Member;
import poll.service.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin
public class PollController {

    private final String ROOT_PATH = "/birds";
    private final String ROOT_PATH_UNRESTRICTED = ROOT_PATH + "-enriched";
    private final String VOTE_PATH = ROOT_PATH + "/vote";
    private final String ADD_BIRD_PATH = ROOT_PATH + "/add";
    private final String REMOVE_BIRD_PATH = ROOT_PATH + "/remove";

    // The PollController depends on the PollService, so it needs to keep a reference to it.
    private PollService pollService;

    // The fact that the constructor for the PollController requires a
    // WelcomService argument tells Spring to auto-configure a PollService
    // and pass it to the constructor. This is called "Dependency Injection",
    // and it (a) saves boilerplate code, and (b) makes it easy to swap
    // components. (We can change the PollService implementation without
    // changing any code in the rest of the system.)
    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @GetMapping(ROOT_PATH)
    public MappingJacksonValue getAllCandidates() {
        // Change the filter to apply voteCount if not admin otherwise none!
        // If a normal member is making the request revoke voteCount attribute, otherwise show all attributes.
        String filter = "voteCount";
        return getMappingJacksonValue(filter);
    }

    @GetMapping(ROOT_PATH_UNRESTRICTED)
    public MappingJacksonValue getAllCandidates(Authentication authentication) {
        // Change the filter to apply voteCount if not admin otherwise none!
        // If a normal member is making the request revoke voteCount attribute, otherwise show all attributes.
        String filter = "voteCount";
        if (authentication != null && authentication.isAuthenticated()) {
            filter = null;
        }
        return getMappingJacksonValue(filter);
    }

    private MappingJacksonValue getMappingJacksonValue(String filter) {
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept(filter);
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("candidateFilter", simpleBeanPropertyFilter);

        List<Candidate> candidateList = pollService.getAllCandidates();
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(candidateList);
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

    @GetMapping(VOTE_PATH + "/{membershipId}")
    public MappingJacksonValue getVote(@PathVariable String membershipId) {
        // Guard Clauses
        if (!isStringValid(membershipId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Error.INVALID_MEMBER.toString());
        }
        if (!pollService.hasMember(membershipId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Error.MEMBER_NON_EXISTENT.toString());
        }

        Candidate candidate = pollService.getMember(membershipId).getCandidateVotedFor();

        if (candidate == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Error.NO_CANDIDATE_VOTE.toString());
        }

        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept("voteCount");
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("candidateFilter", simpleBeanPropertyFilter);
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(candidate);
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }

    @PutMapping(VOTE_PATH)
    public ResponseEntity<Void> makeVote(@RequestBody Map<String, Member> signedVote) {
        // Separate the data into parsable variables.
        String commonName = signedVote.keySet().stream().findFirst().orElse(null);
        Member member = signedVote.values().stream().findFirst().orElse(null);

        // Guard Clauses
        if (!isStringValid(commonName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Error.INVALID_CANDIDATE.toString());
        }
        if (member == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Error.INVALID_MEMBER.toString());
        }
        if (!pollService.hasCandidate(commonName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Error.CANDIDATE_NON_EXISTENT.toString());
        }
        if (!pollService.hasMember(member.getMembershipId())) {
            pollService.addMember(member);
        }

        Member voter = pollService.getMember(member.getMembershipId());
        Candidate lastVote = voter.getCandidateVotedFor();
        Candidate newVote = pollService.getCandidate(commonName);

        if (newVote.equals(lastVote)) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }

        pollService.makeVote(voter, lastVote, newVote);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping(VOTE_PATH)
    public ResponseEntity<Void> retractVote(@RequestBody String membershipId) {
        // Guard Clauses
        if (!isStringValid(membershipId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Error.INVALID_MEMBER.toString());
        }
        if (!pollService.hasMember(membershipId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Error.MEMBER_NON_EXISTENT.toString());
        }

        Member voter = pollService.getMember(membershipId);
        Candidate lastVote = voter.getCandidateVotedFor();

        if (lastVote == null) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }

        pollService.retractVote(voter, lastVote);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

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

//    @PostMapping(ROOT_PATH)
//    public ResponseEntity<Void> addWelcome(@RequestBody Poll newPoll) {
//        if (ws.hasWelcome(newPoll.getLang())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
//
//        ws.addWelcome(newPoll);
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Location",ROOT_PATH + "/" + newPoll.getLang());
//
//        return new ResponseEntity<>(headers, HttpStatus.CREATED);
//    }
//
//    @GetMapping(ROOT_PATH + "/{lang}")
//    public Poll getWelcome(@PathVariable String lang, @RequestParam(required=false) String name) {
//        Poll poll = ws.getWelcome(lang, name);
//        if (poll == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        }
//        return poll;
//    }
//
//    @PutMapping(ROOT_PATH + "/{lang}")
//    public ResponseEntity<Void> updateWelcome(@RequestBody Poll newPoll, @PathVariable String lang) {
//        if (!ws.hasWelcome(lang)){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        }
//        if (!Objects.equals(lang, newPoll.getLang())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
//        }
//        ws.addWelcome(newPoll);
//        return new ResponseEntity<>(HttpStatus.ACCEPTED);
//    }
//
//    @DeleteMapping(ROOT_PATH + "/{lang}")
//    public ResponseEntity<Void> removeWelcome(@PathVariable String lang) {
//        if (!ws.hasWelcome(lang)){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        }
//        ws.removeWelcome(lang);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

}

// Implementation of the business logic, living in the service sub-package.
// Discoverable for auto-configuration, thanks to the @Component annotation.

package poll.service;

import org.springframework.stereotype.Component;
import poll.exception.Error;
import poll.model.Candidate;
import poll.model.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PollServiceImpl implements PollService {

    // Very simple in-memory database
    // We have to be careful with this 'database'. In order to avoid objects
    // in the database being mutated accidentally, we must always copy objects
    // before insertion and retrieval.

    private boolean isPollOpen = false;

    // Key -> commonName, value -> Candidate
    private final Map<String, Candidate> candidateDb;
    // Key -> membershipId, value -> Member
    private final Map<String, Member> memberDb;

    /**
     * This constructor will create a Poll Service that stores the Candidate as
     * the key, and the Integer as the value, which represents the number of votes
     * that the candidate has received.
     */
    public PollServiceImpl() {
        candidateDb = new HashMap<>();
        memberDb = new HashMap<>();
    }

    /**
     * This method will determine if a candidate exists in the poll service.
     *
     * @param commonName The common name of the animal.
     * @return The boolean value if the candidate exists in the poll.
     */
    @Override
    public boolean hasCandidate(String commonName) {
        return candidateDb.containsKey(commonName);
    }

    /**
     * This method will vote for a specified candidate and update the voter object to store
     * their current vote.
     *
     * @param commonName The common name of the animal.
     * @param member The member who made the vote.
     */
    @Override
    public void makeVote(Member voter, Candidate lastVote, Candidate newVote) {
        if (lastVote != null) {
            lastVote.decrementVote();
        }
        voter.setCandidateVotedFor(newVote);
        newVote.incrementVote();
    }

    /**
     * This method will vote for a specified candidate and update the voter object to store
     * their current vote.
     *
     * @param commonName The common name of the animal.
     * @param member The member who made the vote.
     */
    @Override
    public void retractVote(Member voter, Candidate lastVote) {
        lastVote.decrementVote();
        voter.setCandidateVotedFor(null);
    }


    /**
     * This will add a candidate to the database, only if it doesn't already exist.
     *
     * @param candidate The candidate object describing the animal.
     */
    @Override
    public void addCandidate(Candidate candidate) {
        // Guard clauses.
        if (candidate == null) {
            throw new RuntimeException(Error.INVALID_CANDIDATE.toString());
        }
        if (hasCandidate(candidate.getCommonName())) {
            throw new RuntimeException(Error.CANDIDATE_ALREADY_EXISTS.toString());
        }

        // Add the candidate to the database.
        candidateDb.put(candidate.getCommonName(), candidate);
    }

    /**
     * @param commonName
     * @return
     */
    @Override
    public Candidate getCandidate(String commonName) {
        return candidateDb.get(commonName);
    }


    /**
     * This method will get all the candidates in the database.
     *
     * @return The list of all candidates in the database.
     */
    @Override
    public List<Candidate> getAllCandidates() {
        return new ArrayList<>(candidateDb.values());
    }

    /**
     * This will remove a candidate from the database.
     *
     * @param commonName The common name of the animal.
     */
    @Override
    public void removeCandidate(String commonName) {
        candidateDb.remove(commonName);
    }

    /**
     * This method will get the number of votes that a candidate has received.
     *
     * @param commonName The common name of the animal.
     * @return The integer value of the number of votes for the given candidate.
     */
    @Override
    public Integer getCandidateCount(String commonName) {
        return candidateDb.get(commonName).getVoteCount();
    }


    /**
     * This will add a member to the database.
     *
     * @param member The member object describing the individual.
     */
    @Override
    public void addMember(Member member) {
        memberDb.put(member.getMembershipId(), member);
    }

    /**
     * This method will determine if a member exists in the poll service.
     *
     * @param membershipId The membership identifier for the specified member.
     * @return The boolean value if the member exists in the poll.
     */
    @Override
    public boolean hasMember(String membershipId) {
        return memberDb.containsKey(membershipId);
    }

    /**
     * @param membershipId
     * @return
     */
    @Override
    public Member getMember(String membershipId) {
        return memberDb.get(membershipId);
    }


    public boolean isPollOpen() {
        return isPollOpen;
    }

    public void setPollOpen(boolean pollOpen) {
        isPollOpen = pollOpen;
    }
}

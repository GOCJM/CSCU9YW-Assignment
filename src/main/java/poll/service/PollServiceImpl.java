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

    // Poll Service status, for the admin to toggle.
    private boolean isPollOpen = false;

    // Databases for the candidates and members.
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
     * @param voter The member who made the vote.
     * @param lastVote The member's last vote.
     * @param newVote The member's vote they are trying to make.
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
     * This method will retract the vote for a specified candidate and update the voter object to remove
     * their current vote.
     *
     * @param voter The member who made the vote.
     * @param lastVote The member's last vote.
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
     * This method will get the candidate from the database with the specified common name.
     *
     * @param commonName The common name of the animal.
     * @return The candidate object that matches the common name.
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
     * This method will get the member from the database with the specified membership identifier.
     *
     * @param membershipId The membership identifier for the specified member.
     * @return The member object describing the individual.
     */
    @Override
    public Member getMember(String membershipId) {
        return memberDb.get(membershipId);
    }

    /**
     * This method will get the state of the poll's openness.
     *
     * @return The boolean value determining if the poll is open or not.
     */
    public boolean isPollOpen() {
        return isPollOpen;
    }

    /**
     * This method will set the state of the poll's openness.
     *
     * @param pollOpen The boolean value determining if the poll is open or not.
     */
    public void setPollOpen(boolean pollOpen) {
        isPollOpen = pollOpen;
    }
}

// An interface to the business logic, living in the service sub-package.

package poll.service;

import poll.model.Candidate;
import poll.model.Member;

import java.util.List;

public interface PollService {

    // CANDIDATE SECTION

    // Adds a candidate to the database.
    void addCandidate(Candidate candidate);

    // Gets a candidate from the database.
    Candidate getCandidate(String commonName);

    // Returns a candidate count.
    Integer getCandidateCount(String commonName);

    // Returns a list of all candidates in the database.
    List<Candidate> getAllCandidates();

    // Removes the candidate from the database.
    void removeCandidate(String commonName);

    // Checks if a candidate exists.
    boolean hasCandidate(String commonName);

    // Vote for a specific candidate.
    void makeVote(Member voter, Candidate lastVote, Candidate newVote);

    // MEMBER SECTION

    // Adds a member to the database.
    void addMember(Member member);

    // Checks if a member exists in the database.
    boolean hasMember(String membershipId);

    // Removes a vote from a candidate and member.
    void retractVote(Member voter, Candidate lastVote);

    // Gets a member from the database.
    Member getMember(String membershipId);

    // Checks if the poll is currently open.
    boolean isPollOpen();

    // Sets the status of the poll's openness.
    void setPollOpen(boolean pollOpen);
}

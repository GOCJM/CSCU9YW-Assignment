// An interface to the business logic, living in the service sub-package.

package poll.service;

import poll.model.Candidate;
import poll.model.Member;

import java.util.List;

public interface PollService {

    // CANDIDATE SECTION

    void retractVote(Member voter, Candidate lastVote);

    // Adds a candidate to the database, or overwrites an existing one.
    void addCandidate(Candidate candidate);

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

    void addMember(Member member);

    boolean hasMember(String membershipId);

    Member getMember(String membershipId);
}

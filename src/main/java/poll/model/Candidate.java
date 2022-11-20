package poll.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonFilter("candidateFilter")
public class Candidate {

    private String scientificName;
    private String commonName;
    private String description;
    private int voteCount;

    /**
     * This constructor will create a candidate for the vote.
     * @param scientificName The scientific name of the animal.
     * @param commonName The common name of the animal.
     * @param description A brief description of the animal.
     */
    public Candidate(String scientificName, String commonName, String description) {
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.description = description;
        this.voteCount = 0;
    }

    /**
     * This constructor will create a candidate for the vote.
     * It will be useful to copy candidate objects to ensure
     * immutability within the database.
     * @param candidate The candidate object describing the animal.
     */
    public Candidate(Candidate candidate) {
        this.scientificName = candidate.getScientificName();
        this.commonName = candidate.getCommonName();
        this.description = candidate.getDescription();
        this.voteCount = 0;
    }

    /**
     * This constructor will create a candidate with blank attributes.
     */
    public Candidate() {
        this.scientificName = "";
        this.commonName = "";
        this.description = "";
        this.voteCount = 0;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // This annotation will ensure that members will not see current vote count for the candidate,
    // when the candidates are listed.
//    @JsonIgnore
    public int getVoteCount() {
        return voteCount;
    }

    public void incrementVote() {
        this.voteCount++;
    }

    public void decrementVote() {
        if (this.voteCount > 0) {
            this.voteCount--;
        }
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "scientificName='" + scientificName + '\'' +
                ", commonName='" + commonName + '\'' +
                ", description='" + description + '\'' +
                ", voteCount=" + voteCount +
                '}';
    }
}

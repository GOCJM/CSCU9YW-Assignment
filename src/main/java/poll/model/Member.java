package poll.model;

public class Member {

    private String membershipId;
    private String name;
    private int age;
    private String regionOfScotland;
    private Candidate candidateVotedFor;

    /**
     * This constructor will create a member with all the attributes.
     * @param membershipId The membership identifier for the specified member.
     * @param name The full name of the specified member.
     * @param age The current age of the specified member.
     * @param regionOfScotland The region of Scotland that the member resides.
     */
    public Member(String membershipId, String name, int age, String regionOfScotland) {
        this.membershipId = membershipId;
        this.name = name;
        this.age = age;
        this.regionOfScotland = regionOfScotland;
        this.candidateVotedFor = null;
    }

    /**
     * This constructor will create a member with the minimum attributes.
     * @param membershipId The membership identifier for the specified member.
     * @param name The full name of the specified member.
     */
    public Member(String membershipId, String name) {
        this.membershipId = membershipId;
        this.name = name;
        this.candidateVotedFor = null;
    }

    /**
     * This constructor will create a member with blank attributes.
     */
    public Member() {
        this.membershipId = "";
        this.name = "";
        this.age = -1;
        this.regionOfScotland = "";
        this.candidateVotedFor = null;
    }


    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRegionOfScotland() {
        return regionOfScotland;
    }

    public void setRegionOfScotland(String regionOfScotland) {
        this.regionOfScotland = regionOfScotland;
    }

    public Candidate getCandidateVotedFor() {
        return candidateVotedFor;
    }

    public void setCandidateVotedFor(Candidate candidateVotedFor) {
        this.candidateVotedFor = candidateVotedFor;
    }

    @Override
    public String toString() {
        return "Member{" +
                "membershipId='" + membershipId + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", regionOfScotland='" + regionOfScotland + '\'' +
                ", candidateVotedFor=" + candidateVotedFor +
                '}';
    }
}

package poll.exception;

public enum Error {
    INVALID_CANDIDATE("The candidate is invalid, check it is in the correct format."),
    INVALID_MEMBER("The member is invalid, check it is in the correct format."),
    CANDIDATE_ALREADY_EXISTS("This candidate already exists."),
    CANDIDATE_NON_EXISTENT("This candidate does not exist."),
    MEMBER_NON_EXISTENT("This member does not exist, trying voting to register."),
    NO_CANDIDATE_VOTE("This member has not registered a vote.");

    private final String description;

    /**
     * This constructor will create a new error with a description.
     *
     * @param description The brief description of the error.
     */
    Error(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}

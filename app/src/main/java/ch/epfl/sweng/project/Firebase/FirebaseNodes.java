package ch.epfl.sweng.project.Firebase;

/**
 * Non instantiable class, which purpose is to hold constants and enumerations that define the names and types of nodes
 * used to access the remote Firebase database throughout the application.
 */
public class FirebaseNodes {

    private FirebaseNodes() {}

    // Root Nodes
    public static final String USERS = "users";
    public static final String CHALLENGES = "challenges";
    public static final String MESSAGES = "messages";

    // Challenge Nodes
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String CHECKPOINTS = "checkpoints";

    // Message creation
    public static final String MEX_FROM = "from";
    public static final String MEX_SENDER = "sender";
    public static final String MEX_CHALLENGE_TYPE = "challenge_type";
    public static final String MEX_FIRST_VALUE = "first_value";
    public static final String MEX_SECOND_VALUE = "second_value";
    public static final String MEX_ADDRESSEE = "addressee";
    public static final String MEX_TYPE = "type";
    public static final String MEX = "message";
    public static final String MEX_TIME = "time";
    public static final String MEX_UID = "UID";

    // User nodes
    public static final String NAME = "name";
    public static final String PROFILE_PIC_URL = "profile_pic_url";
    public static final String STATISTICS = "statistics";
    public static final String AVAILABLE = "available";

    // Statistics
    public static final String TOTAL_RUNNING_TIME = "total_running_time";
    public static final String TOTAL_RUNNING_DISTANCE = "total_running_distance";
    public static final String TOTAL_NUMBER_OF_RUNS = "total_number_of_runs";
    public static final String TOTAL_NUMBER_OF_CHALLENGES = "total_number_of_challenges";
    public static final String TOTAL_NUMBER_OF_WON_CHALLENGES = "total_number_of_won_challenges";
    public static final String TOTAL_NUMBER_OF_LOST_CHALLENGES = "total_number_of_lost_challenges";


    public enum ChallengeStatus {
        READY("readyStatus"),
        FINISH("finishStatus"),
        ABORT("abortStatus"),
        IN_ROOM("in room"),
        DATA("checkpoints");

        private final String nodeName;

        ChallengeStatus(final String nodeName) {
            this.nodeName = nodeName;
        }

        @Override
        public String toString() {
            return nodeName;
        }
    }
}

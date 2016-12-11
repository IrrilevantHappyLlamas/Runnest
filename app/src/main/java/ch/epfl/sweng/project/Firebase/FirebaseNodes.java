package ch.epfl.sweng.project.Firebase;

/**
 * Non instantiable class, which purpose is to hold constants and enumerations that define the names and types of nodes
 * used to access the remote Firebase database throughout the application.
 */
public class FirebaseNodes {

    private FirebaseNodes() {}

    public static final String USERS_NODE = "users";

    public static final String MESSAGES_CHILD = "messages";
    public static final String FROM_CHILD = "from";
    public static final String SENDER_CHILD = "sender";
    public static final String CHALLENGE_TYPE_CHILD = "challenge_type";
    public static final String FIRST_VALUE_CHILD = "first_value";
    public static final String SECOND_VALUE_CHILD = "second_value";
    public static final String ADDRESSEE_CHILD = "addressee";
    public static final String TYPE_CHILD = "type";
    public static final String MESSAGE_CHILD = "message";
    public static final String TIME_CHILD = "time";
    public static final String NAME_CHILD = "name";
    public static final String PROFILE_PIC_URL_CHILD = "profile_pic_url";
    public static final String STATISTICS_CHILD = "statistics";
    public static final String TOTAL_RUNNING_TIME_CHILD = "total_running_time";
    public static final String TOTAL_RUNNING_DISTANCE_CHILD = "total_running_distance";
    public static final String TOTAL_NUMBER_OF_RUNS_CHILD = "total_number_of_runs";
    public static final String TOTAL_NUMBER_OF_CHALLENGES_CHILD = "total_number_of_challenges";
    public static final String TOTAL_NUMBER_OF_WON_CHALLENGES_CHILD = "total_number_of_won_challenges";
    public static final String TOTAL_NUMBER_OF_LOST_CHALLENGES_CHILD = "total_number_of_lost_challenges";
}

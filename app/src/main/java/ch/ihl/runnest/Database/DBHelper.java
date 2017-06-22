package ch.ihl.runnest.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import ch.ihl.runnest.Model.Challenge;
import ch.ihl.runnest.Model.CheckPoint;
import ch.ihl.runnest.Model.Run;
import ch.ihl.runnest.Model.Track;

/**
 * This class provides methods to interact with a SQLite local database.
 * It allows to store and retrieve Runs and Challenges.
 *
 * @author Pablo Pfister
 */
public class DBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;

    private static final String DATABASE_NAME = "runs.db";

    private static final String RUNS_TABLE_NAME = "runs";
    private static final String CHECKPOINTS_TABLE_NAME = "checkpoints";
    private static final String CHALLENGES_TABLE_NAME = "challenges";

    private static final String[] RUNS_COLS = {"id", "isChallenge", "name", "duration", "checkpointsFromId", "checkpointsToId"};
    private static final String[] CHECKPOINTS_COLS = {"id", "latitude", "longitude"};
    private static final String[] CHALLENGES_COLS = {"id", "opponentName", "type", "goal", "result", "myRunId", "opponentRunId"};

    private Context mContext = null;

    /**
     * The constructor of the class.
     *
     * @param context to use to open or create the database
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        mContext = context;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createEffortsTableQuery = "CREATE TABLE " + RUNS_TABLE_NAME + " ("
                + RUNS_COLS[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RUNS_COLS[1] + " INTEGER, "
                + RUNS_COLS[2] + " TEXT, "
                + RUNS_COLS[3] + " TEXT, "
                + RUNS_COLS[4] + " INTEGER, "
                + RUNS_COLS[5] + " INTEGER)";
        db.execSQL(createEffortsTableQuery);

        String createCheckpointsTableQuery = "CREATE TABLE " + CHECKPOINTS_TABLE_NAME + " ("
                + CHECKPOINTS_COLS[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CHECKPOINTS_COLS[1] + " DOUBLE, "
                + CHECKPOINTS_COLS[2] + " DOUBLE )";
        db.execSQL(createCheckpointsTableQuery);

        String createChallengesTableQuery = "CREATE TABLE " + CHALLENGES_TABLE_NAME + " ("
                + CHALLENGES_COLS[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CHALLENGES_COLS[1] + " TEXT, "
                + CHALLENGES_COLS[2] + " TEXT, "
                + CHALLENGES_COLS[3] + " DOUBLE, "
                + CHALLENGES_COLS[4] + " INTEGER, "
                + CHALLENGES_COLS[5] + " INTEGER, "
                + CHALLENGES_COLS[6] + " INTEGER)";
        db.execSQL(createChallengesTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (db == null || oldVersion < 0 || newVersion < oldVersion) {
            throw new IllegalArgumentException();
        }

        String dropEffortsTableQuery = "DROP TABLE IF EXISTS " + RUNS_TABLE_NAME;
        db.execSQL(dropEffortsTableQuery);

        String dropCheckpointsTableQuery = "DROP TABLE IF EXISTS " + CHECKPOINTS_TABLE_NAME;
        db.execSQL(dropCheckpointsTableQuery);

        String dropChallengesTableQuery = "DROP TABLE IF EXISTS " + CHALLENGES_TABLE_NAME;
        db.execSQL(dropChallengesTableQuery);

        onCreate(db);
    }

    /**
     * Insert a Run in the database.
     *
     * @param run the run to insert
     * @return true if the insertion was successful, false otherwise
     */
    public boolean insert(Run run) {
        if (run == null) {
            throw new IllegalArgumentException();
        }

        return insert(run, false) != -1;
    }

    /**
     * Insert a Challenge in the database.
     *
     * @param challenge the challenge to insert
     * @return true if the insertion was successful, false otherwise
     */
    public boolean insert(Challenge challenge) {
        if (challenge == null) {
            throw new IllegalArgumentException();
        }

        String opponentName = challenge.getOpponentName();
        Challenge.Type type = challenge.getType();
        double goal = challenge.getGoal();

        int result = 1;
        switch (challenge.getResult()) {
            case WON:
                result = 0;
                break;
            case LOST:
                result = 1;
                break;
            case ABORTED_BY_ME:
                result = 2;
                break;
            case ABORTED_BY_OTHER:
                result = 3;
                break;
            default:
        }

        long myRunId = insert(challenge.getMyRun(), true);
        long opponentRunId = insert(challenge.getOpponentRun(), true);

        ContentValues valuesToInsert = new ContentValues();
        valuesToInsert.put(CHALLENGES_COLS[1], opponentName);
        valuesToInsert.put(CHALLENGES_COLS[2], type.toString());
        valuesToInsert.put(CHALLENGES_COLS[3], goal);
        valuesToInsert.put(CHALLENGES_COLS[4], result);
        valuesToInsert.put(CHALLENGES_COLS[5], myRunId);
        valuesToInsert.put(CHALLENGES_COLS[6], opponentRunId);

        long challengeId = db.insert(CHALLENGES_TABLE_NAME, null, valuesToInsert);
        if (challengeId != -1) {
            challenge.setId(challengeId);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Insert a Run in the database marking it as a challenge if needed.
     *
     * @param run the run to insert
     * @param isChallenge true if it's part of a challenge, false otherwise
     * @return the id of the inserted run
     */
    private long insert(Run run, boolean isChallenge) {
        //insert all checkpoints that form the track
        Track track = run.getTrack();
        List<CheckPoint> checkpoints = track.getCheckpoints();
        long checkpointsFromId = -1;
        long checkpointsToId = -1;
        for (CheckPoint checkpoint : checkpoints) {
            checkpointsToId = insert(checkpoint);
            if (checkpointsToId == -1) {
                return -1;
            }
            if (checkpointsFromId == -1) {
                checkpointsFromId = checkpointsToId;
            }
        }

        //insert Run
        ContentValues valuesToInsert = new ContentValues();
        int challenge = isChallenge ? 1 : 0;
        valuesToInsert.put(RUNS_COLS[1], challenge);
        valuesToInsert.put(RUNS_COLS[2], run.getName());
        valuesToInsert.put(RUNS_COLS[3], run.getDuration());
        valuesToInsert.put(RUNS_COLS[4], checkpointsFromId);
        valuesToInsert.put(RUNS_COLS[5], checkpointsToId);

        long insertedRun = db.insert(RUNS_TABLE_NAME, null, valuesToInsert);
        run.setId(insertedRun);
        return insertedRun;
    }

    /**
     * Insert a checkpoint in the checkpoints table.
     *
     * @param checkpoint the checkpoint to insert
     * @return the id of the inserted row
     */
    private long insert(CheckPoint checkpoint) {
        ContentValues valuesToInsert = new ContentValues();
        valuesToInsert.put(CHECKPOINTS_COLS[1], checkpoint.getLatitude());
        valuesToInsert.put(CHECKPOINTS_COLS[2], checkpoint.getLongitude());

        return db.insert(CHECKPOINTS_TABLE_NAME, null, valuesToInsert);
    }

    /**
     * Deletes a given run.
     *
     * @param run to delete
     * @return true if the deletion was successful
     */
    public boolean delete(Run run) {
        if (run == null) {
            throw new IllegalArgumentException();
        }

        long id = run.getId();
        if  (id >= 0) {
            String selectRun = RUNS_COLS[0] + " = " + id;
            Cursor result = db.query(RUNS_TABLE_NAME, RUNS_COLS, selectRun, null, null, null, null);
            if (result.getCount() == 1) {
                result.moveToNext();
                long checkpointsFromId = result.getLong(3);
                long checkpointsToId = result.getLong(4);
                String deleteCheckpointsQuery = CHECKPOINTS_COLS[0] + " >= " + checkpointsFromId + " AND "
                        + CHECKPOINTS_COLS[0] + " <= " + checkpointsToId;
                db.delete(CHECKPOINTS_TABLE_NAME, deleteCheckpointsQuery, null);
            }
            return db.delete(RUNS_TABLE_NAME, selectRun, null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Deletes a given challenge.
     *
     * @param challenge to delete
     * @return true if the deletion was successful
     */
    public boolean delete(Challenge challenge) {
        if (challenge == null) {
            throw new IllegalArgumentException();
        }

        long id = challenge.getId();
        if  (id >= 0) {
            delete(challenge.getMyRun());
            delete(challenge.getOpponentRun());

            String selectChallenge = CHALLENGES_COLS[0] + " = " + id;
            return db.delete(CHALLENGES_TABLE_NAME, selectChallenge, null) > 0;
        } else {
            return false;
        }
    }

    /**
     * Retrieve all runs present in the database.
     *
     * @return the list of runs present in the database
     */
    public List<Run> fetchAllRuns() {
        Cursor result = db.query(RUNS_TABLE_NAME, RUNS_COLS, null, null, null, null, null);
        List<Run> runs = new ArrayList<>();
        if (result.getCount() > 0) {
            while (result.moveToNext()) {
                long id = result.getLong(0);
                boolean isChallenge = result.getInt(1) == 1;

                if (!isChallenge) {
                    Run run = fetchRun(id);
                    runs.add(run);
                }
            }
        }
        result.close();
        return runs;
    }

    /**
     * Retrieve all challenges present in the database.
     *
     * @return the list of challenges present in the database
     */
    public List<Challenge> fetchAllChallenges() {
        Cursor result = db.query(CHALLENGES_TABLE_NAME, CHALLENGES_COLS, null, null, null, null, null);
        List<Challenge> challenges = new ArrayList<>();
        if (result.getCount() > 0) {
            while (result.moveToNext()) {
                long id = result.getLong(0);
                String opponentName = result.getString(1);

                Challenge.Type type = null;
                if (result.getString(2).equals(Challenge.Type.TIME.toString())) {
                    type = Challenge.Type.TIME;
                } else if (result.getString(2).equals(Challenge.Type.DISTANCE.toString())) {
                    type = Challenge.Type.DISTANCE;
                }

                double goal = result.getDouble(3);

                Challenge.Result resultOfTheRun = Challenge.Result.LOST;
                switch (result.getShort(4)) {
                    case 0:
                        resultOfTheRun = Challenge.Result.WON;
                        break;
                    case 1:
                        resultOfTheRun = Challenge.Result.LOST;
                        break;
                    case 2:
                        resultOfTheRun = Challenge.Result.ABORTED_BY_ME;
                        break;
                    case 3:
                        resultOfTheRun = Challenge.Result.ABORTED_BY_OTHER;
                        break;
                    default:
                }

                Run myRun = fetchRun(result.getLong(5));
                Run opponentRun = fetchRun(result.getLong(6));
                Challenge challenge = new Challenge(opponentName, type, goal, resultOfTheRun, myRun, opponentRun);
                challenge.setId(id);
                challenges.add(challenge);
            }
        }
        result.close();
        return challenges;
    }

    /**
     * Retrieve a specific run from the database given its id.
     *
     * @param id the id of the run to retrieve
     * @return the run
     */
    private Run fetchRun(long id) {
        String selectRun = RUNS_COLS[0] + " = " + id;
        Cursor result = db.query(RUNS_TABLE_NAME, RUNS_COLS, selectRun, null, null, null, null);
        if (result.getCount() == 1) {
            result.moveToNext();
            String name = result.getString(2);
            long duration = Long.parseLong(result.getString(3));
            long fromId = result.getLong(4);
            long toId = result.getLong(5);
            Track track = fetchTrack(fromId, toId);
            Run run = new Run(name, id);
            run.setTrack(track);
            run.setDuration(duration);

            return run;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Retrieve a track from the database given its starting and ending index.
     *
     * @param fromId the id of the first checkpoint
     * @param toId the id of the last checkpoint
     * @return the track
     */
    private Track fetchTrack(long fromId, long toId) {
        String selection = CHECKPOINTS_COLS[0] + " >= " + fromId + " AND " + CHECKPOINTS_COLS[0] + " <= " + toId;
        Cursor result = db.query(CHECKPOINTS_TABLE_NAME, CHECKPOINTS_COLS, selection, null, null, null, null);
        Track track = new Track();
        if (result.getCount() > 0) {
            while (result.moveToNext()) {
                Double latitude = result.getDouble(1);
                Double longitude = result.getDouble(2);

                CheckPoint checkpoint = new CheckPoint(latitude, longitude);
                track.add(checkpoint);
            }
        }
        result.close();
        return track;
    }

    /**
     * Getter for the path of the database <code>File</code>
     *
     * @return  database <code>File</code> path
     */
    public File getDatabasePath() {
        return mContext.getDatabasePath(DATABASE_NAME);
    }

    /**
     * Getter for the database
     *
     * @return the database
     */
    public SQLiteDatabase getDatabase() {
        return db;
    }
}
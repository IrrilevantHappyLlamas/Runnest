package ch.epfl.sweng.project.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.sweng.project.Model.CheckPoint;
import ch.epfl.sweng.project.Model.Effort;
import ch.epfl.sweng.project.Model.Run;
import ch.epfl.sweng.project.Model.Track;

public class DBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;

    private static final String DATABASE_NAME = "efforts.db";

    private static final String EFFORTS_TABLE_NAME = "efforts";
    private static final String CHECKPOINTS_TABLE_NAME = "checkpoints";

    public static final String[] EFFORTS_COLS = {"id", "name", "type", "checkpointsFromId", "checkpointsToId"};
    public static final String[] CHECKPOINTS_COLS = {"id", "latitude", "longitude", "altitude", "time"};

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createEffortsTableQuery = "CREATE TABLE " + EFFORTS_TABLE_NAME + " (" + EFFORTS_COLS[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EFFORTS_COLS[1] + " TEXT, " + EFFORTS_COLS[2] + " TEXT, "+ EFFORTS_COLS[3] + " INTEGER, " + EFFORTS_COLS[4] + " INTEGER)";
        String createCheckpointsTableQuery = "CREATE TABLE " + CHECKPOINTS_TABLE_NAME + " (" + CHECKPOINTS_COLS[0] + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CHECKPOINTS_COLS[1] + " DOUBLE, " + CHECKPOINTS_COLS[2] + " DOUBLE, " + CHECKPOINTS_COLS[3] + " DOUBLE, " + CHECKPOINTS_COLS[4] + " INTEGER)";
        db.execSQL(createEffortsTableQuery);
        db.execSQL(createCheckpointsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropEffortsTableQuery = "DROP TABLE IF EXISTS " + EFFORTS_TABLE_NAME;
        String dropCheckpointsTableQuery = "DROP TABLE IF EXISTS " + CHECKPOINTS_TABLE_NAME;
        db.execSQL(dropEffortsTableQuery);
        db.execSQL(dropCheckpointsTableQuery);
        onCreate(db);
    }

    public boolean insert(Effort effort) {
        //insert all checkpoints
        Track track = effort.getTrack();
        List<CheckPoint> checkpoints = track.getCheckpoints();
        long checkpointsFromId = -1;
        long checkpointsToId = -1;
        for (CheckPoint checkpoint : checkpoints) {
            checkpointsToId = insert(checkpoint);
            if (checkpointsToId == -1) {
                return false;
            }
            if (checkpointsFromId == -1) {
                checkpointsFromId = checkpointsToId;
            }
        }

        //insert Effort
        String name = effort.getName();
        String type = "run";
        ContentValues effortContentValues = new ContentValues();
        effortContentValues.put(EFFORTS_COLS[1], name);
        effortContentValues.put(EFFORTS_COLS[2], type);
        effortContentValues.put(EFFORTS_COLS[3], checkpointsFromId);
        effortContentValues.put(EFFORTS_COLS[4], checkpointsToId);
        long insertedEffort = db.insert(EFFORTS_TABLE_NAME, null, effortContentValues);
        return insertedEffort != -1;
    }

    /**
     * Inserts a checkpoint in the checkpoints table
     * @param checkpoint the checkpoint to insert
     * @return the id of the inserted row
     */
    private long insert(CheckPoint checkpoint) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHECKPOINTS_COLS[1], checkpoint.getLatitude());
        contentValues.put(CHECKPOINTS_COLS[2], checkpoint.getLongitude());
        contentValues.put(CHECKPOINTS_COLS[3], checkpoint.getAltitude());
        contentValues.put(CHECKPOINTS_COLS[4], checkpoint.getTime());
        return db.insert(CHECKPOINTS_TABLE_NAME, null, contentValues);
    }

    /**
     * Delete an effort given its id
     * @param id the id of the effort to delete
     * @return true if the deletion was succesfull
     */
    private boolean deleteEffort(long id) {
        //also needs to delete checkpoints
        return db.delete(EFFORTS_TABLE_NAME, EFFORTS_COLS[0] + " = " + id, null) > 0;
    }

    public List<Run> fetchAllEfforts() {
        Cursor result = db.query(EFFORTS_TABLE_NAME, EFFORTS_COLS, null, null, null, null, null);
        List<Run> efforts = new ArrayList<>();
        if (result.getCount() > 0) {
            while (result.moveToNext()) {
                //long id = result.getLong(0);
                String name = result.getString(1);
                //String type = result.getString(2);
                long fromId = result.getLong(3);
                long toId = result.getLong(4);
                Track track = fetchTrack(fromId, toId);
                Run run = new Run(name);
                run.setTrack(track);
                efforts.add(run);
            }
        }
        result.close();
        return efforts;
    }

    private Track fetchTrack(long fromId, long toId) {
        System.out.println("_____-----_____-----_____ " + fromId + " "+ toId);
        String selection = CHECKPOINTS_COLS[0] + " >= " + fromId + " AND " + CHECKPOINTS_COLS[0] + " <= " + toId;
        Cursor result = db.query(CHECKPOINTS_TABLE_NAME, CHECKPOINTS_COLS, selection, null, null, null, null);
        Track track = new Track();
        if (result.getCount() > 0) {
            while (result.moveToNext()) {
                Location location = new Location("");
                location.setLatitude(result.getDouble(1));
                location.setLongitude(result.getDouble(2));
                location.setAltitude(result.getDouble(3));
                location.setTime(result.getLong(4));
                CheckPoint checkpoint = new CheckPoint(location);
                boolean isAdded = track.add(checkpoint);
                System.out.println(isAdded + " " + track.getTotalCheckPoints());
            }
        }
        result.close();
        return track;
    }
}
package ransomts.foobardarts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by tim on 3/12/17.
 */

public class DartDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Darts.db";

    public DartDbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String SQL_CREATE_ENTRIES =
            DartsContract.Users.SQL_CREATE_ENTRIES + DartsContract.GameNames.SQL_CREATE_ENTRIES +
            DartsContract.Game.SQL_CREATE_ENTRIES + DartsContract.Stats.SQL_CREATE_ENTRIES;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Writeup of my sqlite database so I can use those sweet sweet string completions aw yeah
    public static final class DartsContract {
        // Just to make sure I won't call this
        private DartsContract(){}

        public static class Users implements BaseColumns {
            public static final String TABLE_NAME = "users";
            public static final String COLUMN_ID = "id";
            public static final String COLUMN_USERNAME = "username";

            public static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + Users.TABLE_NAME + " (" +
                    Users.COLUMN_ID +        " VARCHAR(255), " +
                    Users.COLUMN_USERNAME  + " VARCHAR(255), " +
                            "PRIMARY KEY (" + Users.COLUMN_ID + "));";

            public static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        }

        public static class Stats implements BaseColumns {
            public static final String TABLE_NAME = "statistics";
            public static final String COLUMN_WLRATIO = "win_lose_ratio";
            public static final String COLUMN_TOTAL_BULLS = "total_bulls";
            public static final String COLUMN_NUM_TOTAL_POINTS = "total_points";
            public static final String COLUMN_MOST_HIT_NUMBER = "most_hit_number";
            public static final String COLUMN_USER_ID = "user_id";


            public static final String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_WLRATIO + " REAL, " +
                    COLUMN_TOTAL_BULLS + " BIGINT, " +
                    COLUMN_NUM_TOTAL_POINTS + " BIGINT, " +
                    COLUMN_MOST_HIT_NUMBER  + " INT8, " +
                    COLUMN_USER_ID + " VARCHAR(255), " +
                    "PRIMARY KEY (" + COLUMN_USER_ID +")," +
                            "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES" +
                            Users.TABLE_NAME + "." + Users.COLUMN_ID + ");";

            public static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        }
        public static class GameNames implements BaseColumns {
            public static final String TABLE_NAME = "game_types";
            public static final String COLUMN_GAMENAME = "game_name";
            public static final String COLUMN_RULES = "rules";

            public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_RULES + " TEXT, " +
                    COLUMN_GAMENAME + " VARCHAR(255), " +
                    "PRIMARY KEY (" + COLUMN_GAMENAME + ")" +
                    ");";

            public static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        }
        public static class Game implements BaseColumns {
            public static final String TABLE_NAME = "game";
            public static final String COLUMN_START_TIME = "game_started";
            public static final String COLUMN_PLAYERID = "player_id";
            public static final String COLUMN_WON = "won";
            public static final String COLUMN_GAME_NAME = "game_name";
            public static final String COLUMN_GAME_DATA = "game_data";
            public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_GAME_DATA + " VARCHAR(255), " +
                    COLUMN_GAME_NAME + " VARCHAR(255), " +
                    COLUMN_PLAYERID + " VARCHAR(255), " +
                    COLUMN_START_TIME + " DATETIME, " +
                    COLUMN_WON + " BOOLEAN, " +
                    "PRIMARY KEY (" + COLUMN_PLAYERID + ", " + COLUMN_START_TIME + ", " + COLUMN_GAME_NAME + ")" +
                    ");" ;

            public static final String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        }
    }
}

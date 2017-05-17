package ransomts.foobardarts.X01;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Model of a Game entry in the firebase database
 */

abstract class Game implements Parcelable {

    private int shotsPerTurn;
    private Date startTime;
    private Date endTime;
    private String gameId;
    private String winner;
    private String state;
    private Turn mostRecentTurn;
    // Having this be an abstracted class adds a layer to the database playersReady entry
    // Maybe that's not so good? It looks fine for now
    private PlayersReady playersReady;
    private ArrayList<Turn> turns;
    private HashMap<String, Integer> currentScores;

    //DatabaseReference gameRef;
    private final String TAG = "GAME";

    public Game() {
    }

    // Constructor specifically for the X01_game class
    public Game(int shotsPerTurn,
                String gameId,
                PlayersReady playersReady) {
        this(shotsPerTurn, null, null, gameId, null, "notStarted", null, playersReady, null, null, null);
        turns = new ArrayList<>();
        //getGameRef().setValue(this);
    }

    // Do we already know everything?
    public Game(int shotsPerTurn,
                Date startTime,
                Date endTime,
                String gameId,
                String winner,
                String state,
                Turn mostRecentTurn,
                PlayersReady playersReady,
                ArrayList<String> players,
                ArrayList<Turn> turns,
                HashMap<String, Integer> currentScores) {

        setShotsPerTurn(shotsPerTurn);
        setStartTime(startTime);
        setEndTime(endTime);
        setGameId(gameId);
        setWinner(winner);
        setState(state);
        setMostRecentTurn(mostRecentTurn);
        setPlayersReady(playersReady);
        //setPlayers(players);
        setTurns(turns);
        setCurrentScores(currentScores);

        setInitialScores();

        //gameRef = getGameRef();
    }

    // game identifier for the games node in the database
    public void setGameId(String gameId) {

        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    // The state of the game, notStarted, inProgress, finished
    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    // Time that the game began play
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    // Time that the winner was decided
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    // User id of the winner of the game
    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }

    // Used when setting up the game in the notStarted node in the database
    public void setPlayersReady(PlayersReady playersReady) {
        this.playersReady = playersReady;
    }

    public PlayersReady getPlayersReady() {
        return playersReady;
    }

    // The list of turns in the game
    public void setTurns(ArrayList<Turn> turns) {
        this.turns = turns;
    }

    public ArrayList<Turn> getTurns() {
        return turns;
    }

    // Maybe this one isn't needed?
    public void setMostRecentTurn(Turn mostRecentTurn) {
        this.mostRecentTurn = mostRecentTurn;
    }

    public Turn getMostRecentTurn() {
        return mostRecentTurn;
    }

    public HashMap<String, Integer> getCurrentScores() {
        return currentScores;
    }

    public void setCurrentScores(HashMap<String, Integer> currentScores) {
        this.currentScores = currentScores;
    }

    public int getShotsPerTurn() {
        return shotsPerTurn;
    }

    public void setShotsPerTurn(int shotsPerTurn) {
        this.shotsPerTurn = shotsPerTurn;
    }

    // should return the key to the winner entry in the currentScores hashMap, or null if not won yet
    abstract String winConditionMet();
    abstract Integer calculateNewScore(Turn turn);
    abstract void setInitialScores();

    public boolean updateScore(Turn turn) {
        // put returns a null if there was no entry with that key previously, returns the previous value if the key existed
        return currentScores.put(turn.getPlayerId(), calculateNewScore(turn)) != null;
    }

    // returns a string of the winners ID when a winner is decided
    public String addTurn(Turn turn) {
        updateScore(turn); // update the local scores
        // actually add the turn to the list of turns
        turns.add(turn);
        // push turn to database
        DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("games").child(getState()).child(getGameId());

        gameRef.child("turns").setValue(getTurns());
        gameRef.child("currentScores").setValue(currentScores);
        return winConditionMet();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.shotsPerTurn);
        dest.writeLong(this.startTime != null ? this.startTime.getTime() : -1);
        dest.writeLong(this.endTime != null ? this.endTime.getTime() : -1);
        dest.writeString(this.gameId);
        dest.writeString(this.winner);
        dest.writeString(this.state);
        dest.writeParcelable(this.mostRecentTurn, flags);
        dest.writeParcelable(this.playersReady, flags);
        //dest.writeStringList(this.players);
        dest.writeTypedList(this.turns);
        dest.writeSerializable(this.currentScores);
    }

    protected Game(Parcel in) {
        this.shotsPerTurn = in.readInt();
        long tmpStartTime = in.readLong();
        this.startTime = tmpStartTime == -1 ? null : new Date(tmpStartTime);
        long tmpEndTime = in.readLong();
        this.endTime = tmpEndTime == -1 ? null : new Date(tmpEndTime);
        this.gameId = in.readString();
        this.winner = in.readString();
        this.state = in.readString();
        this.mostRecentTurn = in.readParcelable(Turn.class.getClassLoader());
        this.playersReady = in.readParcelable(PlayersReady.class.getClassLoader());
        //this.players = in.createStringArrayList();
        this.turns = in.createTypedArrayList(Turn.CREATOR);
        this.currentScores = (HashMap<String, Integer>) in.readSerializable();
    }


    void moveGameForward() {


        DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("games").child(getState()).child(getGameId());

        gameRef.removeValue();

        gameRef = gameRef.getParent().getParent();

        switch (state) {
            case "notStarted":
                gameRef = gameRef.child("inProgress");
                state = "inProgress";
                break;
            case "inProgress":
                gameRef = gameRef.child("finished");
                state = "error";
                break;
            default:
                Log.e(TAG, "Tried to move game too far forward");
        }
        gameRef = gameRef.child(getGameId());
        gameRef.setValue(this);
    }

    /*
    public DatabaseReference getGameRef() {
        if (gameRef == null) {
            gameRef = FirebaseDatabase.getInstance().getReference().
                    child("games").
                    child(state).
                    child(getGameId());
        }
        return gameRef;
    }
    */
}

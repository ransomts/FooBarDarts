package ransomts.foobardarts.X01;

/*
  Created by tim on 3/11/17.

  Java class to model a game of 501 or 301
 */

import android.os.Parcel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

class X01_game extends Game {

    // only data that is specific to a 501 game
    private int scoreGoal;
    private boolean doubleIn;
    private boolean doubleOut;

    X01_game() {  }

    @Override
    public void setInitialScores() {
        Set<String> players = getPlayersReady().getPlayersReady().keySet();
        HashMap<String, Integer> initialScores = new HashMap<>(players.size());

        // Everybody starts with a scoreGoal to hit
        for (String player : players) {
            initialScores.put(player, scoreGoal);
        }
        setCurrentScores(initialScores);
    }

    @Override
    public Integer calculateNewScore(Turn turn) {
        int oldScore = getCurrentScores().get(turn.getPlayerId());
        return oldScore - turn.getPointTotal();
    }

    @Override
    String winConditionMet() {
        // The last shot may need to be a double
        if (doubleOut && !getMostRecentTurn().lastShotIsDouble()) {
            return null;
        }
        // when a player has a score of exactly 0, they win
        if (getCurrentScores().containsValue(0)) {
            for (String playerId : getCurrentScores().keySet()) {
                if (getCurrentScores().get(playerId) == 0) {
                    setEndTime(Calendar.getInstance().getTime());
                    return playerId;
                }
            }
        }
        return null;
    }

    X01_game(int scoreGoal, boolean double_in, boolean double_out, String gameId, PlayersReady playersReady) {

        // 3 shots in a 01 game
        super(3, gameId, playersReady);

        this.scoreGoal = scoreGoal;
        this.doubleIn = double_in;
        this.doubleOut = double_out;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.scoreGoal);
        dest.writeByte(this.doubleIn ? (byte) 1 : (byte) 0);
        dest.writeByte(this.doubleOut ? (byte) 1 : (byte) 0);
    }

    protected X01_game(Parcel in) {
        super(in);
        this.scoreGoal = in.readInt();
        this.doubleIn = in.readByte() != 0;
        this.doubleOut = in.readByte() != 0;
    }

    public static final Creator<X01_game> CREATOR = new Creator<X01_game>() {
        @Override
        public X01_game createFromParcel(Parcel source) {
            return new X01_game(source);
        }

        @Override
        public X01_game[] newArray(int size) {
            return new X01_game[size];
        }
    };
}

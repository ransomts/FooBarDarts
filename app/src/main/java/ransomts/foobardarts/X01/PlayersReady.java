package ransomts.foobardarts.X01;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Small class to help determine when all networked players are ready to begin,
 * created to be compatible with the firebase database and able to be passed around in Android
 */

public class PlayersReady implements Parcelable {
    HashMap<String, Boolean> playersReady;
    private ArrayList<String> turnOrder;

    public PlayersReady() {

        playersReady = new HashMap<>();
    }

    public PlayersReady(HashMap<String, Boolean> playersReady) {
        this();
        setPlayersReady(playersReady);
    }

    public void setPlayersReady(HashMap<String, Boolean> playersReady) {
        this.playersReady = playersReady;
    }

    public boolean allPlayersReady() {

        for (String key : playersReady.keySet()) {
            if (!playersReady.get(key)) {
                return false;
            }
        }

        // Once every person is ready, assign an order to them
        turnOrder = new ArrayList<>();
        for (String key : playersReady.keySet()) {
            turnOrder.add(key);
        }
        // and destroy the hashmap, since every person is ready now
        // plus this changes how the object is represented in the database
        playersReady = null;
        return true;
    }

    public void updatePlayerStatus(String player, boolean status) {
        playersReady.put(player, status);
    }

    public HashMap<String, Boolean> getPlayersReady() {
        return playersReady;
    }

    public void addPlayer(String playerName) {
        // All players start out not ready
        playersReady.put(playerName, false);
    }

    public ArrayList<String> getTurnOrder() { return turnOrder; }
    public void setTurnOrder(ArrayList<String> turnOrder) {
        this.turnOrder = turnOrder;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.playersReady);
        dest.writeStringList(this.turnOrder);
    }

    protected PlayersReady(Parcel in) {
        this.playersReady = (HashMap<String, Boolean>) in.readSerializable();
        this.turnOrder = in.createStringArrayList();
    }

    public static final Parcelable.Creator<PlayersReady> CREATOR = new Parcelable.Creator<PlayersReady>() {
        @Override
        public PlayersReady createFromParcel(Parcel source) {
            return new PlayersReady(source);
        }

        @Override
        public PlayersReady[] newArray(int size) {
            return new PlayersReady[size];
        }
    };
}

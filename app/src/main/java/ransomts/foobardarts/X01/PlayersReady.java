package ransomts.foobardarts.X01;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

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

    public PlayersReady() {
        playersReady = new HashMap<>();
    }

    public PlayersReady(HashMap<String, Boolean> playersReady) {
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
        return true;
    }

    public void updatePlayerStatus(String player, boolean status) {
        playersReady.put(player, status);
    }

    public HashMap<String, Boolean> getPlayersReady() {
        return playersReady;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.playersReady.size());
        for (Map.Entry<String, Boolean> entry : this.playersReady.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
    }

    public void addPlayer(String playerName) {
        // All players start out not ready
        playersReady.put(playerName, false);
    }

    protected PlayersReady(Parcel in) {
        int playersReadySize = in.readInt();
        this.playersReady = new HashMap<String, Boolean>(playersReadySize);
        for (int i = 0; i < playersReadySize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.playersReady.put(key, value);
        }
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

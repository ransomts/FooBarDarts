package ransomts.foobardarts.X01;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by tim on 4/17/17.
 */

class PlayersReady {
    Map<String, Boolean> playersReady;

    public PlayersReady() {    }

    public PlayersReady(Map<String, Boolean> playersReady) {
        setPlayersReady(playersReady);
    }

    public void setPlayersReady(Map<String,Boolean> playersReady) {
        this.playersReady = playersReady;
    }

    public Map<String, Boolean> getPlayersReady() {
        return playersReady;
    }
}

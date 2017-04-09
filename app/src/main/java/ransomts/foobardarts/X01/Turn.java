package ransomts.foobardarts.X01;

/*
  Created by tim on 3/11/17.

  Java object to model one dart turn in any game
 */

import java.util.ArrayList;
import java.util.List;

class Turn {

    public void setPointTotal(int pointTotal) {
        this.pointTotal = pointTotal;
    }

    public int getPointTotal() {
        return pointTotal;
    }

    // these say they're unused but the database uses them to parse into the json format
    public List<Modifier> getMods() { return mods; }
    public List<Integer> getValues() { return values; }
    public String getPlayerId() { return playerId; }

    enum Modifier {Single, Double, Triple}

    private String playerId;
    private List<Integer> values;
    private List<Modifier> mods;
    private int pointTotal;

    Turn() {
        this("Ziltoid", 3);
    }

    Turn(String playerId) {
        this(playerId, 3);
    }

    Turn(String playerId, int shots_per_turn) {

        values = new ArrayList<Integer>(shots_per_turn);
        mods = new ArrayList<Modifier>(shots_per_turn);
        this.playerId = playerId;
    }

    boolean addShot(int value, Modifier mod) {
        if (value == 25 && mod == Modifier.Triple || values.size() > 3) {
            return false;
        }
        values.add(value);
        mods.add(mod);
        return true;
    }
}

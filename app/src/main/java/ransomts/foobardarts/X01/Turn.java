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

    Turn(String playerId, int shots_per_turn) {

        values = new ArrayList<Integer>(shots_per_turn);
        mods = new ArrayList<Modifier>(shots_per_turn);
        this.playerId = playerId;
    }

    boolean add_shot(int value, Modifier mod) {
        if (value == 25 && mod == Modifier.Triple) {
            return false;
        }
        values.add(value);
        mods.add(mod);
        return true;
    }
/*
    @Override
    public String toString() {
        String temp = playerId + "[";
        for (int i = 0; i < shots_taken; i++) {
            temp += "{" + mods[i] + values[i] + "}";
            // trying to keep this to be valid json
            if (i != shots_taken - 1) {
                temp += ",";
            }
        }
        return temp + "]";
    }
    */
}

package ransomts.foobardarts.X01;

/*
  Created by tim on 3/11/17.

  Java object to model one dart turn in any game
 */

import java.util.ArrayList;
import java.util.List;

class Turn {

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

        values = new ArrayList<>(shots_per_turn);
        mods = new ArrayList<>(shots_per_turn);
        this.playerId = playerId;
    }

    // these say they're unused but the database uses them to parse into the json format
    public int getPointTotal() {
        return pointTotal;
    }

    public void setPointTotal(int pointTotal) {
        this.pointTotal = pointTotal;
    }

    public List<Modifier> getMods() {
        return mods;
    }

    public List<Integer> getValues() {
        return values;
    }

    public String getPlayerId() {
        return playerId;
    }

    void addShot(int value, Modifier mod) {
        if (value == 25 && mod == Modifier.Triple || values.size() > 3) {
            values.add(0);
            mods.add(Modifier.Single);
            return;
        }
        values.add(value);
        mods.add(mod);

        switch(mod) {
            case Double: value *= 2; break;
            case Triple: value *= 3; break;
        }
        pointTotal += value;
    }

    void removeShot() {
        if (values.size() == 0) { return; }
        values.remove(values.size() - 1);
        mods.remove(mods.size() - 1);
    }
}

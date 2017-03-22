package ransomts.foobardarts;

/*
  Created by tim on 3/11/17.

  Java object to model one dart turn in any game
 */

class Turn {

    enum Modifier {Single, Double, Triple}

    private String playerId;
    private int shots_taken;
    private int[] values;
    private Modifier[] mods;

    Turn() {
        this("Ziltoid", 3);
    }

    Turn(String playerId, int shots_per_turn) {

        shots_taken = 0;
        values = new int[shots_per_turn];
        mods = new Modifier[shots_per_turn];
        this.playerId = playerId;
    }

    boolean add_shot(int value, Modifier mod) {
        if (value == 25 && mod == Modifier.Triple) {
            return false;
        }
        values[shots_taken] = value;
        mods[shots_taken] = mod;
        shots_taken++;
        return true;
    }

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
}

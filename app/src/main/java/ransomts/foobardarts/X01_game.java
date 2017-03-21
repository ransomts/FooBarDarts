package ransomts.foobardarts;

/*
  Created by tim on 3/11/17.

  Java class to model a game of 501 or 301
 */

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

class X01_game {

    private String start_time;
    private String[] players;
    private int player_turn_index;
    private int score_goal;
    private boolean double_in;
    private boolean double_out;
    private LinkedList<Turn>[] turns;
    private HashMap<String, Integer> current_scores;
    private int winningPlayerIndex;
    private int shots_per_turn;

    private int get_player_turn_index() {
        return player_turn_index;
    }

    private void set_player_turn_index(int player_turn_index) {
        this.player_turn_index = player_turn_index % players.length;
    }

    X01_game() {
        this(new String[]{"Player One"}, 501, false, false);
    }

    X01_game(String[] players, int score_goal, boolean double_in, boolean double_out) {

        this.start_time = DateFormat.getTimeInstance().format(new Date());
        this.players = new String[players.length];

        System.arraycopy(players, 0, this.players, 0, players.length);

        this.player_turn_index = 0;
        this.score_goal = score_goal;
        this.double_in = double_in;
        this.double_out = double_out;
        this.winningPlayerIndex = -1;
        this.shots_per_turn = 3;

        current_scores = new HashMap<String, Integer>();
        turns = new LinkedList[players.length];
        for (int i = 0; i < players.length; i++) {
            turns[i] = new LinkedList<Turn>();
            this.current_scores.put(players[i], score_goal);
        }
    }

    boolean add_turn(int[] shot_values, Turn.Modifier[] mod_values) {

        // Make sure everything is well formed
        for (int i = 0; i < 3; i++) {
            // TODO: double check this conditional
            if (shot_values[i] < 1 || shot_values[i] > 20 && shot_values[i] != 25) {
                return false;
            }
        }

        Turn turn = new Turn();
        int point_total = 0;
        for (int i = 0; i < 3; i++) {
            turn.add_shot(shot_values[i], mod_values[i]);
            switch (mod_values[i]) {
                case Double:
                    shot_values[i] *= 2;
                    break;
                case Triple:
                    shot_values[i] *= 3;
                    break;
            }
            point_total += shot_values[i];
        }

        String cur_play = getCurrentPlayer();
        if (current_scores.get(cur_play) - point_total >= 0) {
            current_scores.put(cur_play, current_scores.get(cur_play) - point_total);
        }
        current_scores.put(cur_play, current_scores.get(cur_play));
        LinkedList<Turn> current_turn = this.turns[player_turn_index];
        current_turn.add(turn);
        set_player_turn_index(get_player_turn_index() + 1);

        if (winCondition()) {
            winningPlayerIndex = player_turn_index;
        }
        return true;
    }

    private boolean winCondition() {
        return current_scores.get(getCurrentPlayer()) == 0;
    }

    public void store_game_in_database() {

    }

    String getCurrentPlayer() {
        return players[player_turn_index];
    }

    String getFirstPlayerName() {
        return players[0];
    }

    int getFirstPlayerScore() {
        return current_scores.get(getFirstPlayerName());
    }

    int getCurrentScore(String player) {
        return current_scores.get(player);
    }
    public int getNumPlayers() {
        return players.length;
    }

    public int getShotsPerTurn() {
        return shots_per_turn;
    }

    private int getScoreGoal() {
        return score_goal;
    }

    private boolean getDubIn() {
        return double_in;
    }
    public boolean getDubOut() {
        return double_out;
    }
}

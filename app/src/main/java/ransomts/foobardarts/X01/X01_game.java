package ransomts.foobardarts.X01;

/*
  Created by tim on 3/11/17.

  Java class to model a game of 501 or 301
 */

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        this(new String[]{"Player One"}, 501, false, false, "");
    }

    X01_game(String[] players, int score_goal, boolean double_in, boolean double_out, String game_id) {

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

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().
                getReference().child("game").child("game_id").child("most_recent_turn");

        ValueEventListener mostRecentTurnListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                handleDataChange(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(mostRecentTurnListener);
    }

    // TODO: Rename method
    private void handleDataChange(Object value) {
        //String values = (String) value;

    }

    boolean add_turn(int[] shot_values, Turn.Modifier[] mod_values) {

        // Make sure everything is well formed
        for (int i = 0; i < 3; i++) {
            // TODO: double check this conditional
            if (shot_values[i] < 1 || shot_values[i] > 20 && shot_values[i] != 25) {
                return false;
            }
        }

        Turn turn = make_turn(shot_values, mod_values);

        // handle all the local stuff
        update_current_scores(turn);
        update_current_turn(turn);
        // and update the database
        add_turn_to_database(turn);

        if (winCondition()) {
            winningPlayerIndex = player_turn_index;
        }
        return true;
    }

    private Turn make_turn(int[] shot_values, Turn.Modifier[] mod_values) {

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

        turn.setPointTotal(point_total);
        return turn;
    }

    private void update_current_turn(Turn turn) {
        LinkedList<Turn> current_turn = this.turns[player_turn_index];
        current_turn.add(turn);
        set_player_turn_index(get_player_turn_index() + 1);
    }

    private void update_current_scores(Turn turn) {
        int point_total = turn.getPointTotal();

        String cur_play = getCurrentPlayer();
        if (current_scores.get(cur_play) - point_total >= 0) {
            current_scores.put(cur_play, current_scores.get(cur_play) - point_total);
        }
        current_scores.put(cur_play, current_scores.get(cur_play));
    }

    private void add_turn_to_database(Turn turn) {
        DatabaseReference turn_reference = FirebaseDatabase.getInstance().getReference();
        turn_reference = turn_reference.child("game").child("game_id").child("turns");
        turn_reference = turn_reference.push();
        turn_reference.setValue(turn);

        DatabaseReference turn_reference2 = FirebaseDatabase.getInstance().getReference();
        turn_reference2 = turn_reference2.child("game").child("game_id").child("most_recent_turn");
        turn_reference2.setValue(turn);
    }

    private boolean winCondition() {
        return current_scores.get(getCurrentPlayer()) == 0;
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

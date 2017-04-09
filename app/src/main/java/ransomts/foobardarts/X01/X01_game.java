package ransomts.foobardarts.X01;

/*
  Created by tim on 3/11/17.

  Java class to model a game of 501 or 301
 */

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

class X01_game {

    private DatabaseReference game_ref;
    private String game_id;
    private Date start_time;
    private String[] players;
    private int playerTurnIndex;
    private int scoreGoal;
    private boolean double_in;
    private boolean double_out;
    private LinkedList<Turn> turns;
    private HashMap<String, Integer> current_scores;
    private int winningPlayerIndex;
    private int shots_per_turn;

    X01_game() {
        this(new String[]{"Player One"}, 501, false, false, "local_game");
    }

    X01_game(String[] players, int scoreGoal, boolean double_in, boolean double_out, String game_id) {

        initializeFields(players, scoreGoal, double_in, double_out, game_id);

        initializeGameInDatabase(game_id);

        initializeDatabaseListeners(game_id);
    }

    private void initializeFields
            (String[] players, int score_goal, boolean double_in, boolean double_out, String game_id) {

        this.players = new String[players.length];
        start_time = Calendar.getInstance().getTime();
        System.arraycopy(players, 0, this.players, 0, players.length);

        this.playerTurnIndex = 0;
        this.scoreGoal = score_goal;
        this.double_in = double_in;
        this.double_out = double_out;
        this.winningPlayerIndex = -1;
        this.shots_per_turn = 3;
        this.game_id = game_id;

        current_scores = new HashMap<String, Integer>();
        turns = new LinkedList<Turn>();

        game_ref = FirebaseDatabase.getInstance().getReference().child("game").child(game_id);
    }
    private void initializeGameInDatabase(String game_id) {

        String game_name = "X01";
        DatabaseReference game_ref = FirebaseDatabase.getInstance().getReference();
        game_ref = game_ref.child("game").child(game_id).child("game");

        game_ref.child("start_time").setValue(start_time);
        game_ref.child("game_type").setValue(game_name);
        game_ref.child("players").push().setValue("Ziltoid");

    }
    private void initializeDatabaseListeners(String game_id) {

        DatabaseReference mostRecentTurnRef = game_ref.child("most_recent_turn");
        ValueEventListener mostRecentTurnListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Integer> database_values = (ArrayList<Integer>) dataSnapshot.child("values").getValue();
                ArrayList<Turn.Modifier> database_mods = (ArrayList<Turn.Modifier>) dataSnapshot.child("mods").getValue();
                //Turn t = makeTurn(database_values,database_mods);
                //updateCurrentTurn(t);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };
        mostRecentTurnRef.addValueEventListener(mostRecentTurnListener);

        DatabaseReference currentScoresRef = game_ref.child("current_scores");
        ValueEventListener currentScoresListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO: No way in hell this works
                Turn turn = (Turn) dataSnapshot.getValue();
                updateCurrentScoresFromDatabase(turn);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        currentScoresRef.addValueEventListener(currentScoresListener);

        DatabaseReference winnerRef = game_ref.child("game").child("winner");
        ValueEventListener winnerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // TODO: implement
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        winnerRef.addValueEventListener(winnerListener);
    }

    private int getPlayerTurnIndex() {
        return playerTurnIndex;
    }

    private void setPlayerTurnIndex(int player_turn_index) {
        this.playerTurnIndex = player_turn_index % players.length;
    }

    boolean addTurn(ArrayList<Integer> shot_values, ArrayList<Turn.Modifier> mod_values, String player_id) {

        Turn turn = makeTurn(shot_values, mod_values, player_id);

        // and update the database, this will also trigger the local listener to update local data
        addTurnToDatabase(turn);

        /*
        if (winCondition()) {
            updateWinnerDatabase();
        }
        */
        return true;
    }

    private boolean winCondition() {
        return current_scores.get(getCurrentPlayer()) == 0;
    }

    private void updateWinnerDatabase() {
        DatabaseReference winnerRef = game_ref.child("game").child("winner");
        winnerRef.setValue(getCurrentPlayer());
    }

    public String getCurrentPlayer() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user.getDisplayName();
    }

    private Turn makeTurn(ArrayList<Integer> shot_values, ArrayList<Turn.Modifier> mod_values, String player_id) {

        Turn turn = new Turn(player_id);

        int point_total = 0;
        for (int i = 0; i < 3; i++) {
            int shotValue = shot_values.get(i);
            Turn.Modifier modValue = mod_values.get(i);
            turn.addShot(shotValue, modValue);
            switch (mod_values.get(i)) {
                case Double:
                    shot_values.set(i, shot_values.get(i) * 2);
                    break;
                case Triple:
                    shot_values.set(i, shot_values.get(i) * 3);
                    break;
            }
            point_total += shot_values.get(i);
        }

        turn.setPointTotal(point_total);
        return turn;
    }

    private void updateCurrentScoresFromDatabase(Turn turn) {

        //current_scores.put(turn.getPlayerId(), turn.getPointTotal());
    }

    private void addTurnToDatabase(Turn turn) {
        DatabaseReference turn_reference = game_ref.child("turns");
        turn_reference.push().setValue(turn);

        DatabaseReference most_recent_turn_ref = game_ref.child("most_recent_turn");
        most_recent_turn_ref.setValue(turn);
    }

    int getCurrentScore(String player) {
        return current_scores.get(player);
    }

    public int getShotsPerTurn() {
        return shots_per_turn;
    }

    public int getFirstPlayerScore() {
        return current_scores.get(players[0]);
    }

    public Turn getLatestTurn() {
        return turns == null || turns.size() == 0 ? null : turns.getLast();
    }
}

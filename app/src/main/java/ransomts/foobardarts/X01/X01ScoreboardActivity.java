package ransomts.foobardarts.X01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ransomts.foobardarts.R;

public class X01ScoreboardActivity extends AppCompatActivity
        implements View.OnClickListener {

    X01_game game;
    ArrayList<Turn.Modifier> mods;
    ArrayList<Integer> shots;
    int current_shot_index;
    Turn.Modifier shot_modifier;

    ToggleButton doubles;
    ToggleButton triples;
    TextView local_player;
    TextView remote_player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x01_scoreboard);

        //String[] players = getIntent().getStringArrayExtra("players");
        String[] players = new String[]{"Ziltoid"};
        int score_goal = getIntent().getIntExtra("score_goal", -1);
        boolean in = getIntent().getBooleanExtra("double_in", true);
        boolean out = getIntent().getBooleanExtra("double_out", true);
        String game_id = getIntent().getStringExtra("game_id");

        game = new X01_game(players, score_goal, in, out, game_id);

        current_shot_index = 0;
        mods = new ArrayList<>(game.getShotsPerTurn());
        shots = new ArrayList<>(game.getShotsPerTurn());

        setupScoreDatabaseListeners(game_id);

        doubles = (ToggleButton) findViewById(R.id.toggle_double);
        triples = (ToggleButton) findViewById(R.id.toggle_triple);

        local_player = (TextView) findViewById(R.id.view_local_player);
        remote_player = (TextView) findViewById(R.id.view_remote_player);
    }

    private void initializeDatabaseListeners(String game_id) {
    }

    void handleUndoButton() {

        // TODO: Be able to undo last shot of previous turn
        if (current_shot_index > 0) {
            current_shot_index--;
        }
        shots.set(current_shot_index,0);
        mods.set(current_shot_index, Turn.Modifier.Single);
    }

    void handleShotValues(int shot_value) {

        shots.add(shot_value);
        handleModifiers(findViewById(R.id.button0));
        update_local_values();
        current_shot_index = (current_shot_index + 1) % game.getShotsPerTurn();

        if (current_shot_index == 0) {
            game.addTurn(shots, mods, game.getCurrentPlayer());
            shots.clear();
            mods.clear();
        }

    }

    private void handleModifiers(View v) {
        switch (v.getId()) {
            // TODO: Can these if statements be reduced logically?
            case R.id.toggle_double:
                if (doubles.isChecked()) {
                    shot_modifier = Turn.Modifier.Double;
                    triples.setChecked(false);
                } else {
                    shot_modifier = Turn.Modifier.Single;
                }
                break;
            case R.id.toggle_triple:
                if (triples.isChecked()) {
                    shot_modifier = Turn.Modifier.Triple;
                    doubles.setChecked(false);
                } else {
                    shot_modifier = Turn.Modifier.Single;
                }
                break;
            default:
                shot_modifier = Turn.Modifier.Single;
                doubles.setChecked(false);
                triples.setChecked(false);
                // Shouldn't actually need this case, but whatever
                // update: turned out to actually be useful, go me
        }
        mods.add(shot_modifier);
    }

    private void update_local_values() {

        String display_string;

        switch (current_shot_index) {
            case 0:
                display_string = String.valueOf(shots.get(0)) + "\n" + String.valueOf(mods.get(0));
                ((TextView) findViewById(R.id.view_first_shot)).setText(display_string);
                break;
            case 1:
                // eat your heart out Java types
                display_string = String.valueOf(shots.get(1)) + "\n" + String.valueOf(mods.get(1));
                ((TextView) findViewById(R.id.view_second_shot)).setText(display_string);
                break;
            case 2:
                display_string = String.valueOf(shots.get(2)) + "\n" + String.valueOf(mods.get(2));
                ((TextView) findViewById(R.id.view_third_shot)).setText(display_string);
                break;
        }
        doubles.setChecked(false);
        triples.setChecked(false);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button0:
            case R.id.button1:
            case R.id.button2:
            case R.id.button3:
            case R.id.button4:
            case R.id.button5:
            case R.id.button6:
            case R.id.button7:
            case R.id.button8:
            case R.id.button9:
            case R.id.button10:
            case R.id.button11:
            case R.id.button12:
            case R.id.button13:
            case R.id.button14:
            case R.id.button15:
            case R.id.button16:
            case R.id.button17:
            case R.id.button18:
            case R.id.button19:
            case R.id.button20:
                // Can't decide if this is lazy or stupid. It's one of them
                Button b = (Button) v;
                // All the buttons are labeled with their numeric values, so...
                handleShotValues(Integer.valueOf(b.getText().toString()));
                break;
            case R.id.buttonBull:
                handleShotValues(25);
                break;
            // TODO: Undo button showing some runtime logic errors
            case R.id.buttonUndo:
                handleUndoButton();
            case R.id.toggle_double:
            case R.id.toggle_triple:
                handleModifiers(v);
                break;
        }
    }

    public void setupScoreDatabaseListeners(String game_id) {
        DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference();
        gameRef = gameRef.child("game").child(game_id);

        DatabaseReference mostRecentTurnRef = gameRef.child("game").child(game_id).child("most_recent_turn");
        ValueEventListener mostRecentTurnListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String values = dataSnapshot.toString();
                //updateVisuals();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mostRecentTurnRef.addValueEventListener(mostRecentTurnListener);


        DatabaseReference turnRef = gameRef.child("most_recent_turn");
        ValueEventListener turnListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("values").getValue() == null) { return; }
                // because fuck memory space amirite
                long shot_a = (long) dataSnapshot.child("values").child("0").getValue();
                int a = (int) shot_a;

                long shot_b = (long) dataSnapshot.child("values").child("1").getValue();
                int b = (int) shot_b;

                long shot_c = (long) dataSnapshot.child("values").child("2").getValue();
                int c = (int) shot_c;

                // and fuck these things too >.>
                Turn.Modifier mod_a =
                        Turn.Modifier.valueOf(dataSnapshot.child("mods").child("0").getValue().toString());

                Turn.Modifier mod_b =
                        Turn.Modifier.valueOf(dataSnapshot.child("mods").child("1").getValue().toString());

                Turn.Modifier mod_c =
                        Turn.Modifier.valueOf(dataSnapshot.child("mods").child("2").getValue().toString());

                ArrayList<Integer> shots = new ArrayList<>(3);
                shots.add(a);
                shots.add(b);
                shots.add(c);

                ArrayList<Turn.Modifier> mods = new ArrayList<>(3);
                mods.add(mod_a);
                mods.add(mod_b);
                mods.add(mod_c);

                Turn turn = new Turn(dataSnapshot.child("playerId").getValue().toString());
                for (int i = 0; i < 3; i++) {
                    turn.addShot(shots.get(i), mods.get(i));
                }

                updateScoreboardFromDatabase(turn);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        turnRef.addValueEventListener(turnListener);

    }

    // specifically, update the visuals based on the X01 game controller a la mvc
    private void updateScoreboardFromDatabase(Turn turn) {

        //if (turn == null || turn.getPlayerId().compareTo(game.getCurrentPlayer()) == 0) {
        if (turn == null) {
            return;
        }

        List<Integer> remoteShots = turn.getValues();
        List<Turn.Modifier> remoteMods = turn.getMods();

        String display_string;
        display_string = String.valueOf(remoteShots.get(0)) + "\n" + String.valueOf(remoteMods.get(0));
        ((TextView) findViewById(R.id.view_first_remote_shot)).setText(display_string);
        display_string = String.valueOf(remoteShots.get(1)) + "\n" + String.valueOf(remoteMods.get(1));
        ((TextView) findViewById(R.id.view_second_remote_shot)).setText(display_string);
        display_string = String.valueOf(remoteShots.get(2)) + "\n" + String.valueOf(remoteMods.get(2));
        ((TextView) findViewById(R.id.view_third_remote_shot)).setText(display_string);

        String remotePlayerName = turn.getPlayerId();
        ((TextView) findViewById(R.id.view_remote_player)).setText(remotePlayerName);
    }
}

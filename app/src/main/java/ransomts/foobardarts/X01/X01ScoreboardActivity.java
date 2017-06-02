package ransomts.foobardarts.X01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ransomts.foobardarts.R;

public class X01ScoreboardActivity extends AppCompatActivity
        implements View.OnClickListener {

    // The game that the turn will be added to
    X01_game game;
    // The turn that this activity is constructing
    Turn turn;
    Turn.Modifier shotModifier;
    String localPlayerName;

    ToggleButton doubles;
    ToggleButton triples;
    TextView localPlayerView;
    TextView remotePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x01_scoreboard);

        game = getIntent().getParcelableExtra("game");
        localPlayerName = getIntent().getStringExtra("localPlayerName");

        turn = new Turn(localPlayerName);
        shotModifier = Turn.Modifier.Single;

        doubles = (ToggleButton) findViewById(R.id.toggle_double);
        triples = (ToggleButton) findViewById(R.id.toggle_triple);

        localPlayerView = (TextView) findViewById(R.id.view_local_player);
        remotePlayerView = (TextView) findViewById(R.id.view_remote_player);

        DatabaseReference remoteTurns = FirebaseDatabase.getInstance().getReference();
        remoteTurns = remoteTurns.child("games").child(game.getState()).child(game.getGameId());
        remoteTurns.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Things that can change in the game object:
                // turns - when a turn is submitted, it is added to the arraylist
                // current_scores - always computed on the submitting device, then pushed and pulled here
                // winner - when a winner is declared, this will not be null
                Game new_game = dataSnapshot.getValue(Game.class);

                if (new_game.getWinner() != null) {
                    // A winner is you!
                    LinearLayout localPlayerScore = (LinearLayout) findViewById(R.id.local_player_score);
                    LinearLayout remotePlayerScore = (LinearLayout) findViewById(R.id.remote_player_score);

                    if (new_game.getWinner().equals(localPlayerName)) {
                        localPlayerScore.setBackgroundColor(0x42f45c);
                        remotePlayerScore.setBackgroundColor(0xe26b38);
                    } else {
                        remotePlayerScore.setBackgroundColor(0x42f45c);
                        localPlayerScore.setBackgroundColor(0xe26b38);
                    }
                }
                if (localPlayerName.equals(new_game.getPlayersReady().getTurnOrder().get(new_game.getTurnPointer())));
                updateRemoteScoreboard(game.getMostRecentTurn());
                game.setCurrentScores(new_game.getCurrentScores());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // Just activate or deactivate every button on the page
    private void activateScoreButtons(boolean enabled) {
        int[] ids = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                R.id.button10, R.id.button11, R.id.button12, R.id.button13, R.id.button14,
                R.id.button15, R.id.button16, R.id.button17, R.id.button18, R.id.button19, R.id.button20,
                R.id.buttonBull, R.id.buttonUndo, R.id.toggle_double, R.id.toggle_triple};

        for (int i = 0; i < ids.length; i++) {
            Button button = (Button) findViewById(ids[i]);
            button.setEnabled(enabled);
        }
    }

    // When a numeric button is hit, this interfaces to the updateLocalScoreboard to reflect the shot
    void handleShotValues(int shotValue) {

        // keep track of the shots as they are being entered
        turn.addShot(shotValue, shotModifier);
        // update the visuals on the devices as the player is shooting
        updateLocalScoreboard(turn);
        // post the turn to the database when the turn is finished
        if (turn.getShotsTaken() == turn.getShotsPerTurn()) {
            game.addTurn(turn);
            turn = new Turn(localPlayerName);
        }
        // reset the modifier back to a single no matter what happens
        shotModifier = Turn.Modifier.Single;
    }

    // Keeps the shotModifier updated to the value shown in the toggle buttons, logic handled here
    private void handleModifiers(View v) {
        switch (v.getId()) {
            // Double Button was hit
            case R.id.toggle_double:
                // is it now checked?
                if (doubles.isChecked()) {
                    shotModifier = Turn.Modifier.Double;
                    triples.setChecked(false);
                } else {
                    shotModifier = Turn.Modifier.Single;
                }
                break;
            case R.id.toggle_triple:
                if (triples.isChecked()) {
                    shotModifier = Turn.Modifier.Triple;
                    doubles.setChecked(false);
                } else {
                    shotModifier = Turn.Modifier.Single;
                }
                break;
            default:
                shotModifier = Turn.Modifier.Single;
                doubles.setChecked(false);
                triples.setChecked(false);
                // Shouldn't actually need this case, but whatever
                // update: turned out to actually be useful, go me
        }
    }

    /*
    Updates the scoreboard for a local player (/id:local_player_score) as they hit buttons.
     */
    private void updateLocalScoreboard(Turn turn) {

        String display_string;

        TextView[] textViews = {
                ((TextView) findViewById(R.id.view_first_local_shot)),
                ((TextView) findViewById(R.id.view_second_local_shot)),
                ((TextView) findViewById(R.id.view_third_local_shot))
        };

        for (int i = 0; i < turn.getShotsPerTurn(); i++) {
            textViews[i].setText("[m][s]");
        }

        for (int i = 0; i < turn.getShotsTaken(); i++) {
            display_string = String.valueOf(turn.getValues().get(i)) + "\n" +
                    String.valueOf(turn.getMods().get(i));
            textViews[i].setText(display_string);
        }
        doubles.setChecked(false);
        triples.setChecked(false);
    }

    /*
Updates the scoreboard for a remote player (/id:remote_player_score) when they submit their turn to the database.
 */
    private void updateRemoteScoreboard(Turn turn) {

        String display_string;

        TextView remotePlayerName = (TextView) findViewById(R.id.view_remote_player);
        remotePlayerName.setText(turn.getPlayerId());

        TextView[] textViews = {
                ((TextView) findViewById(R.id.view_first_remote_shot)),
                ((TextView) findViewById(R.id.view_second_remote_shot)),
                ((TextView) findViewById(R.id.view_third_remote_shot))
        };

        for (int i = 0; i < turn.getShotsPerTurn(); i++) {
            textViews[i].setText("[m][s]");
        }

        for (int i = 0; i < turn.getShotsTaken(); i++) {
            display_string = String.valueOf(turn.getValues().get(i)) + "\n" +
                    String.valueOf(turn.getMods().get(i));
            textViews[i].setText(display_string);
        }
        doubles.setChecked(false);
        triples.setChecked(false);
    }

    // specifically, update the visuals based on the X01 game controller a la mvc
    // used to update the scoreboard of a remote player, to update a local display (before posting the turn)
    // use updateLocalScoreboard
    private void notifyScoreboard(Turn turn) {

        if (turn.getPlayerId().equals(localPlayerName)) {
            //update_local_values(turn);
            return;
        }

        List<Integer> remoteShots = turn.getValues();
        List<Turn.Modifier> remoteMods = turn.getMods();

        TextView[] textViews = {
                ((TextView) findViewById(R.id.view_first_remote_shot)),
                ((TextView) findViewById(R.id.view_second_remote_shot)),
                ((TextView) findViewById(R.id.view_third_remote_shot))
        };

        String[] displayStrings = {
                String.valueOf(remoteShots.get(0)) + "\n" + String.valueOf(remoteMods.get(0)),
                String.valueOf(remoteShots.get(1)) + "\n" + String.valueOf(remoteMods.get(1)),
                String.valueOf(remoteShots.get(2)) + "\n" + String.valueOf(remoteMods.get(2))};

        for (int i = 0; i < remoteShots.size(); i++) {
            textViews[i].setText(displayStrings[i]);
        }

        String remotePlayerName = turn.getPlayerId();
        ((TextView) findViewById(R.id.view_remote_player)).setText(remotePlayerName);
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
            case R.id.buttonUndo:
                turn.removeShot();
                updateLocalScoreboard(turn);
                break;
            case R.id.toggle_double:
            case R.id.toggle_triple:
                handleModifiers(v);
                break;
        }
    }
}

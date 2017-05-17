package ransomts.foobardarts.X01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

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

        doubles = (ToggleButton) findViewById(R.id.toggle_double);
        triples = (ToggleButton) findViewById(R.id.toggle_triple);

        localPlayerView = (TextView) findViewById(R.id.view_local_player);
        remotePlayerView = (TextView) findViewById(R.id.view_remote_player);
    }

    void handleShotValues(int shotValue) {

        //handleModifiers(findViewById(R.id.button0));
        update_local_values(turn);

        turn.addShot(shotValue, shotModifier);

        if (turn.getShotsTaken() == turn.getShotsPerTurn()) {
            game.addTurn(turn);
            turn = new Turn(localPlayerName);
        }
    }

    private void handleModifiers(View v) {
        switch (v.getId()) {
            // TODO: Can these if statements be reduced logically?
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

    private void update_local_values(Turn turn) {

        String display_string;

        for (int i = 0; i < turn.getShotsTaken(); i++) {
            display_string = String.valueOf(turn.getValues().get(i)) + "\n" +
                    String.valueOf(turn.getMods().get(i));

            ((TextView) findViewById(R.id.view_first_shot)).setText(display_string);
        }
        doubles.setChecked(false);
        triples.setChecked(false);
    }

    // specifically, update the visuals based on the X01 game controller a la mvc
    private void notifyScoreboard(Turn turn) {

        if (turn.getPlayerId().equals(localPlayerName)) {
            //update_local_values(turn);
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
                turn.removeShot();
            case R.id.toggle_double:
            case R.id.toggle_triple:
                handleModifiers(v);
                break;
        }
    }
}

package ransomts.foobardarts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Arrays;

public class X01ScoreboardActivity extends AppCompatActivity
        implements View.OnClickListener {

    X01_game game;
    Turn.Modifier mods[];
    int shots[];
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

        game = new X01_game(players, score_goal, in, out);

        current_shot_index = 0;
        mods = new Turn.Modifier[game.getShotsPerTurn()];
        shots = new int[game.getShotsPerTurn()];
        Arrays.fill(mods, Turn.Modifier.Single);
        Arrays.fill(shots, 0);


        doubles = (ToggleButton) findViewById(R.id.toggle_double);
        triples = (ToggleButton) findViewById(R.id.toggle_triple);

        local_player = (TextView) findViewById(R.id.view_local_player);
        remote_player = (TextView) findViewById(R.id.view_remote_player);
    }

    void handle_undo_button() {

        // TODO: Be able to undo last shot of previous turn
        if (current_shot_index > 0) {
            current_shot_index--;
        }
        shots[current_shot_index] = 0;
        mods[current_shot_index] = Turn.Modifier.Single;

        update_visuals();
    }

    void handle_shot_values(int shot_value) {

        // TODO: Put in logic to not allow triple bulls
        shots[current_shot_index] = shot_value;
        current_shot_index = (current_shot_index + 1) % game.getShotsPerTurn();
        update_visuals();

        if (current_shot_index == 0) {
            game.add_turn(shots, mods);
            Arrays.fill(mods, Turn.Modifier.Single);
            Arrays.fill(shots, 0);
        }
    }

    private void handle_modifiers(View v) {
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
        }
        mods[current_shot_index] = shot_modifier;
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
                handle_shot_values(Integer.valueOf(b.getText().toString()));
                break;
            case R.id.buttonBull:
                handle_shot_values(25);
                break;
            // TODO: Undo button showing some runtime logic errors
            case R.id.buttonUndo:
                handle_undo_button();
            case R.id.toggle_double:
            case R.id.toggle_triple:
                handle_modifiers(v);
                break;
        }
    }

    void update_visuals() {
        // TODO: grab current player name from login
        String current_user = game.getFirstPlayerName();

        String display_string;

        // sometimes the best solution is a dumb one, kiss
        display_string = String.valueOf(shots[0]) + " " + String.valueOf(mods[0]);
        ((TextView) findViewById(R.id.view_first_shot)).setText(display_string);
        // eat your heart out Java types
        display_string = String.valueOf(shots[1]) + " " + String.valueOf(mods[1]);
        ((TextView) findViewById(R.id.view_second_shot)).setText(display_string);

        display_string = String.valueOf(shots[2]) + " " + String.valueOf(mods[2]);
        ((TextView) findViewById(R.id.view_third_shot)).setText(display_string);

        doubles.setChecked(false);
        triples.setChecked(false);

        local_player.setText(current_user + " " + game.getFirstPlayerScore());
        String remote_player_name = game.getCurrentPlayer();
        remote_player.setText(remote_player_name + " " + game.getCurrentScore(remote_player_name));
    }
}

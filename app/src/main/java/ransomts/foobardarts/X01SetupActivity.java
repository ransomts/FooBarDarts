package ransomts.foobardarts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class X01SetupActivity extends AppCompatActivity
        implements View.OnClickListener {

    Spinner score_goal_spinner;

    CheckBox double_in_toggle;
    CheckBox double_out_toggle;

    ExpandableListView players_view;
    List<String> player_list;
    boolean double_in;
    boolean double_out;
    int score_goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x01_setup);

        List<String> x01_score_goals = new ArrayList<String>();
        x01_score_goals.add("301");
        x01_score_goals.add("501");
        x01_score_goals.add("701");
        x01_score_goals.add("1001");

        player_list = new ArrayList<String>();
        player_list.add("Ziltoid"); // TODO: Update this to pull currently logged in user

        players_view = (ExpandableListView) findViewById(R.id.x01_players_list);
        //players_view.put("Players", player_list); // TODO: Add this stuff to the expandable list

        score_goal_spinner = (Spinner) findViewById(R.id.spinner_score_goal);
        double_in_toggle = (CheckBox) findViewById(R.id.checkbox_double_in);
        double_out_toggle = (CheckBox) findViewById(R.id.checkbox_double_out);
    }

    void pullParameters() {
        double_in = double_in_toggle.isChecked();
        double_out = double_out_toggle.isChecked();

        // ehhh this one felt bad
        score_goal = Integer.valueOf(score_goal_spinner.getSelectedItem().toString());

        // TODO: pull a list of players from the expanding list
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_local_game:
                intent = new Intent(this, X01ScoreboardActivity.class);
                pullParameters();
                intent.putExtra("double_in", double_in);
                intent.putExtra("double_out", double_out);
                intent.putExtra("score_goal", score_goal);
                intent.putExtra("players", player_list.toArray());
                startActivity(intent);
                break;
            case R.id.button_network_game:
                break;
        }
    }
}

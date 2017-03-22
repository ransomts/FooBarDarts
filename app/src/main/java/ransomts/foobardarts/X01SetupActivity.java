package ransomts.foobardarts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class X01SetupActivity extends AppCompatActivity
        implements View.OnClickListener {

    Spinner score_goal_spinner;

    CheckBox double_in_toggle;
    CheckBox double_out_toggle;

    private DatabaseReference game_ready_reference;
    private ValueEventListener ready_table_listener;

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

    public void begin_local_game() {
        Intent intent = new Intent(this, X01ScoreboardActivity.class);
        pullParameters();
        intent.putExtra("double_in", double_in);
        intent.putExtra("double_out", double_out);
        intent.putExtra("score_goal", score_goal);
        intent.putExtra("players", player_list.toArray());
        startActivity(intent);
        finish();
    }

    public void start_network_game() {

        final EditText textBox = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Input New Game Id")
                .setMessage("Some sort of sub message")
                .setView(textBox)
                .setPositiveButton("Start Game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String game_id = textBox.getText().toString();

                        game_ready_reference = FirebaseDatabase.getInstance().getReference("game/" + game_id);

                        game_ready_reference.removeEventListener(ready_table_listener);
                        // This probably is very inefficient, but we'll see
                        ready_table_listener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Add current user to waiting list
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String data = dataSnapshot.child("game").child(game_id).getValue(String.class);
                                data += user.getUid();
                                database.getReference("game").child(game_id).setValue(data);
                                // TODO: Update player list on page
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // TODO: log this or something guy
                            }
                        };
                        game_ready_reference.addValueEventListener(ready_table_listener);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    public void join_network_game () {

        final EditText textBox = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Input Game Id")
                .setMessage("")
                .setView(textBox)
                .setPositiveButton("Start Game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String game_id = textBox.getText().toString();

                        game_ready_reference = FirebaseDatabase.getInstance().getReference("game/" + game_id);
                        game_ready_reference.removeEventListener(ready_table_listener);
                        // This probably is very inefficient, but we'll see
                        ready_table_listener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // TODO: Update player list on page
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // TODO: log this or something guy
                            }
                        };
                        game_ready_reference.addValueEventListener(ready_table_listener);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.button_local_game:
                begin_local_game();
                break;
            case R.id.button_start_network_game:
                start_network_game();
                break;
            case R.id.button_join_network_game:
                join_network_game();
                break;
        }
    }
}

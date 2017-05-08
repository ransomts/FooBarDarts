package ransomts.foobardarts.X01;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ransomts.foobardarts.DartDb;

import ransomts.foobardarts.NetworkPlayerListAdapter;
import ransomts.foobardarts.R;
import ransomts.foobardarts.Tuple;

public class X01SetupActivity extends AppCompatActivity
        implements View.OnClickListener {

    Spinner score_goal_spinner;

    CheckBox double_in_toggle;
    CheckBox double_out_toggle;
    RecyclerView networkPlayerView;
    RecyclerView.LayoutManager networkPlayersLM;
    NetworkPlayerListAdapter networkPlayerListAdapter;

    ArrayList<Tuple> player_list;
    boolean double_in;
    boolean double_out;
    int score_goal;

    String game_id;
    DatabaseReference game_reference;

    private String TAG = "X01SetupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x01_setup);

        List<String> x01_score_goals = new ArrayList<String>();
        x01_score_goals.add("301");
        x01_score_goals.add("501");
        x01_score_goals.add("701");
        x01_score_goals.add("1001");

        game_id = null;

        // Set up the recycle list and it's adapter which we notify to update list
        player_list = new ArrayList<Tuple>();
        networkPlayerView = (RecyclerView) findViewById(R.id.network_players_view);
        networkPlayerListAdapter = new NetworkPlayerListAdapter(player_list);

        // use a linear layout manager
        networkPlayersLM = new LinearLayoutManager(this);
        networkPlayerView.setLayoutManager(networkPlayersLM);

        networkPlayerView.setAdapter(networkPlayerListAdapter);

        score_goal_spinner = (Spinner) findViewById(R.id.spinner_score_goal);
        double_in_toggle = (CheckBox) findViewById(R.id.checkbox_double_in);
        double_out_toggle = (CheckBox) findViewById(R.id.checkbox_double_out);

        game_reference = FirebaseDatabase.getInstance().getReference().child("game");
    }

    private void setup_network_database_listeners() {

        //ExpandableListView net_players_list = (ExpandableListView) findViewById(R.id.x01_players_list);
        DatabaseReference ready_players_entry = game_reference.child("ready_to_start");

        FirebaseListAdapter net_players_adapter = new FirebaseListAdapter<PlayersReady>(this,
                PlayersReady.class, R.layout.network_player_layout, ready_players_entry) {
            @Override
            protected void populateView(View v, PlayersReady playersReady, int position) {
                TextView player_id = (TextView) findViewById(R.id.net_player_id);
                TextView player_status = (TextView) findViewById(R.id.net_player_status);

                player_id.setText(playersReady.getPlayersReady().toString());
                player_status.setText("status");
            }
        };
        //net_players_list.setAdapter(net_players_adapter);
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
        intent.putExtra("game_id", "local_game");
        intent.putExtra("double_in", double_in);
        intent.putExtra("double_out", double_out);
        intent.putExtra("score_goal", score_goal);
        intent.putExtra("players", player_list.toArray());
        startActivity(intent);
        finish();
    }

    // Starts the network game sequence
    // First, ask for the game id to connect to the game entry in the database
    public void start_network_game() {

        ask_for_network_game_id();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        player_list.add(new Tuple(userName, false));
    }

    private void ask_for_network_game_id() {
        final EditText textBox = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Input Game Id")
                .setMessage("Current:" + game_id)
                .setView(textBox)
                .setPositiveButton("Start Game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        game_id = textBox.getText().toString();
                        setup_network_game_ready();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    // The game_id is a field and is set when the alert dialog collects data from the user
    public void setup_network_game_ready() {
        // Turn the toggle button back on
        ToggleButton toggle = (ToggleButton) findViewById(R.id.button_join_network_game);
        toggle.setEnabled(true);
        toggle.setChecked(false);

        // Point the reference to the specified game
        game_reference = game_reference.child(game_id);

        // Update the database entry for the current user with an initial "not ready"
        // Put a listener on the toggle button to update database when it's hit
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        final DatabaseReference current_user_ref = game_reference.child("ready_to_start").child(currentUser);
        current_user_ref.setValue(false);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                current_user_ref.setValue(isChecked);
            }
        });

        // Update the list of network players to reflect the ready_to_start node in the database
        game_reference.child("ready_to_start").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // todo: Update network players list to reflect dataSnapshot
                boolean dataSetChanged = false;

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Tuple user = new Tuple(child.getKey(), child.getValue());

                    int index = userExistsInPlayerList(user);
                    if (index == -1) {
                        player_list.add(user);
                        dataSetChanged = true;
                    } else {
                        player_list.remove(index);
                        player_list.add(user);
                        dataSetChanged = true;
                    }
                }
                if (dataSetChanged) {
                    networkPlayerListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, "Updating network player list failed");
            }
        });

        //setup_network_database_listeners();
    }

    private int userExistsInPlayerList(Tuple user) {
        for (int i = 0; i < player_list.size(); i++) {
            if (player_list.get(i).getA().equals(user.getA())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_local_game:
                begin_local_game();
                break;
            case R.id.button_start_network_game:
                start_network_game();
                break;
        }
    }
}

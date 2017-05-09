package ransomts.foobardarts.X01;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ransomts.foobardarts.NetworkPlayerListAdapter;
import ransomts.foobardarts.R;
import ransomts.foobardarts.StartupScreenActivity;
import ransomts.foobardarts.Tuple;

public class X01SetupActivity extends AppCompatActivity
        implements View.OnClickListener {

    Spinner score_goal_spinner;

    CheckBox double_in_toggle;
    CheckBox double_out_toggle;
    RecyclerView networkPlayerView;
    RecyclerView.LayoutManager networkPlayersLM;
    NetworkPlayerListAdapter networkPlayerListAdapter;

    ArrayList<Tuple<String, Boolean>> player_list;
    boolean double_in;
    boolean double_out;
    int score_goal;
    String currentUser = "Ziltoid";

    String game_id;
    DatabaseReference game_reference;

    private String TAG = "X01SetupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x01_setup);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        game_id = null;

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            currentUser = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }

        // Set up the recycle list and it's adapter which we notify to update list
        player_list = new ArrayList<>();
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

    private void ask_for_network_game_id() {
        final EditText textBox = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Input Game Id")
                .setMessage("Current:" + game_id)
                .setView(textBox)
                .setPositiveButton("Start Game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        game_id = textBox.getText().toString();
                        player_list.add(new Tuple<>(currentUser, false));
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
                boolean allReady = true;

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Tuple<String, Boolean> user = new Tuple<>(child.getKey(), (Boolean)child.getValue());

                    int index = userExistsInPlayerList(user);
                    if (index == -1) {
                        player_list.add(user);
                        dataSetChanged = true;
                    } else {
                        player_list.remove(index);
                        player_list.add(user);
                        dataSetChanged = true;
                    }

                    if (!user.getB()) {
                        allReady = false;
                    }
                }
                if (dataSetChanged) {
                    networkPlayerListAdapter.notifyDataSetChanged();
                    // Maybe put in some sort of countdown animation here?
                    if (allReady) {
                        start_network_game();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, "Updating network player list failed");
            }
        });

        //setup_network_database_listeners();
    }

    private void start_network_game() {
        // Handle database management
        //
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
                ask_for_network_game_id();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = new Intent(this, StartupScreenActivity.class);
                startActivity(intent);
                // TODO: this is the better way, but it minimizes the application?
                //NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

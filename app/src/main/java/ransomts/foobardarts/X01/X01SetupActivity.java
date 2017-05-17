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
import java.util.Calendar;
import java.util.HashMap;

import ransomts.foobardarts.NetworkPlayerListAdapter;
import ransomts.foobardarts.R;
import ransomts.foobardarts.StartupScreenActivity;
import ransomts.foobardarts.Tuple;

public class X01SetupActivity extends AppCompatActivity
        implements View.OnClickListener {

    Spinner scoreGoalSpinner;

    CheckBox doubleInToggle;
    CheckBox doubleOutToggle;
    RecyclerView networkPlayerView;
    RecyclerView.LayoutManager networkPlayersLM;
    NetworkPlayerListAdapter networkPlayerListAdapter;

    ArrayList<Tuple<String, Boolean>> playersReadyList;
    PlayersReady playerList;
    boolean doubleIn;
    boolean doubleOut;
    int scoreGoal;
    String currentUser = "Ziltoid";

    String game_id;

    X01_game game;

    private String TAG = "X01SetupActivity";
    private DatabaseReference gameReference;

    public X01SetupActivity() {
    }

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
        playerList = new PlayersReady();
        playersReadyList = new ArrayList<>();
        networkPlayerView = (RecyclerView) findViewById(R.id.network_players_view);
        networkPlayerListAdapter = new NetworkPlayerListAdapter(playersReadyList);

        // use a linear layout manager
        networkPlayersLM = new LinearLayoutManager(this);
        networkPlayerView.setLayoutManager(networkPlayersLM);
        networkPlayerView.setAdapter(networkPlayerListAdapter);

        scoreGoalSpinner = (Spinner) findViewById(R.id.spinner_score_goal);
        doubleInToggle = (CheckBox) findViewById(R.id.checkbox_double_in);
        doubleOutToggle = (CheckBox) findViewById(R.id.checkbox_double_out);
    }

    public void pullGameParametersFromActivity() {
        doubleIn = doubleInToggle.isChecked();
        doubleOut = doubleOutToggle.isChecked();

        // ehhh this one felt bad
        scoreGoal = Integer.valueOf(scoreGoalSpinner.getSelectedItem().toString());

    }

    public void startGame() {
        Intent intent = new Intent(this, X01ScoreboardActivity.class);

        game.setStartTime(Calendar.getInstance().getTime());
        game.moveGameForward();

        intent.putExtra("game", game);
        intent.putExtra("localPlayerName", currentUser);
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
                        playerList.addPlayer(currentUser);
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

        pullGameParametersFromActivity();

        gameReference = FirebaseDatabase.getInstance().getReference().child("games").child("notStarted").child(game_id);
        gameReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "Data changed in game listener");
                X01_game databaseGame = dataSnapshot.getValue(X01_game.class);
                // Join an existing game or start a new one
                if (databaseGame != null) {
                    Log.i(TAG, "There is already a game open, connecting");
                    game = databaseGame;
                } else {
                    Log.i(TAG, "Creating a new game");
                    game = new X01_game(
                            Integer.valueOf(scoreGoalSpinner.getSelectedItem().toString()),
                            doubleInToggle.isChecked(),
                            doubleOutToggle.isChecked(),
                            game_id,
                            playerList);
                    Log.i(TAG, "Adding the game to the database");
                    gameReference.setValue(game);
                }
                setupPlayersReadyListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setupPlayersReadyListener() {
        // Update the list of network players to reflect the ready_to_start node in the database
        gameReference.child("playersReady").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "Data changed in players ready listener");
                PlayersReady playersReady = dataSnapshot.getValue(PlayersReady.class);

                if (playersReady != null) {
                    Log.i(TAG, "There is already playersReady data in the database");

                    if (!playersReady.getPlayersReady().containsKey(currentUser)) {
                        Log.i(TAG, "Adding the local user to the playersReady object and pushing to database");
                        playersReady.addPlayer(currentUser);
                        gameReference.child("playersReady").setValue(playersReady);
                        return;
                    }

                    game.setPlayersReady(playersReady);

                    if (game.getPlayersReady().allPlayersReady()) {
                        Log.i(TAG, "Starting Scoreboard Activity");
                        //setPlayersReady(playersReady);
                        networkPlayerListAdapter.notifyDataSetChanged();
                        startGame();
                    } else {
                        setPlayersReady(playersReady);
                        networkPlayerListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, "Updating network player list failed");
            }
        });
        //*/
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button_join_network_game:
                ToggleButton tb = (ToggleButton) findViewById(R.id.button_join_network_game);
                game.getPlayersReady().updatePlayerStatus(currentUser, tb.isChecked());
                gameReference.child("playersReady").setValue(game.getPlayersReady());
                break;
            case R.id.button_local_game:
                startGame();
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
                // this is the better way, but it minimizes the application now?
                //NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setPlayersReady(PlayersReady playersReady) {

        HashMap<String, Boolean> networkPlayersMap = playersReady.getPlayersReady();
        playersReadyList.clear();
        for (String player : networkPlayersMap.keySet()) {
            Tuple playerStatus = new Tuple(player, networkPlayersMap.get(player));
            playersReadyList.add(playerStatus);
        }

    }
}

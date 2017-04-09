package ransomts.foobardarts;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tim on 3/17/17.
 *
 * File to actually access the firebase
 */

public class DartDb {

    private FirebaseAuth mAuth;

    DatabaseReference database;
    FirebaseUser user;

    private final String TAG = "DartDb";

    public DartDb() {
        database = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    void getDisplayNameEntry() {

        Query name_query = database.child("users").child(user.getUid());
        name_query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        Log.w("DARTDB", value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }

        );
    }

    public void updateUserReadyForGame(String username, String game_id, boolean ready) {
        Map<String, Object> info = new HashMap<>();
        info.put(username, ready);
        database.child("game").child(game_id).child("ready_to_start").updateChildren(info);
    }

    void updateDisplayNameEntry(String new_display) {
        if (user == null) {
            return;
        }
        if (new_display == null) {
            new_display = user.getDisplayName();
        }
        String user_id = user.getUid();
        Map<String, Object> info = new HashMap<>();
        info.put(user_id, new_display);
        database.child("users").updateChildren(info);
    }

    void sendRegistrationToServer(String refreshedToken) {
        // TODO: implement
    }

    void writeToDatabase() {

        // Read from the database
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}

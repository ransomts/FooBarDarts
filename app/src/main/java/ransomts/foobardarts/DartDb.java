package ransomts.foobardarts;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by tim on 3/17/17.
 *
 * File to actually access the firebsae database
 */

class DartDb {

    private FirebaseAuth mAuth;

    FirebaseDatabase database;
    DatabaseReference myRef;

    private final String TAG = "DartDb";

    DartDb() {
    }

    void sendRegistrationToServer(String refreshedToken) {
        // TODO: implement
    }

    void writeToDatabase() {

        myRef.setValue("Hello, World!");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
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

package ransomts.foobardarts;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by tim on 3/17/17.
 *
 * File to actually access the firebsae database, using DartDbHelper.java
 */

class DartDb {

    DartDb() {}

    public void writeToDatabase() {
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
    }
}

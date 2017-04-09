package ransomts.foobardarts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UtilityScreenActivity extends AppCompatActivity
    implements View.OnClickListener {

    DartDb database;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility_screen);

        database = new DartDb();

        TextView status = (TextView) findViewById(R.id.utility_textView);
        status.setText("Something");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String name = auth.getCurrentUser().getDisplayName();
            status.setText(name);
        }

        ValueEventListener nameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView status = (TextView) findViewById(R.id.utility_textView);
                status.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addValueEventListener(nameListener);
    }

    public void test_input() {
        final EditText txtUrl = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Input Text to Toast")
                .setMessage("Some sort of sub message")
                .setView(txtUrl)
                .setPositiveButton("Start Game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String url = txtUrl.getText().toString();

                        Context context = getApplicationContext();
                        CharSequence text = url;
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
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
        switch (v.getId()) {
            case R.id.test_database_button:
                // note: make sure you're logged in first
                database.updateDisplayNameEntry("Tim Ransom");
                database.getDisplayNameEntry();
                break;
            case R.id.test_fcm_button:
                //test_messaging();
                break;
            case R.id.test_input_button:
                test_input();
                break;
        }
    }
}

package ransomts.foobardarts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UtilityScreenActivity extends AppCompatActivity
    implements View.OnClickListener {

    DartDb database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility_screen);

        database = new DartDb();
    }

    public void test_messaging() {

    }

    public void test_input() {
        final EditText txtUrl = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Moustachify Link")
                .setMessage("Paste in the link of an image to moustachify!")
                .setView(txtUrl)
                .setPositiveButton("Moustachify", new DialogInterface.OnClickListener() {
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
                database.updateDisplayNameEntry("Timothy Ransom");
                database.getDisplayNameEntry();
                break;
            case R.id.test_fcm_button:
                test_messaging();
                break;
            case R.id.test_input_button:
                test_input();
                break;
        }
    }
}

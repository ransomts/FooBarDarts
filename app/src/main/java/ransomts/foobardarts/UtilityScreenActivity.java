package ransomts.foobardarts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class UtilityScreenActivity extends AppCompatActivity
    implements View.OnClickListener {

    DartDb database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility_screen);

        database = new DartDb();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_database_button:
                database.writeToDatabase();
                break;
        }
    }
}

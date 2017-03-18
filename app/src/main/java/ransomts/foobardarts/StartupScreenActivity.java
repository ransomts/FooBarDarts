package ransomts.foobardarts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartupScreenActivity extends AppCompatActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup_screen);
    }


    @Override
    public void onClick(View v) {

        Intent intent = null;

        switch (v.getId()) {
            case R.id.play_x01_button:
                intent = new Intent(this, X01SetupActivity.class);
                break;
            case R.id.play_cricket_button:
                // TODO: implement
                //intent = new Intent(this, CricketSetupActivity.class);
                break;
            case R.id.play_other_button:
                // TODO: implement
                //intent = new Intent(this, SelectGameActivity.class);
                break;
            case R.id.account_settings_button:
                intent = new Intent(this, AccountScreenActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }


    }
}

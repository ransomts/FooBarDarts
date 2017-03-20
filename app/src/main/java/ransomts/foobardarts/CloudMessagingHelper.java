package ransomts.foobardarts;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by tim on 3/19/17.
 */

public class CloudMessagingHelper extends FirebaseInstanceIdService {

    private final String TAG = "CloudMessagingHelper";
    private DartDb database;

    public CloudMessagingHelper () {

    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        database.sendRegistrationToServer(refreshedToken);
    }
}

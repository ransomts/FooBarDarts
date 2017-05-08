package ransomts.foobardarts.X01;

import android.app.Activity;
import android.view.View;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;

/**
 * Created by tim on 4/17/17.
 */

public class NetworkPlayerAdapter extends FirebaseListAdapter {
    /**
     * @param activity    The activity containing the ListView
     * @param modelClass  Firebase will marshall the data at a location into
     *                    an instance of a class that you provide
     * @param modelLayout This is the layout used to represent a single list item.
     *                    You will be responsible for populating an instance of the corresponding
     *                    view with the data from an instance of modelClass.
     * @param ref         The Firebase location to watch for data changes. Can also be a slice of a location,
     *                    using some combination of {@code limit()}, {@code startAt()}, and {@code endAt()}.
     */
    public NetworkPlayerAdapter(Activity activity, Class modelClass, int modelLayout, Query ref) {
        super(activity, modelClass, modelLayout, ref);
    }

    @Override
    protected void populateView(View v, Object model, int position) {

    }
}

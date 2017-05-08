package ransomts.foobardarts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter class to implement the network player view
 * 8.5.2017
 */

public class NetworkPlayerListAdapter  extends RecyclerView.Adapter<NetworkPlayerListAdapter.ViewHolder>{

    // ArrayList
    // Key: Position in list
    //
    // Value: HashMap
    // Key: Player name
    // Value: Player status (true if ready)
    private ArrayList<Tuple<String, Boolean>> networkPlayers;

    private NetworkPlayerListAdapter() {
        networkPlayers = new ArrayList<>();
    }

    public NetworkPlayerListAdapter(ArrayList<Tuple<String, Boolean>> networkPlayers) {
        this();
        this.networkPlayers = networkPlayers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        LinearLayout b = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.network_player_layout, parent, false);

        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Tuple networkPlayer = networkPlayers.get(position);
        if (networkPlayer != null) {
            holder.playerName.setText((String) networkPlayer.getA());
            String status = "not ready";
            if ((boolean) networkPlayer.getB()) {
                status = "ready";
            }
            holder.playerStatus.setText(status);
        }
    }

    @Override
    public int getItemCount() {
        return networkPlayers.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder {

        LinearLayout mLinearLayout;
        TextView playerName;
        TextView playerStatus;
        //private String TAG = "NETWORK.PLAYER.VIEWHOLDER";

        ViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            mLinearLayout = linearLayout;
            playerName = (TextView) mLinearLayout.getChildAt(0);
            playerStatus = (TextView) mLinearLayout.getChildAt(1);
        }
    }
}

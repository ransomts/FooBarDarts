package ransomts.foobardarts.X01;

/*
  Created by tim on 3/11/17.

  Java object to model one dart turn in any game
 */

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

class Turn implements Parcelable {

    enum Modifier {Single, Double, Triple}

    private int pointTotal;
    private int shotsPerTurn;
    private String playerId;
    private List<Integer> values;
    private List<Modifier> mods;

    Turn() {
        this("Ziltoid", 3);
    }

    Turn(String playerId) {
        this(playerId, 3);
    }

    Turn(String playerId, int shotsPerTurn) {

        this.playerId = playerId;
        this.shotsPerTurn = shotsPerTurn;
        values = new ArrayList<>(shotsPerTurn);
        mods = new ArrayList<>(shotsPerTurn);
    }

    // these say they're unused but the database uses them to parse into the json format
    public int getPointTotal() {
        return pointTotal;
    }

    public int getShotsTaken() { return getValues().size(); }

    public void setPointTotal(int pointTotal) {
        this.pointTotal = pointTotal;
    }

    public List<Modifier> getMods() {
        return mods;
    }

    public List<Integer> getValues() {
        return values;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getShotsPerTurn() {
        return shotsPerTurn;
    }

    void addShot(int value, Modifier mod) {

        // should never happen, but no triple bulls and no more than three shots per turn
        if (value == 25 && mod == Modifier.Triple || values.size() > 3) {
            values.add(0);
            mods.add(Modifier.Single);
            return;
        }
        values.add(value);
        mods.add(mod);

        switch(mod) {
            case Double: value *= 2; break;
            case Triple: value *= 3; break;
        }
        pointTotal += value;
    }

    void removeShot() {
        if (values.size() == 0) { return; }
        values.remove(values.size() - 1);
        mods.remove(mods.size() - 1);
    }

    public boolean lastShotIsDouble() {
        return mods.get(mods.size()) == Modifier.Double;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pointTotal);
        dest.writeInt(this.shotsPerTurn);
        dest.writeString(this.playerId);
        dest.writeList(this.values);
        dest.writeList(this.mods);
    }

    protected Turn(Parcel in) {
        this.pointTotal = in.readInt();
        this.shotsPerTurn = in.readInt();
        this.playerId = in.readString();
        this.values = new ArrayList<Integer>();
        in.readList(this.values, Integer.class.getClassLoader());
        this.mods = new ArrayList<Modifier>();
        in.readList(this.mods, Modifier.class.getClassLoader());
    }

    public static final Parcelable.Creator<Turn> CREATOR = new Parcelable.Creator<Turn>() {
        @Override
        public Turn createFromParcel(Parcel source) {
            return new Turn(source);
        }

        @Override
        public Turn[] newArray(int size) {
            return new Turn[size];
        }
    };
}

package ransomts.foobardarts.X01;

import java.util.ArrayList;
import java.util.Map;

/**
 * Model of a User entry in the firebase database
 */

public class User {
    private String userName;
    private Map<String, Object> userStats;
    private ArrayList<String> gamesUserPlayed;

    public User() {}
    public User(String userName, Map<String, Object> userStats, ArrayList<String> gamesUserPlayed) {
        setUserName(userName);
        setGamesUserPlayed(gamesUserPlayed);
        setUserStats(userStats);
    }

    public String getUserName() { return userName; }
    public Map<String, Object> getUserStats() { return userStats; }
    public ArrayList<String> getGamesUserPlayed() { return gamesUserPlayed; }

    public void setUserName(String userName) { this.userName = userName; }
    public void setUserStats(Map<String, Object> userStats) { this.userStats = userStats; }
    public void setGamesUserPlayed(ArrayList<String> gamesUserPlayed) { this.gamesUserPlayed = gamesUserPlayed; }

}

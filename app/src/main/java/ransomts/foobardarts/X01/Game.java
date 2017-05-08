package ransomts.foobardarts.X01;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Model of a Game entry in the firbase database
 */

class Game {
    private String gameId;
    private Date startTime;
    private String winner;
    private ArrayList<String> players;
    private PlayersReady playersReady;
    private ArrayList<Turn> turns;
    private Turn mostRecentTurn;

    public Game() {}

    public Game(String gameId, Date startTime, String winner, ArrayList<String> players,
                PlayersReady playersReady, ArrayList<Turn> turns, Turn mostRecentTurn) {
        setGameId(gameId);
        setStartTime(startTime);
        setWinner(winner);
        setPlayers(players);
        setPlayersReady(playersReady);
        setTurns(turns);
        setMostRecentTurn(mostRecentTurn);
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }

    public void setPlayers(ArrayList<String> players) {
        this.players = players;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public void setPlayersReady(PlayersReady playersReady) {
        this.playersReady = playersReady;
    }

    public PlayersReady getPlayersReady() {
        return playersReady;
    }

    public void setTurns(ArrayList<Turn> turns) {
        this.turns = turns;
    }

    public ArrayList<Turn> getTurns() {
        return turns;
    }

    public void setMostRecentTurn(Turn mostRecentTurn) {
        this.mostRecentTurn = mostRecentTurn;
    }

    public Turn getMostRecentTurn() {
        return mostRecentTurn;
    }
}

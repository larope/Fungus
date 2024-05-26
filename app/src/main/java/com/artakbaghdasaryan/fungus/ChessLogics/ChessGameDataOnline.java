package com.artakbaghdasaryan.fungus.ChessLogics;

public class ChessGameDataOnline {
    public long durationInMilliseconds;
    public long incrementInMilliseconds;
    public String mode;

    public String hostId;
    public String hostname;

    public String playerWhiteId;
    public String playerBlackId;

    public String playerWhiteUsername;
    public String playerBlackUsername;

    public boolean isHostWhite;


    public String gameId;

    public ChessGameDataOnline(String gameId, long durationInMilliseconds, long incrementInMilliseconds, String mode, String hostId, String hostname){
        this.durationInMilliseconds = durationInMilliseconds;
        this.incrementInMilliseconds = incrementInMilliseconds;
        this.mode = mode;
        this.hostId = hostId;
        this.hostname = hostname;
        this.gameId = gameId;
    }

    public ChessGameDataOnline(){}

    public long getDurationInMilliseconds() {
        return durationInMilliseconds;
    }
    public void setDurationInMilliseconds(long durationInMilliseconds) {
        this.durationInMilliseconds = durationInMilliseconds;
    }
    public String getMode() {
        return mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }
    public long getIncrementInMilliseconds() {
        return incrementInMilliseconds;
    }
    public void setIncrementInMilliseconds(long incrementInMilliseconds) {
        this.incrementInMilliseconds = incrementInMilliseconds;
    }
    public String getHostId() {
        return hostId;
    }
    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getPlayerBlackId() {
        return playerBlackId;
    }

    public void setPlayerBlackId(String playerBlackId) {
        this.playerBlackId = playerBlackId;
    }

    public String getPlayerWhiteId() {
        return playerWhiteId;
    }

    public void setPlayerWhiteId(String playerWhiteId) {
        this.playerWhiteId = playerWhiteId;
    }

    public String getPlayerWhiteUsername() {
        return playerWhiteUsername;
    }

    public void setPlayerWhiteUsername(String playerWhiteUsername) {
        this.playerWhiteUsername = playerWhiteUsername;
    }

    public String getPlayerBlackUsername() {
        return playerBlackUsername;
    }

    public void setPlayerBlackUsername(String playerBlackUsername) {
        this.playerBlackUsername = playerBlackUsername;
    }

    public static ChessGameDataOnline Empty = new ChessGameDataOnline("",0,0,"","", "");
}
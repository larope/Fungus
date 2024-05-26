package com.artakbaghdasaryan.fungus.ChessLogics;

public class CellOnlineData {
    public int x;
    public int y;
    public PieceType type;

    public CellOnlineData(int x, int y, PieceType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    public CellOnlineData(){

    }

    public static CellOnlineData Empty = new CellOnlineData();
}

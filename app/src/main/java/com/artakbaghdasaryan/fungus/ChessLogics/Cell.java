package com.artakbaghdasaryan.fungus.ChessLogics;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

public class Cell {
    public Vector2Int position;
    public CellColor color;
    public Piece piece;

    public Cell(int x, int y, CellColor cellColor, Piece piece){
        this.position = new Vector2Int(x,y);
        this.position.y = y;
        this.color = cellColor;
        this.piece = piece;
    }
    public Cell(Vector2Int position, CellColor cellColor, Piece piece){
        this.position = new Vector2Int(position.x,position.y);
        this.color = cellColor;
        this.piece = piece;
    }

    public Cell Copy(){
        return new Cell(position, color, piece);
    }
}

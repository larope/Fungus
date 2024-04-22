package com.artakbaghdasaryan.fungus.ChessLogics;

import androidx.lifecycle.viewmodel.CreationExtras;

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

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Cell other = (Cell) obj;

        if(other.position.equals(position) && other.piece.type == piece.type){
            return true;
        }
        else{
            return false;
        }
    }


    public static Cell Empty = new Cell(-69, -69, CellColor.black, Piece.Empty);
}

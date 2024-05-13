package com.artakbaghdasaryan.fungus.ChessLogics;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;

public class KingPattern extends MovingPattern{
    private MovingPattern _bishopPattern;
    private MovingPattern _rookPattern;
    private MovingPattern _knightPattern;


    public KingPattern(){
        super();
        _bishopPattern = new BishopPattern();
        _rookPattern = new RookPattern();
        _knightPattern = new KingPattern();
    }

    public ArrayList<Cell> GetAvailableMoves(Board board, Vector2Int fromPosition) {
        ArrayList<Cell> available = new ArrayList<>();
        Vector2Int boardSize = board.GetSize();
        Piece piece = board.GetCell(fromPosition).piece;

        return available;
    }

}

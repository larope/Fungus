package com.artakbaghdasaryan.fungus.ChessLogics;

import android.util.Log;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;

public class KingPattern extends MovingPattern {
    public KingPattern() {
        super();
        _pattern = new ArrayList<>();

        _pattern.add(new Vector2Int(-1, -1));
        _pattern.add(new Vector2Int(-1, 0));
        _pattern.add(new Vector2Int(-1, 1));
        _pattern.add(new Vector2Int(1, 1));
        _pattern.add(new Vector2Int(1, 0));
        _pattern.add(new Vector2Int(1, -1));
        _pattern.add(new Vector2Int(0, -1));
        _pattern.add(new Vector2Int(0, 1));

        _maximumMoves = _pattern.size();
    }

    public ArrayList<Cell> GetAvailableSafeMoves(Board board, Vector2Int fromPosition) {
        PieceColor color = board.GetCell(fromPosition).piece.color;
        ArrayList<Cell> availableMoves = super.GetAvailableSafeMoves(board, fromPosition);

        if(board.IsCastlingAvailableKingSide(color)){
            availableMoves.add(board.GetCell(6, board.GetLine().get(color)));
        }
        if(board.IsCastlingAvailableQueenSide(color)){
            availableMoves.add(board.GetCell(2, board.GetLine().get(color)));
        }

        return availableMoves;
    }
}
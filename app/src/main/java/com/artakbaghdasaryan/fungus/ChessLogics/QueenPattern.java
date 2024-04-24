package com.artakbaghdasaryan.fungus.ChessLogics;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;

public class QueenPattern extends MovingPattern{
    private MovingPattern _bishopPattern;
    private MovingPattern _rookPattern;

    public QueenPattern(){
        super();

        _bishopPattern = new BishopPattern();
        _rookPattern = new RookPattern();
    }

    @Override
    public ArrayList<Cell> GetAvailableMoves(Board board, Vector2Int fromPosition) {
        ArrayList<Cell> available = new ArrayList<>();

        available.addAll(_bishopPattern.GetAvailableMoves(board, fromPosition));
        available.addAll(_rookPattern.GetAvailableMoves(board, fromPosition));

        return available;
    }
}

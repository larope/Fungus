package com.artakbaghdasaryan.fungus.ChessLogics;

import android.util.Log;

import com.artakbaghdasaryan.fungus.Util.OutOfBoardException;
import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

public class MovingPattern {
    public ArrayList<Vector2Int> _pattern;
    private int _maximumMoves;

    public MovingPattern(){
        _maximumMoves = 0;
    }
    public MovingPattern(ArrayList<Vector2Int> pattern){
        _pattern = pattern;
        _maximumMoves = pattern.size();
    }
    public MovingPattern(Vector2Int[] pattern){
        _pattern = new ArrayList<Vector2Int>();
        for (Vector2Int position : pattern){
            _pattern.add(position);
        }
        _maximumMoves = _pattern.size();
    }

    public ArrayList<Cell> GetAvailableMoves(Board board, Vector2Int fromPosition) {
        ArrayList<Cell> available = new ArrayList<>();
        Vector2Int boardSize = board.GetSize();


        for(int i = 0; i < _maximumMoves; i++){
            Vector2Int nextPosition = Vector2Int.Plus(fromPosition, _pattern.get(i));
            Log.wtf("Move", nextPosition.x + " " + nextPosition.y);
            if(!board.IsOutOfBoard(nextPosition)){
                Cell cellAtPosition = board.GetCell(nextPosition);
                available.add(cellAtPosition);
            }
        }

        return available;
    }

    public static MovingPattern rookPattern = new MovingPattern(new Vector2Int[]{
                new Vector2Int(1,0),
                new Vector2Int(2,0),
                new Vector2Int(3,0),
                new Vector2Int(4,0),
                new Vector2Int(5,0),
                new Vector2Int(6,0),
                new Vector2Int(7,0),
                new Vector2Int(8,0),

                new Vector2Int(-1,0),
                new Vector2Int(-2,0),
                new Vector2Int(-3,0),
                new Vector2Int(-4,0),
                new Vector2Int(-5,0),
                new Vector2Int(-6,0),
                new Vector2Int(-7,0),
                new Vector2Int(-8,0),

                new Vector2Int(0,1),
                new Vector2Int(0,2),
                new Vector2Int(0,3),
                new Vector2Int(0,4),
                new Vector2Int(0,5),
                new Vector2Int(0,6),
                new Vector2Int(0,7),
                new Vector2Int(0,8),

                new Vector2Int(0,-1),
                new Vector2Int(0,-2),
                new Vector2Int(0,-3),
                new Vector2Int(0,-4),
                new Vector2Int(0,-5),
                new Vector2Int(0,-6),
                new Vector2Int(0,-7),
                new Vector2Int(0,-8)
    });

    public static MovingPattern bishopPattern = new MovingPattern(new Vector2Int[]{
            new Vector2Int(1,1),
            new Vector2Int(2,2),
            new Vector2Int(3,3),
            new Vector2Int(4,4),
            new Vector2Int(5,5),
            new Vector2Int(6,6),
            new Vector2Int(7,7),
            new Vector2Int(8,8),

            new Vector2Int(-1,-1),
            new Vector2Int(-2,-1),
            new Vector2Int(-3,-1),
            new Vector2Int(-4,-1),
            new Vector2Int(-5,-1),
            new Vector2Int(-6,-1),
            new Vector2Int(-7,-1),
            new Vector2Int(-8,-1),

            new Vector2Int(-1,1),
            new Vector2Int(-1,2),
            new Vector2Int(-1,3),
            new Vector2Int(-1,4),
            new Vector2Int(-1,5),
            new Vector2Int(-1,6),
            new Vector2Int(-1,7),
            new Vector2Int(-1,8),

            new Vector2Int(1,-1),
            new Vector2Int(2,-2),
            new Vector2Int(3,-3),
            new Vector2Int(4,-4),
            new Vector2Int(5,-5),
            new Vector2Int(6,-6),
            new Vector2Int(7,-7),
            new Vector2Int(8,-8)
    });

    public static MovingPattern knightPattern = new MovingPattern(new Vector2Int[]{
            new Vector2Int(1,2),
            new Vector2Int(1,-2),
            new Vector2Int(-1,2),
            new Vector2Int(-1,-2),
            new Vector2Int(2,1),
            new Vector2Int(2,-1),
            new Vector2Int(-2,1),
            new Vector2Int(-2,-1),
    });

    public static MovingPattern queenPattern = new MovingPattern(new Vector2Int[]{
            new Vector2Int(1,1),
            new Vector2Int(2,2),
            new Vector2Int(3,3),
            new Vector2Int(4,4),
            new Vector2Int(5,5),
            new Vector2Int(6,6),
            new Vector2Int(7,7),
            new Vector2Int(8,8),

            new Vector2Int(-1,-1),
            new Vector2Int(-2,-1),
            new Vector2Int(-3,-1),
            new Vector2Int(-4,-1),
            new Vector2Int(-5,-1),
            new Vector2Int(-6,-1),
            new Vector2Int(-7,-1),
            new Vector2Int(-8,-1),

            new Vector2Int(-1,1),
            new Vector2Int(-1,2),
            new Vector2Int(-1,3),
            new Vector2Int(-1,4),
            new Vector2Int(-1,5),
            new Vector2Int(-1,6),
            new Vector2Int(-1,7),
            new Vector2Int(-1,8),

            new Vector2Int(1,-1),
            new Vector2Int(2,-2),
            new Vector2Int(3,-3),
            new Vector2Int(4,-4),
            new Vector2Int(5,-5),
            new Vector2Int(6,-6),
            new Vector2Int(7,-7),
            new Vector2Int(8,-8),

            new Vector2Int(1,0),
            new Vector2Int(2,0),
            new Vector2Int(3,0),
            new Vector2Int(4,0),
            new Vector2Int(5,0),
            new Vector2Int(6,0),
            new Vector2Int(7,0),
            new Vector2Int(8,0),

            new Vector2Int(-1,0),
            new Vector2Int(-2,0),
            new Vector2Int(-3,0),
            new Vector2Int(-4,0),
            new Vector2Int(-5,0),
            new Vector2Int(-6,0),
            new Vector2Int(-7,0),
            new Vector2Int(-8,0),

            new Vector2Int(0,1),
            new Vector2Int(0,2),
            new Vector2Int(0,3),
            new Vector2Int(0,4),
            new Vector2Int(0,5),
            new Vector2Int(0,6),
            new Vector2Int(0,7),
            new Vector2Int(0,8),

            new Vector2Int(0,-1),
            new Vector2Int(0,-2),
            new Vector2Int(0,-3),
            new Vector2Int(0,-4),
            new Vector2Int(0,-5),
            new Vector2Int(0,-6),
            new Vector2Int(0,-7),
            new Vector2Int(0,-8)
    });

    public static MovingPattern kingPattern = new MovingPattern(new Vector2Int[]{
            new Vector2Int(-1,-1),
            new Vector2Int(-1,0),
            new Vector2Int(-1,1),
            new Vector2Int(1,1),
            new Vector2Int(1,0),
            new Vector2Int(1,-1),
            new Vector2Int(0,-1),
            new Vector2Int(0,1)
    });

    public static MovingPattern pawnPattern = new MovingPattern(new Vector2Int[]{
            new Vector2Int(-1,-1),
            new Vector2Int(-1,1),
            new Vector2Int(1,1)
    });
}

package com.artakbaghdasaryan.fungus.ChessLogics;

import android.util.Log;

import com.artakbaghdasaryan.fungus.Util.OutOfBoardException;
import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

public class MovingPattern {
    protected ArrayList<Vector2Int> _pattern;
    protected int _maximumMoves;

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
        Cell cell = board.GetCell(fromPosition);


        for(int i = 0; i < _maximumMoves; i++){
            Vector2Int nextPosition = Vector2Int.Plus(fromPosition, _pattern.get(i));


            if(!board.IsOutOfBoard(nextPosition) && cell.piece.color != board.GetCell(nextPosition).piece.color){
                Cell cellAtPosition = board.GetCell(nextPosition);
                available.add(cellAtPosition);
            }
        }

        return available;
    }

    public ArrayList<Cell> GetAvailableSafeMoves(Board board, Vector2Int fromPosition) {
        Piece piece = board.GetCell(fromPosition).piece;
        PieceColor color = piece.color;
        PieceColor opponentColor = color == PieceColor.white ? PieceColor.black : PieceColor.white;
        Vector2Int kingPosition;


        ArrayList<Cell> available = GetAvailableMoves(board, fromPosition);
        ArrayList<Cell> finalAvailable = new ArrayList<Cell>();

        ArrayList<Cell> opponentMoves = board.GetAllAvailableMoves(opponentColor);


        for(Cell cell : available) {
            Vector2Int position = new Vector2Int(cell.position.x, cell.position.y);
            board.Move(fromPosition, position);
            kingPosition = board.GetKingPosition(color);

            board.RefreshAvailableMoves();
            opponentMoves = board.GetAllAvailableMoves(opponentColor);
            if(!opponentMoves.contains(board.GetCell(kingPosition))){
                finalAvailable.add(board.GetCell(position));
            }

            board.UnMove();
        }
        return finalAvailable;
    }

    public static MovingPattern GetPattern(PieceType type){
        switch(type){
            case rook:
                return rookPattern;
            case bishop:
                return bishopPattern;
            case knight:
                return knightPattern;
            case queen:
                return queenPattern;
            case king:
                return kingPattern;
            case pawn:
                return pawnPattern;
            default:
                return whitePawnPattern;
        }
    }

    public static MovingPattern rookPattern = new RookPattern();

    public static MovingPattern bishopPattern = new BishopPattern();

    public static MovingPattern knightPattern = new KnightPattern();

    public static MovingPattern queenPattern = new QueenPattern();

    public static MovingPattern kingPattern = new KingPattern();

    public static MovingPattern whitePawnPattern = new MovingPattern(new Vector2Int[]{
            new Vector2Int(-1,1),
            new Vector2Int(0,1),
            new Vector2Int(0,2),
            new Vector2Int(1,1)
    });
    public static MovingPattern blackPawnPattern = new MovingPattern(new Vector2Int[]{
            new Vector2Int(-1,-1),
            new Vector2Int(0,-1),
            new Vector2Int(0,-2),
            new Vector2Int(1,-1)
    });
    public static MovingPattern pawnPattern = new PawnPattern();
}

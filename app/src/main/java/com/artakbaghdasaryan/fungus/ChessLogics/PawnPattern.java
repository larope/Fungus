package com.artakbaghdasaryan.fungus.ChessLogics;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;

public class PawnPattern extends MovingPattern {
    public PawnPattern(){ super(); }

    @Override
    public ArrayList<Cell> GetAvailableMoves(Board board, Vector2Int fromPosition) {
        ArrayList<Cell> available = new ArrayList<>();
        Piece piece = board.GetCell(fromPosition).piece;

        int modifier = board.GetModifier(piece.color);

        if(
                !board.IsOutOfBoard(fromPosition.x-1, fromPosition.y+modifier)
                        && board.GetCell(fromPosition.x-1, fromPosition.y+modifier).piece != Piece.Empty
                        && board.GetCell(fromPosition.x-1, fromPosition.y+modifier).piece.color != piece.color
        ){
            available.add(board.GetCell(fromPosition.x-1, fromPosition.y+modifier));
        }
        if(!board.IsOutOfBoard(fromPosition.x+1, fromPosition.y+modifier) && board.GetCell(fromPosition.x+1, fromPosition.y+modifier).piece != Piece.Empty && board.GetCell(fromPosition.x+1, fromPosition.y+modifier).piece.color != piece.color){
            available.add(board.GetCell(fromPosition.x+1, fromPosition.y+modifier));
        }

        if(!board.IsOutOfBoard(fromPosition.x, fromPosition.y+modifier) && board.GetCell(fromPosition.x, fromPosition.y+modifier).piece == Piece.Empty){
            available.add(board.GetCell(fromPosition.x, fromPosition.y+modifier));
        }
        else{
            return available;
        }
        if(!board.IsOutOfBoard(fromPosition.x, fromPosition.y+modifier*2) && board.GetCell(fromPosition.x, fromPosition.y+modifier*2).piece == Piece.Empty){
            if(fromPosition.y == board.GetLine().get(piece.color)+modifier){
                available.add(board.GetCell(fromPosition.x, fromPosition.y+modifier*2));
            }
        }

        return available;
    }
}

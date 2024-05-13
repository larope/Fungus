package com.artakbaghdasaryan.fungus.ChessLogics;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;

public class PawnPattern extends MovingPattern {
    public PawnPattern(){ super(); }

    @Override
    public ArrayList<Cell> GetAvailableMoves(Board board, Vector2Int fromPosition) {
        ArrayList<Cell> available = new ArrayList<>();
        Vector2Int boardSize = board.GetSize();
        Piece piece = board.GetCell(fromPosition).piece;

        int modifier = 1;

        if(piece.color == PieceColor.white){
            modifier = 1;
        }
        else{
            modifier = -1;
        }
//        new Vector2Int(-1,1)
//        new Vector2Int(1,1)

//        new Vector2Int(0,1)
//        new Vector2Int(0,2)

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
        if(!board.IsOutOfBoard(fromPosition.x, fromPosition.y+modifier*2) && board.GetCell(fromPosition.x, fromPosition.y+modifier*2).piece == Piece.Empty){
            if(piece.color == PieceColor.white && fromPosition.y == 1){
                available.add(board.GetCell(fromPosition.x, fromPosition.y+modifier*2));
            }
            else if(piece.color == PieceColor.black && fromPosition.y == 6){
                available.add(board.GetCell(fromPosition.x, fromPosition.y+modifier*2));
            }
        }

        return available;
    }
}

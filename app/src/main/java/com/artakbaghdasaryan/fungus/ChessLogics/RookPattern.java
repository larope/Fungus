package com.artakbaghdasaryan.fungus.ChessLogics;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;

public class RookPattern extends MovingPattern {
    public RookPattern(){
        super();
    }

    @Override
    public ArrayList<Cell> GetAvailableMoves(Board board, Vector2Int fromPosition) {
        ArrayList<Cell> available = new ArrayList<>();
        Vector2Int boardSize = board.GetSize();
        Piece piece = board.GetCell(fromPosition).piece;

        int iterator = 1;

        while(!board.IsOutOfBoard(fromPosition.x + iterator, fromPosition.y)){
            Cell newCell = board.GetCell(fromPosition.x + iterator, fromPosition.y);

            if(newCell.piece != Piece.Empty){
                if(newCell.piece.color != piece.color){
                    available.add(newCell);
                }

                break;
            }

            available.add(newCell);

            iterator++;
        }

        iterator = 1;

        while(!board.IsOutOfBoard(fromPosition.x - iterator, fromPosition.y)){
            Cell newCell = board.GetCell(fromPosition.x - iterator, fromPosition.y);

            if(newCell.piece != Piece.Empty){
                if(newCell.piece.color != piece.color){
                    available.add(newCell);
                }

                break;
            }

            available.add(newCell);

            iterator++;
        }

        iterator = 1;

        while(!board.IsOutOfBoard(fromPosition.x, fromPosition.y - iterator)){
            Cell newCell = board.GetCell(fromPosition.x, fromPosition.y - iterator);

            if(newCell.piece != Piece.Empty){
                if(newCell.piece.color != piece.color){
                    available.add(newCell);
                }

                break;
            }

            available.add(newCell);

            iterator++;
        }

        iterator = 1;

        while(!board.IsOutOfBoard(fromPosition.x, fromPosition.y + iterator)){
            Cell newCell = board.GetCell(fromPosition.x, fromPosition.y + iterator);

            if(newCell.piece != Piece.Empty){
                if(newCell.piece.color != piece.color){
                    available.add(newCell);
                }

                break;
            }

            available.add(newCell);

            iterator++;
        }

        return available;
    }


}

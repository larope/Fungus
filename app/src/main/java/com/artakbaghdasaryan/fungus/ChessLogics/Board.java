package com.artakbaghdasaryan.fungus.ChessLogics;


import android.util.Log;

import com.artakbaghdasaryan.fungus.Util.OutOfBoardException;
import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Board {
    private Vector2Int _boardSize = new Vector2Int(-1,-1);
    private final Cell[][] _cells;
    private final ArrayList<Cell> _availableMovesBlack;
    private final ArrayList<Cell> _availableMovesWhite;

    private Move _lastMove;

    public Board(Vector2Int boardSize, Cell[][] cells){
        _availableMovesBlack = new ArrayList<>();
        _availableMovesWhite = new ArrayList<>();

        _cells = new Cell[boardSize.y][boardSize.x];
        _boardSize = boardSize;

        for(int y = 0; y < boardSize.y; y++){
            for(int x = 0; x < boardSize.x; x++){
                _cells[y][x] = cells[y][x];
            }
        }
        _lastMove = Move.Empty;
        RefreshAvailableMoves();
    }

    public ArrayList<Cell> GetAvailableMoves(Cell cell) {
        ArrayList result = new ArrayList<Cell>();

        if(cell.piece != Piece.Empty){
            result = cell.piece.pattern.GetAvailableMoves(this, cell.position);
        }

        return result;
    }

    public ArrayList<Cell> GetAvailableMoves(MovingPattern pattern, Vector2Int position) {
        return pattern.GetAvailableMoves(this, position);
    }

    public boolean IsOutOfBoard(Vector2Int cellPosition){
        return IsOutOfBoard(cellPosition.x, cellPosition.y);
    }
    public boolean IsOutOfBoard(int x, int y){
        if(x >= _boardSize.x || y >= _boardSize.y || x < 0 || y < 0){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean IsCellAttacked(Vector2Int cellPosition, PieceColor color){
        return IsCellAttacked(cellPosition.x, cellPosition.y, color);
    }
    public boolean IsCellAttacked(int x, int y, PieceColor color){
        ArrayList<Cell> availableMoves;

        if(color == PieceColor.white){
            availableMoves = _availableMovesWhite;
        }
        else{
            availableMoves = _availableMovesBlack;
        }

        for(Cell cell : availableMoves){
            if(cell.position.x == x && cell.position.y == y){
                return true;
            }
        }
        return false;
    }
    public Cell GetCell(int x, int y) {
        if(_boardSize.equals(new Vector2Int(-1,-1))){
            Log.e("MERR", "Board doesn't have any size.");
        }
        if(IsOutOfBoard(x,y)){
            Log.e("MERR", "Cell coordinate is out of board");
        }
        return _cells[y][x];
    }
    public Cell GetCell(Vector2Int position){
        return GetCell(position.x, position.y);
    }

    public void SetCell(int x, int y, Cell newCell){
        if(_boardSize.equals(new Vector2Int(-1,-1))){
            Log.e("MERR", "Board doesn't have any size.");
        }
        if(IsOutOfBoard(x,y)){
            Log.e("MERR", "Cell coordinate is out of board");
        }

        _cells[y][x] = newCell;
        RefreshAvailableMoves();
    }

    public void SetCell(Vector2Int position, Cell newCell){
        SetCell(position.x, position.y, newCell);
    }
    public Vector2Int GetSize(){
        return _boardSize;
    }

    public ArrayList<Cell> GetAllAvailableMoves(PieceColor color){
        if(color == PieceColor.white){
            return _availableMovesWhite;
        }
        else{
            return _availableMovesBlack;
        }
    }

    public void RefreshAvailableMoves(PieceColor color){
        if(color == PieceColor.white){
            _availableMovesWhite.clear();
            for(int y = 0; y < _boardSize.y; y++){
                for(int x = 0; x < _boardSize.x; x++){
                    Cell currentcell = GetCell(x,y);
                    if(currentcell.piece != Piece.Empty && currentcell.piece.color == PieceColor.white){
                        _availableMovesWhite.addAll(GetAvailableMoves(currentcell));
                    }
                }
            }
        }
        else{
            _availableMovesBlack.clear();
            for(int y = 0; y < _boardSize.y; y++){
                for(int x = 0; x < _boardSize.x; x++){
                    Cell currentcell = GetCell(x,y);
                    if(currentcell.piece != Piece.Empty && currentcell.piece.color == PieceColor.black){
                        _availableMovesBlack.addAll(GetAvailableMoves(currentcell));
                    }
                }
            }
        }
    }
    public void RefreshAvailableMoves(){
        _availableMovesWhite.clear();
        _availableMovesBlack.clear();

        for(int y = 0; y < _boardSize.y; y++){
            for(int x = 0; x < _boardSize.x; x++){
                Cell currentcell = GetCell(x,y);
                if(currentcell.piece != Piece.Empty && currentcell.piece.color == PieceColor.white){
                    _availableMovesWhite.addAll(GetAvailableMoves(currentcell));
                }
            }
        }

        for(int y = 0; y < _boardSize.y; y++){
            for(int x = 0; x < _boardSize.x; x++){
                Cell currentcell = GetCell(x,y);
                if(currentcell.piece != Piece.Empty && currentcell.piece.color == PieceColor.black){
                    _availableMovesBlack.addAll(GetAvailableMoves(currentcell));
                }
            }
        }
    }

    public Vector2Int GetKingPosition(PieceColor color) {
        for(Cell[] line : _cells){
            for(Cell cell : line){
                if (cell.piece != Piece.Empty && cell.piece.color == color && cell.piece.type == PieceType.king) {
                    return cell.position;
                }
            }
        }
        return Vector2Int.empty;
    }

    public void Move(Vector2Int from, Vector2Int to){
        _lastMove = new Move(GetCell(from), GetCell(to));

        GetCell(to).piece = GetCell(from).piece;
        GetCell(from).piece = Piece.Empty;

        RefreshAvailableMoves();
    }

    public void UnMove(){
        if(_lastMove != Move.Empty){
            GetCell(_lastMove.from.position).piece = _lastMove.from.piece;
            GetCell(_lastMove.to.position).piece = _lastMove.to.piece;
            _lastMove = Move.Empty;
        }
    }

    public Move GetLastMove(){
        return _lastMove;
    }
}

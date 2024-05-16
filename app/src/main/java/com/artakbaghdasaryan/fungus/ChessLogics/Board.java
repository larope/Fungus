package com.artakbaghdasaryan.fungus.ChessLogics;


import android.util.Log;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;
import java.util.HashMap;

public class Board {
    private HashMap<PieceColor, Boolean> _kingMoved;
    private HashMap<PieceColor, Boolean> _kingRookMoved;
    private HashMap<PieceColor, Boolean> _queenRookMoved;

    private Vector2Int _boardSize = new Vector2Int(-1,-1);
    private final Cell[][] _cells;
    private final ArrayList<Cell> _availableMovesBlack;
    private final ArrayList<Cell> _availableMovesWhite;

    private Move _lastMove;

    public Board(Vector2Int boardSize, Cell[][] cells){
        _availableMovesBlack = new ArrayList<>();
        _availableMovesWhite = new ArrayList<>();

        _kingMoved = new HashMap<>();
        _kingMoved.put(PieceColor.white, false);
        _kingMoved.put(PieceColor.black, false);

        _kingRookMoved = new HashMap<>();
        _kingRookMoved.put(PieceColor.white, false);
        _kingRookMoved.put(PieceColor.black, false);

        _queenRookMoved = new HashMap<>();
        _queenRookMoved.put(PieceColor.white, false);
        _queenRookMoved.put(PieceColor.black, false);



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

    public void UnMove(){
        if(_lastMove != Move.Empty){
            _kingRookMoved = _lastMove.kingRookMoved;
            _queenRookMoved = _lastMove.queenRookMoved;
            _kingMoved = _lastMove.kingMoved;

            GetCell(_lastMove.from.position).piece = _lastMove.from.piece;
            GetCell(_lastMove.to.position).piece = _lastMove.to.piece;
            _lastMove = Move.Empty;
        }
    }

    public Move GetLastMove(){
        return _lastMove;
    }

    public boolean Move(Vector2Int from, Vector2Int to){
        _lastMove = new Move(GetCell(from), GetCell(to), _kingMoved, _queenRookMoved, _queenRookMoved, false);

        Piece piece = GetCell(from).piece;
        PieceColor color = piece.color;

        if(GetCell(from).piece.type == PieceType.king){
            if(!_kingMoved.get(color) && to.x == 6 && IsCastlingAvailableKingSide(color)){
                _kingMoved.put(color, true);
                CastleKingSide(color);
                return true;
            }
            else if(!_kingMoved.get(color) && to.x == 2 && IsCastlingAvailableQueenSide(color)){
                _kingMoved.put(color, true);
                CastleQueenSide(color);
                return true;
            }

            _kingMoved.put(color, true);
        }

        if(piece.type == PieceType.rook){
            if(from.x == 7){
                _kingRookMoved.put(color, true);
            }
            else if(from.x == 0){
                _queenRookMoved.put(color, true);
            }
        }


        if(piece.type == PieceType.rook){
            if(to.x == 7){
                _kingRookMoved.put(color, true);
            }
            else if(to.x == 0){
                _queenRookMoved.put(color, true);
            }
        }


        GetCell(to).piece = GetCell(from).piece;
        GetCell(from).piece = Piece.Empty;

        RefreshAvailableMoves();
        return false;
    }

    public void CastleKingSide(PieceColor color){
        HashMap<PieceColor, Vector2Int> kingPositionsBefore = new HashMap<>();
        kingPositionsBefore.put(PieceColor.white, new Vector2Int(4,0));
        kingPositionsBefore.put(PieceColor.black, new Vector2Int(4,7));

        HashMap<PieceColor, Vector2Int> RookPositionsBefore = new HashMap<>();
        RookPositionsBefore.put(PieceColor.white, new Vector2Int(7,0));
        RookPositionsBefore.put(PieceColor.black, new Vector2Int(7,7));

        HashMap<PieceColor, Vector2Int> kingPositionsAfter = new HashMap<>();
        kingPositionsAfter.put(PieceColor.white, new Vector2Int(6,0));
        kingPositionsAfter.put(PieceColor.black, new Vector2Int(6,7));

        HashMap<PieceColor, Vector2Int> RookPositionsAfter = new HashMap<>();
        RookPositionsAfter.put(PieceColor.white, new Vector2Int(5,0));
        RookPositionsAfter.put(PieceColor.black, new Vector2Int(5,7));

        Move(kingPositionsBefore.get(color), kingPositionsAfter.get(color));
        Move(RookPositionsBefore.get(color), RookPositionsAfter.get(color));

    }

    public void CastleQueenSide(PieceColor color){
        HashMap<PieceColor, Vector2Int> kingPositionsBefore = new HashMap<>();
        kingPositionsBefore.put(PieceColor.white, new Vector2Int(4,0));
        kingPositionsBefore.put(PieceColor.black, new Vector2Int(4,7));

        HashMap<PieceColor, Vector2Int> RookPositionsBefore = new HashMap<>();
        RookPositionsBefore.put(PieceColor.white, new Vector2Int(0,0));
        RookPositionsBefore.put(PieceColor.black, new Vector2Int(0,7));

        HashMap<PieceColor, Vector2Int> kingPositionsAfter = new HashMap<>();
        kingPositionsAfter.put(PieceColor.white, new Vector2Int(2,0));
        kingPositionsAfter.put(PieceColor.black, new Vector2Int(2,7));

        HashMap<PieceColor, Vector2Int> RookPositionsAfter = new HashMap<>();
        RookPositionsAfter.put(PieceColor.white, new Vector2Int(3,0));
        RookPositionsAfter.put(PieceColor.black, new Vector2Int(3,7));

        Move(kingPositionsBefore.get(color), kingPositionsAfter.get(color));
        Move(RookPositionsBefore.get(color), RookPositionsAfter.get(color));
        Log.d("MALOG", "QUEENCATLEING");
    }

    public boolean IsCastlingAvailableKingSide(PieceColor color){
        PieceColor opponentColor = color == PieceColor.white ? PieceColor.black : PieceColor.white;
        HashMap<PieceColor, Integer> lineForColor = GetLine();

        if(
                !_kingRookMoved.get(color) && !_kingMoved.get(color) &&
                        GetCell(5, lineForColor.get(color)).piece.type == PieceType.empty &&
                        GetCell(6, lineForColor.get(color)).piece.type == PieceType.empty &&
                        !IsCellAttacked(new Vector2Int(5, lineForColor.get(color)), opponentColor) &&
                        !IsCellAttacked(new Vector2Int(6, lineForColor.get(color)), opponentColor)
        ){
            return true;
        }
        Log.d("MALOG", "KINGSIDE" + !_kingRookMoved.get(color) + " " + !_kingMoved.get(color) + " " +  (GetCell(5, lineForColor.get(color)).piece.type == PieceType.empty) + " " + (GetCell(6, lineForColor.get(color)).piece.type == PieceType.empty));
        return false;
    }

    public boolean IsCastlingAvailableQueenSide(PieceColor color){
        PieceColor opponentColor = color == PieceColor.white ? PieceColor.black : PieceColor.white;
        HashMap<PieceColor, Integer> lineForColor = GetLine();

        if(!_kingRookMoved.get(color) && !_kingMoved.get(color) &&
                GetCell(1, lineForColor.get(color)).piece == Piece.Empty &&
                GetCell(2, lineForColor.get(color)).piece == Piece.Empty &&
                GetCell(3,lineForColor.get(color)).piece == Piece.Empty &&
                !IsCellAttacked(new Vector2Int(1, lineForColor.get(color)), opponentColor) &&
                !IsCellAttacked(new Vector2Int(2, lineForColor.get(color)), opponentColor) &&
                !IsCellAttacked(new Vector2Int(3, lineForColor.get(color)), opponentColor)
        ){
            return true;
        }
        Log.d("MALOG", "QUEENSIDE" + !_kingRookMoved.get(color) + " " + !_kingMoved.get(color) + " " +  (GetCell(5, lineForColor.get(color)).piece.type == PieceType.empty) + " " + (GetCell(6, lineForColor.get(color)).piece.type == PieceType.empty));
        return false;
    }

    public HashMap<PieceColor, Integer> GetLine(){
        HashMap<PieceColor, Integer> lineForColor = new HashMap<>();
        lineForColor.put(PieceColor.white, 0);
        lineForColor.put(PieceColor.black, 7);

        return lineForColor;
    }
}

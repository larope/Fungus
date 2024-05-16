package com.artakbaghdasaryan.fungus;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.artakbaghdasaryan.fungus.ChessLogics.Board;
import com.artakbaghdasaryan.fungus.ChessLogics.Cell;
import com.artakbaghdasaryan.fungus.ChessLogics.CellColor;
import com.artakbaghdasaryan.fungus.ChessLogics.KingPattern;
import com.artakbaghdasaryan.fungus.ChessLogics.Move;
import com.artakbaghdasaryan.fungus.ChessLogics.Piece;
import com.artakbaghdasaryan.fungus.ChessLogics.PieceColor;
import com.artakbaghdasaryan.fungus.ChessLogics.PieceType;
import com.artakbaghdasaryan.fungus.ChessLogics.RookPattern;
import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;
import java.util.HashMap;

public class ChessGame extends AppCompatActivity {
    private final int boardSize = 8;

    private Board _board;

    private HashMap<Vector2Int, ImageButton> _cellsToButtons;

    private Cell _currentCell;
    private PieceColor _currentPlayerColor;

    private ArrayList<Cell> _availableCells;

    private HashMap<PieceColor, Boolean[]> _castlingAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        _castlingAvailable = new HashMap<PieceColor, Boolean[]>();
        _castlingAvailable.put(PieceColor.black, new Boolean[]{true,true});
        _castlingAvailable.put(PieceColor.white, new Boolean[]{true,true});

        _currentPlayerColor = PieceColor.white;
        _availableCells = new ArrayList<Cell>();
        _currentCell = new Cell(0,0,CellColor.black,Piece.Empty);
        _currentCell.position = Vector2Int.empty;
        _currentCell.piece = Piece.Empty;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_game);

        findViewById(R.id.unMove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MALOG", "UNMOVE!!!!");
                UnMove();
            }
        });

        _cellsToButtons = new HashMap<>();
        //TODO Починить говнокод
        _cellsToButtons.put(new Vector2Int(0,0), (ImageButton) findViewById(R.id.cell11));
        _cellsToButtons.put(new Vector2Int(1,0), (ImageButton) findViewById(R.id.cell12));
        _cellsToButtons.put(new Vector2Int(2,0), (ImageButton) findViewById(R.id.cell13));
        _cellsToButtons.put(new Vector2Int(3,0), (ImageButton) findViewById(R.id.cell14));
        _cellsToButtons.put(new Vector2Int(4,0), (ImageButton) findViewById(R.id.cell15));
        _cellsToButtons.put(new Vector2Int(5,0), (ImageButton) findViewById(R.id.cell16));
        _cellsToButtons.put(new Vector2Int(6,0), (ImageButton) findViewById(R.id.cell17));
        _cellsToButtons.put(new Vector2Int(7,0), (ImageButton) findViewById(R.id.cell18));

        _cellsToButtons.put(new Vector2Int(0,1), (ImageButton) findViewById(R.id.cell21));
        _cellsToButtons.put(new Vector2Int(1,1), (ImageButton) findViewById(R.id.cell22));
        _cellsToButtons.put(new Vector2Int(2,1), (ImageButton) findViewById(R.id.cell23));
        _cellsToButtons.put(new Vector2Int(3,1), (ImageButton) findViewById(R.id.cell24));
        _cellsToButtons.put(new Vector2Int(4,1), (ImageButton) findViewById(R.id.cell25));
        _cellsToButtons.put(new Vector2Int(5,1), (ImageButton) findViewById(R.id.cell26));
        _cellsToButtons.put(new Vector2Int(6,1), (ImageButton) findViewById(R.id.cell27));
        _cellsToButtons.put(new Vector2Int(7,1), (ImageButton) findViewById(R.id.cell28));

        _cellsToButtons.put(new Vector2Int(0,2), (ImageButton) findViewById(R.id.cell31));
        _cellsToButtons.put(new Vector2Int(1,2), (ImageButton) findViewById(R.id.cell32));
        _cellsToButtons.put(new Vector2Int(2,2), (ImageButton) findViewById(R.id.cell33));
        _cellsToButtons.put(new Vector2Int(3,2), (ImageButton) findViewById(R.id.cell34));
        _cellsToButtons.put(new Vector2Int(4,2), (ImageButton) findViewById(R.id.cell35));
        _cellsToButtons.put(new Vector2Int(5,2), (ImageButton) findViewById(R.id.cell36));
        _cellsToButtons.put(new Vector2Int(6,2), (ImageButton) findViewById(R.id.cell37));
        _cellsToButtons.put(new Vector2Int(7,2), (ImageButton) findViewById(R.id.cell38));

        _cellsToButtons.put(new Vector2Int(0,3), (ImageButton) findViewById(R.id.cell41));
        _cellsToButtons.put(new Vector2Int(1,3), (ImageButton) findViewById(R.id.cell42));
        _cellsToButtons.put(new Vector2Int(2,3), (ImageButton) findViewById(R.id.cell43));
        _cellsToButtons.put(new Vector2Int(3,3), (ImageButton) findViewById(R.id.cell44));
        _cellsToButtons.put(new Vector2Int(4,3), (ImageButton) findViewById(R.id.cell45));
        _cellsToButtons.put(new Vector2Int(5,3), (ImageButton) findViewById(R.id.cell46));
        _cellsToButtons.put(new Vector2Int(6,3), (ImageButton) findViewById(R.id.cell47));
        _cellsToButtons.put(new Vector2Int(7,3), (ImageButton) findViewById(R.id.cell48));

        _cellsToButtons.put(new Vector2Int(0,4), (ImageButton) findViewById(R.id.cell51));
        _cellsToButtons.put(new Vector2Int(1,4), (ImageButton) findViewById(R.id.cell52));
        _cellsToButtons.put(new Vector2Int(2,4), (ImageButton) findViewById(R.id.cell53));
        _cellsToButtons.put(new Vector2Int(3,4), (ImageButton) findViewById(R.id.cell54));
        _cellsToButtons.put(new Vector2Int(4,4), (ImageButton) findViewById(R.id.cell55));
        _cellsToButtons.put(new Vector2Int(5,4), (ImageButton) findViewById(R.id.cell56));
        _cellsToButtons.put(new Vector2Int(6,4), (ImageButton) findViewById(R.id.cell57));
        _cellsToButtons.put(new Vector2Int(7,4), (ImageButton) findViewById(R.id.cell58));

        _cellsToButtons.put(new Vector2Int(0,5), (ImageButton) findViewById(R.id.cell61));
        _cellsToButtons.put(new Vector2Int(1,5), (ImageButton) findViewById(R.id.cell62));
        _cellsToButtons.put(new Vector2Int(2,5), (ImageButton) findViewById(R.id.cell63));
        _cellsToButtons.put(new Vector2Int(3,5), (ImageButton) findViewById(R.id.cell64));
        _cellsToButtons.put(new Vector2Int(4,5), (ImageButton) findViewById(R.id.cell65));
        _cellsToButtons.put(new Vector2Int(5,5), (ImageButton) findViewById(R.id.cell66));
        _cellsToButtons.put(new Vector2Int(6,5), (ImageButton) findViewById(R.id.cell67));
        _cellsToButtons.put(new Vector2Int(7,5), (ImageButton) findViewById(R.id.cell68));

        _cellsToButtons.put(new Vector2Int(0,6), (ImageButton) findViewById(R.id.cell71));
        _cellsToButtons.put(new Vector2Int(1,6), (ImageButton) findViewById(R.id.cell72));
        _cellsToButtons.put(new Vector2Int(2,6), (ImageButton) findViewById(R.id.cell73));
        _cellsToButtons.put(new Vector2Int(3,6), (ImageButton) findViewById(R.id.cell74));
        _cellsToButtons.put(new Vector2Int(4,6), (ImageButton) findViewById(R.id.cell75));
        _cellsToButtons.put(new Vector2Int(5,6), (ImageButton) findViewById(R.id.cell76));
        _cellsToButtons.put(new Vector2Int(6,6), (ImageButton) findViewById(R.id.cell77));
        _cellsToButtons.put(new Vector2Int(7,6), (ImageButton) findViewById(R.id.cell78));

        _cellsToButtons.put(new Vector2Int(0,7), (ImageButton) findViewById(R.id.cell81));
        _cellsToButtons.put(new Vector2Int(1,7), (ImageButton) findViewById(R.id.cell82));
        _cellsToButtons.put(new Vector2Int(2,7), (ImageButton) findViewById(R.id.cell83));
        _cellsToButtons.put(new Vector2Int(3,7), (ImageButton) findViewById(R.id.cell84));
        _cellsToButtons.put(new Vector2Int(4,7), (ImageButton) findViewById(R.id.cell85));
        _cellsToButtons.put(new Vector2Int(5,7), (ImageButton) findViewById(R.id.cell86));
        _cellsToButtons.put(new Vector2Int(6,7), (ImageButton) findViewById(R.id.cell87));
        _cellsToButtons.put(new Vector2Int(7,7), (ImageButton) findViewById(R.id.cell88));
        Log.d("MALOG",  _currentCell.position.x + " " + _currentCell.position.y);

        Cell[][] cells = new Cell[boardSize][boardSize];
        int num = 0;

        for(int y = 0; y < boardSize; y++){
            for(int x = 0; x < boardSize; x++){
                CellColor color;
                if((y+x)%2 == 0) {
                    color = CellColor.white;
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.cell_white));
                }else{
                    color = CellColor.black;
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.cell_black));
                }
                cells[y][x] = new Cell(x, y, color, Piece.Empty);

                int finalX = x;
                int finalY = y;

                _cellsToButtons.get(new Vector2Int(x,y)).setScaleType(ImageView.ScaleType.FIT_CENTER);
                _cellsToButtons.get(new Vector2Int(x, y)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectCell(new Vector2Int(finalX, finalY));
                    }
                });
                num++;
            }
        }

        _board = new Board(new Vector2Int(boardSize,boardSize), cells);

        for(int i = 0; i < 8; i++){
            ChangePiece(new Vector2Int(i,1), Piece.WPawn);
            ChangePiece(new Vector2Int(i,6), Piece.BPawn);
        }

        ChangePiece(new Vector2Int(0,0), Piece.WRook);
        ChangePiece(new Vector2Int(7,0), Piece.WRook);

        ChangePiece(new Vector2Int(1,0), Piece.WKnight);
        ChangePiece(new Vector2Int(6,0), Piece.WKnight);

        ChangePiece(new Vector2Int(2,0), Piece.WBishop);
        ChangePiece(new Vector2Int(5,0), Piece.WBishop);

        ChangePiece(new Vector2Int(3,0), Piece.WQueen);
        ChangePiece(new Vector2Int(4,0), Piece.WKing);


        ChangePiece(new Vector2Int(0,7), Piece.BRook);
        ChangePiece(new Vector2Int(7,7), Piece.BRook);

        ChangePiece(new Vector2Int(1,7), Piece.BKnight);
        ChangePiece(new Vector2Int(6,7), Piece.BKnight);

        ChangePiece(new Vector2Int(2,7), Piece.BBishop);
        ChangePiece(new Vector2Int(5,7), Piece.BBishop);

        ChangePiece(new Vector2Int(3,7), Piece.BQueen);
        ChangePiece(new Vector2Int(4,7), Piece.BKing);
    }

    private void ChangePiece(Vector2Int position, Piece piece){
        Cell newCell = _board.GetCell(position);
        newCell.piece = piece;
        _board.SetCell(newCell.position, newCell);

        if(piece == Piece.Empty){
            _cellsToButtons.get(position).setImageDrawable(null);
        }
        else{
            _cellsToButtons.get(position).setImageDrawable(GetDrawableForPiece(newCell.piece.type, newCell.piece.color));
        }
    }

    private void SelectCell(Vector2Int position){
        ReturnNormalColors();

        Cell selectedCell = _board.GetCell(position);

        if(_currentCell.piece.equals(Piece.Empty) && selectedCell.piece.color != _currentPlayerColor){
            return;
        }

        if (position.equals(_currentCell.position)){
            _currentCell = Cell.Empty;
            _availableCells.clear();
            return;
        }

        else if(_currentCell.piece != Piece.Empty && _availableCells.size() > 0 && _currentCell.piece.color == _currentPlayerColor){
            for(int i = 0; i < _availableCells.size(); i++){
                Vector2Int _selectedCellPosition = new Vector2Int(_currentCell.position.x, _currentCell.position.y);
                Log.d("MALOG", _availableCells.get(i).position.x + " " + _availableCells.get(i).position.y + " : " + position.x + " " + position.y);
                if(position.equals(_availableCells.get(i).position) && selectedCell.piece.color != _currentCell.piece.color){
                    if(selectedCell.piece.type == PieceType.king){
                        _castlingAvailable.put(selectedCell.piece.color, new Boolean[]{false,false});
                    }

                    if(position.x == 0){
                        _castlingAvailable.put(
                                selectedCell.piece.color,
                                new Boolean[]{
                                        false,
                                        _castlingAvailable.get(selectedCell.piece.color)[1]
                                });
                    }

                    if(position.x == 0){
                        _castlingAvailable.put(
                                selectedCell.piece.color,
                                new Boolean[]{
                                        _castlingAvailable.get(selectedCell.piece.color)[0],
                                        false
                                });
                    }
                    Move(_selectedCellPosition, position);
                    ChangePlayerColor();
                    return;
                }
            }
            _availableCells.clear();
        }

        if(selectedCell.piece.color != _currentPlayerColor){
            _currentCell = Cell.Empty;
            _availableCells.clear();
            return;
        }


        _availableCells = _board.GetAvailableMoves(selectedCell);
        _availableCells = selectedCell.piece.pattern.GetAvailableSafeMoves(_board, position);

        if(selectedCell.piece.type == PieceType.king){
            KingPattern pattern = (KingPattern)selectedCell.piece.pattern;
            _availableCells = pattern.GetAvailableSafeMoves(_board, position);
        }else if(selectedCell.piece.type == PieceType.rook){
            RookPattern pattern = (RookPattern)selectedCell.piece.pattern;
            _availableCells = pattern.GetAvailableSafeMoves(_board, position);
        }

        if(_availableCells.size() == 0 && selectedCell.piece != Piece.Empty){
            if(selectedCell.color == CellColor.black){
                _cellsToButtons.get(position).setBackgroundColor(getResources().getColor(R.color.cell_black_highlighted));
            }
            else{
                _cellsToButtons.get(position).setBackgroundColor(getResources().getColor(R.color.cell_white_highlighted));
            }
        }

        for (Cell cell : _availableCells) {
            if (cell.color == CellColor.black) {
                _cellsToButtons.get(cell.position).setBackgroundColor(getResources().getColor(R.color.cell_black_highlighted));
            } else {
                _cellsToButtons.get(cell.position).setBackgroundColor(getResources().getColor(R.color.cell_white_highlighted));
            }
        }

        _currentCell = _board.GetCell(position.x, position.y);
    }

    private void Move(Vector2Int selectedCellPosition, Vector2Int position) {
        _board.Move(selectedCellPosition, position);


        Piece piece = _board.GetCell(position).piece;

        _cellsToButtons.get(selectedCellPosition).setImageDrawable(null);
        if(piece == Piece.Empty){
            _cellsToButtons.get(position).setImageDrawable(null);
        }
        else{
            _cellsToButtons.get(position).setImageDrawable(GetDrawableForPiece(piece.type, piece.color));
        }
    }

    private void UnMove() {
        Move lastMove = _board.GetLastMove();

        if(lastMove == Move.Empty) {
            return;
        }

        _cellsToButtons.get(lastMove.from.position).setImageDrawable(null);

        Log.d("MALOG", lastMove.from.position.x + " " + lastMove.from.position.y + " " + lastMove.to.position.x + " " + lastMove.to.position.y);
        if(lastMove.fromPiece == Piece.Empty){
            _cellsToButtons.get(lastMove.from.position).setImageDrawable(null);
        }
        else{
            _cellsToButtons.get(lastMove.from.position).setImageDrawable(GetDrawableForPiece(lastMove.fromPiece.type, lastMove.fromPiece.color));
        }

        _cellsToButtons.get(lastMove.to.position).setImageDrawable(null);
        if(lastMove.toPiece == Piece.Empty){
            _cellsToButtons.get(lastMove.to.position).setImageDrawable(null);
        }
        else{
            _cellsToButtons.get(lastMove.to.position).setImageDrawable(GetDrawableForPiece(lastMove.toPiece.type, lastMove.toPiece.color));
        }

        _board.UnMove();
        ChangePlayerColor();
    }

    private Drawable GetDrawableForPiece(PieceType type, PieceColor color){
        if(color == PieceColor.white){
            switch (type){
                case knight:
                    return getResources().getDrawable(R.drawable.knight_w);
                case bishop:
                    return getResources().getDrawable(R.drawable.bishop_w);
                case rook:
                    return getResources().getDrawable(R.drawable.rook_w);
                case pawn:
                    return getResources().getDrawable(R.drawable.pawn_w);
                case queen:
                    return getResources().getDrawable(R.drawable.queen_w);
                case king:
                    return getResources().getDrawable(R.drawable.king_w);
                default:
                    return getResources().getDrawable(R.drawable.knight_white);
            }
        }else{
            switch (type){
                case knight:
                    return getResources().getDrawable(R.drawable.knight_b);
                case bishop:
                    return getResources().getDrawable(R.drawable.bishop_b);
                case rook:
                    return getResources().getDrawable(R.drawable.rook_b);
                case pawn:
                    return getResources().getDrawable(R.drawable.pawn_b);
                case queen:
                    return getResources().getDrawable(R.drawable.queen_b);
                case king:
                    return getResources().getDrawable(R.drawable.king_b);
                default:
                    return getResources().getDrawable(R.drawable.knight_white);
            }
        }

    }

    private void ChangePlayerColor(){
        if(_currentPlayerColor == PieceColor.white){
            _currentPlayerColor = PieceColor.black;
        }else if(_currentPlayerColor == PieceColor.black){
            _currentPlayerColor = PieceColor.white;
        }
    }

    private void ReturnNormalColors(){
        for(int y = 0; y < boardSize; y++){
            for(int x = 0; x < boardSize; x++){
                if(_board.GetCell(x,y).color == CellColor.black){
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.cell_black));
                }
                else{
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.cell_white));
                }
            }
        }
    }

    private boolean CastlingAvailable(PieceColor color, int side){
        if(color == PieceColor.black){
            if(_castlingAvailable.get(color)[side] == false){
                return false;
            }


        }
        else{
            if(_castlingAvailable.get(color)[side] == false){
                return false;
            }


        }
        return true;
    }
}
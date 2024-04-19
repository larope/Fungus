package com.artakbaghdasaryan.fungus;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.artakbaghdasaryan.fungus.ChessLogics.Board;
import com.artakbaghdasaryan.fungus.ChessLogics.Cell;
import com.artakbaghdasaryan.fungus.ChessLogics.CellColor;
import com.artakbaghdasaryan.fungus.ChessLogics.Piece;
import com.artakbaghdasaryan.fungus.ChessLogics.PieceColor;
import com.artakbaghdasaryan.fungus.ChessLogics.PieceType;
import com.artakbaghdasaryan.fungus.Util.OutOfBoardException;
import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.HashMap;
import java.util.Map;

public class ChessGame extends AppCompatActivity {
    private final int boardSize = 8;
    private Board _board;
    private HashMap<Vector2Int, ImageButton> _cellsToButtons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_game);

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

        Cell[][] cells = new Cell[boardSize][boardSize];
        int num = 0;

        for(int y = 0; y < boardSize; y++){
            for(int x = 0; x < boardSize; x++){
                CellColor color;
                if((y+x)%2 == 0) {
                    color = CellColor.white;
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.white));
                }else{
                    color = CellColor.black;
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.black));
                }
                cells[y][x] = new Cell(x, y, color, Piece.Empty);

                int finalX = x;
                int finalY = y;
                _cellsToButtons.get(new Vector2Int(x, y)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowAvailableMoves(new Vector2Int(finalX, finalY));
                    }
                });
                num++;
            }
        }

        _board = new Board(new Vector2Int(boardSize,boardSize), cells);

        AddPiece(new Vector2Int(1,1), Piece.WKnight);
        AddPiece(new Vector2Int(2,2), Piece.WRook);
        AddPiece(new Vector2Int(3,3), Piece.WKnight);
        AddPiece(new Vector2Int(2,3), Piece.WRook);
        AddPiece(new Vector2Int(4,4), Piece.WRook);
        AddPiece(new Vector2Int(4,3), Piece.WRook);



    }

    private void AddPiece(Vector2Int position, Piece piece){
        Cell newCell = _board.GetCell(position);
        newCell.piece = piece;
        _board.SetCell(newCell.position, newCell);
        _cellsToButtons.get(position).setImageDrawable(GetDrawableForPiece(newCell.piece.type, PieceColor.black));
    }

    private void ShowAvailableMoves(Vector2Int position){
        ReturnNormalColors();
        for(Cell cell : _board.GetAvailableMoves(_board.GetCell(position))){
            _cellsToButtons.get(cell.position).setBackgroundColor(getResources().getColor(R.color.red));
        }
    }

    private Drawable GetDrawableForPiece(PieceType type, PieceColor color){
        switch (type){
            case knight:
                return getResources().getDrawable(R.drawable.knight_white);
            default:
                return getResources().getDrawable(R.drawable.knight_white);
        }
    }

    private void ReturnNormalColors(){
        for(int y = 0; y < boardSize; y++){
            for(int x = 0; x < boardSize; x++){
                if(_board.GetCell(x,y).color == CellColor.black){
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.black));
                }
                else{
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.white));
                }
            }
        }
    }
}
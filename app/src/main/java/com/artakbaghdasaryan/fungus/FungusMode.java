package com.artakbaghdasaryan.fungus;

import static android.app.PendingIntent.getActivity;

import static com.artakbaghdasaryan.fungus.SettingsFragment.SETTINGS_MIRROR_PIECES;
import static com.artakbaghdasaryan.fungus.SettingsFragment.SETTINGS_MIRROR_TIMER;
import static com.artakbaghdasaryan.fungus.SettingsFragment.SETTINGS_SHOW_LAST_MOVE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.artakbaghdasaryan.fungus.ChessLogics.BishopPattern;
import com.artakbaghdasaryan.fungus.ChessLogics.Board;
import com.artakbaghdasaryan.fungus.ChessLogics.Cell;
import com.artakbaghdasaryan.fungus.ChessLogics.CellColor;
import com.artakbaghdasaryan.fungus.ChessLogics.KingPattern;
import com.artakbaghdasaryan.fungus.ChessLogics.KnightPattern;
import com.artakbaghdasaryan.fungus.ChessLogics.Move;
import com.artakbaghdasaryan.fungus.ChessLogics.MovingPattern;
import com.artakbaghdasaryan.fungus.ChessLogics.PawnPattern;
import com.artakbaghdasaryan.fungus.ChessLogics.Piece;
import com.artakbaghdasaryan.fungus.ChessLogics.PieceColor;
import com.artakbaghdasaryan.fungus.ChessLogics.PieceType;
import com.artakbaghdasaryan.fungus.ChessLogics.QueenPattern;
import com.artakbaghdasaryan.fungus.ChessLogics.RookPattern;
import com.artakbaghdasaryan.fungus.Util.Timer;
import com.artakbaghdasaryan.fungus.Util.Vector2Int;
import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.K;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FungusMode extends AppCompatActivity {
    private static final int _boardSize = 8;
    private static final long TIMER_INTERVAL = 100; // Timer interval in milliseconds (adjust as needed)
    private static final long TIMER_DURATION = 60000; // Timer duration in milliseconds (adjust as needed)
    public static final String FUNGUS_SHARED_PREFERENCES_NAME = "ChessGameFungusMode";
    public static final String CHESS_GAME_STATE_FUNGUS_MODE = "ChessGameStateFungusMode";


    private Board _board;
    private Cell _currentCell;
    private PieceColor _currentPlayerColor;
    private ArrayList<Cell> _availableCells;
    private HashMap<PieceColor, Long> _timers;
    private boolean _isFirstMove = true;
    private boolean _isPromotionMove = false;
    private Vector2Int _promotionPosition;
    private boolean _gameEnded = false;

    private long _increment = 5000;

    private boolean _enabledPiecesMirroring = false;
    private boolean _enabledTimerMirroring = false;
    private boolean _showLastMove = false;

    private HashMap<Vector2Int, ImageButton> _cellsToButtons;
    private HashMap<PieceColor, TextView> _timerUI;
    private Handler _handler;
    private Runnable _runnable;
    private HashMap<PieceType, ImageButton> _promotionButtons;

    private Dialog _gameResultWindow;
    private void ShowPopup(String winner){
        _gameEnded = true;
        SaveChessGame(null);

        _gameResultWindow.setContentView(R.layout.game_result_popup);
        ((TextView)_gameResultWindow.findViewById(R.id.winner_text)).setText((winner + " won!").toUpperCase());
        _gameResultWindow.findViewById(R.id.back_to_menu_popup).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SaveChessGame(null);
                        _gameEnded = true;
                        GoBack();
                    }
                }
        );
        _gameResultWindow.findViewById(R.id.new_game_popup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveChessGame(null);
                _gameEnded = true;
                StartNewGame();
            }
        });

        _gameResultWindow.setCancelable(false);
        _gameResultWindow.setCanceledOnTouchOutside(false);
        _gameResultWindow.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_game);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        _gameResultWindow = new Dialog(this);


        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveChessGame(GetCurrentState());

                GoBack();
            }
        });

        findViewById(R.id.newGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartNewGame();
            }
        });

        GetButtons();

        Cell[][] cells = new Cell[_boardSize][_boardSize];

        for (int y = 0; y < _boardSize; y++) {
            for (int x = 0; x < _boardSize; x++) {
                CellColor color;
                if ((y + x) % 2 == 0) {
                    color = CellColor.white;
                } else {
                    color = CellColor.black;
                }
                cells[y][x] = new Cell(x,y,color,Piece.Empty);
            }
        }

        _availableCells = new ArrayList<Cell>();
        _currentCell = new Cell(0,0,CellColor.black,Piece.Empty);
        _currentCell.position = Vector2Int.empty;
        _currentCell.piece = Piece.Empty;

        _promotionPosition = Vector2Int.empty;
        _currentPlayerColor = PieceColor.white;
        _isPromotionMove = false;
        _timers = new HashMap<>();

        ChessGameData chessGame = LoadChessGame();

        if(extras != null && extras.getBoolean("isNewGame") == true){
            long timeForGame = Timer.ConvertToMilliseconds(extras.getInt("hours"), extras.getInt("minutes"), extras.getInt("seconds"));
            _timers.put(PieceColor.white, timeForGame);
            _timers.put(PieceColor.black, timeForGame);
            _increment = extras.getInt("increment")*1000;
            SetUpBoard(cells);
            SetUpTimer();

        }
        else if(chessGame != null){
            _board = new Board(new Vector2Int(_boardSize, _boardSize), cells);

            for(int y = 0; y < _boardSize; y++){
                for(int x = 0; x < _boardSize; x++){
                    _board.GetCell(x,y).piece = Piece.GetPiece(
                            chessGame.board.GetCell(x,y).piece.type, chessGame.board.GetCell(x,y).piece.color);
                }
            }
            _timers = chessGame.timers;
            _currentPlayerColor = chessGame.currentPlayerColor;
            _isPromotionMove = chessGame.isPromotionMove;
            _promotionPosition = chessGame.promotionPosition;
            SetUpTimer();
            UpdatePatternsFungus();
        }
        else{
            StartNewGame();
            SetUpTimer();

        }


        _promotionButtons = new HashMap<>();
        _promotionButtons.put(PieceType.queen, (ImageButton) findViewById(R.id.promoteQueen));
        _promotionButtons.put(PieceType.rook, (ImageButton) findViewById(R.id.promoteRook));
        _promotionButtons.put(PieceType.bishop, (ImageButton) findViewById(R.id.promoteBishop));
        _promotionButtons.put(PieceType.knight, (ImageButton) findViewById(R.id.promoteKnight));

        _promotionButtons.get(PieceType.queen).setBackgroundColor(getResources().getColor(R.color.cell_white));
        _promotionButtons.get(PieceType.rook).setBackgroundColor(getResources().getColor(R.color.cell_black));
        _promotionButtons.get(PieceType.bishop).setBackgroundColor(getResources().getColor(R.color.cell_white));
        _promotionButtons.get(PieceType.knight).setBackgroundColor(getResources().getColor(R.color.cell_black));

        for(PieceType key : _promotionButtons.keySet()){
            final PieceType param = key;
            _promotionButtons.get(key).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Promote(param);
                }
            });
        }

        if(!_isPromotionMove) {
            DisablePromotionUI();
        }else{
            EnablePromotionUI();
        }




        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        int width = (int) (screenWidth / 8);
        int height = (int) (screenWidth / 8);

        for(int y = 0; y < _boardSize; y++){
            for(int x = 0; x < _boardSize; x++){
                if((y+x)%2 == 0) {
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.cell_white));
                }else{
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.cell_black));
                }

                int finalX = x;
                int finalY = y;

                _cellsToButtons.get(new Vector2Int(x, y)).setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                ViewGroup.LayoutParams params = _cellsToButtons.get(new Vector2Int(x, y)).getLayoutParams();
                params.width = width;
                params.height = height;
                _cellsToButtons.get(new Vector2Int(x, y)).setLayoutParams(params);
                _cellsToButtons.get(new Vector2Int(x, y)).setSoundEffectsEnabled(false);
                _cellsToButtons.get(new Vector2Int(x, y)).setPadding(8, 8, 8, 8);

                _cellsToButtons.get(new Vector2Int(x, y)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SelectCell(new Vector2Int(finalX, finalY));
                    }
                });
            }
        }

        UpdatePatternsClassic();
        _board.RefreshAvailableMoves();

        UpdateImagesRotations();
        RefreshImages();
    }

    private void EnablePromotionUI() {
        for(PieceType key : _promotionButtons.keySet()){
            _promotionButtons.get(key).setVisibility(View.VISIBLE);
            _promotionButtons.get(key).setImageDrawable(GetDrawableForPiece(key, _currentPlayerColor));
            _promotionButtons.get(key).setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }
    private void DisablePromotionUI(){
        for(PieceType key : _promotionButtons.keySet()){
            _promotionButtons.get(key).setVisibility(View.INVISIBLE);
        }
    }

    private void GoBack() {
        if(_gameEnded){
            SaveChessGame(null);
        }

        Intent intent = new Intent();
        intent.setClass(FungusMode.this, MainActivity.class);
        startActivity(intent);
    }

    private ChessGameData GetCurrentState() {
        return new ChessGameData(
                _board,
                _currentPlayerColor,
                _timers, _isPromotionMove,
                _promotionPosition);
    }

    private void SetUpTimer() {

        _timerUI = new HashMap<>();
        _timerUI.put(PieceColor.black, findViewById(R.id.timer1));
        _timerUI.put(PieceColor.white, findViewById(R.id.timer2));

        UpdateTimersUI();

        _handler = new Handler();
        _runnable = new Runnable() {
            @Override
            public void run() {
                if (_timers.get(_currentPlayerColor) > 0) {
                    _timers.put(_currentPlayerColor, _timers.get(_currentPlayerColor)-TIMER_INTERVAL);
                    _timerUI.get(_currentPlayerColor).setText(Timer.FormatTime(_timers.get(_currentPlayerColor)));

                    _handler.postDelayed(this, TIMER_INTERVAL);
                } else {

                    TimeLose(PieceColor.white == _currentPlayerColor ? PieceColor.black : PieceColor.white);
                }
            }
        };
    }

    private void UpdateTimersUI() {
        _timerUI.get(PieceColor.black).setText(Timer.FormatTime(_timers.get(PieceColor.black)));
        _timerUI.get(PieceColor.white).setText(Timer.FormatTime(_timers.get(PieceColor.white)));
    }

    private void TimeLose(PieceColor currentPlayerColor) {
        Lost(currentPlayerColor);
    }

    private void Lost(PieceColor color) {
        ShowPopup(color.toString());
    }

    private void UpdatePatternsFungus(){
        for(int y = 0; y < _board.GetSize().y; y++) {
            for (int x = 0; x < _board.GetSize().x; x++) {
                Vector2Int position = new Vector2Int(x, y);
                Piece piece = _board.GetCell(position).piece;
                if(piece.type != PieceType.empty
                        && piece.type != PieceType.king
                        && piece.type != PieceType.queen
                        && piece.type != PieceType.pawn
                ){
                    piece.pattern = GetPattern(_board.GetCell(position));
                }
            }
        }
    }


    private void SetUpBoard(Cell[][] cells) {
        _board = new Board(new Vector2Int(_boardSize, _boardSize), cells);

        int blackY = _board.GetLine().get(PieceColor.black);
        int whiteY = _board.GetLine().get(PieceColor.white);

        int blackModifier = _board.GetModifier(PieceColor.black);
        int whiteModifier = _board.GetModifier(PieceColor.white);

        for(int i = 0; i < 8; i++){
            ChangePiece(new Vector2Int(i,whiteY+whiteModifier), new Piece(PieceType.pawn, PieceColor.white, new PawnPattern()));
            ChangePiece(new Vector2Int(i,blackY+blackModifier), new Piece(PieceType.pawn, PieceColor.black, new PawnPattern()));
        }

        ChangePiece(new Vector2Int(0,whiteY), new Piece(PieceType.rook, PieceColor.white, new RookPattern()));
        ChangePiece(new Vector2Int(7,whiteY), new Piece(PieceType.rook, PieceColor.white, new RookPattern()));

        ChangePiece(new Vector2Int(1,whiteY), new Piece(PieceType.knight, PieceColor.white, new KnightPattern()));
        ChangePiece(new Vector2Int(6,whiteY), new Piece(PieceType.knight, PieceColor.white, new KnightPattern()));

        ChangePiece(new Vector2Int(2,whiteY), new Piece(PieceType.bishop, PieceColor.white, new BishopPattern()));
        ChangePiece(new Vector2Int(5,whiteY), new Piece(PieceType.bishop, PieceColor.white, new BishopPattern()));

        ChangePiece(new Vector2Int(3,whiteY), new Piece(PieceType.queen, PieceColor.white, new QueenPattern()));
        ChangePiece(new Vector2Int(4,whiteY), new Piece(PieceType.king, PieceColor.white, new QueenPattern()));

        ChangePiece(new Vector2Int(0,blackY), new Piece(PieceType.rook, PieceColor.black, new RookPattern()));
        ChangePiece(new Vector2Int(7,blackY), new Piece(PieceType.rook, PieceColor.black, new RookPattern()));

        ChangePiece(new Vector2Int(1,blackY), new Piece(PieceType.knight, PieceColor.black, new KnightPattern()));
        ChangePiece(new Vector2Int(6,blackY), new Piece(PieceType.knight, PieceColor.black, new KnightPattern()));

        ChangePiece(new Vector2Int(2,blackY), new Piece(PieceType.bishop, PieceColor.black, new BishopPattern()));
        ChangePiece(new Vector2Int(5,blackY), new Piece(PieceType.bishop, PieceColor.black, new BishopPattern()));

        ChangePiece(new Vector2Int(3,blackY), new Piece(PieceType.queen, PieceColor.black, new QueenPattern()));
        ChangePiece(new Vector2Int(4,blackY), new Piece(PieceType.king, PieceColor.black, new KingPattern()));

    }

    private void GetButtons() {
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
    }

    private void ChangePiece(Vector2Int position, Piece piece){
        Cell newCell = _board.GetCell(position);
        newCell.piece = piece;
        Log.d("SAS", position.x + " " + position.y + " 354CG");
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

        if(_currentCell.piece.equals(Piece.Empty) && selectedCell.piece.color != _currentPlayerColor || _isPromotionMove){
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

                if(position.equals(_availableCells.get(i).position) && selectedCell.piece.color != _currentCell.piece.color){
                    Move(_selectedCellPosition, position);
                    HaveLost(PieceColor.white);
                    HaveLost(PieceColor.black);
                    UpdatePatternsFungus();

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

        if(_availableCells.isEmpty() && selectedCell.piece != Piece.Empty){
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
        if(_isFirstMove){
            _handler.postDelayed(_runnable, TIMER_INTERVAL);
            _isFirstMove = false;
        }

        if(_isPromotionMove){
            return;
        }

        Piece piece = _board.GetCell(selectedCellPosition).piece;
        PieceColor color = piece.color;
        PieceColor opponentColor = color == PieceColor.white ? PieceColor.black : PieceColor.white;

//        if(_board.GetCell(position).piece != Piece.Empty && color != _board.GetCell(position).piece.color) {
//            _captureSound.start();
//        }
//        else if(_board.GetCell(position).piece == Piece.Empty){
//            _moveSound.start();
//        }

        if(piece.type == PieceType.pawn && position.y == _board.GetLine().get(opponentColor)){
            _board.Move(selectedCellPosition, position);
            _isPromotionMove = true;
            _promotionPosition = position;
            EnablePromotionUI();

            return;
        }

        _board.Move(selectedCellPosition, position);

        ChangePlayerColor();
        RefreshImages();
    }

    private void Promote(PieceType type) {
        _board.GetCell(_promotionPosition).piece = Piece.GetPiece(type, _currentPlayerColor);
        _isPromotionMove = false;

        RefreshImages();
        DisablePromotionUI();
        ChangePlayerColor();
    }

    private void RefreshImages(){
        for(int y = 0; y < _board.GetSize().y; y++){
            for(int x = 0; x < _board.GetSize().x; x++){
                Vector2Int position = new Vector2Int(x,y);
                Piece piece = _board.GetCell(position).piece;

                if(piece == Piece.Empty){
                    _cellsToButtons.get(position).setImageDrawable(null);
                }
                else{
                    _cellsToButtons.get(position).setImageDrawable(GetDrawableForPiece(piece.type, piece.color));
                }
            }
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
                    return null;
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
                    return null;
            }
        }
    }

    private void ChangePlayerColor(){
        _timers.put(_currentPlayerColor, _timers.get(_currentPlayerColor) + _increment);
        UpdateTimersUI();
        if(_currentPlayerColor == PieceColor.white){
            _currentPlayerColor = PieceColor.black;
        }else if(_currentPlayerColor == PieceColor.black){
            _currentPlayerColor = PieceColor.white;
        }

        UpdateImagesRotations();
    }

    private void UpdatePatternsClassic(){
        for(int y = 0; y < _board.GetSize().y; y++) {
            for (int x = 0; x < _board.GetSize().x; x++) {
                Vector2Int position = new Vector2Int(x, y);
                Piece piece = _board.GetCell(position).piece;
                if(piece.type != PieceType.empty){
                    piece.pattern = MovingPattern.GetPattern(piece.type);
                }
            }
        }
    }

    private void UpdateImagesRotations() {
        if(_enabledPiecesMirroring) {
            for (int y = 0; y < _boardSize; y++) {
                for (int x = 0; x < _boardSize; x++) {
                    if (_currentPlayerColor == PieceColor.white) {
                        Objects.requireNonNull(_cellsToButtons.get(new Vector2Int(x, y))).setRotation(0);
                    } else if (_currentPlayerColor == PieceColor.black) {
                        Objects.requireNonNull(_cellsToButtons.get(new Vector2Int(x, y))).setRotation(180);
                    }
                }
            }
        }

        if(_enabledTimerMirroring){
            for(PieceColor color : _timerUI.keySet()){
                if(_currentPlayerColor == PieceColor.white){
                    Objects.requireNonNull(_timerUI.get(color)).setRotation(0);
                }
                else if(_currentPlayerColor == PieceColor.black){
                    Objects.requireNonNull(_timerUI.get(color)).setRotation(180);
                }
            }
        }

        if(_showLastMove && _board.GetLastMove() != Move.Empty){
            Cell from = _board.GetLastMove().from;
            Cell to = _board.GetLastMove().to;

            if(from.color == CellColor.white){
                Objects.requireNonNull(_cellsToButtons.get(from.position)).setBackgroundColor(getResources().getColor(R.color.last_move_white));
            }
            else{
                Objects.requireNonNull(_cellsToButtons.get(from.position)).setBackgroundColor(getResources().getColor(R.color.last_move_black));
            }

            if(to.color == CellColor.white){
                Objects.requireNonNull(_cellsToButtons.get(to.position)).setBackgroundColor(getResources().getColor(R.color.last_move_white));
            }
            else{
                Objects.requireNonNull(_cellsToButtons.get(to.position)).setBackgroundColor(getResources().getColor(R.color.last_move_black));
            }
        }
    }

    private void ReturnNormalColors(){
        for(int y = 0; y < _boardSize; y++){
            for(int x = 0; x < _boardSize; x++){
                if(_board.GetCell(x,y).color == CellColor.black){
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.cell_black));
                }
                else{
                    _cellsToButtons.get(new Vector2Int(x, y)).setBackgroundColor(getResources().getColor(R.color.cell_white));
                }
            }
        }
    }

    private MovingPattern GetPattern(Cell cell){
        Vector2Int position = cell.position;

        switch (position.x){
            case 0: case 7:
                return MovingPattern.rookPattern;
            case 1: case 6:
                return MovingPattern.knightPattern;
            case 2: case 5:
                return MovingPattern.bishopPattern;
            case 3: case 4:
                return MovingPattern.GetPattern(cell.piece.type);
        }

        return  MovingPattern.whitePawnPattern;
    }

    private void UpdatePattern(Vector2Int position) {
        Cell selectedCell = _board.GetCell(position);

        if( selectedCell.piece.type != PieceType.pawn
                && selectedCell.piece.type != PieceType.king
                && selectedCell.piece.type != PieceType.queen
        ) {
            _board.GetCell(position).piece.pattern = GetPattern(_board.GetCell(position));
        }
    }

    private void SaveChessGame(ChessGameData chessGame) {
        SharedPreferences sharedPreferences = getSharedPreferences(FUNGUS_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String chessGameJson = gson.toJson(chessGame);

        editor.putString(CHESS_GAME_STATE_FUNGUS_MODE, chessGameJson);
        editor.apply();
    }


    private ChessGameData LoadChessGame() {
        SharedPreferences sharedPreferences = getSharedPreferences(FUNGUS_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String chessGameJson = sharedPreferences.getString(CHESS_GAME_STATE_FUNGUS_MODE, null);

        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        _enabledPiecesMirroring = sharedPref.getBoolean(SETTINGS_MIRROR_PIECES, false);
        _enabledTimerMirroring = sharedPref.getBoolean(SETTINGS_MIRROR_TIMER, false);
        _showLastMove = sharedPref.getBoolean(SETTINGS_SHOW_LAST_MOVE, false);

        if (chessGameJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(chessGameJson, ChessGameData.class);
        } else {
            return null;
        }
    }

    private void StartNewGame(){
        SaveChessGame(null);
        Intent intent = new Intent(FungusMode.this, CreateFungusGame.class);
        startActivity(intent);
    }

    private boolean HaveLost(PieceColor color){
        ArrayList<Cell> availableSafeMoves = new ArrayList<>();

        for(int y = 0; y < _boardSize; y++){
            for (int x = 0; x < _boardSize; x++) {
                Cell cell = _board.GetCell(x,y);

                if(cell.piece.type != PieceType.empty && cell.piece.color == color){
                    availableSafeMoves.addAll(cell.piece.pattern.GetAvailableSafeMoves(_board, new Vector2Int(x,y)));
                }
            }
        }

        if(availableSafeMoves.isEmpty()){
            Lost(color == PieceColor.white ? PieceColor.black : PieceColor.white);
            _gameEnded = true;
            return true;
        }

        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!_gameEnded){
            //SaveChessGame(GetCurrentState());
        }else{
            SaveChessGame(null);
        }

    }
}

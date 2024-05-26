package com.artakbaghdasaryan.fungus;

import static android.app.PendingIntent.getActivity;

import static com.artakbaghdasaryan.fungus.SettingsFragment.SETTINGS_SHOW_LAST_MOVE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.artakbaghdasaryan.fungus.ChessLogics.Board;
import com.artakbaghdasaryan.fungus.ChessLogics.Cell;
import com.artakbaghdasaryan.fungus.ChessLogics.CellColor;
import com.artakbaghdasaryan.fungus.ChessLogics.CellOnlineData;
import com.artakbaghdasaryan.fungus.ChessLogics.ChessGameData;
import com.artakbaghdasaryan.fungus.ChessLogics.ChessGameDataOnline;
import com.artakbaghdasaryan.fungus.ChessLogics.Move;
import com.artakbaghdasaryan.fungus.ChessLogics.MoveOnlineData;
import com.artakbaghdasaryan.fungus.ChessLogics.MovingPattern;
import com.artakbaghdasaryan.fungus.ChessLogics.Piece;
import com.artakbaghdasaryan.fungus.ChessLogics.PieceColor;
import com.artakbaghdasaryan.fungus.ChessLogics.PieceType;
import com.artakbaghdasaryan.fungus.Util.Timer;
import com.artakbaghdasaryan.fungus.Util.Vector2Int;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ChessOnlineGame extends AppCompatActivity {
    private static final int _boardSize = 8;
    private static final long TIMER_INTERVAL = 100; // Timer interval in milliseconds (adjust as needed)
    private static final long TIMER_DURATION = 60000; // Timer duration in milliseconds (adjust as needed)


    private Board _board;
    private Cell _currentCell;
    private PieceColor _currentPlayerColor;
    private ArrayList<Cell> _availableCells;
    private HashMap<PieceColor, Long> _timers;
    private boolean _isFirstMove = true;
    private boolean _isPromotionMove = false;
    private Vector2Int _promotionPosition;
    private int _promotionXBefore;

    private long _increment = 5000;

    private boolean _showLastMove = false;

    private HashMap<Vector2Int, ImageButton> _cellsToButtons;
    private HashMap<PieceColor, TextView> _timerUI;
    private Handler _handler;
    private Runnable _runnable;
    private HashMap<PieceType, ImageButton> _promotionButtons;

    private Dialog _gameResultWindow;

    private ChessGameDataOnline dataOnline;

    private FirebaseFirestore _dataBase;

    private MoveOnlineData _lastMove;

    private PieceColor _thisPlayerColor;

    private CellOnlineData _lastChangedCell = CellOnlineData.Empty;

    private void ShowPopup(String winner){

        _gameResultWindow.setContentView(R.layout.game_result_popup);
        ((TextView)_gameResultWindow.findViewById(R.id.winner_text)).setText((winner + " won!").toUpperCase());
        _gameResultWindow.findViewById(R.id.back_to_menu_popup).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GoBack();
                    }
                }
        );

        _gameResultWindow.setCancelable(false);
        _gameResultWindow.setCanceledOnTouchOutside(false);
        _gameResultWindow.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_online_game);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        _dataBase = FirebaseFirestore.getInstance();

        _lastMove = MoveOnlineData.Empty;

        _gameResultWindow = new Dialog(this);


        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoBack();
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

        if(extras != null){
            long duration = extras.getLong("duration");

            _increment = extras.getLong("increment");

            _timers.put(PieceColor.white, duration);
            _timers.put(PieceColor.black, duration);

            dataOnline = new ChessGameDataOnline(
                    extras.getString("gameId"),
                    duration,
                    _increment,
                    extras.getString("mode"),
                    extras.getString("hostId"),
                    extras.getString("hostUsername")
            );

            _thisPlayerColor = Objects.equals(extras.getString("currentPlayerColor"), "white") ? PieceColor.white : PieceColor.black;

            Log.d("IMPORTANT", _thisPlayerColor.name());

            SetUpBoard(cells);
            SetUpTimer();
        }else{
            Log.e("MERR", "NODATA");
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
                        SelectCell(new Vector2Int(finalX, finalY),false);
                    }
                });
            }
        }

        UpdatePatternsClassic();
        _board.RefreshAvailableMoves();

        UpdateImagesRotations();
        RefreshImages();
        _dataBase.collection("games")
                .document(dataOnline.gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(documentSnapshot != null){
                            HashMap<String, Object> moveMap = (HashMap)documentSnapshot.get("LastMove");

                            if(moveMap == null){
                                return;
                            }

                            if(moveMap.get("playerColor") == _thisPlayerColor.name()){
                                return;
                            }

                            if((boolean) moveMap.get("promoted")){
                                Vector2Int promotionPosition = new Vector2Int(
                                        ((Long) moveMap.get("promotionX")).intValue(),
                                        _board.GetLine().get(_thisPlayerColor));

                                if(_promotionPosition.equals(promotionPosition)){
                                    return;
                                }

                                Cell newCell = new Cell(
                                        promotionPosition,
                                        _board.GetCell(promotionPosition).color,
                                        Piece.GetPiece(PieceType.valueOf((String) moveMap.get("promotionPiece")), _currentPlayerColor)
                                );

                                _board.SetCell(promotionPosition, newCell);
                                _board.GetCell(new Vector2Int(
                                        ((Long) moveMap.get("promotionXBefore")).intValue(),
                                        promotionPosition.y+_board.GetModifier(_thisPlayerColor)
                                )).piece = Piece.Empty;

                                _timers.put(_thisPlayerColor == PieceColor.white ? PieceColor.black : PieceColor.white, (long) moveMap.get("timer"));
                                UpdateTimersUI();
                                RefreshImages();
                                DisablePromotionUI();
                                ChangePlayerColor();
                            }


                            Log.d("MALOG", "Refresh");

                            Vector2Int from = new Vector2Int(((Long) moveMap.get("fromX")).intValue(), ((Long) moveMap.get("fromY")).intValue());
                            Vector2Int to = new Vector2Int(((Long) moveMap.get("toX")).intValue(), ((Long) moveMap.get("toY")).intValue());

                            Vector2Int lastFrom = new Vector2Int(Integer.parseInt(_lastMove.fromX+""), Integer.parseInt(""+_lastMove.fromY));
                            Vector2Int lastTo = new Vector2Int(Integer.parseInt(_lastMove.toX+""), Integer.parseInt(""+_lastMove.toY));

                            Log.d("MALOG", "from: " + from.x + ":" + from.y + "   to: " + to.x + ":" + to.y );

                            if(from.equals(lastFrom)){
                                return;
                            }

                            if(moveMap != null && (long)moveMap.get("fromX") != -69){
                                Log.d("MALOG", "moveBeingDone");
                                _lastMove.fromX = ((long) moveMap.get("fromX"));
                                _lastMove.fromY = ((long) moveMap.get("fromY"));

                                _lastMove.toX = ((long) moveMap.get("toX"));
                                _lastMove.toY = ((long) moveMap.get("toY"));

                                _lastMove.kingMovedBlack = ((boolean) moveMap.get("kingMovedBlack"));
                                _lastMove.kingMovedWhite = ((boolean) moveMap.get("kingMovedWhite"));

                                _lastMove.kingRookMovedBlack = ((boolean)moveMap.get("kingRookMovedBlack"));
                                _lastMove.kingRookMovedWhite = ((boolean)moveMap.get("kingRookMovedWhite"));

                                _lastMove.queenRookMovedBlack = ((boolean)moveMap.get("queenRookMovedBlack"));
                                _lastMove.queenRookMovedWhite = ((boolean)moveMap.get("queenRookMovedWhite"));


                                Move(
                                        new Vector2Int(Integer.parseInt(_lastMove.fromX+""), Integer.parseInt(_lastMove.fromY+"")),
                                        new Vector2Int(Integer.parseInt(_lastMove.toX+""), Integer.parseInt(_lastMove.toY+""))
                                );
//
//                                SelectCell(from, true);
//                                SelectCell(to, true);

                                HaveLost(PieceColor.white);
                                HaveLost(PieceColor.black);
                            }
                        }
                    }
                });

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
        Intent intent = new Intent();
        intent.setClass(ChessOnlineGame.this, MainActivity.class);

        FirebaseFirestore.getInstance().collection("games").document(dataOnline.gameId).delete();

        startActivity(intent);
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

    private void SetUpBoard(Cell[][] cells) {
        _board = new Board(new Vector2Int(_boardSize, _boardSize), cells);

        int blackY = _board.GetLine().get(PieceColor.black);
        int whiteY = _board.GetLine().get(PieceColor.white);

        int blackModifier = _board.GetModifier(PieceColor.black);
        int whiteModifier = _board.GetModifier(PieceColor.white);

        for(int i = 0; i < 8; i++){
            ChangePiece(new Vector2Int(i,whiteY+whiteModifier), Piece.WPawn);
            ChangePiece(new Vector2Int(i,blackY+blackModifier), Piece.BPawn);
        }

        ChangePiece(new Vector2Int(0,whiteY), Piece.WRook);
        ChangePiece(new Vector2Int(7,whiteY), Piece.WRook);

        ChangePiece(new Vector2Int(1,whiteY), Piece.WKnight);
        ChangePiece(new Vector2Int(6,whiteY), Piece.WKnight);

        ChangePiece(new Vector2Int(2,whiteY), Piece.WBishop);
        ChangePiece(new Vector2Int(5,whiteY), Piece.WBishop);

        ChangePiece(new Vector2Int(3,whiteY), Piece.WQueen);
        ChangePiece(new Vector2Int(4,whiteY), Piece.WKing);

        ChangePiece(new Vector2Int(0,blackY), Piece.BRook);
        ChangePiece(new Vector2Int(7,blackY), Piece.BRook);

        ChangePiece(new Vector2Int(1,blackY), Piece.BKnight);
        ChangePiece(new Vector2Int(6,blackY), Piece.BKnight);

        ChangePiece(new Vector2Int(2,blackY), Piece.BBishop);
        ChangePiece(new Vector2Int(5,blackY), Piece.BBishop);

        ChangePiece(new Vector2Int(3,blackY), Piece.BQueen);
        ChangePiece(new Vector2Int(4,blackY), Piece.BKing);
    }

    private void GetButtons() {
        _cellsToButtons = new HashMap<>();

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

        _board.SetCell(newCell.position, newCell);

        if(piece == Piece.Empty){
            _cellsToButtons.get(position).setImageDrawable(null);
        }
        else{
            _cellsToButtons.get(position).setImageDrawable(GetDrawableForPiece(newCell.piece.type, newCell.piece.color));
        }
    }


        @SuppressLint("UseCompatLoadingForDrawables")
    private void SelectCell(Vector2Int position, boolean isOpponent){
        ReturnNormalColors();

        if(_currentPlayerColor != _thisPlayerColor && !isOpponent){
            return;
        }

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
            GradientDrawable drawable = new GradientDrawable();

            if(cell.piece != Piece.Empty) {
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setCornerRadius(50f);
                if (cell.color == CellColor.black) {
                    drawable.setColor(getResources().getColor(R.color.cell_black));
                }
                else {
                    drawable.setColor(getResources().getColor(R.color.cell_white));
                }
            }
            else{
                if (cell.color == CellColor.black) {
                    drawable.setColor(getResources().getColor(R.color.cell_black_highlighted));
                }
                else {
                    drawable.setColor(getResources().getColor(R.color.cell_white_highlighted));
                }
            }

            _cellsToButtons.get(cell.position).setBackground(drawable);

        }

        _currentCell = _board.GetCell(position.x, position.y);

    }

    private void Move(Vector2Int selectedCellPosition, Vector2Int position) {
        //TODO Add online logics
        if(_isFirstMove){
            _handler.postDelayed(_runnable, TIMER_INTERVAL);
            _isFirstMove = false;
        }

        if(_isPromotionMove){
            //return;
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
            _promotionXBefore = selectedCellPosition.x;
            _promotionPosition = position;
            EnablePromotionUI();

            return;
        }
        _board.Move(selectedCellPosition, position);

        _lastMove = new MoveOnlineData(_board.GetLastMove());
        _lastMove.playerColor = _thisPlayerColor.name();
        _lastMove.timer = _timers.get(_thisPlayerColor);
        _dataBase.collection("games")
                .document(dataOnline.gameId)
                .update("LastMove",
                        _lastMove
                );

//        _dataBase.collection("games")
//                .document(dataOnline.gameId)
//                .update("LastChangesUserId",
//                        FirebaseAuth.getInstance().getCurrentUser().getUid()
//                );


        ChangePlayerColor();
        RefreshImages();
    }

    private void Promote(PieceType type) {
        _board.GetCell(_promotionPosition).piece = Piece.GetPiece(type, _currentPlayerColor);
        _isPromotionMove = false;

        MoveOnlineData data = new MoveOnlineData(_board.GetLastMove());

        data.promoted = true;
        data.promotionX = _promotionPosition.x;
        data.promotionPiece = type.name();
        data.promotionXBefore = _promotionXBefore;
        data.playerColor = _thisPlayerColor.name();
        data.timer = _timers.get(_thisPlayerColor);

        _dataBase.collection("games")
                .document(dataOnline.gameId)
                .update("LastMove",
                        data
                );

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
                _cellsToButtons.get(new Vector2Int(x,y)).setBackground(getResources().getDrawable(R.drawable.cell_btn));

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
                return cell.piece.pattern;
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

    private ChessGameData LoadChessGame() {
        //TODO Change Loading mechanic
        SharedPreferences sharedPreferences = getSharedPreferences("ChessGame", Context.MODE_PRIVATE);
        String chessGameJson = sharedPreferences.getString("ChessGameState", null);

        SharedPreferences sharedPref = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        _showLastMove = sharedPref.getBoolean(SETTINGS_SHOW_LAST_MOVE, false);

        if (chessGameJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(chessGameJson, ChessGameData.class);
        } else {
            return null;
        }
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
            return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        FirebaseFirestore.getInstance().collection("games").document(dataOnline.gameId).delete();

    }
}
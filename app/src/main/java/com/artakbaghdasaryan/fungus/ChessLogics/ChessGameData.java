package com.artakbaghdasaryan.fungus.ChessLogics;

import com.artakbaghdasaryan.fungus.ChessLogics.Board;
import com.artakbaghdasaryan.fungus.ChessLogics.Cell;
import com.artakbaghdasaryan.fungus.ChessLogics.PieceColor;
import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;
import java.util.HashMap;

public class ChessGameData {
    public Board board;
    public PieceColor currentPlayerColor;
    public HashMap<PieceColor, Long> timers;
    public boolean isPromotionMove = false;
    public Vector2Int promotionPosition;

    public ChessGameData (Board board, PieceColor currentPlayerColor, HashMap<PieceColor, Long> timers, boolean isPromotionMove, Vector2Int promotionPosition) {
        this.board = board;
        this.currentPlayerColor = currentPlayerColor;
        this.timers = timers;
        this.isPromotionMove = isPromotionMove;
        this.promotionPosition = promotionPosition;
    }

    public ChessGameData() {

    }
}

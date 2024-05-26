package com.artakbaghdasaryan.fungus.ChessLogics;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

public class MoveOnlineData {
    public long fromX;
    public long fromY;
    public long toX;
    public long toY;

    public boolean isCastling;

    public boolean kingMovedWhite;
    public boolean kingMovedBlack;

    public boolean kingRookMovedWhite;
    public boolean kingRookMovedBlack;

    public boolean queenRookMovedWhite;
    public boolean queenRookMovedBlack;


    public MoveOnlineData(int fromX, int fromY, int toX, int toY, boolean isCastling, boolean kingMovedWhite, boolean kingMovedBlack, boolean kingRookMovedWhite, boolean kingRookMovedBlack, boolean queenRookMovedWhite, boolean queenRookMovedBlack){
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
        this.isCastling = isCastling;
        this.kingMovedWhite = kingMovedWhite;
        this.kingMovedBlack = kingMovedBlack;
        this.kingRookMovedWhite = kingRookMovedWhite;
        this.kingRookMovedBlack = kingRookMovedBlack;
        this.queenRookMovedWhite = queenRookMovedWhite;
        this.queenRookMovedBlack = queenRookMovedBlack;
    }

    public MoveOnlineData(Move move){
        this.fromX = move.from.position.x;
        this.fromY = move.from.position.y;
        this.toX = move.to.position.x;
        this.toY = move.to.position.y;

        this.isCastling = move.isCastling;

        this.kingMovedWhite = move.kingMoved.get(PieceColor.white);
        this.kingMovedBlack = move.kingMoved.get(PieceColor.black);

        this.kingRookMovedWhite = move.kingRookMoved.get(PieceColor.white);
        this.kingRookMovedBlack = move.kingRookMoved.get(PieceColor.black);

        this.queenRookMovedWhite = move.queenRookMoved.get(PieceColor.white);
        this.queenRookMovedBlack = move.queenRookMoved.get(PieceColor.black);
    }

    public static MoveOnlineData Empty = new MoveOnlineData(-69,0,0,0,false,false,false,false,false,false,false);
}

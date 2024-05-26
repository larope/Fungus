package com.artakbaghdasaryan.fungus.ChessLogics;

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

    public boolean castledKingSide = false;
    public boolean castledQueenSide = false;

    public boolean promoted = false;
    public int promotionX = -69;
    public int promotionXBefore = -69;
    public String promotionPiece = "";
    public String playerColor;

    public long timer;

    public MoveOnlineData(int fromX, int fromY, int toX, int toY, boolean isCastling, boolean kingMovedWhite, boolean kingMovedBlack, boolean kingRookMovedWhite, boolean kingRookMovedBlack, boolean queenRookMovedWhite, boolean queenRookMovedBlack, String playerColor){
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
        this.playerColor = playerColor;
    }

    public MoveOnlineData(Move move){
        this.fromX = move.from.position.x;
        this.fromY = move.from.position.y;
        this.toX = move.to.position.x;
        this.toY = move.to.position.y;

        this.isCastling = move.isCastling;

        this.kingMovedWhite = Boolean.TRUE.equals(move.kingMoved.get(PieceColor.white));
        this.kingMovedBlack = Boolean.TRUE.equals(move.kingMoved.get(PieceColor.black));

        this.kingRookMovedWhite = Boolean.TRUE.equals(move.kingRookMoved.get(PieceColor.white));
        this.kingRookMovedBlack = Boolean.TRUE.equals(move.kingRookMoved.get(PieceColor.black));

        this.queenRookMovedWhite = Boolean.TRUE.equals(move.queenRookMoved.get(PieceColor.white));
        this.queenRookMovedBlack = Boolean.TRUE.equals(move.queenRookMoved.get(PieceColor.black));
    }

    public static MoveOnlineData Empty = new MoveOnlineData(-69,0,0,0,false,false,false,false,false,false,false, "none");
}

package com.artakbaghdasaryan.fungus.ChessLogics;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class Move {
    public Cell from;
    public Cell to;

    public Piece fromPiece;
    public Piece toPiece;
    boolean isCastling;

    public HashMap<PieceColor, Boolean> kingMoved;
    public HashMap<PieceColor, Boolean> kingRookMoved;
    public HashMap<PieceColor, Boolean> queenRookMoved;

    @NonNull
    public String toString() {
        return "Move{" +
                "from=" + from +
                ", to=" + to +
                ", fromPiece=" + fromPiece +
                ", toPiece=" + toPiece +
                ", isCastling=" + isCastling +
                ", kingMoved=" + kingMoved +
                ", kingRookMoved=" + kingRookMoved +
                ", queenRookMoved=" + queenRookMoved +
                '}';
    }

    public Move(Cell from, Cell to, HashMap<PieceColor, Boolean> kingMoved, HashMap<PieceColor, Boolean> kingRookMoved, HashMap<PieceColor, Boolean> queenRookMoved, boolean isCastling){
        this.from = new Cell(from.position.x, from.position.y, from.color, from.piece);
        this.to =  new Cell(to.position.x, to.position.y, to.color, to.piece);

        this.kingMoved = new HashMap<>();
        this.kingRookMoved = new HashMap<>();
        this.queenRookMoved = new HashMap<>();

        this.kingMoved.putAll(kingMoved);
        this.kingRookMoved.putAll(kingRookMoved);
        this.queenRookMoved.putAll(queenRookMoved);

        this.fromPiece = from.piece;
        this.toPiece = to.piece;

        this.isCastling = isCastling;
    }

    public Move(MoveOnlineData data,  Board board){
        Cell fromCell = board.GetCell((int) data.fromX, (int) data.fromY);
        Cell toCell = board.GetCell((int) data.toX, (int) data.toY);

        this.from = new Cell((int) data.fromX, (int) data.fromY, fromCell.color, fromCell.piece);
        this.to =  new Cell((int) data.toX, (int) data.toY, toCell.color, toCell.piece);

        this.fromPiece = this.from.piece;
        this.toPiece = this.to.piece;
        this.isCastling = data.isCastling;

        this.kingMoved = new HashMap<>();
        this.kingRookMoved = new HashMap<>();
        this.queenRookMoved = new HashMap<>();

        kingMoved.put(PieceColor.white, data.kingMovedWhite);
        kingMoved.put(PieceColor.black, data.kingMovedBlack);

        kingRookMoved.put(PieceColor.white, data.kingRookMovedWhite);
        kingRookMoved.put(PieceColor.black, data.kingRookMovedBlack);

        queenRookMoved.put(PieceColor.white, data.queenRookMovedWhite);
        queenRookMoved.put(PieceColor.black, data.queenRookMovedBlack);
    }

    public static Move Empty = new Move(Cell.Empty, Cell.Empty, new HashMap<>(), new HashMap<>(), new HashMap<>(), false);
}

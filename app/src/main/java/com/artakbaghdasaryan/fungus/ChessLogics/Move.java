package com.artakbaghdasaryan.fungus.ChessLogics;

import java.util.HashMap;

public class Move {
    public Cell from;
    public Cell to;

    public Piece fromPiece;
    public Piece toPiece;

    public final HashMap<PieceColor, Boolean> kingMoved;
    public final HashMap<PieceColor, Boolean> kingRookMoved;
    public final HashMap<PieceColor, Boolean> queenRookMoved;
    boolean isCastling;

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

    public static Move Empty = new Move(Cell.Empty, Cell.Empty, new HashMap<>(), new HashMap<>(), new HashMap<>(), false);
}

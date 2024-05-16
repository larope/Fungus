package com.artakbaghdasaryan.fungus.ChessLogics;

public class Move {
    public Cell from;
    public Cell to;
    public Piece fromPiece;
    public Piece toPiece;

    public Move(Cell from, Cell to){
        this.from = new Cell(from.position.x, from.position.y, from.color, from.piece);
        this.to =  new Cell(to.position.x, to.position.y, to.color, to.piece);;
        this.fromPiece = from.piece;
        this.toPiece = to.piece;
    }

    public static Move Empty = new Move(Cell.Empty, Cell.Empty);
}

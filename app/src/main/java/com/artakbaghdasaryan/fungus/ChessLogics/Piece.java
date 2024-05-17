package com.artakbaghdasaryan.fungus.ChessLogics;

import android.graphics.drawable.Drawable;

import com.artakbaghdasaryan.fungus.R;
import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Piece {
    public PieceType type;
    public PieceColor color;
    public MovingPattern pattern;

    public Piece(){}

    public Piece(PieceType type){
        this.type = type;
        pattern = new MovingPattern();
    }
    public Piece(PieceType type, PieceColor color){
        this.type = type;
        this.color = color;
        pattern = new MovingPattern();
    }
    public Piece(PieceType type, PieceColor color, MovingPattern pattern){
        this.type = type;
        this.color = color;
        this.pattern = pattern;
    }

    public Piece Clone() {
        return new Piece(type, color, pattern);
    }

    public static Piece GetPiece(PieceType type, PieceColor color){
        if(color == PieceColor.white){
            switch (type){
                case knight:
                    return WKnight;
                case bishop:
                    return WBishop;
                case rook:
                    return WRook;
                case pawn:
                    return WPawn;
                case queen:
                    return WQueen;
                case king:
                    return WKing;
                default:
                    return Empty;
            }
        }else{
            switch (type){
                case knight:
                    return BKnight;
                case bishop:
                    return BBishop;
                case rook:
                    return BRook;
                case pawn:
                    return BPawn;
                case queen:
                    return BQueen;
                case king:
                    return BKing;
                default:
                    return Empty;
            }
        }
    }

    public static final Piece Empty = new Piece(PieceType.empty);

    public static Piece WRook = new Piece(
            PieceType.rook,
            PieceColor.white,
            MovingPattern.rookPattern
    );
    public static Piece BRook = new Piece(
            PieceType.rook,
            PieceColor.black,
            MovingPattern.rookPattern
    );
    public static Piece WKnight = new Piece(
            PieceType.knight,
            PieceColor.white,
            MovingPattern.knightPattern
    );
    public static Piece BKnight = new Piece(
            PieceType.knight,
            PieceColor.black,
            MovingPattern.knightPattern
    );
    public static Piece WBishop = new Piece(
            PieceType.bishop,
            PieceColor.white,
            MovingPattern.bishopPattern
    );
    public static Piece BBishop = new Piece(
            PieceType.bishop,
            PieceColor.black,
            MovingPattern.bishopPattern
    );
    public static Piece WQueen = new Piece(
            PieceType.queen,
            PieceColor.white,
            MovingPattern.queenPattern
    );
    public static Piece BQueen = new Piece(
            PieceType.queen,
            PieceColor.black,
            MovingPattern.queenPattern
    );
    public static Piece WKing = new Piece(
            PieceType.king,
            PieceColor.white,
            MovingPattern.kingPattern
    );
    public static Piece BKing = new Piece(
            PieceType.king,
            PieceColor.black,
            MovingPattern.kingPattern
    );
    public static Piece WPawn = new Piece(
            PieceType.pawn,
            PieceColor.white,
            MovingPattern.pawnPattern
    );
    public static Piece BPawn = new Piece(
            PieceType.pawn,
            PieceColor.black,
            MovingPattern.pawnPattern
    );
}

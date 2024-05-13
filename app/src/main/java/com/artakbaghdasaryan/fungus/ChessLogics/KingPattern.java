package com.artakbaghdasaryan.fungus.ChessLogics;

import android.util.Log;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

import java.util.ArrayList;

public class KingPattern extends MovingPattern {
    public KingPattern() {
        super();
        _pattern = new ArrayList<>();

        _pattern.add(new Vector2Int(-1, -1));
        _pattern.add(new Vector2Int(-1, 0));
        _pattern.add(new Vector2Int(-1, 1));
        _pattern.add(new Vector2Int(1, 1));
        _pattern.add(new Vector2Int(1, 0));
        _pattern.add(new Vector2Int(1, -1));
        _pattern.add(new Vector2Int(0, -1));
        _pattern.add(new Vector2Int(0, 1));

        _maximumMoves = _pattern.size();
    }

    public ArrayList<Cell> GetAvailableSafeMoves(Board board, Vector2Int fromPosition) {
        ArrayList<Cell> available = new ArrayList<>();
        Cell cell = new Cell(fromPosition, board.GetCell(fromPosition).color, board.GetCell(fromPosition).piece.Clone());
        Piece piece = cell.piece.Clone();

        PieceColor enemyColor = piece.color == PieceColor.white ? PieceColor.black : PieceColor.white;

        board.SetCell(fromPosition, Cell.Empty);
        board.RefreshAvailableMoves();

        for (Vector2Int move : _pattern) {
            if (!board.IsOutOfBoard(fromPosition.x + move.x, fromPosition.y + move.y) &&
                    !board.IsCellAttacked(fromPosition.x + move.x, fromPosition.y + move.y, enemyColor) &&
                    (board.GetCell(fromPosition.x + move.x, fromPosition.y + move.y).piece != Piece.Empty &&
                            board.GetCell(fromPosition.x + move.x, fromPosition.y + move.y).piece.color != piece.color ||
                            board.GetCell(fromPosition.x + move.x, fromPosition.y + move.y).piece == Piece.Empty)
            ) {
                available.add(board.GetCell(fromPosition.x + move.x, fromPosition.y + move.y));
            }
        }

        board.SetCell(fromPosition, cell);
        board.RefreshAvailableMoves();

        return available;
    }
}
package com.artakbaghdasaryan.fungus.ChessLogics;

import com.artakbaghdasaryan.fungus.Util.Vector2Int;

public class KnightPattern extends MovingPattern {
    public KnightPattern() {
        super(new Vector2Int[]{
                new Vector2Int(1,2),
                new Vector2Int(1,-2),
                new Vector2Int(-1,2),
                new Vector2Int(-1,-2),
                new Vector2Int(2,1),
                new Vector2Int(2,-1),
                new Vector2Int(-2,1),
                new Vector2Int(-2,-1),
        });
    }
}

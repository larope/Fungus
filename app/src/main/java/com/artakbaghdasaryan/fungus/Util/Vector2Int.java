package com.artakbaghdasaryan.fungus.Util;

public class Vector2Int
{
    public int x;
    public int  y;

    public Vector2Int() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void Move(Vector2Int moveDirection){
        this.Plus(moveDirection);
    }

    public Vector2Int Plus(Vector2Int other){
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    public Vector2Int Minus(Vector2Int other){
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }


    public static Vector2Int Plus(Vector2Int vec1, Vector2Int vec2){
        return new Vector2Int(vec1.x + vec2.x, vec1.y + vec2.y);
    }
    public static Vector2Int Minus(Vector2Int vec1, Vector2Int vec2){
        return new Vector2Int(vec1.x - vec2.x, vec1.y - vec2.y);
    }
    public static Vector2Int up = new Vector2Int(0,1);
    public static Vector2Int right = new Vector2Int(1,0);
    public static Vector2Int down = new Vector2Int(0,-1);
    public static Vector2Int left = new Vector2Int(-1,0);
    public static Vector2Int upRight = new Vector2Int(1,1);
    public static Vector2Int upLeft = new Vector2Int(-1,1);
    public static Vector2Int downLeft = new Vector2Int(-1,-1);
    public static Vector2Int downRight = new Vector2Int(1,-1);
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Vector2Int other = (Vector2Int) obj;

        if(x == other.x && y == other.y){
            return true;
        }
        else{
            return false;
        }
    }

}

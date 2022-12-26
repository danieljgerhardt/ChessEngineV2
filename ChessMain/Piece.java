package ChessMain;

import java.util.ArrayList;

public class Piece {
     private String type;
     private String color;
     private int row;
     private int column;
     private int value;
     private int moveCount;
     private ArrayList<Tile> possibleMoves = new ArrayList<>();
     private boolean hasMoved;

     public Piece() {
          this.type = "e";
          this.color = "e";
     }

     public Piece(String type, String color, int row, int column) {
          this.type = type;
          this.color = color;
          this.row = row;
          this.column = column;
          this.hasMoved = false;
          this.moveCount = 0;
          switch (this.type) {
               case "P":
                    this.value = 1;
                    break;
               case "N":
                    this.value = 3;
                    break;
               case "B":
                    this.value = 3;
                    break;
               case "R":
                    this.value = 5;
                    break;
               case "Q":
                    this.value = 9;
                    break;
               case "K":
                    this.value = 150;
                    break;
          }
     }
     public void addToPossibleMoves(Tile tile) {
         this.possibleMoves.add(tile);
     }

     public void clearPossibleMoves() {
         this.possibleMoves.clear();
     }

     public String getType() {
         return this.type;
     }

     public Piece getPieceCopy() {
         String type = this.getType();
         String color = this.getColor();
         int row = this.getRow();
         int column = this.getColumn();
         Piece ret = new Piece(type, color, row, column);
         ret.setHasMoved(this.getHasMoved());
         ret.setMoveCount(this.getMoveCount());
         return ret;
     }

     public int getValue() {
          return this.value;
     }

     public int getMoveCount() {
          return this.moveCount;
     }

     public void incrementMoveCount() {
          this.moveCount++;
     }

     public void decrementMoveCount() {
          this.moveCount--;
     }

     public void setMoveCount(int mc) {
         this.moveCount = mc;
     }

     public void setType(String newType) {
         this.type = newType;
     }

     public void setHasMoved(Boolean moved) {
         this.hasMoved = moved;
     }

     public boolean getHasMoved() {
         return this.hasMoved;
     }

     public String getColor() {
         return this.color;
     }

     public String getOppositeColor() {
         if (this.color.equals("w")) {
              return "b";
         } else {
              return "w";
         }
     }

     public int getRow() {
         return this.row;
     }

     public int getColumn() {
         return this.column;
     }

     public ArrayList<Tile> getPossibleMoves() {
         return this.possibleMoves;
     }

     public void setRow(int row) {
        this.row = row;
     }

     public void setColumn(int column) {
        this.column = column;
     }

     @Override
     public String toString() {
         return this.color + this.type + " ";
     }

     @Override
     public boolean equals(Object other) {
         if (other == this) {
           return true;
         }

         if (!(other instanceof Piece)) {
              return false;
         }

         Piece test = (Piece) other;

         //row, column, color, piece
         if (test.getRow() == this.getRow() && test.getColumn() == this.getColumn()
                 && test.getColor().equals(this.getColor()) && test.getType().equals(this.getType())) {
              return true;
         } else {
              return false;
         }
     }

     public boolean isPawn() {
          return this.type.equals("P");
     }

     public boolean isKing() {
          return this.type.equals("K");
     }

     public static ArrayList<Move> getListOfMoves(Piece p, Move previousMove, Board b) {
          ArrayList<Move> ret = new ArrayList<>();
          boolean castling = false;
          boolean enPassant = false;
          ArrayList<Tile> possibleMovesPerPiece = p.getPossibleMoves();
          for (int i = 0; i < possibleMovesPerPiece.size(); i++) {
               if (p.isKing() && Math.abs((p.getColumn()
                         - possibleMovesPerPiece.get(i).getColumn())) == 2) {
                    castling = true;
               }
               if (previousMove != null && previousMove.getStartingPiece().isPawn()
                         && previousMove.getStartingPiece().getColor().equals("b")
                         && previousMove.getStartingPiece().getRow() == 5 && p.isPawn()) {
                    enPassant = true;
               }
               if (previousMove != null && previousMove.getStartingPiece().isPawn() &&
                         previousMove.getStartingPiece().getColor().equals("w")
                         && previousMove.getStartingPiece().getRow() == 4 && p.isPawn()) {
                    enPassant = true;
               }
               Move testMove = new Move(p, possibleMovesPerPiece.get(i).getPiece(),
                         b, enPassant, previousMove.getEndingTile(), castling);
               if (testMove.makeMove()) {
                    ret.add(testMove);
                    testMove.undoMove();
               }
               castling = false;
               enPassant = false;
          }
          return ret;
     }
}

package ChessMain;

public class Tile {

     private int row;
     private int column;
     private String color;
     private Piece piece;
     private boolean threatenedByWhite;
     private boolean threatenedByBlack;

     public Tile(int row, int column, String color) {
          this.row = row;
          this.column = column;
          this.color = color;
          this.piece = new Piece("e", "e", this.row, this.column);
          this.threatenedByWhite = false;
          this.threatenedByBlack = false;
     }

     public void setPiece(Piece piece) {
          this.piece = piece;
     }

     public void setThreatened(String color) {
          if (color.equals("w")) {
               this.threatenedByWhite = true;
          } else {
               this.threatenedByBlack = true;
          }
     }

     public String toTileNotation() {
          //ARRAY DIMENSION 1 TO CHESS NOTATION:
          //0 = 8, 1 = 7, 2 = 6, 3 = 5, etc.
          //ARRAY DIMENSION 2 TO CHESS NOTATION:
          //0 = a, 2 = b, 3 = c, 4 = d, etc.

          String dim1 = "";
          switch (this.column) {
               case 0:
                    dim1 = "a";
                    break;
               case 1:
                    dim1 = "b";
                    break;
               case 2:
                    dim1 = "c";
                    break;
               case 3:
                    dim1 = "d";
                    break;
               case 4:
                    dim1 = "e";
                    break;
               case 5:
                    dim1 = "f";
                    break;
               case 6:
                    dim1 = "g";
                    break;
               case 7:
                    dim1 = "h";
                    break;
        }
        int dim2 = 8 - this.row;
        return dim1 + dim2;
     }

     public Piece getPiece() {
          return this.piece;
     }

     public int getRow() {
          return this.row;
     }

     public String getColor() {
          return this.color;
     }

     public int getColumn() {
          return this.column;
     }

     @Override
     public String toString() {
          //return row + ", " + column + ", " + color + "\n";
          return this.piece.getColor() + this.piece.getType() + " on " + this.row + ", " + this.column;
     }

     @Override
     public boolean equals(Object other) {
          if (other == this) {
            return true;
        }

        if (!(other instanceof Tile)) {
            return false;
        }

        Tile test = (Tile) other;

        //row, column, color, piece
        if (test.getRow() == this.getRow() && test.getColumn() == this.getColumn()
                && test.getColor().equals(this.getColor()) && test.getPiece().equals(this.getPiece())) {
             return true;
        } else {
             return false;
        }
     }

}

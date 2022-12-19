package ChessMain;

public class GameOverException extends RuntimeException {

     String error;

     public GameOverException(String errorMessage) {
          super(errorMessage);
          this.error = errorMessage;
     }

     //-1 = black, 0 = draw, 1 = white
     public int getWinner() {
          if (this.error.equals("white wins")) {
               System.out.println("white wins");
               return 1;
          } else if (this.error.equals("draw")) {
               System.out.println("draw");
               return 0;
          }
          System.out.println("black wins");
          return -1;
     }

}

package ChessMain;

import java.util.*;

public class Engine {

     private Board board;
     private Board testBoard;
     private BMTree moveTree;
     public static final int DEPTH = 3;

     public Engine(Board b) {
          //this.testMap = new TreeMap<>();
          this.board = b;
          this.moveTree = new BMTree();
     }
     public void checkMoveBlack(Move previousMove) {
          //this.generateMoveStreamlined(previousMove, "b");
          this.generateMoveGeneral(previousMove, "b");
     }
     public void checkMoveWhite(Move previousMove) {
          //this.generateMoveStreamlined(previousMove, "w");
          this.generateMoveGeneral(previousMove, "w");
     }
     public void prologue(Move previousMove, boolean whiteToMove) {
          System.out.println("ending game... ");
          if (previousMove == null) {
               return;
          }
          if (previousMove.detectKingThreats("b") == null && !whiteToMove) {
               System.out.println("stalemate");
               throw new GameOverException("draw");
          } else if (previousMove.detectKingThreats("w") == null && whiteToMove) {
               System.out.println("stalemate");
               throw new GameOverException("draw");
          }
          if (whiteToMove) {
               throw new GameOverException("black wins");
          } else {
               throw new GameOverException("white wins");
          }
     }
     public Move generateMoveGeneral(Move previousMove, String color) {
          int candidateAmount = 0;
          boolean castling = false;
          boolean enPassant = false;
          this.testBoard = this.board.getBoardCopy();
          Piece empty = new Piece("e", "e", 0, 0);
          Move generator = new Move(empty, empty, this.testBoard);
          ArrayList<Piece> possibleStartingPieces = new ArrayList<>();
          ArrayList<Move> candidateMoves = new ArrayList<>();
          for (Tile[] tArray : this.board.getTileArray()) {
               for (Tile t : tArray) {
                    if (t.getPiece().getColor().equals(color)) {
                         possibleStartingPieces.add(t.getPiece());
                         generator.generatePossibleMoves(t.getPiece());
                    }
               }
          }
          ArrayList<Tile> possibleMovesPerPiece;
          //these next two lines should be monitored when branching out the use case of this method
          double currentEval = Eval.evaluatePosition(this.testBoard);
          double bestEval = currentEval;
          int bestIndex = 0;
          for (Piece piece : possibleStartingPieces) {
               possibleMovesPerPiece = piece.getPossibleMoves();
               for (int i = 0; i < possibleMovesPerPiece.size(); i++) {
                    this.testBoard = this.board.getBoardCopy();
                    if (piece.isKing() && Math.abs((piece.getColumn()
                            - possibleMovesPerPiece.get(i).getColumn())) == 2) {
                         castling = true;
                    }
                    if (previousMove.getStartingPiece().isPawn()
                            && previousMove.getStartingPiece().getColor().equals("b")
                            && previousMove.getStartingPiece().getRow() == 5 && piece.isPawn()) {
                         enPassant = true;
                    }
                    if (previousMove.getStartingPiece().isPawn() &&
                            previousMove.getStartingPiece().getColor().equals("w")
                            && previousMove.getStartingPiece().getRow() == 4 && piece.isPawn()) {
                         enPassant = true;
                    }
                    Move testMove = new Move(piece, possibleMovesPerPiece.get(i).getPiece(),
                            this.testBoard, enPassant, previousMove.getEndingTile(), castling);
                    if (testMove.makeMove()) {
                         currentEval = Eval.evaluatePosition(this.testBoard);
                         candidateAmount++;
                         candidateMoves.add(testMove);
                         if (currentEval > bestEval) {
                              bestIndex = candidateAmount - 1;
                              bestEval = currentEval;
                    }
                    testMove.undoMove();
                    castling = false;
                    enPassant = false;
                    }
               }
          }
          if (candidateMoves.size() < 1) {
               this.prologue(previousMove, previousMove.getStartingPiece().getColor().equals("b"));
          }
          //System.out.println("Selected Candidate Move " + (bestIndex + 1) + " ; Eval: " + bestEval);
          Move toMake = new Move(candidateMoves.get(bestIndex).getStartingPiece(),
                  candidateMoves.get(bestIndex).getEndingTile().getPiece(), this.board, enPassant,
                  previousMove.getEndingTile(), castling);
          return toMake;
     }

     public Move generateMoveDriver(String c) {
          this.moveTree.clear();
          //add check for first move
          TreeNode<BMPair> newRoot = new TreeNode<>(new BMPair(Game.getMostRecentMove(), this.board));
          this.moveTree.setRoot(newRoot);
          newRoot.setIsRoot();
          //populate move tree
          generateMoveTree(this.moveTree.getRoot(), c);
          //scan new move tree and return best move
          return this.moveTree.executeMiniMax().getData().getMove();
     }

     public void generateMoveTree(TreeNode<BMPair> caller, String currColor) {
          if (caller.getDepth() == DEPTH) {
               //base case
               return;
          }
          Board currBoard = caller.getData().getBoard();
          Move generator = new Move(new Piece(), new Piece(), currBoard);
          ArrayList<Piece> startingPieces = new ArrayList<>();
          //generate every possible move
          for (Tile tArray[] : currBoard.getTileArray()) {
               for (Tile t : tArray) {
                    if (currColor.equals(t.getPiece().getColor())) {
                         startingPieces.add(t.getPiece());
                         generator.generatePossibleMoves(t.getPiece());
                         Board forLoopBoard = currBoard.getBoardCopy();
                         for (Move m : Piece.getListOfMoves(t.getPiece(), Game.getMostRecentMove(), forLoopBoard)) {
                              m.makeMove();
                              BMPair newBMPair = new BMPair(m, forLoopBoard.getBoardCopy());
                              m.undoMove();
                              forLoopBoard = currBoard.getBoardCopy();
                              /*if (caller.hasChildren()) {
                                   if (Math.abs(newBMPair.getEval()) - Math.abs(caller.bestChildrenEval(Game.getOppositeColor(currColor)).getData().getEval()) > 0) {
                                        caller.addChild(newBMPair);
                                   }
                              } else {
                                   caller.addChild(newBMPair);
                              }*/
                              caller.addChild(newBMPair);
                         }
                    }
               }
          }
          //generate each move for each child
          for (TreeNode<BMPair> child : caller.getChildren()) {  
               generateMoveTree(child, Game.getOppositeColor(currColor));
          }
     }
}

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
     public static double evaluatePosition(Board board) {
          //Positive = better for white, negative = better for black
          //More extreme value = higher advantage

          double materialEval = board.getTotalPieceValue("w") - board.getTotalPieceValue("b");
          //pawns on the same column(pawn structure eval)
          double pawnStructureEval = pawnStructureEval(board);
          //bishop pair
          double bishopPairEval = bishopPairEval(board);
          //pushed and passed pawns
          double pushedPawnsEval = pushedPawnsEval(board);
          //optimally placed pieces
          double optimalPiecesEval = optimalPiecesEval(board);
          //empty squares near a king(especially if the empty squares match that of an enemy bishop)

          //king in center in opening

          //avoid repeating moves

          //Weighting evals
          //Material = 90%
          //Pushed Pawns = 2.5%
          //Optimal Pieces = 2.5%
          //Pawn structure = 2.5%
          //Having bishop pair = 2.5%
          return (materialEval * .9) + (optimalPiecesEval * .025) +
                    (pushedPawnsEval * .025) + (pawnStructureEval * .025) +
                    (bishopPairEval * .025);
     }
     public static int pawnStructureEval(Board board) {
          int whitePawnsPerColumn;
          int blackPawnsPerColumn;
          int whiteBadColumns = 0;
          int blackBadColumns = 0;
          for (int column = 0; column < 8; column++) {
               whitePawnsPerColumn = 0;
               blackPawnsPerColumn = 0;
               for (int row = 0; row < 8; row++) {
                    Piece test = board.getTileArray()[row][column].getPiece();
                    if (test.getType().equals("P")) {
                         if (test.getColor().equals("w")) {
                              whitePawnsPerColumn++;
                              //System.out.println("white pawn " + row + ", " + column);
                         } else if (test.getColor().equals("b")) {
                              blackPawnsPerColumn++;
                              //System.out.println("black pawn " + row + ", " + column);
                         }
                    }
               }
               if (whitePawnsPerColumn > 1) {
                    whiteBadColumns++;
               } else if (blackPawnsPerColumn > 1) {
                    blackBadColumns++;
               }
          }
          return blackBadColumns - whiteBadColumns;
     }
     public static double pushedPawnsEval(Board board) {
          //passed pawns(pawns that have no opposition from enemy pawns)
          //pawns closer to promotion
          double ret = 0;
          Tile[][] tArray = board.getTileArray();
          int numWhitePawns = 0;
          int numBlackPawns = 0;
          for (int j = 0; j < tArray[0].length; j++) {
               for (int i = 0; i < tArray.length; i++) {
                    Piece test = tArray[i][j].getPiece();
                    if (test.isPawn()) {
                         if (test.getColor().equals("w")) {
                              numWhitePawns++;
                              switch (test.getRow()) {
                                   case 3:
                                        ret += 0.4;
                                        break;
                                   case 5:
                                        ret += 0.6;
                                        break;
                                   case 6:
                                        ret += 3;
                                        break;
                                   default:
                                        break;
                              }
                         } else {
                              numBlackPawns++;
                              switch (test.getRow()) {
                                   case 4:
                                        ret += 0.4;
                                        break;
                                   case 2:
                                        ret += 0.6;
                                        break;
                                   case 0:
                                        ret -= 3;
                                        break;
                                   default:
                                        break;
                              }
                         }
                    }
               }
               if (numWhitePawns == 1 && numWhitePawns > numBlackPawns) {
                    ret++;
               } else if (numBlackPawns == 1 && numBlackPawns > numWhitePawns) {
                    ret--;
               }
               numWhitePawns = 0;
               numBlackPawns = 0;
          }
          return ret;
     }
     public static int bishopPairEval(Board board) {
          int bishopDifferential = 0;
          int whiteBishops = 0;
          int whiteKnights = 0;
          int blackBishops = 0;
          int blackKnights = 0;
          Tile[][] arr = board.getTileArray();
          for (int row = 0; row < 8; row++) {
               for (int column = 0; column < 8; column++) {
                    if (arr[row][column].getPiece().getType().equals("B")) {
                         if (arr[row][column].getPiece().getColor().equals("w")) {
                              whiteBishops++;
                         } else if (arr[row][column].getPiece().getColor().equals("b")) {
                              blackBishops++;
                         }
                    } else if (arr[row][column].getPiece().getType().equals("N")) {
                         if (arr[row][column].getPiece().getColor().equals("w")) {
                              whiteKnights++;
                         } else if (arr[row][column].getPiece().getColor().equals("b")) {
                              blackKnights++;
                         }
                    }
               }
          }
          if ((whiteBishops + whiteKnights) == (blackBishops + blackKnights)) {
               if (whiteBishops == 2 && whiteBishops > blackBishops) {
                    bishopDifferential = 1;
               } else if (blackBishops == 2 && blackBishops > whiteBishops) {
                    bishopDifferential = -1;
               }
          }
          //2 = white has 2 bishops, black has none
          //-2 = black has 2 bishops, white has none
          return bishopDifferential;
     }
     public static double optimalPiecesEval(Board board) {
          Tile[][] arr = board.getTileArray();
          double ret = 0;
          for (int row = 0; row < 8; row++) {
               for (int column = 0; column < 8; column++) {
                    Piece test = arr[row][column].getPiece();
                    if (test.getType().equals("N") || test.getType().equals("Q")) {
                         if (row == 3 || row == 4) {
                              if (column == 3 || column == 4) {
                                   if (test.getColor().equals("w")) {
                                        ret++;
                                   } else {
                                        ret--;
                                   }
                              }
                         }
                         if (row == 2 || row == 5) {
                              if (column == 2 || column == 5) {
                                   if (test.getType().equals("N") || test.getType().equals("Q")) {
                                        if (test.getColor().equals("w")) {
                                             ret += 0.5;
                                        } else {
                                             ret -= 0.5;
                                        }
                                   }
                              }
                         }
                         if (row == 0) {
                              if (column == 0) {
                                   if (test.getType().equals("N") || test.getType().equals("Q")) {
                                        if (test.getColor().equals("w")) {
                                             ret -= 1;
                                        } else {
                                             ret += 1;
                                        }
                                   }
                              }
                         }
                    }
               }
          }
          return ret;
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
          double currentEval = this.evaluatePosition(this.testBoard);
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
                         currentEval = this.evaluatePosition(this.testBoard);
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
          /*BMPair ret = this.moveTree.executeMiniMax().getData();
          System.out.println(ret);
          return ret.getMove();*/
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

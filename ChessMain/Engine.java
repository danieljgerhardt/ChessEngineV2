package ChessMain;

import java.util.*;

public class Engine {

     private Board board;
     private Board testBoard;

     public Engine(Board b) {
          this.board = b;
     }

     public Move generateMoveBlack(Move previousMove) {
          return this.generateMoveGeneral(previousMove, "b");
     }

     public Move generateMoveWhite(Move previousMove) {
          return this.generateMoveGeneral(previousMove, "w");
     }

     public void checkMoveBlack(Move previousMove) {
          //this.generateMoveStreamlined(previousMove, "b");
          this.generateMoveGeneral(previousMove, "b");
     }

     public void checkMoveWhite(Move previousMove) {

          //this.generateMoveStreamlined(previousMove, "w");
          this.generateMoveGeneral(previousMove, "w");
     }

     public Move generateFirstMoveWhite() {
          int candidateAmount = 0;
          this.testBoard = this.board.getBoardCopy();
          Piece empty = new Piece("e", "e", 0, 0);
          Move generator = new Move(empty, empty, this.testBoard);
          ArrayList<Piece> possibleStartingPieces = new ArrayList<>();
          ArrayList<Move> candidateMoves = new ArrayList<>();
          for (Tile[] tArray : this.board.getTileArray()) {
               for (Tile t : tArray) {
                    if (t.getPiece().getColor().equals("w")) {
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
                    Move testMove = new Move(piece, possibleMovesPerPiece.get(i).getPiece(), this.testBoard);
                    if (testMove.makeMove()) {
                         currentEval = this.evaluatePosition(this.testBoard);
                         candidateAmount++;
                         candidateMoves.add(testMove);
                         if (currentEval > bestEval) {
                              bestIndex = candidateAmount - 1;
                              bestEval = currentEval;
                         }
                         testMove.undoMove();
                    }
               }
          }
          if (candidateMoves.size() < 1) {
               this.prologue(null, true);
          }
          //System.out.println("Selected Candidate Move " + (bestIndex + 1) + " ; Eval: " + bestEval);
          return new Move(candidateMoves.get(bestIndex).getStartingPiece(),
                  candidateMoves.get(bestIndex).getEndingTile().getPiece(), this.board);
     }

     public double evaluatePosition(Board board) {
          //Positive = better for white, negative = better for black
          //More extreme value = higher advantage

          double materialEval = board.getTotalPieceValue("w") - board.getTotalPieceValue("b");
          //pawns on the same column(pawn structure eval)
          double pawnStructureEval = this.pawnStructureEval(board);
          //bishop pair
          double bishopPairEval = this.bishopPairEval(board);
          //centralized pieces

          //knights on the edge

          //empty squares near a king(especially if the empty squares match that of an enemy bishop)

          //king in center in opening

          //avoid repeating moves

          //ability to promote

          //Weighting evals
          //Material = 90%
          //Pawn structure = 5%
          //Having bishop pair = 5%
          return (materialEval * .9) + (pawnStructureEval * .05) + (bishopPairEval * .05);
     }

     public int pawnStructureEval(Board board) {
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

     public int pushedPawnsEval(Board board) {
          //passed pawns(pawns that have no opposition from enemy pawns)
          //pawns closer to promotion
          return 0;
     }
     public int bishopPairEval(Board board) {
          int bishopDifferential = 0;
          int whiteBishops = 0;
          int whiteKnights = 0;
          int blackBishops = 0;
          int blackKnights = 0;
          for (int row = 0; row < 8; row++) {
               for (int column = 0; column < 8; column++) {
                    if (board.getTileArray()[row][column].getPiece().getType().equals("B")) {
                         if (board.getTileArray()[row][column].getPiece().getColor().equals("w")) {
                              whiteBishops++;
                         } else if (board.getTileArray()[row][column].getPiece().getColor().equals("b")) {
                              blackBishops++;
                         }
                    } else if (board.getTileArray()[row][column].getPiece().getType().equals("N")) {
                         if (board.getTileArray()[row][column].getPiece().getColor().equals("w")) {
                              whiteKnights++;
                         } else if (board.getTileArray()[row][column].getPiece().getColor().equals("b")) {
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

     public void generateMoveStreamlined(Move previousMove, String color) {
          boolean castling = false;
          boolean enPassant = false;
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
          for (Piece piece : possibleStartingPieces) {
               possibleMovesPerPiece = piece.getPossibleMoves();
               for (int i = 0; i < possibleMovesPerPiece.size(); i++) {
                    if (piece.isKing() && Math.abs((piece.getColumn() - possibleMovesPerPiece.get(i).getColumn())) == 2) {
                         castling = true;
                    }
                    if (color.equals("w") && previousMove.getStartingPiece().isPawn() && previousMove.getStartingPiece().getColor().equals("b") && previousMove.getStartingPiece().getRow() == 5 && piece.isPawn()) { //this line is questionable
                         //System.out.println("can en passant");
                         enPassant = true;
                    }
                    if (color.equals("b") && previousMove.getStartingPiece().isPawn() && previousMove.getStartingPiece().getColor().equals("w") && previousMove.getStartingPiece().getRow() == 4 && piece.isPawn()) { //this line is questionable
                         //System.out.println("can en passant");
                         enPassant = true;
                    }
                    Move testMove = new Move(piece, possibleMovesPerPiece.get(i).getPiece(), this.board.getBoardCopy(), enPassant, previousMove.getEndingTile(), castling);
                    if (testMove.makeMove()) {
                         //System.out.println("possible player move " + (i + 1) + " " + testMove);
                         candidateMoves.add(testMove);
                         testMove.undoMove();
                         castling = false;
                         enPassant = false;
                    }
               }
               //System.out.println("---------------------");
          }
          if (candidateMoves.size() < 1) {
               this.prologue(previousMove, color.equals("w"));
          }
     }

}

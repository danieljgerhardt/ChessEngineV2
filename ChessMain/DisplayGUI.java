package ChessMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DisplayGUI extends JFrame implements ActionListener {

     private Board gameBoard;
     private JButton[][] tileButtons = new JButton[8][8];
     private ArrayList<Piece> piecesClicked = new ArrayList<>();
     private ArrayList<Move> moveList = new ArrayList<>();
     private JLabel winLabel = new JLabel(), loseLabel = new JLabel(), drawLabel = new JLabel();
     private JPanel winPanel = new JPanel(), losePanel = new JPanel(), drawPanel = new JPanel(), buttonPanel;
     private String colorChoice = "";
     private Game game;
     private JFrame frame = new JFrame("Chess");
     private JFrame winFrame = new JFrame("White wins!");
     private JFrame loseFrame = new JFrame("Black wins!");
     private JFrame drawFrame = new JFrame("The game is a draw!");
     private Color lightColor = new Color(238, 238, 210);
     private Color darkColor = new Color(118, 150, 86);
     private boolean gameEndedForWriting = false;

     public DisplayGUI(Game readGame) {
          gameBoard = readGame.getBoard();
          gameBoard.arrangePiecesWhite();

          this.frame.setSize(840, 840);  //used to be 1024
          this.frame.setLocationRelativeTo(null);
          this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          this.frame.setVisible(true);

          this.winFrame.setSize(160, 60);
          this.winLabel.setText("White wins!");
          this.winPanel.add(winLabel);
          this.winFrame.setLocationRelativeTo(null);
          this.winFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

          this.loseFrame.setSize(160, 60);
          this.loseLabel.setText("Black wins!");
          this.losePanel.add(loseLabel);
          this.loseFrame.setLocationRelativeTo(null);
          this.loseFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

          this.drawFrame.setSize(160, 60);
          this.drawLabel.setText("The game is a draw!");
          this.drawPanel.add(drawLabel);
          this.drawFrame.setLocationRelativeTo(null);
          this.drawFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

          this.makeGrid();
          this.addPieces();

          frame.add(buttonPanel);
          this.addWriteWhenClose(frame);
          this.addWriteWhenClose(winFrame);
          this.addWriteWhenClose(drawFrame);
          this.addWriteWhenClose(loseFrame);

          while (!this.colorChoice.equalsIgnoreCase("white") &&
          !this.colorChoice.equalsIgnoreCase("black")) {
                this.colorChoice =
                JOptionPane.showInputDialog(null, "Which color would you like to play as? (White/Black)");
          }
          this.game = readGame;
          if (this.colorChoice.equals("black")) {
               this.game.executeFirstComputerMove();
               this.addPieces();
          }
     }


     public void makeGrid() {
          buttonPanel = new JPanel((new GridLayout(8, 8)));
          for (int i = 0; i < 8; i++) {
               for (int j = 0; j < 8; j++) {
                    tileButtons[i][j] = new JButton();
                    tileButtons[i][j].addActionListener(this);
                    tileButtons[i][j].setEnabled(true);
                    if ((i + j) % 2 == 0) {
                         tileButtons[i][j].setBackground(lightColor);
                    } else {
                         tileButtons[i][j].setBackground(darkColor);
                    }
                    buttonPanel.add(tileButtons[i][j]);
               }
          }
     }

     public void addPieces() {
          for (int i = 0; i < 8; i++) {
               for (int j = 0; j < 8; j++) {
                    String pieceString = this.gameBoard.getPieces()[i][j];
                    String directory = "ChessMain/Images/" + pieceString + ".png";
                    //Scaling = 117
                    tileButtons[i][j].setIcon(new ImageIcon(((
                            new ImageIcon(directory).getImage().
                                    getScaledInstance(117, 117, java.awt.Image.SCALE_SMOOTH)))));
               }
          }
     }

     @Override
     public void actionPerformed(ActionEvent e) {
          for (int i = 0; i < 8; i++) {
               for (int j = 0; j < 8; j++) {
                    if (e.getSource() == tileButtons[i][j]) {
                         piecesClicked.add(this.gameBoard.getTileArray()[i][j].getPiece());
                         try {
                              if (piecesClicked.size() > 1) {
                                   if (this.colorChoice.equals("white")) {
                                        if (this.game.executePlayerMove(piecesClicked.get(piecesClicked.size() - 2),
                                         piecesClicked.get(piecesClicked.size() - 1))) {
                                             this.addPieces();
                                             //this.game.executeComputerMove("b");
                                             this.game.executeComputerMove("b");
                                        }
                                   } else {
                                        if (this.game.executePlayerMove(piecesClicked.get(piecesClicked.size() - 2),
                                        piecesClicked.get(piecesClicked.size() - 1))) {
                                             this.addPieces();
                                             //this.game.executeComputerMove("w");
                                             this.game.executeComputerMove("w");
                                        }
                                   }
                                   this.game.getGameOverStatus();
                                   if (this.game.getWhiteToMove() == this.piecesClicked.get(piecesClicked.size() - 2).
                                           getColor().equals("w")) {
                                        //get white to move and make sure first piece and second piece match rules
                                        if (this.game.executePlayerMove(piecesClicked.get(piecesClicked.size() - 2),
                                                piecesClicked.get(piecesClicked.size() - 1))) {
                                             this.addPieces();
                                        }
                                   }
                              }
                              this.game.getGameOverStatus();
                         } catch (GameOverException ex) {
                              this.gameEndedForWriting = true;
                              if (ex.getWinner() == 1) {
                                   this.winFrame.add(winPanel);
                                   this.winFrame.setVisible(true);
                                   //System.out.println("white wins");
                              } else if (ex.getWinner() == 0) {
                                   this.drawFrame.add(drawPanel);
                                   this.drawFrame.setVisible(true);
                              } else {
                                   this.loseFrame.add(losePanel);
                                   this.loseFrame.setVisible(true);
                                   //System.out.println("black wins");
                              }
                              boolean flip = true;
                              for (int x = 0; x < this.moveList.size(); x++) {
                                   if (flip) {
                                        System.out.print((x + 1) + ". " + this.moveList.get(x).toString() + " ");
                                   } else {
                                        System.out.print(this.moveList.get(x).toString() + "\n");
                                   }

                                   flip = !flip;
                              }
                         }
                    }
               }
           }
           //Remove to implement blindfold chess!
           this.addPieces();
     }

     public static void main(String[] args) {
          String[] boardOptions = {"basic", "previous game", "queens galore", "bishopless",  "bishops against knights",
                  "castle test", "pawn army", "test pawn capture"};
          String boardChoice = "";
          /*String boardOptionDisplay = "Enter the name of the board to play.\n"
                  + "1. Basic 2. Previous Game\n"
                  + "3. Queens Galore 4. Bishopless \n"
                  + "5. Bishops Against Knights 6. Castle Test\n"
                  + "7. Pawn Army 8. Test Pawn Capture";*/
          String boardOptionDisplay = "Enter the name of the board to play.\n"
                    + "1. Basic 2. Previous Game\n"
                    + "3. Bishopless \n";
          boolean looped = false;
          while (true) {
               try {
                    if (!looped) {
                         while (!Arrays.asList(boardOptions).contains(boardChoice.toLowerCase())) {
                              boardChoice = JOptionPane.showInputDialog(null, boardOptionDisplay);
                              try {
                                   int index = Integer.parseInt(boardChoice) - 1;
                                   try {
                                        boardChoice = boardOptions[index];
                                   } catch (IndexOutOfBoundsException ee) {
                                        System.out.println("enter a valid number");
                                   }
                              } catch (NumberFormatException e) {

                              }
                         }
                    } else {
                         //user attempted to load game that has been completed
                         while (!Arrays.asList(boardOptions).contains(boardChoice.toLowerCase())
                                 || boardChoice.equalsIgnoreCase("previous game")) {
                              boardChoice = JOptionPane.showInputDialog(null, boardOptionDisplay);
                              try {
                                   int index = Integer.parseInt(boardChoice) - 1;
                                   try {
                                        boardChoice = boardOptions[index];
                                   } catch (IndexOutOfBoundsException ee) {
                                        System.out.println("enter a valid number");
                                   }
                              } catch (NumberFormatException e) {

                              }
                         }
                    }

                    String directory = "ChessMain/Boards/" + boardChoice.replaceAll("\\s", "") + ".txt";
                    Game gRead = ChessIO.readFile(directory);
                    DisplayGUI GUI = new DisplayGUI(gRead);
                    break;
               } catch (IllegalArgumentException e) {
                    looped = true;
                    System.out.println("THAT GAME HAS BEEN COMPLETED");
               }
          }
     }

     //may be used to tint color of tiles that have been clicked/moved on
     public void setTileColor(Color c, int row, int column) {
          this.tileButtons[row][column].setBackground(c);
     }

     public void print(String filePath) {
          boolean canCastleWhiteKingside = this.game.getWhiteCanCastleKingside();
          boolean canCastleWhiteQueenside = this.game.getWhiteCanCastleQueenside();
          boolean canCastleBlackKingside = this.game.getBlackCanCastleKingside();
          boolean canCastleBlackQueenside = this.game.getBlackCanCastleQueenside();
          boolean whiteToMove = this.game.getWhiteToMove();
          ChessIO.printFile(this.gameBoard, whiteToMove, canCastleWhiteKingside, canCastleWhiteQueenside,
                  canCastleBlackKingside, canCastleBlackQueenside, this.gameEndedForWriting, filePath);
     }

     public void addWriteWhenClose(JFrame f) {
          f.addWindowListener(new WindowAdapter() {
               @Override
               public void windowClosing(WindowEvent e) {
                    print("ChessMain/Boards/previousgame.txt");
                    super.windowClosing(e);
               }
          });
     }

}

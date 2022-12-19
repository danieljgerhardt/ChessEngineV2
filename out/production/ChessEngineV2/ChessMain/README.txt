=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: dgerh
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. 2D Arrays

There are two layers of 2D arrays used to model the game state. There is the array of strings that keep track of the
pieces, and the array of tiles used for board management. The chess board is itself a "2D array", so using 2D arrays to keep
track of what is on the board makes a lot of sense.

  2. Collections

Collections are used to model a number of different aspects of the game state. The following are implemented with
ArrayLists: move lists, threat lists for checks, possible moves, post move threat lists for pins, a list of pieces
that have been clicked, and more. ArrayLists can be added to without a fixed length, which is useful for all of 
these cases. The key value pair is not necessary for any of these lists, so a map is not required and would
add unnecessary complexity. I did consider using maps for pieces and their corresponding move lists, but it is
more practical to have move lists be an encapsulated state. Additionally, sets do not make sense because duplicates
are important to keep track of, especially in move lists(while moves generally are unique even if they "appear"
the same, this is a good safety precaution).

  3. File I/O

File I/O is used for loading different boards to play, and storing the game after the program is ended. The loading
of the game accounts for whether castling is possible on both sides for both players, and stores if the game was 
ended so that a game that is over cannot be loaded.

  4. Complex Game Logic

Chess was noted in the rubric as the game that accomplishes this concept. Checks, en passant, promotion(left out of the
rubric), castling, and all rules of chess have been implemented.

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

Classes: 

1) Board -- this stores a 2D array of strings that represent the pieces, and is what actually models the active game
state. It also sets up the colored tiles, and is useful for finding specific pieces(i.e. when looking if castling is 
possible).
2) ChessIO -- contains static methods for reading and writing game files.
3) DisplayGUI -- the GUI components of the game are managed in this class, and it is the main class for the program.
4) Engine -- originally the engine was meant to play against you. Now, it is used to check if the game has ended by
checking if there are any possible moves. It still contains lots of fragments of ideas for the AI that I hope to use
in the future.
5) Game -- the Game class is sort of like the mom of the chess game. It executes the moves, contains the game board,
and keeps track of important game information like a move list, board list, and if the game has ended.
6) GameOverException -- this is an exception class used to end the game. It is caught by the getGameOverStatus() method
in the Game class so that at any time this exception can be caught just by calling that method.
7) Move -- perhaps my greatest creation. An over 700 line behemoth that represents a "Move" object. The move 
object contains all the information for a move, and the class contains methods for generating possible moves, checking
for checks and pins, and all other logic for moves in the chess game.
8) Piece -- a piece! It has a type, a color, a row, a column, a hasMoved boolean and a value(this was going to 
be used for the AI). These include empty, bishop, knight, king, queen, rook, and pawn.
9) Tile -- represents one tile on the chess board. Has a row, column, color, and piece(can be "empty").


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

Many! Mostly with squashing bugs, but also poor planning. There are a lot of messy details and strange loop arounds to
solve issues that absolutely could've been solved in pretty ways. The nitty gritty details of the code are pretty
ridiculous a lot of the time, and a lot of it is simple code expanded to ridiculous length when more advanced methods
I'm sure could've saved time and confusion. The design is not too bad, but more classes to help with organization
would definitely have been smarter. Castling and en passant took a while. I had some bugs like a bug with promotion
that was somehow fixed by using a different method in the Engine that as far as I can tell does the same thing. I look
at this code and feel pride but also horror because I in some ways don't know how what I did does what it does(and
appears to work pretty well!). I originally wanted to implement AI but sadly it didn't come together.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

In some ways the design is something I am very proud of. Every design choice makes a good amount of sense, even if in
practice it got pretty messy. The separation of functionality is rather good. The private state is encapsulated well,
but because there are so many moving parts sometimes so much needs to be adjusted for a simple change that the whole
thing gets extremely confusing. Undoing a move is such a ridiculous feat sometimes, that I just did not implement it
as something that can be done by the user. If I could have changed the design, I probably would've added a TestBoard
and TestMove to separate functionality for the engine classes that got really messy. Also, a class that stores a board
and a paired evaluation may have been a major help to making an AI actually work. A number associated with each move
to keep track of which move is which would have been wise, and would have aided in the possible implementation of undo 
functionality.

========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

Chess piece images: https://commons.wikimedia.org/wiki/Category:PNG_chess_pieces/Standard_transparent

package ChessMain;

public abstract class Eval {

	private static int[][] pawnEvalsBlack = {
			{0,  0,  0,  0,  0,  0,  0,  0},
			{0,  0,  0, -5, -5,  0,  0,  0},
			{0,  2,  3,  4,  4,  3,  2,  0},
			{0,  4,  6, 10, 10,  6,  4,  0},
			{0,  6,  9, 10, 10,  9,  6,  0},
			{4,  8, 12, 16, 16, 12,  8,  4},
			{5, 10, 15, 20, 20, 15, 10,  5},
			{0,  0,  0,  0,  0,  0,  0,  0}
	};
	private static int[][] pawnEvalsWhite = {
			{0,  0,  0,  0,  0,  0,  0,  0},
			{5, 10, 15, 20, 20, 15, 10,  5},
			{4,  8, 12, 16, 16, 12,  8,  4},
			{0,  6,  9, 10, 10,  9,  6,  0},
			{0,  4,  6, 10, 10,  6,  4,  0},
			{0,  2,  3,  4,  4,  3,  2,  0},
			{0,  0,  0, -5, -5,  0,  0,  0},
			{0,  0,  0,  0,  0,  0,  0,  0}
	};

	private static int bishopEvalsUniversal[][] = {
			{-5, -5, -5, -5, -5, -5, -5, -5},
			{-5, 10,  5,  8,  8,  5, 10, -5},
			{-5,  5,  3,  8,  8,  3,  5, -5},
			{-5,  3, 10,  3,  3, 10,  3, -5},
			{-5,  3, 10,  3,  3, 10,  3, -5},
			{-5,  5,  3,  8,  8,  3,  5, -5},
			{-5, 10,  5,  8,  8,  5, 10, -5},
			{-5, -5, -5, -5, -5, -5, -5, -5}
	};
	private static int knightEvalsUniversal[][] = {
			{-10, -5, -5, -5, -5, -5, -5, -10},
			{ -8,  0,  0,  3,  3,  0,  0, -8},
			{ -8,  0, 10,  8,  8, 10,  0, -8},
			{ -8,  0,  8, 10, 10,  8,  0, -8},
			{ -8,  0,  8, 10, 10,  8,  0, -8},
			{ -8,  0, 10,  8,  8, 10,  0, -8},
			{ -8,  0,  0,  3,  3,  0,  0, -8},
			{-10, -5, -5, -5, -5, -5, -5, -10}
	};

	public static double evaluatePosition(Board board) {
		//Positive = better for white, negative = better for black
		//More extreme value = higher advantage

		double materialEval = board.getTotalPieceValue("w") - board.getTotalPieceValue("b");
		//pawns on the same column(pawn structure eval)
		double pawnStructureEval = pawnStructureEval(board);
		//bishop pair
		double bishopPairEval = bishopPairEval(board);
		//passed pawns
		double pushedPawnsEval = passedPawnsEval(board);
		//optimally placed pieces
		double optimalPiecesEval = optimalPiecesEval(board);
		//has castled
		double castleEval = castleEval();
		//empty squares near a king(especially if the empty squares match that of an enemy bishop)

		//king in center in opening

		//avoid repeating moves

		//Weighting of evaluations
		//Material = 87%
		//Pushed Pawns = 1%
		//Optimal Pieces = 3%
		//Pawn structure = 1%
		//Having bishop pair = 1%
		//Castled = 7%
		return (materialEval * 0.85) + (optimalPiecesEval * .06) +
				(pushedPawnsEval * .01) + (pawnStructureEval * .01) +
				(bishopPairEval * .01) + (castleEval * .06);
	}
	private static int pawnStructureEval(Board board) {
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
	private static double passedPawnsEval(Board board) {
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
					} else {
						numBlackPawns++;
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
	private static int bishopPairEval(Board board) {
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
	private static double optimalPiecesEval(Board board) {
		Tile[][] arr = board.getTileArray();
		double eval = 0;
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				Piece test = arr[row][column].getPiece();
				boolean isWhite = test.getColor().equals("w");
				if (test.getType().equals("N")) {
					if (isWhite) {
						eval += knightEvalsUniversal[row][column];
					} else {
						eval -= knightEvalsUniversal[row][column];
					}
				} else if (test.getType().equals("B")) {
					if (isWhite) {
						eval += bishopEvalsUniversal[row][column];
					} else {
						eval -= bishopEvalsUniversal[row][column];
					}
				} else if (test.getType().equals("P")) {
					if (isWhite) {
						eval += pawnEvalsWhite[row][column];
					} else {
						eval -= pawnEvalsBlack[row][column];
					}
				}
			}
		}
		return eval / 5;
	}
	private static int castleEval() {
		int ret = 0;
		boolean whiteCastled = false;
		boolean blackCastled = false;
		for (Move m : Game.activeMoveList) {
			if (m.getIsACastleExecution()) {
				if (m.getStartingPiece().getColor().equals("w")) {
					ret += 2;
					whiteCastled = true;
				} else {
					ret -= 2;
					blackCastled = true;
				}
			}
		}
		if (!whiteCastled) ret -= 0.5;
		if (!blackCastled) ret -= 0.5;
		return ret;
	}



}

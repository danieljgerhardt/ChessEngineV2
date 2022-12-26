package ChessMain;

public class BMPair {

	private Move move;
	private Board board;
	private double eval;

	public BMPair(Move m, Board b) {
		this.move = m;
		this.board = b;
		this.eval = Engine.evaluatePosition(this.board);
	}

	public double getEval() {
		return this.eval;
	}

	public Board getBoard() {
		return this.board;
	}

}

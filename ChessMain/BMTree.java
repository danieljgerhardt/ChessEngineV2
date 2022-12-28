package ChessMain;

import com.sun.source.tree.Tree;

import java.util.ArrayList;

public class BMTree {

	private TreeNode<BMPair> root;

	private static int maxDepth;

	public BMTree() {

	}

	private ArrayList<TreeNode<BMPair>> allNodes = new ArrayList<>();

	public void setRoot(TreeNode<BMPair> newRoot) {
		this.root = newRoot;
	}

	public TreeNode<BMPair> getRoot() {
		return this.root;
	}

	public int getDepth() {
		this.maxDepth = 0;
		getDepthRec(this.root);
		return maxDepth;
	}

	private void getDepthRec(TreeNode<BMPair> p) {
		for (TreeNode<BMPair> child : p.getChildren()) {
			if (child.getDepth() > maxDepth) maxDepth = child.getDepth();
			getDepthRec(child);
		}
	}

	public void clear() {
		this.root = null;
	}

	public TreeNode<BMPair> executeMiniMax() {
		return this.minimax(this.root);
	}

	private TreeNode<BMPair> minimax(TreeNode<BMPair> caller) {
		if (caller.getDepth() == Engine.DEPTH) {
			return caller;
		}
		double value;
		int index = -1, finalIndex = 0;
		if (!caller.isWhiteNode()) {
			//maximizer
			value = Double.NEGATIVE_INFINITY;
			for (TreeNode<BMPair> child : caller.getChildren()) {
				index++;
				double compare = minimax(child).getData().getEval();
				value = Math.max(value, compare);
				if (value == compare) {
					finalIndex = index;
				}
			}
		} else {
			//minimizer
			value = Double.POSITIVE_INFINITY;
			for (TreeNode<BMPair> child : caller.getChildren()) {
				index++;
				double compare = minimax(child).getData().getEval();
				value = Math.min(value, compare);
				if (value == compare) {
					finalIndex = index;
				}
			}
		}
		return caller.getChildren().get(finalIndex);
	}

	@Override
	public String toString() {
		String ret = this.root.toString();
		for (TreeNode<BMPair> child : this.root.getChildren()) {
			ret += "\n" + child.toString();
		}
		return ret;
	}
}

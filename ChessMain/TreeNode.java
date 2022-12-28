package ChessMain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> {

	private T data;
	private TreeNode<T> parent;
	private List<TreeNode<T>> children;
	private int depth;

	private boolean isRoot = false;

	public TreeNode(T data) {
		this.data = data;
		this.children = new LinkedList<>();
		this.depth = 0;
	}

	public TreeNode<T> addChild(T newData) {
		TreeNode<T> childNode = new TreeNode<>(newData);
		childNode.setParent(this);
		childNode.setDepth(this.depth + 1);
		this.children.add(childNode);
		return childNode;
	}

	public void setIsRoot() {
		this.isRoot = true;
	}

	public boolean getIsRoot() {
		return this.isRoot;
	}

	public void setParent(TreeNode p) {
		this.parent = p;
	}

	public void setDepth(int d) {
		this.depth = d;
	}

	public int getDepth() {
		return this.depth;
	}

	public TreeNode<T> getParent() {
		return this.parent;
	}

	public List<TreeNode<T>> getChildren() {
		return this.children;
	}

	public T getData() {
		return this.data;
	}

	public boolean hasChildren() {
		return !(this.children.size() == 0);
	}
	@Override
	public String toString() {
		BMPair ret = (BMPair) this.data;
		return ret.getMove().toString();
	}

	public TreeNode<BMPair> maxChildrenEval() {
		int index = 0;
		double max = 0;
		BMPair temp;
		if (this.hasChildren()) {
			for (int i = 0; i < this.getChildren().size(); i++) {
				temp = (BMPair) this.getChildren().get(i).getData();
				if (temp.getEval() > max) {
					max = temp.getEval();
					index = i;
				}
			}
			return (TreeNode<BMPair>) this.getChildren().get(index);
		}
		return null;
	}

	public TreeNode<BMPair> minChildrenEval() {
		int index = 0;
		double min = 0;
		BMPair temp;
		if (this.hasChildren()) {
			for (int i = 0; i < this.getChildren().size(); i++) {
				temp = (BMPair) this.getChildren().get(i).getData();
				if (temp.getEval() < min) {
					min = temp.getEval();
					index = i;
				}
			}
			return (TreeNode<BMPair>) this.getChildren().get(index);
		}
		return null;
	}

	public static TreeNode<BMPair> bestLine(TreeNode<BMPair> caller, int depth) {
		if (caller == null) return null;
		if (depth == 0) {
			while (caller.hasParent() && !caller.getParent().getIsRoot()) {
				caller = caller.getParent();
			}
			return caller;
		}
		return bestLine(caller.bestChildrenEval(Game.getOppositeColor(caller.getData().getMove().getStartingPiece().getColor())), depth - 1);
	}
	private boolean hasParent() {
		return (this.parent != null);
	}

	public boolean isWhiteNode() {
		BMPair test = (BMPair) this.getData();
		return test.getMove().getStartingPiece().getColor().equals("w");
	}

	public TreeNode<BMPair> bestChildrenEval(String color) {
		if (color.equals("w")) {
			return this.maxChildrenEval();
		} else {
			return this.minChildrenEval();
		}

	}

}
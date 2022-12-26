package ChessMain;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {

	private T data;
	private TreeNode<T> parent;
	private List<TreeNode<T>> children;
	private int depth;

	public TreeNode(T data) {
		this.data = data;
		this.children = new ArrayList<>();
		this.depth = 0;
	}

	public TreeNode<T> addChild(T newData) {
		TreeNode<T> childNode = new TreeNode<>(newData);
		childNode.setParent(this);
		childNode.setDepth(this.depth + 1);
		this.children.add(childNode);
		return childNode;
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

}
package ChessMain;

public class BMTree {

	private TreeNode<BMPair> root;

	private static int maxDepth;

	public BMTree() {

	}

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

}

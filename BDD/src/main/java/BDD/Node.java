package BDD;

public class Node {
    private final String value;
    private final int level;
    private Node[] ancestors;
    private Node leftChild;
    private Node rightChild;

    public Node(Node parent, String value, int level) {
        this.value = value;
        this.level = level;
        this.ancestors = new Node[]{parent};
    }

    public String getValue() {
        return value;
    }

    public int getLevel() {
        return level;
    }

    public Node[] getAncestors() {
        return ancestors;
    }

    public void setAncestors(Node[] grandancestor, Node parent) {
        int numParents = ancestors.length;
        int numGrandparents = grandancestor.length;
        Node[] newParents = new Node[numParents + numGrandparents];
        int index = 0;

        for (Node node : ancestors) {
            newParents[index] = node;
            index++;
        }

        for (Node grandparent : grandancestor) {
            newParents[index] = grandparent;
            index++;

            if (parent != null && grandparent != null) {
                if (grandparent.getLeftChild().equals(parent)) {
                    grandparent.setLeftChild(this);
                } else {
                    grandparent.setRightChild(this);
                }
            }
        }

        ancestors = newParents;
    }

    public Node removeAncestor(Node ancestor) {
        for (int i = 0; i < ancestors.length; i++) {
            if (ancestors[i] != null && ancestors[i].equals(ancestor)) {
                ancestors[i] = null;
                return ancestor;
            }
        }
        return null;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

}

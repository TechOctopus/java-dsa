package BDD;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class BDD {
    private String order;
    private String[] varTypesForCreation;
    private Node[] previousNode = new Node[1];
    private Node[] currentNode = new Node[2];
    private Node root = null;
    private double numOfNodesAfterReduce;
    private double numOfNodesBeforeReduce;

    public String getOrder() {
        return order;
    }

    private void calculateMaxNumberOfNodes() {
        this.numOfNodesBeforeReduce = Math.pow(2, this.order.length() + 1) - 1;
    }

    private void increaseNumOfNodesAfterReduce() {
        if (this.getNumOfNodesAfterReduce() == 0) this.numOfNodesAfterReduce = 1;
        else this.numOfNodesAfterReduce += 2;
    }

    private void decreaseNumOfNodesAfterReduce() {
        this.numOfNodesAfterReduce -= 1;
    }

    public double getNumOfNodesAfterReduce() {
        return numOfNodesAfterReduce;
    }

    public double getNumOfNodesBeforeReduce() {
        return numOfNodesBeforeReduce;
    }

    private void deleteNode(Node nodeToDelete, int index) {
        Node[] parentNodes = this.currentNode[index].getAncestors();

        for (Node parentNode : parentNodes) {
            if (parentNode != null) {
                if (parentNode.getLeftChild().equals(this.currentNode[index]))
                    parentNode.setLeftChild(nodeToDelete);
                else
                    parentNode.setRightChild(nodeToDelete);
                nodeToDelete.setAncestors(parentNodes, null);
            }
        }

        this.currentNode[index] = null;
    }

    private String removeLetter(String expression, String letter) {
        while (expression.contains(letter)) {
            expression = expression.replace(letter, "");
        }
        return expression;
    }

    private int hash(String key, int tableSize) {
        return Math.abs(key.hashCode() % tableSize);
    }

    private void handleReduction() {
        Node[] hashTable = new Node[this.currentNode.length * 2];

        for (int i = 0; i < this.currentNode.length; i++) {
            if (this.currentNode[i] != null) {
                String value = this.currentNode[i].getValue();
                int pos = this.hash(value, hashTable.length);

                while (hashTable[pos] != null) {
                    if (hashTable[pos].getValue().equals(value)) break;
                    if (++pos >= hashTable.length) pos = 0;
                }

                if (hashTable[pos] != null) {
                    this.deleteNode(hashTable[pos], i);
                    if (hashTable[pos].getLevel() != this.varTypesForCreation.length) {
                        this.decreaseNumOfNodesAfterReduce();
                    }
                } else {
                    hashTable[pos] = this.currentNode[i];
                }
            }
        }

        for (int i = 0; i < this.previousNode.length; i++) {
            if (this.previousNode[i] != null) {
                Node lowChild = this.previousNode[i].getLeftChild();
                Node[] parents = this.previousNode[i].getAncestors();

                if (lowChild.equals(this.previousNode[i].getRightChild())) {
                    this.decreaseNumOfNodesAfterReduce();
                    if (this.previousNode[i].equals(this.root)) {
                        this.root = lowChild;
                    }
                    lowChild.setAncestors(parents, this.previousNode[i]);
                    this.previousNode[i] = lowChild.removeAncestor(this.previousNode[i]);
                }
            }
        }
    }

    private Node shannonExtension(Node parent, String expression, int varPos, boolean flag) {
        if (varPos > this.varTypesForCreation.length) {
            String value = expression.equals("1") ? "1" : "0";
            return new Node(parent, value, this.varTypesForCreation.length);
        }

        String variable = this.varTypesForCreation[varPos - 1];
        String newExp = this.newExpression(expression, variable, flag);

        return new Node(parent, newExp, varPos);
    }

    private String newExpression(String expression, String upper, boolean nullOrZero) {

        String lower = upper.toLowerCase();

        if (expression.equals("1") || expression.equals("0")) return expression;

        String[] parts = expression.contains("+") ? expression.split("\\+") : new String[]{expression};
        String[] hash = new String[parts.length * 2];

        for (String part : parts)
            if ((nullOrZero && part.equals(lower)) || (!nullOrZero && part.equals(upper))) return "1";

        if ((nullOrZero && expression.equals(lower)) || (!nullOrZero && expression.equals(upper))) return "1";
        if ((!nullOrZero && expression.equals(lower)) || (nullOrZero && expression.equals(upper))) return "0";


        int temp = 0, count = 0;

        for (int i = 0; i < parts.length; i++) {
            if (!parts[i].isBlank()) {
                if (parts[i].contains(nullOrZero ? upper : lower)) {
                    if (parts[i].contains(lower) && parts[i].contains(upper)) {
                        if (++count == parts.length) return "0";
                    }
                    parts[i] = "";
                    temp++;
                } else if (parts[i].contains(nullOrZero ? lower : upper)) {
                    parts[i] = this.removeLetter(parts[i], nullOrZero ? lower : upper);
                    if (parts[i].isBlank()) temp++;
                }
            }
        }

        temp = parts.length - temp - 1;

        for (int i = 0; i < parts.length; i++) {
            if (!parts[i].isBlank()) {
                int pos = this.hash(parts[i], hash.length);
                while (hash[pos] != null) {
                    if (hash[pos].equals(parts[i])) break;
                    if (++pos >= hash.length) pos = 0;
                }
                if (hash[pos] != null) {
                    parts[i] = "";
                    temp--;
                } else hash[pos] = parts[i];
            }
        }

        lower = expression;

        StringBuilder expressionBuilder = new StringBuilder();
        for (String part : parts) {
            if (!part.isBlank()) {
                expressionBuilder.append(part);
                if (temp-- > 0) expressionBuilder.append("+");
            }
        }

        expression = expressionBuilder.toString();
        if (expression.isBlank()) {
            if (lower.contains(upper)) expression = nullOrZero ? "0" : "1";
            else expression = nullOrZero ? "1" : "0";
        }

        return expression;
    }

    @SuppressWarnings("Duplicates")
    private String generateOrder(String bfunction) {
        char[] chars = bfunction.toCharArray();

        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isLetter(c)) {
                sb.append(Character.toUpperCase(c));
            }
        }

        TreeSet<Character> set = new TreeSet<>();
        for (int i = 0; i < sb.length(); i++) {
            set.add(sb.charAt(i));
        }

        StringBuilder result = new StringBuilder();
        for (Character c : set) {
            result.append(c);
        }

        return result.toString();
    }

    private long getNumberOfCombinations(String order) {
        long factorial = 1;
        for (int i = 1; i <= order.length(); i++) {
            factorial *= i;
        }
        return factorial;
    }

    public String getCombination(String str, long index) {
        int n = str.length();
        long factorial = this.getNumberOfCombinations(str);
        if (index < 0 || index >= factorial) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }
        StringBuilder sb = new StringBuilder(n);
        List<Character> chars = new ArrayList<>();
        for (char c : str.toCharArray()) {
            chars.add(c);
        }
        for (int i = 0; i < n; i++) {
            factorial /= (n - i);
            int j = Math.toIntExact((index / factorial));
            char c = chars.remove(j);
            sb.append(c);
            index -= j * factorial;
        }
        return sb.toString();
    }

    public void create(String bfunction, String order) {
        if (this.root != null) this.clear();

        this.order = order.toUpperCase();

        this.previousNode[0] = new Node(null, bfunction, 0);
        this.root = this.previousNode[0];
        this.increaseNumOfNodesAfterReduce();


        this.varTypesForCreation = this.order.split("");
        for (int i = 1; i <= varTypesForCreation.length; i++) {
            int num = 0;

            for (Node node : this.previousNode) {
                if (node != null) {

                    Node low = this.shannonExtension(node, node.getValue(), i, true);
                    Node high = this.shannonExtension(node, node.getValue(), i, false);

                    node.setLeftChild(this.currentNode[num++] = low);
                    node.setRightChild(this.currentNode[num++] = high);
                    if (i != this.varTypesForCreation.length) this.increaseNumOfNodesAfterReduce();
                }
            }

            this.handleReduction();
            this.previousNode = this.currentNode;
            this.currentNode = new Node[this.previousNode.length * 2];
        }

        this.calculateMaxNumberOfNodes();
    }

    public void createWithBeastOrder(String bfunction) {
        String order = generateOrder(bfunction);
        String beastOrder = "";
        double minNumOfNodesAfterReduce = Double.MAX_VALUE;

        if (order.length() > 20) throw new IllegalArgumentException("Too many variables");
        else if (order.length() > 6) {
            Random random = new Random();
            for (int i = 0; i < order.length(); i++) {
                String combination = getCombination(order, random.nextLong(getNumberOfCombinations(order)));
                this.create(bfunction, combination);
                if (minNumOfNodesAfterReduce > this.numOfNodesAfterReduce) {
                    minNumOfNodesAfterReduce = this.numOfNodesAfterReduce;
                    beastOrder = this.order;
                }
            }
        } else {
            for (long i = 0; i < getNumberOfCombinations(order); i++) {
                this.create(bfunction, getCombination(order, i));
                if (minNumOfNodesAfterReduce > this.numOfNodesAfterReduce) {
                    minNumOfNodesAfterReduce = this.numOfNodesAfterReduce;
                    beastOrder = this.order;
                }
            }
        }

        this.create(bfunction, beastOrder);
    }

    public String use(String inputs) {
        Node currentNode = this.root;
        String[] parts = inputs.split("");
        int currentLevel = currentNode.getLevel() - 1;

        for (int i = currentLevel; i < this.order.length(); i++) {
            int nodeLevel = currentNode.getLevel();
            if (i == nodeLevel) {
                currentNode = parts[nodeLevel].equals("0") ?
                        currentNode.getLeftChild() :
                        currentNode.getRightChild();
            }
        }

        return currentNode.getValue();
    }

    private void clear() {
        this.root = null;
        this.numOfNodesAfterReduce = 0;
        this.numOfNodesBeforeReduce = 0;
        this.previousNode = new Node[1];
        this.currentNode = new Node[2];
        this.varTypesForCreation = null;
        this.order = "";
    }
}

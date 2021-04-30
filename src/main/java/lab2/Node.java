package lab2;

import java.io.Serializable;
import java.util.StringJoiner;

public class Node implements Serializable {
    private int data;
    private char character;
    private Node leftNode;
    private Node rightNode;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Node.class.getSimpleName() + "[", "]")
                .add("data=" + data)
                .add("character=" + character)
                .add("leftNode=" + leftNode)
                .add("rightNode=" + rightNode)
                .toString();
    }

}

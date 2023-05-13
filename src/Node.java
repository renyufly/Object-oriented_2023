public class Node {
    private int id;   //人员ID
    private int weight;  //权值
    private Node node;  //子节点

    public Node(int id) {
        this.id = id;
        this.weight = 0;
        this.node = null;
    }

    public Node(int id, int weight) {
        this.id = id;
        this.weight = weight;
        this.node = null;
    }

    public int getId() {
        return this.id;
    }

    public int getWeight() {
        return this.weight;
    }

}

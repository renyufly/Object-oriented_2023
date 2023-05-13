import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Graph {   //带权无向图---邻接表
    private ArrayList<Integer> nodes;     //所有结点Id集合
    private HashMap<Integer, LinkedList<Node>> adjacencyMatrix;  //邻接表最左侧所有结点,每个结点有个链表 (Id-链表)
    private HashMap<Integer, LinkedList<Integer>> adjacencyIndex;
    private ArrayList<Node> nodeArray;   //所有结点集合

    public Graph() {
        this.nodes = new ArrayList<>();
        this.adjacencyMatrix = new HashMap<>();
        adjacencyIndex = new HashMap<>();
        this.nodeArray = new ArrayList<>();
    }

    public void initialSet() {
        for (Integer keyId : this.adjacencyIndex.keySet()) {
            for (Integer id : this.nodes) {
                if (!adjacencyIndex.get(keyId).contains(id)) {
                    Node newnode = new Node(id, 609090909);
                    adjacencyMatrix.get(keyId).add(newnode);
                    adjacencyIndex.get(keyId).add(id);
                }
            }
        }
    }

    public void reset() {
        for (Integer keyId : this.adjacencyIndex.keySet()) {
            for (int i = 0; i < adjacencyMatrix.get(keyId).size(); i++) {
                if (adjacencyMatrix.get(keyId).get(i).getWeight() == 609090909) {
                    adjacencyMatrix.get(keyId).remove(i);
                    adjacencyIndex.get(keyId).remove(i);
                    i--;
                }
            }
        }
    }

    public void addNode(int id) {  //在邻接表加入新结点
        int flag = 0;
        for (int i = 0; i < nodes.size(); i++) {
            if (adjacencyMatrix.get(nodes.get(i)).getFirst().getId() == id) {
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            Node newNode = new Node(id);
            nodes.add(id);
            LinkedList<Node> newlink = new LinkedList<>();
            newlink.add(newNode);
            adjacencyMatrix.put(id, newlink);
            LinkedList<Integer> link = new LinkedList<>();
            link.add(id);
            adjacencyIndex.put(id, link);
            nodeArray.add(newNode);
        }
    }

    public void addEdge(int fromId, int toId, int weight) {   //添加带权有向边
        Node newnode = new Node(toId, weight);
        adjacencyMatrix.get(fromId).add(newnode);
        adjacencyIndex.get(fromId).add(toId);
    }

    public ArrayList<Integer> getNodesId() {
        return this.nodes;
    }

    public int getEdgeWeight(int fromId, int toId) {
        for (int i = 0; i < adjacencyMatrix.get(fromId).size(); i++) {
            if (adjacencyMatrix.get(fromId).get(i).getId() == toId) {
                return adjacencyMatrix.get(fromId).get(i).getWeight();
            }
        }
        return 0;
    }

    public void deleteEdge(int fromId, int toId) {
        for (int i = 0; i < adjacencyMatrix.get(fromId).size(); i++) {
            if (adjacencyMatrix.get(fromId).get(i).getId() == toId) {
                adjacencyMatrix.get(fromId).remove(i);
                adjacencyIndex.get(fromId).remove(i);
                return;
            }
        }
    }

}

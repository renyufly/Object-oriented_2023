import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DisjointSet {   //并查集算法——检查图的连通性

    private HashMap<Integer, Integer> pre;    //存储结点的父子关系
    private HashMap<Integer, Integer> rank;     // 在“按秩合并”里用到

    private final HashSet<HashMap<Integer, Integer>> backup;  //对应边关系

    public DisjointSet() {
        this.pre = new HashMap<>(4096);
        this.rank = new HashMap<>(4096);
        this.backup = new HashSet<>();
    }

    public void add(int id) {
        if (!pre.containsKey(id)) {
            pre.put(id, id);
            rank.put(id, 0);
        }
    }

    public int find(int id) {
        int rep = id;         //代表元素
        while (rep != pre.get(rep)) {
            rep = pre.get(rep);
        }

        int now = id;
        while (now != rep) {
            int fa = pre.get(now);
            pre.put(now, rep);
            now = fa;
        }
        return rep;
    }

    public void addBackUp(int id1, int id2) {
        int curId1 = id1;
        int curId2 = id2;
        if (id1 > id2) {
            int tmp = curId1;
            curId1 = curId2;
            curId2 = tmp;
        }
        //
        HashMap edge = new HashMap<>();
        edge.put(curId1, curId2);
        backup.add(edge);
        //backup.add(new (curId1, curId2));
    }

    public int merge(int id1, int id2) {
        int fa1 = find(id1);
        int fa2 = find(id2);
        if (fa1 == fa2) {
            return -1;
        }
        int rank1 = rank.get(fa1);
        int rank2 = rank.get(fa2);
        if (rank1 < rank2) {
            pre.put(fa1, fa2);
        } else {
            if (rank1 == rank2) {
                rank.put(fa1, rank1 + 1);
            }
            pre.put(fa2, fa1);
        }
        return 0;
    }

    public int remove(int id1, int id2, int size, ArrayList<Integer> peopleId) {
        for (Integer keyId : peopleId) {
            pre.put(keyId, keyId);
            rank.put(keyId, 0);
        }
        int blocksum = size;
        int curId1 = id1;
        int curId2 = id2;
        if (id1 > id2) {
            int tmp = curId1;
            curId1 = curId2;
            curId2 = tmp;
        }
        HashMap edge = new HashMap<>();
        edge.put(curId1, curId2);
        backup.remove(edge);
        //backup.remove(new Pair<>(curId1, curId2));
        for (HashMap<Integer, Integer> pair : backup) {
            for (Integer key: pair.keySet()) {
                if (merge(key, pair.get(key)) == 0) {
                    blocksum--;
                }
            }
        }
        return blocksum;
    }

    public HashSet<HashMap<Integer, Integer>> getBackup() {
        return this.backup;
    }
}

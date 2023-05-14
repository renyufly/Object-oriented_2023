import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Qts {

    private HashMap<Integer, Integer> degree;  //每个结点度数。前ID，后度数
    private ArrayList<Integer> startEdge;    //每条边的起点
    private ArrayList<Integer> endEdge;     //每条边的终点
    private HashMap<Integer, Integer> visitTime;  //节点被访问的时间. 前ID，后对应Id的时间
    private HashMap<Integer, ArrayList<Integer>> enode;   //无向图中每个节点的邻接节点集合

    public Qts(HashMap<Integer, Integer> degree, HashMap<Integer, Integer> visitTime,
               HashMap<Integer, ArrayList<Integer>> enode,
               HashSet<HashMap<Integer, Integer>> backup) {
        this.degree = degree;
        this.visitTime = visitTime;
        this.enode = enode;
        this.startEdge = new ArrayList<>();
        this.endEdge = new ArrayList<>();
        for (HashMap<Integer, Integer> pair : backup) {
            for (Integer key: pair.keySet()) {
                this.startEdge.add(key);
                this.endEdge.add(pair.get(key));
            }
        }
    }

    public int getCnt() {
        int cnt = 0;
        for (Integer key : degree.keySet()) {   //new
            ArrayList<Integer> arrayList = new ArrayList<>();
            enode.put(key, arrayList);
        }
        visitTime = new HashMap<>();  //相应也要更新
        for (int i = startEdge.size() - 1; i >= 0; i--) {
            int u = startEdge.get(i);
            int v = endEdge.get(i);
            if (degree.get(u) > degree.get(v)) {
                int tmp = u;
                u = v;
                v = tmp;
            } else if (degree.get(u) == degree.get(v) && (u > v)) {
                int tmp = u;
                u = v;
                v = tmp;
            }
            if (!enode.containsKey(u)) {
                ArrayList<Integer> arrayList = new ArrayList<Integer>();
                enode.put(u, arrayList);
            }
            enode.get(u).add(v);
            if (!enode.containsKey(v)) {
                ArrayList<Integer> arrayList = new ArrayList<>();
                enode.put(v, arrayList);
            }
        }

        int time = 1;
        for (Integer degKey : degree.keySet()) {
            for (int k = 0; k < enode.get(degKey).size(); k++) {
                int v = enode.get(degKey).get(k);
                visitTime.put(v, time);
            }
            for (int p = 0; p < enode.get(degKey).size(); p++) {
                int v = enode.get(degKey).get(p);
                for (int q = 0; q < enode.get(v).size(); q++) {
                    int w = enode.get(v).get(q);
                    if (visitTime.get(w) != null) {
                        if (visitTime.get(w) == time) {
                            cnt++;
                        }
                    }
                }
            }
            time++;
        }
        return cnt;
    }
}

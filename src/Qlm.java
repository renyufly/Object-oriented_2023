import com.oocourse.spec3.main.Person;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

public class Qlm {
    private static final int INF = 609090909;
    private HashMap<Integer, Integer> dis;   //记录节点到起始节点的最短距离 (Id-距离)
    private HashMap<Integer, Integer> pre;   //记录最短路径下该节点的前驱节点 (Id-前驱结点Id)
    private HashMap<Integer, Boolean> vis;   //标记节点是否已经在队列中 (Id-判断)
    // private Graph graph;    //图--邻接表
    private ArrayList<Integer> peopleId;     //记录人的Id
    private HashMap<Integer, Person> people;

    public Qlm(ArrayList<Integer> peopleId, HashMap<Integer, Person> people) {
        this.dis = new HashMap<>();
        this.pre = new HashMap<>();
        this.vis = new HashMap<>();
        this.peopleId = peopleId;
        this.people = people;
    }

    public void spfa(int s) {   //最短路径计算
        for (Integer nodeId : this.peopleId) {
            dis.put(nodeId, INF);
            pre.put(nodeId, nodeId);
            vis.put(nodeId, false);
        }

        Deque<Integer> q = new ArrayDeque<>();  //双端队列(两端都可进出)
        q.offer(s);    //在双端队列中插入元素
        dis.put(s, 0);

        int cnt = 1;
        int sum = 0;   // cnt是队列元素总数
        while (!q.isEmpty()) {
            int u = q.poll(); //poll返回给定双端队列的第一个元素(并删除)
            if (dis.get(u) * cnt > sum) {
                q.offer(u);   //插入u （u是当前结点，s是目标起始结点）
                continue;
            }
            vis.put(u, false);
            cnt--;
            sum -= dis.get(u);
            for (Integer nodeId : this.peopleId) {
                if (dis.get(u) + ((MyPerson)this.people.get(nodeId)).queryIdValue(u)
                        < dis.get(nodeId)) {
                    int tmp = dis.get(u) + ((MyPerson)this.people.get(nodeId)).queryIdValue(u);
                    dis.put(nodeId, tmp);
                    if (u != s) {
                        pre.put(nodeId, u);
                    }
                    if (!vis.get(nodeId)) {
                        vis.put(nodeId, true);
                        cnt++;
                        sum += dis.get(nodeId);
                        if (q.isEmpty() || dis.get(nodeId) >
                                dis.get(q.peek())) { //peek返回双端队列的头部(不删)
                            q.offer(nodeId);  //插入尾部
                        } else {
                            q.offerFirst(nodeId);  //插入头部
                        }
                    }
                }
            }
        }
    }

    public int queryLm(int id) {
        int ans = 609090909;
        spfa(id);
        // 计算每个起始节点到其他节点的最短路径
        for (Integer nodeId : this.peopleId) {
            if (pre.get(nodeId) != nodeId) {
                ans = Math.min(ans, ((MyPerson)this.people.get(id)).queryIdValue(nodeId)
                        + dis.get(nodeId));
            }
        }
        for (Integer nodeId : this.peopleId) {
            for (Integer jid : this.peopleId) {
                if (nodeId != id && jid != id && findx(nodeId) != findx(jid)) {
                    ans = Math.min(ans, dis.get(nodeId) + dis.get(jid)
                            + ((MyPerson)this.people.get(nodeId)).queryIdValue(jid));
                }
            }
        }

        return ans;
    }

    public int findx(int x) {   //查找节点所属的集合（用于判断是否形成环）
        if (pre.get(x) == x) {
            return x;
        } else {
            pre.put(x, findx(pre.get(x)));
            return pre.get(x);
        }
    }

}

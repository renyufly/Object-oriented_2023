import java.util.ArrayList;
import java.util.HashMap;

public class Dceok {

    private int limit;
    private HashMap<Integer, Integer> beforeEmoji = new HashMap<>();
    private HashMap<Integer, Integer> afterEmoji = new HashMap<>();
    private HashMap<Integer, Integer> beforeMessage = new HashMap<>();
    private HashMap<Integer, Integer> afterMessage = new HashMap<>();
    private int result;

    public Dceok(int limit, ArrayList<HashMap<Integer, Integer>> beforeData,
                 ArrayList<HashMap<Integer, Integer>> afterData, int result) {
        this.limit = limit;
        beforeEmoji = beforeData.get(0);
        afterEmoji = afterData.get(0);
        beforeMessage = beforeData.get(1);
        afterMessage = afterData.get(1);
        this.result = result;
    }

    public int dceOkTest() {
        for (Integer emojiId : beforeEmoji.keySet()) {
            if (beforeEmoji.get(emojiId) >= limit && !afterEmoji.containsKey(emojiId)) {
                return 1;
            }
        }
        for (Integer emojiId : afterEmoji.keySet()) {
            int flag = 0;
            for (Integer eid : beforeEmoji.keySet()) {
                if ((emojiId == eid) && afterEmoji.get(emojiId).equals(beforeEmoji.get(eid))) {
                    flag = 1; //两个Integer型不能直接==，要么转int要么equals
                    break;
                }
            }
            if (flag == 0) {
                return 2;
            }
        }
        int cnt = 0;
        for (Integer eid : beforeEmoji.keySet()) {
            if (beforeEmoji.get(eid) >= limit) {
                cnt++;
            }
        }
        if (cnt != afterEmoji.size()) {
            return 3;
        } // 4不出现
        for (Integer messId : beforeMessage.keySet()) {
            if (beforeMessage.get(messId) != null &&
                    afterEmoji.containsKey(beforeMessage.get(messId))) {
                if (!afterMessage.containsKey(messId) || afterMessage.get(messId)
                        != beforeMessage.get(messId)) {
                    return 5;
                }
            }
        }
        for (Integer messId : beforeMessage.keySet()) {
            if (beforeMessage.get(messId) == null) {
                if (!afterMessage.containsKey(messId) || afterMessage.get(messId)
                        != beforeMessage.get(messId)) {
                    return 6;
                }
            }
        }
        int messcnt = beforeMessage.size();
        for (Integer mid : beforeMessage.keySet()) {
            if (beforeMessage.get(mid) != null) {
                if (!afterEmoji.containsKey(beforeMessage.get(mid))) {
                    messcnt--;
                }
            }
        }
        if (afterMessage.size() != messcnt) {
            return 7;
        }
        if (result != afterEmoji.size()) {
            return 8;
        }
        return 0;
    }

}

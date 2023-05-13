import java.util.HashMap;

public class ExceptionCounter {   //异常计数类
    private int count;
    private HashMap<Integer, Integer> peopleCount;   //人员Id
    private HashMap<Integer, Integer> groupCount;   //小组Id
    private HashMap<Integer, Integer> messageCount;  //消息Id
    private HashMap<Integer, Integer> emojiCount;    //emoji的Id

    public ExceptionCounter() {
        count = 0;
        peopleCount = new HashMap<>();
        groupCount = new HashMap<>();
        messageCount = new HashMap<>();
        emojiCount = new HashMap<>();
    }

    // 计数（++）
    public void incrementCount() {   //对应异常计数
        count++;
    }

    public void increPeopleCount(int id) {  //对应触发人Id计数
        if (peopleCount.containsKey(id)) {
            int tmp = peopleCount.get(id);
            tmp++;
            peopleCount.put(id, tmp);
        } else {
            peopleCount.put(id, 1);
        }
    }

    public void increGroupCount(int id) {
        if (groupCount.containsKey(id)) {
            int tmp = groupCount.get(id);
            tmp++;
            groupCount.put(id, tmp);
        } else {
            groupCount.put(id, 1);
        }
    }

    public void increMessageCount(int id) {
        if (messageCount.containsKey(id)) {
            int tmp = messageCount.get(id);
            tmp++;
            messageCount.put(id, tmp);
        } else {
            messageCount.put(id, 1);
        }
    }

    public void increEmojiCount(int id) {
        if (emojiCount.containsKey(id)) {
            int tmp = emojiCount.get(id);
            tmp++;
            emojiCount.put(id, tmp);
        } else {
            emojiCount.put(id, 1);
        }
    }

    // 得到计数数据
    public int getCount() {
        return count;
    }

    public int getPeopleCount(int id) {
        return peopleCount.get(id);
    }

    public int getGroupCount(int id) {
        return groupCount.get(id);
    }

    public int getMessageCount(int id) {
        return messageCount.get(id);
    }

    public int getEmojiCount(int id) {
        return emojiCount.get(id);
    }

}

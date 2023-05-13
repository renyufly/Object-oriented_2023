import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MyPerson implements Person {
    private int id;
    private String name;
    private int age;
    private HashMap<Integer, Person> acquaintance;    // 熟人数组  [前为id]
    private HashMap<Integer, Integer> value;   // 亲密值
    private int money;            // 钱数，初始值为 0
    private int socialValue;      // 社交值，初始值为 0
    private LinkedList<Message> messages;  //链表实现

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.acquaintance = new HashMap<>();
        this.value = new HashMap<>();
        this.money = 0;
        this.socialValue = 0;
        this.messages = new LinkedList<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getAge() {
        return this.age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Person) {
            return (((Person) obj).getId() == id);
        }
        return false;
    }

    @Override
    public boolean isLinked(Person person) {
        if (person.getId() == this.id) {
            return true;
        }
        if (acquaintance.containsKey(person.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public int queryValue(Person person) {
        if (value.containsKey(person.getId())) {
            return this.value.get(person.getId());
        }
        return 0;
    }

    @Override
    public int compareTo(Person p2) {
        return name.compareTo(p2.getName());
    }

    @Override
    public void addSocialValue(int num) {
        int tmp = this.socialValue;
        this.socialValue = tmp + num;
    }

    @Override
    public int getSocialValue() {
        return this.socialValue;
    }

    @Override
    public List<Message> getMessages() {
        return this.messages;
    }

    @Override
    public List<Message> getReceivedMessages() {  //返回第0-4条消息（最新的）
        ArrayList result = new ArrayList<>();
        int length = 5;
        if (this.messages.size() < 5) {
            length = this.messages.size();
        }
        for (int i = 0; i < length; i++) {
            result.add(this.messages.get(i));   //按顺序添加
        }
        return result;
    }

    @Override
    public void addMoney(int num) {
        int tmp = this.money;
        this.money = tmp + num;
    }

    @Override
    public int getMoney() {
        return this.money;
    }

    // 自定义方法
    public void addAcquain(Person person) {   //添加熟人
        this.acquaintance.put(person.getId(), person);
    }

    public void addValue(int id, int newValue) {    // 添加\修改 亲密值（与熟人组顺序对应）
        this.value.put(id, newValue);
    }

    public void removeRelation(int id) {
        this.acquaintance.remove(id);
        this.value.remove(id);
    }

    public void addFirstMessage(Message message) {  //消息插入头部
        this.messages.addFirst(message);
    }

    public HashMap<Integer, Integer> getValue() {
        return this.value;
    }

    public void deleteNotice() {
        for (int i = 0; i < this.messages.size(); i++) {
            if (this.messages.get(i) instanceof NoticeMessage) {
                this.messages.remove(i);
                i--;
            }
        }
    }

}

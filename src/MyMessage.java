import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Person;

public class MyMessage implements Message {
    private int id;            //独一无二ID
    private int socialValue;   //消息的社交值
    private int type;         //消息的种类，有 0 和 1 两个取值
    private Person person1;  //消息的发送者(非null)
    private Person person2;  //消息的接收者  (nullable)
    private Group group;     //消息的接收组  (nullable)

    public MyMessage(int messageId, int messageSocialValue, Person messagePerson1,
                     Person messagePerson2) {
        this.id = messageId;
        this.socialValue = messageSocialValue;
        this.person1 = messagePerson1;
        this.person2 = messagePerson2;
        this.type = 0;
        this.group = null;
    }  //此构造的Group为null  (type-0-有接收者，无group)

    public MyMessage(int messageId, int messageSocialValue, Person messagePerson1,
                     Group messageGroup) {
        this.id = messageId;
        this.socialValue = messageSocialValue;
        this.person1 = messagePerson1;
        this.group = messageGroup;
        this.type = 1;
        this.person2 = null;
    }  //  type-1-有接收组

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getSocialValue() {
        return this.socialValue;
    }

    @Override
    public Person getPerson1() {
        return this.person1;
    }

    @Override
    public Person getPerson2() {
        return this.person2;
    }

    @Override
    public Group getGroup() {
        return this.group;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Message) {
            return (((Message) obj).getId() == id);
        }
        return false;
    }

}

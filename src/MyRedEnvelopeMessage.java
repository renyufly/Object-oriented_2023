import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;

public class MyRedEnvelopeMessage extends MyMessage implements RedEnvelopeMessage {
    //super来访问父类的成员方法和变量
    private int money;    // 红包金额

    public MyRedEnvelopeMessage(int messageId, int luckyMoney, Person messagePerson1,
                                Person messagePerson2) {
        super(messageId, luckyMoney * 5, messagePerson1, messagePerson2);
        this.money = luckyMoney;
    }  // type-0: 无接收组

    public MyRedEnvelopeMessage(int messageId, int luckyMoney, Person messagePerson1,
                                Group messageGroup) {
        super(messageId, luckyMoney * 5, messagePerson1, messageGroup);
        this.money = luckyMoney;
    }

    @Override
    public int getMoney() {
        return this.money;
    }

}
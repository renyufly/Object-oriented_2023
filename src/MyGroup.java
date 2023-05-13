import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Person;

import java.util.HashMap;

public class MyGroup implements Group {
    private int id;     //对当前Network中所有Group对象实例而言独一无二的 id
    private HashMap<Integer, Person> people;

    public MyGroup(int id) {
        this.id = id;
        this.people = new HashMap<>();
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Group) {
            return (((Group) obj).getId() == id);
        }
        return false;
    }

    @Override
    public void addPerson(Person person) {
        if (!hasPerson(person)) {
            this.people.put(person.getId(), person);
        }
    }

    @Override
    public boolean hasPerson(Person person) {
        if (this.people.containsKey(person.getId())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getValueSum() {
        int sum = 0;
        for (Integer keyId : this.people.keySet()) {
            for (Integer acKey : ((MyPerson) this.people.get(keyId)).getValue().keySet()) {
                if (this.people.containsKey(acKey)) {   //熟人也得在群里
                    sum += ((MyPerson) this.people.get(keyId)).getValue().get(acKey);
                }
            }
        }
        return sum;
    }

    @Override
    public int getAgeMean() {   //求年龄平均值
        if (people.size() == 0) {
            return 0;
        }
        int ageSum = 0;
        for (Integer keyId : this.people.keySet()) {
            ageSum += this.people.get(keyId).getAge();
        }
        int length = this.people.size();
        return (ageSum / length);
    }

    @Override
    public int getAgeVar() {    //求年龄方差[variance]
        int sum = 0;
        int length = this.people.size();
        if (length == 0) {
            return 0;
        }
        int mean = getAgeMean();
        for (Integer keyId : this.people.keySet()) {
            sum += (this.people.get(keyId).getAge() - mean)
                    * (this.people.get(keyId).getAge() - mean);
        }
        return (sum / length);
    }

    @Override
    public void delPerson(Person person) {  //删组里人
        if (hasPerson(person)) {
            this.people.remove(person.getId());
        }
    }

    @Override
    public int getSize() {   //得组总人数
        return this.people.size();
    }

    //自定义方法
    public void addGroupSocialVa(int num) {  //组里每个人加社交值
        for (Integer key : this.people.keySet()) {
            this.people.get(key).addSocialValue(num);
        }
    }

    public void modifyGroupMoney(int nid, int money) {
        for (Integer keyId : this.people.keySet()) {
            if (keyId != nid) {
                this.people.get(keyId).addMoney(money);
            }
        }
    }

}
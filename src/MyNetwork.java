import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;
import com.oocourse.spec3.exceptions.EqualGroupIdException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.GroupIdNotFoundException;
import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.PathNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.RedEnvelopeMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyNetwork implements Network {
    private HashMap<Integer, Person> people;
    private ArrayList<Integer> peopleId;     //记录人的Id
    private ArrayList<Person> personArray;    //装网络中人的数组
    private HashMap<Integer, Group> groups;
    private HashMap<Integer, Message> messages;  //键为Id
    private HashMap<Integer, Integer> emojiIdList;   //emoji-Id (键为id，存也是id)
    private HashMap<Integer, Integer> emojiHeatList;  //(键为id,值为对应emojiID的热度)
    private ArrayList<Integer> emojiId;
    private Graph peopleGraph;
    private HashMap<Integer, Integer> degree;  //每个结点度数。前ID，后度数
    private HashMap<Integer, Integer> visitTime;  //节点被访问的时间. 前ID，后对应Id的时间
    private HashMap<Integer, ArrayList<Integer>> enode;   //无向图中每个节点的邻接节点集合
    private int blocknum;    //Block_sum (qbs)
    private DisjointSet disjointSet = new DisjointSet();  //并查集

    public MyNetwork() {
        this.people = new HashMap<>();
        this.peopleId = new ArrayList<>();
        this.personArray = new ArrayList<>();
        this.groups = new HashMap<>();
        this.messages = new HashMap<>();
        this.emojiIdList = new HashMap<>();
        this.emojiHeatList = new HashMap<>();
        this.emojiId = new ArrayList<>();
        peopleGraph = new Graph();    //
        degree = new HashMap<>();
        visitTime = new HashMap<>();
        enode = new HashMap<>();
        blocknum = 0;
    }

    @Override
    public boolean contains(int id) {
        if (people.containsKey(id)) {
            return true;
        }
        return false;
    }

    @Override
    public Person getPerson(int id) {
        if (contains(id)) {
            return this.people.get(id);
        }
        return null;
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        if (contains(person.getId())) {
            throw new MyEqualPersonIdException(person.getId());  //
        }
        this.people.put(person.getId(), person);
        this.peopleId.add(person.getId());
        personArray.add(person);
        peopleGraph.addNode(person.getId());
        degree.put(person.getId(), 0);
        disjointSet.add(person.getId());  //并查集添加元素
        blocknum++;     //
    }

    @Override
    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);  //
        } else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);  //
        }
        if (getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyEqualRelationException(id1, id2); //
        }
        ((MyPerson) getPerson(id1)).addAcquain(getPerson(id2));
        ((MyPerson) getPerson(id1)).addValue(id2, value);
        ((MyPerson) getPerson(id2)).addAcquain(getPerson(id1));
        ((MyPerson) getPerson(id2)).addValue(id1, value);
        peopleGraph.addEdge((getPerson(id1)).getId(), getPerson(id2).getId(), value);
        peopleGraph.addEdge(getPerson(id2).getId(), (getPerson(id1)).getId(), value);
        int tmp1 = degree.get(id1) + 1;
        degree.put(id1, tmp1);
        int tmp2 = degree.get(id2) + 1;
        degree.put(id2, tmp2);
        disjointSet.addBackUp(id1, id2);
        if (disjointSet.merge(id1, id2) == 0) {   //修改block_sum
            blocknum--;
        }
    }

    @Override
    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {  //更改关系
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);  //
        } else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);  //
        }
        if (id1 == id2) {
            throw new MyEqualPersonIdException(id1);     //
        } else if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2); //
        }
        if (getPerson(id1).queryValue(getPerson(id2)) + value > 0) {  //仅修改value
            int newValue = getPerson(id1).queryValue(getPerson(id2)) + value;
            ((MyPerson) getPerson(id1)).addValue(id2, newValue);
            ((MyPerson) getPerson(id2)).addValue(id1, newValue);
            this.peopleGraph.modifyEdgeWeight(id1, id2, value);//维护图
            this.peopleGraph.modifyEdgeWeight(id2, id1, value);
        } else {    //删关系（删边）
            ((MyPerson) getPerson(id1)).removeRelation(id2);
            ((MyPerson) getPerson(id2)).removeRelation(id1);
            int tmp = this.peopleId.size();
            this.blocknum = disjointSet.remove(id1, id2, tmp, peopleId);
            this.peopleGraph.deleteEdge(id1, id2);   //维护图
            this.peopleGraph.deleteEdge(id2, id1);
        }
    }

    @Override
    public int queryValue(int id1, int id2) throws
            PersonIdNotFoundException, RelationNotFoundException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);  //
        } else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);  //
        }
        if (!getPerson(id1).isLinked(getPerson(id2))) {
            throw new MyRelationNotFoundException(id1, id2); //
        }
        return getPerson(id1).queryValue(getPerson(id2));
    }

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        if (!contains(id1)) {
            throw new MyPersonIdNotFoundException(id1);  //
        } else if (!contains(id2)) {
            throw new MyPersonIdNotFoundException(id2);  //
        }
        if (disjointSet.find(id1) == disjointSet.find(id2)) {
            return true;
        }
        return false;
    }

    @Override
    public int queryBlockSum() {
        return blocknum;
    }

    @Override
    public int queryTripleSum() {
        Qts qts = new Qts(degree, visitTime, enode, disjointSet.getBackup());
        int ret = qts.getCnt();
        return ret;
    }

    @Override
    public void addGroup(Group group) throws EqualGroupIdException {
        if (this.groups.containsKey(group.getId())) {
            throw new MyEqualGroupIdException(group.getId());   //
        }
        this.groups.put(group.getId(), group);
    }

    @Override
    public Group getGroup(int id) {
        if (this.groups.containsKey(id)) {
            return this.groups.get(id);
        }
        return null;
    }

    @Override
    public void addToGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        if (!this.groups.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);   //
        } else if (!this.people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);  //
        }
        if (groups.containsKey(id2) && people.containsKey(id1)) {
            if (getGroup(id2).hasPerson(getPerson(id1))) {
                throw new MyEqualPersonIdException(id1);  //
            } else {
                if (getGroup(id2).getSize() <= 1111) {  // <= 1111 才组加人
                    getGroup(id2).addPerson(getPerson(id1));
                }
            }
        }
    }

    @Override
    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id); //
        }
        return getGroup(id).getValueSum();
    }

    @Override
    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        if (!groups.containsKey(id)) {
            throw new MyGroupIdNotFoundException(id); //
        }
        return getGroup(id).getAgeVar();
    }

    @Override
    public void delFromGroup(int id1, int id2)
            throws GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        if (!this.groups.containsKey(id2)) {
            throw new MyGroupIdNotFoundException(id2);   //
        } else if (!this.people.containsKey(id1)) {
            throw new MyPersonIdNotFoundException(id1);  //
        }
        if (groups.containsKey(id2) && people.containsKey(id1)) {
            if (!getGroup(id2).hasPerson(getPerson(id1))) {
                throw new MyEqualPersonIdException(id1);  //
            } else {
                getGroup(id2).delPerson(getPerson(id1));
            }
        }
    }

    @Override
    public boolean containsMessage(int id) {
        return this.messages.containsKey(id);
    }

    @Override
    public void addMessage(Message message) throws
            EqualMessageIdException, EmojiIdNotFoundException, EqualPersonIdException {
        if (this.messages.containsKey(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        } else {
            if ((message instanceof EmojiMessage) &&
                    !containsEmojiId(((EmojiMessage) message).getEmojiId())) {
                throw new MyEmojiIdNotFoundException(((EmojiMessage) message).getEmojiId());
            } else {
                if (message.getType() == 0 && message.getPerson1() == message.getPerson2()) {
                    throw new MyEqualPersonIdException(message.getPerson1().getId());
                } else {
                    this.messages.put(message.getId(), message);
                }
            }
        }
    }

    @Override
    public Message getMessage(int id) {
        if (this.messages.containsKey(id)) {
            return this.messages.get(id);
        }
        return null;
    }

    @Override
    public void sendMessage(int id) throws
            RelationNotFoundException, MessageIdNotFoundException, PersonIdNotFoundException {
        if (!containsMessage(id)) {
            throw new MyMessageIdNotFoundException(id);
        } else {
            if (getMessage(id).getType() == 0 &&
                    !(getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()))) {
                throw new MyRelationNotFoundException(getMessage(id).getPerson1().getId(),
                        getMessage(id).getPerson2().getId());
            } else if (getMessage(id).getType() == 1 &&
                    !(getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1()))) {
                throw new MyPersonIdNotFoundException(getMessage(id).getPerson1().getId());
            }
            if (containsMessage(id) && getMessage(id).getType() == 0 &&
                    getMessage(id).getPerson1().isLinked(getMessage(id).getPerson2()) &&
                    getMessage(id).getPerson1() != getMessage(id).getPerson2()) {
                int num = getMessage(id).getSocialValue();   //修改社交值
                getMessage(id).getPerson1().addSocialValue(num);
                getMessage(id).getPerson2().addSocialValue(num);
                if (getMessage(id) instanceof RedEnvelopeMessage) {
                    int money = ((RedEnvelopeMessage) getMessage(id)).getMoney();
                    getMessage(id).getPerson1().addMoney(0 - money);  //发的人减钱
                    getMessage(id).getPerson2().addMoney(money);  //收的人加钱
                }
                if (getMessage(id) instanceof EmojiMessage) {
                    if (this.emojiHeatList.containsKey(
                            ((EmojiMessage) getMessage(id)).getEmojiId())) {
                        int tmp = emojiHeatList.get(((EmojiMessage) getMessage(id)).getEmojiId());
                        tmp++;
                        emojiHeatList.put(((EmojiMessage) getMessage(id)).getEmojiId(), tmp);
                    }
                }
                ((MyPerson) (getMessage(id).getPerson2())).addFirstMessage(getMessage(id)); //接收插入最新
                this.messages.remove(id);
            }
            if (containsMessage(id) && getMessage(id).getType() == 1 &&
                    getMessage(id).getGroup().hasPerson(getMessage(id).getPerson1())) {
                int num = getMessage(id).getSocialValue();
                ((MyGroup) getMessage(id).getGroup()).addGroupSocialVa(num);

                if (getMessage(id) instanceof RedEnvelopeMessage) {
                    int i = ((RedEnvelopeMessage) getMessage(id)).getMoney()
                            / getMessage(id).getGroup().getSize();
                    int tmp = 0 - i * (getMessage(id).getGroup().getSize() - 1);
                    getMessage(id).getPerson1().addMoney(tmp);
                    ((MyGroup) getMessage(id).getGroup()).modifyGroupMoney(
                            getMessage(id).getPerson1().getId(), i);
                }
                if (getMessage(id) instanceof EmojiMessage) {
                    if (this.emojiHeatList.containsKey(
                            ((EmojiMessage) getMessage(id)).getEmojiId())) {
                        int tmp = emojiHeatList.get(((EmojiMessage) getMessage(id)).getEmojiId());
                        tmp++;
                        emojiHeatList.put(((EmojiMessage) getMessage(id)).getEmojiId(), tmp);
                    }
                }
                this.messages.remove(id);
            }
        }
    }

    @Override
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);  //
        } else {
            return getPerson(id).getSocialValue();
        }
    }

    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);  //
        } else {
            return getPerson(id).getReceivedMessages();
        }
    }

    @Override
    public boolean containsEmojiId(int id) {
        return emojiIdList.containsKey(id);
    }

    @Override
    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (emojiIdList.containsKey(id)) {
            throw new MyEqualEmojiIdException(id);
        } else {
            emojiIdList.put(id, id);
            emojiHeatList.put(id, 0);
            emojiId.add(id);
        }
    }

    @Override
    public int queryMoney(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            return getPerson(id).getMoney();
        }
    }

    @Override
    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (!emojiIdList.containsKey(id)) {
            throw new MyEmojiIdNotFoundException(id);
        } else {
            return this.emojiHeatList.get(id);
        }
    }

    @Override
    public int deleteColdEmoji(int limit) {
        for (int i = 0; i < emojiId.size(); i++) {
            int id = emojiId.get(i);
            if (this.emojiHeatList.get(id) < limit) {
                this.emojiHeatList.remove(id);
                this.emojiIdList.remove(id);
                this.emojiId.remove(i);
                i--;
            }
        }
        ArrayList<Integer> deletedMessId = new ArrayList<>();
        for (Integer mid : this.messages.keySet()) {
            if (messages.get(mid) instanceof EmojiMessage &&
                    !containsEmojiId(((EmojiMessage) messages.get(mid)).getEmojiId())) {
                deletedMessId.add(mid);  //将删除
            }
        }
        for (Integer did : deletedMessId) {
            this.messages.remove(did);
        }
        return this.emojiIdList.size();
    }

    @Override
    public void clearNotices(int personId) throws PersonIdNotFoundException { //删NoticeMessage(顺序不变)
        if (!contains(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        } else {
            ((MyPerson) getPerson(personId)).deleteNotice();
        }
    }

    @Override
    public int queryBestAcquaintance(int id) throws
            PersonIdNotFoundException, AcquaintanceNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);  //
        } else {
            if (((MyPerson) getPerson(id)).getValue().size() == 0) {
                throw new MyAcquaintanceNotFoundException(id); //
            } else {
                Qba qba = new Qba((MyPerson) getPerson(id));
                return qba.queryBestAc();
            }
        }
    }

    @Override
    public int queryCoupleSum() {
        int sum = 0;
        for (int i = 0; i < peopleId.size(); i++) {
            int tempj = 0;
            if (((MyPerson) getPerson(peopleId.get(i))).getValue().size() > 0) {
                try {
                    tempj = queryBestAcquaintance(peopleId.get(i));
                } catch (PersonIdNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (AcquaintanceNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            for (int j = i + 1; j < peopleId.size(); j++) {
                if (tempj == peopleId.get(j)) {
                    if (((MyPerson) getPerson(tempj)).getValue().size() > 0) {
                        try {
                            if (queryBestAcquaintance(tempj) == peopleId.get(i)) {
                                sum++;
                                break;
                            }
                        } catch (PersonIdNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (AcquaintanceNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
        return sum;
    }

    @Override
    public int queryLeastMoments(int id) throws PersonIdNotFoundException, PathNotFoundException {
        if (!contains(id)) {     //求最小带权环路的总权值（起终点均为getPerson(id)）
            throw new MyPersonIdNotFoundException(id);
        } else {
            Qlm qlm = new Qlm(this.peopleId, this.people);
            int ret = qlm.queryLm(id);
            this.peopleGraph.reset();
            if (ret == 609090909) {
                throw new MyPathNotFoundException(id);
            } else {
                return ret;
            }
        }
    }

    @Override
    public int deleteColdEmojiOKTest(int limit, ArrayList<HashMap<Integer, Integer>> beforeData,
                                     ArrayList<HashMap<Integer, Integer>> afterData, int result) {
        Dceok dceok = new Dceok(limit, beforeData, afterData, result);
        return dceok.dceOkTest();
    }

}

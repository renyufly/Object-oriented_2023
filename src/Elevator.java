import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.TimableOutput;
import java.util.ArrayList;

/*
1、可到达楼层：1 - 11 层
2、初始位置：1 层
3、数量：6 部
4、编号：6 部电梯，ID分别为 1 —6
5、移动一层花费的时间：0.4s  [400ms]
6、开门花费的时间：0.2s      [200ms]
7、关门花费的时间：0.2s      [200ms]
8、限乘人数：6 人
 */

public class Elevator implements Runnable {  //每个电梯一个线程
    private WaitTable waitTable;     // 共享候乘表
    private int capacity;          // 单个电梯的满载容量
    private int id;             //电梯对应ID
    private int destinateFloor;       //目标楼层
    private int curFloor;            //电梯当前楼层
    private int direction;       //运行方向，0-往上，1-往下
    private double speed;       //移动一层的时间
    private int access;      //可达性，掩码表示

    private ArrayList<Person> persons;   //自己单个电梯的舱内人员

    private ArrayList<Person> waitingPerson;   //候选池

    private boolean ismaintain;         //是否维修状态

    public Elevator() {
    }

    public Elevator(int eid, WaitTable waitTable) {     //初始化
        this.waitTable = waitTable;     // 共享候乘表
        capacity = 6;
        this.id = eid;
        curFloor = 1;
        direction = 0;
        destinateFloor = 11;
        this.speed = 0.4;        //
        persons = new ArrayList<>();
        waitingPerson = new ArrayList<>();
        this.ismaintain = false;
        this.access = 2047; // 0111_1111_1111b
    }

    public Elevator(int eid, WaitTable waitTable, int efloor,
                    int ecapacity, double espeed, int eaccess) { //添加电梯
        this.waitTable = waitTable;     // 共享候乘表
        this.capacity = ecapacity;
        this.id = eid;
        this.curFloor = efloor;
        this.direction = 0;
        this.destinateFloor = 11;
        this.speed = espeed;        //
        this.persons = new ArrayList<>();
        this.waitingPerson = new ArrayList<>();
        this.ismaintain = false;
        this.access = eaccess;
    }

    @Override
    public void run() {
        while (true) {
            if (this.ismaintain == true) {     //电梯维修
                synchronized (waitTable) {
                    maintain();
                    waitTable.setHasMaintain(1);
                    waitTable.notifyAll();
                    break;       //结束线程
                }
            }
            if (persons.isEmpty() && waitingPerson.isEmpty()) {
                synchronized (waitTable) {
                    if (waitTable.getIsClose() && (waitTable.getHasMaintain() == 0)) {
                        break;       //结束线程
                    }
                    try {
                        waitTable.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else if (persons.isEmpty()) {
                this.destinateFloor = waitingPerson.get(0).getFrom();
                setDirection();
            } else {      //请求池不为空且自己候选池不为空
                this.destinateFloor = persons.get(0).getShortTermDesti();
                setDirection();
            }
            /*     电梯上下人          */
            if (isGetOn() || isGetOff() && isAccess(curFloor)) {
                synchronized (waitTable) {
                    while (waitTable.getCurFloorEleService(curFloor) > 3) {
                        try {
                            waitTable.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (isGetOn() && !isGetOff()) {  //只接人
                    onlyPickUp();
                } else {
                    synchronized (waitTable) {
                        waitTable.setElevaServiceEachFloor(true, curFloor);
                    }
                    open();
                    getOn();
                    getOff();
                    close();
                    synchronized (waitTable) {
                        waitTable.setElevaServiceEachFloor(false, curFloor);
                        waitTable.notifyAll();
                    }
                    move();
                }
            } else {
                move();
            }

        }
    }

    /*                 */
    public void setWaitingPerson(Person person1) {
        this.waitingPerson.add(person1);
    }

    public int getLoadPerson() {
        return this.persons.size();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getWaitingNumber() {
        if (waitingPerson.isEmpty()) {
            return 0;
        } else {
            return this.waitingPerson.size();
        }
    }

    /*    电梯设置属性方法      */

    private void setDirection() {
        if (this.curFloor > this.destinateFloor) {
            this.direction = 1;   //向下
        } else {
            this.direction = 0;   //向上
        }
    }

    public void setIsmaintain(boolean state) {
        this.ismaintain = state;
    }

    public int getId() {
        return this.id;
    }

    public boolean isAccess(int floor) {
        if ((this.access & (1 << (floor - 1))) != 0) {
            return true;      //可达；
        } else {
            return false;
        }
    }


    /*    电梯运行状态     */
    private void getOn() {     //上电梯
        for (int i = 0; i < waitingPerson.size(); i++) {
            Person pr = waitingPerson.get(i);
            if (pr.getFrom() == this.curFloor && persons.size() < capacity) {
                waitingPerson.remove(i);
                i = i - 1;
                persons.add(pr);
                TimableOutput.println("IN-" + pr.getPrId()
                        + "-" + this.curFloor + "-" + this.id);
            }
        }
    }

    private boolean isGetOn() {        //是否有上电梯
        int flag = 0;
        for (int i = 0; i < waitingPerson.size(); i++) {
            Person pr = waitingPerson.get(i);
            if (pr.getFrom() == this.curFloor && persons.size() < capacity) {
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void getOff() {          //下电梯
        for (int i = 0; i < persons.size(); i++) {
            Person pr = persons.get(i);
            if (pr.getShortTermDesti() == this.curFloor) {
                persons.remove(i);
                i = i - 1;
                TimableOutput.println("OUT-" + pr.getPrId()
                        + "-" + this.curFloor + "-" + this.id);
                if (pr.getDesti() != this.curFloor) {
                    synchronized (waitTable) {
                        PersonRequest personRequest = new PersonRequest(this.curFloor,
                                pr.getDesti(), pr.getPrId());
                        waitTable.addRequest(personRequest);
                        waitTable.notifyAll();
                    }
                }
            }
        }
    }

    private boolean isGetOff() {         //是否有下电梯
        int flag = 0;
        for (int i = 0; i < persons.size(); i++) {
            Person pr = persons.get(i);
            if (pr.getShortTermDesti() == this.curFloor) {
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void open() {    // 开门
        try {
            Thread.currentThread().sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TimableOutput.println("OPEN-" + curFloor + "-" + id);
        try {
            Thread.currentThread().sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void close() {      //关门
        try {
            Thread.currentThread().sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        TimableOutput.println("CLOSE-" + curFloor + "-" + id);

    }

    private void move() {      //移动一层
        try {
            Thread.currentThread().sleep((long) (1000 * speed));    //
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.curFloor == 1) {
            this.direction = 0;
            this.curFloor += 1;
            TimableOutput.println("ARRIVE-" + curFloor + "-" + id);
        } else if (this.curFloor == 11) {
            this.direction = 1;
            this.curFloor -= 1;
            TimableOutput.println("ARRIVE-" + curFloor + "-" + id);
        } else {
            if (this.direction == 0) {
                this.curFloor += 1;
                TimableOutput.println("ARRIVE-" + curFloor + "-" + id);
            } else {
                this.curFloor -= 1;
                TimableOutput.println("ARRIVE-" + curFloor + "-" + id);
            }
        }
    }

    private void maintain() {     //电梯维修

        if (!persons.isEmpty()) {
            synchronized (waitTable) {
                while (waitTable.getCurFloorEleService(curFloor) > 3) {
                    try {
                        waitTable.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            synchronized (waitTable) {
                waitTable.setElevaServiceEachFloor(true, curFloor);
            }
            open();
            for (int i = 0; i < persons.size(); i++) {
                Person pr = persons.get(i);
                persons.remove(i);
                i = i - 1;
                TimableOutput.println("OUT-" + pr.getPrId()
                        + "-" + this.curFloor + "-" + this.id);
                if (pr.getDesti() != curFloor) {
                    PersonRequest newpr = new PersonRequest(curFloor,
                            pr.getDesti(), pr.getPrId());
                    synchronized (waitTable) {
                        waitTable.addRequest(newpr);
                        waitTable.notifyAll();
                    }
                    //人员在当前楼层出去，并回归请求池
                }
            }
            close();
            synchronized (waitTable) {
                waitTable.setElevaServiceEachFloor(false, curFloor);
                waitTable.notifyAll();
            }
        }
        for (int i = 0; i < waitingPerson.size(); i++) {
            Person newperson = waitingPerson.get(i);
            waitingPerson.remove(i);
            i = i - 1;
            PersonRequest newpr = new PersonRequest(this.curFloor,
                    newperson.getDesti(), newperson.getPrId());
            synchronized (waitTable) {
                waitTable.addRequest(newpr);
            }
        }
        TimableOutput.println("MAINTAIN_ABLE-" + this.id);
    }

    private void onlyPickUp() {
        synchronized (waitTable) {
            while (waitTable.getCurFloorElevaPickup(curFloor) > 1) {
                try {
                    waitTable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        synchronized (waitTable) {
            waitTable.setElevaPickupEachFloor(true, curFloor);
            waitTable.setElevaServiceEachFloor(true, curFloor);
        }
        open();
        getOn();
        close();
        synchronized (waitTable) {
            waitTable.setElevaServiceEachFloor(false, curFloor);
            waitTable.setElevaPickupEachFloor(false, curFloor);
            waitTable.notifyAll();
        }
        move();
    }

}

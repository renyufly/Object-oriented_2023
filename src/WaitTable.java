import com.oocourse.elevator3.PersonRequest;

import java.util.ArrayList;

public class WaitTable {     //候乘表
    private ArrayList<PersonRequest> personRequests;    //总的请求队列

    private boolean isClose;             //是否输入关闭
    private int hasMaintain;
    private ArrayList<Integer> elevaServiceEachFloor;     //每层楼的服务中电梯数量
    private ArrayList<Integer> elevaPickupEachFloor;      //每层楼服务中且只接人的电梯数量

    public WaitTable() {
        this.personRequests = new ArrayList<>();
        isClose = false;
        hasMaintain = 0;    //0表示没有电梯在维修
        elevaServiceEachFloor = new ArrayList<>();
        elevaPickupEachFloor = new ArrayList<>();
        for (int i = 0; i < 11; i++) {  //一共11层，对应0~10;
            elevaServiceEachFloor.add(0);
            elevaPickupEachFloor.add(0);
        }
    }

    public synchronized PersonRequest getRequest() {
        PersonRequest pr = this.personRequests.get(0);
        this.personRequests.remove(0);
        return pr;
    }

    public synchronized void addRequest(PersonRequest pr) {            //请求池添加请求
        personRequests.add(pr);
    }

    public synchronized void setClose() {        //请求池关闭
        this.isClose = true;
    }

    public synchronized boolean getIsClose() {
        return this.isClose;
    }

    public synchronized boolean isEmpty() {
        if (this.personRequests.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public synchronized void setHasMaintain(int flag) {
        if (flag == 0) {
            this.hasMaintain = this.hasMaintain + 1;
        } else {
            this.hasMaintain = this.hasMaintain - 1;
        }

    }

    public synchronized int getHasMaintain() {
        return this.hasMaintain;
    }

    public int getCurFloorElevaPickup(int curFloor) {
        return this.elevaPickupEachFloor.get(curFloor - 1);
    }

    public int getCurFloorEleService(int curFloor) {
        return this.elevaServiceEachFloor.get(curFloor - 1);
    }

    public void setElevaPickupEachFloor(boolean operation, int curFloor) {
        if (operation == true) {     //当前楼层只接人电梯数加1
            int tmp = elevaPickupEachFloor.get(curFloor - 1);
            tmp = tmp + 1;
            elevaPickupEachFloor.set(curFloor - 1, tmp);
        } else if (operation == false) {
            int tmp = elevaPickupEachFloor.get(curFloor - 1);
            tmp = tmp - 1;
            elevaPickupEachFloor.set(curFloor - 1, tmp);
        }
    }

    public void setElevaServiceEachFloor(boolean operation, int curFloor) {
        if (operation == true) {     //当前楼层服务中电梯数加1
            int tmp = elevaServiceEachFloor.get(curFloor - 1);
            tmp = tmp + 1;
            elevaServiceEachFloor.set(curFloor - 1, tmp);
        } else if (operation == false) {
            int tmp = elevaServiceEachFloor.get(curFloor - 1);
            tmp = tmp - 1;
            elevaServiceEachFloor.set(curFloor - 1, tmp);
        }
    }

}

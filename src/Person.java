import com.oocourse.elevator3.PersonRequest;
import java.util.ArrayList;

public class Person {
    private PersonRequest personRequest;
    private int shortTermDesti;     //短期目标楼层
    private ArrayList<Integer> usedElevator;

    public Person(PersonRequest pr, int shortDesti, ArrayList<Integer> eused) {
        this.personRequest = pr;
        this.shortTermDesti = shortDesti;
        this.usedElevator = eused;
    }

    public void setShortTermDesti(int shortFloor) {
        this.shortTermDesti = shortFloor;
    }

    public int getShortTermDesti() {
        return this.shortTermDesti;
    }

    public int getFrom() {
        return this.personRequest.getFromFloor();
    }

    public int getPrId() {
        return this.personRequest.getPersonId();
    }

    public int getDesti() {
        return this.personRequest.getToFloor();
    }

    public ArrayList<Integer> getUsedElevator() {
        return this.usedElevator;
    }

    public void addUsedEleva(int eid) {
        this.usedElevator.add(eid);
    }

    public boolean isUsedEle(ArrayList<Integer> routes) {
        int flag = 0;
        for (int j = 0; j < this.usedElevator.size(); j++) {
            if (routes.contains(this.usedElevator.get(j))) {
                flag = 1;
                break;
            }
        }
        if (flag == 1) {
            return true;
        } else {
            return false;
        }
    }

}

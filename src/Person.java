import com.oocourse.elevator3.PersonRequest;

public class Person {
    private PersonRequest personRequest;
    private int shortTermDesti;     //短期目标楼层

    public Person(PersonRequest pr, int shortDesti) {
        this.personRequest = pr;
        this.shortTermDesti = shortDesti;
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

}

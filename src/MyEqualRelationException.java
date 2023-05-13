import com.oocourse.spec3.exceptions.EqualRelationException;

public class MyEqualRelationException extends EqualRelationException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId1;
    private int curId2;

    public MyEqualRelationException(int id1, int id2) {
        counter.incrementCount();
        if (id1 == id2) {
            counter.increPeopleCount(id1);
        } else {
            counter.increPeopleCount(id1);
            counter.increPeopleCount(id2);
        }
        if (id1 > id2) {
            curId1 = id2;
            curId2 = id1;
        } else {
            curId1 = id1;
            curId2 = id2;
        }
    }

    @Override
    public void print() {
        System.out.println("er-" + counter.getCount() + ", " + curId1
                + "-" + counter.getPeopleCount(curId1) +
                ", " + curId2 + "-" + counter.getPeopleCount(curId2));
    }
}

import com.oocourse.spec3.exceptions.EqualPersonIdException;

public class MyEqualPersonIdException extends EqualPersonIdException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId;

    public MyEqualPersonIdException(int id) {
        counter.incrementCount();
        counter.increPeopleCount(id);
        curId = id;
    }

    @Override
    public void print() {
        System.out.println("epi-" + counter.getCount() + ", " +
                curId + "-" + counter.getPeopleCount(curId));
    }
}

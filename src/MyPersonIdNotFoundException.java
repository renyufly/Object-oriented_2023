import com.oocourse.spec3.exceptions.PersonIdNotFoundException;

public class MyPersonIdNotFoundException extends PersonIdNotFoundException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId;

    public MyPersonIdNotFoundException(int id) {
        counter.incrementCount();
        counter.increPeopleCount(id);
        curId = id;
    }

    @Override
    public void print() {
        System.out.println("pinf-" + counter.getCount() + ", " +
                curId + "-" + counter.getPeopleCount(curId));
    }

}
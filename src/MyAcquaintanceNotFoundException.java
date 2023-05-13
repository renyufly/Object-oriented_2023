import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;

public class MyAcquaintanceNotFoundException extends AcquaintanceNotFoundException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId;

    public MyAcquaintanceNotFoundException(int id) {
        counter.incrementCount();
        counter.increPeopleCount(id);
        curId = id;
    }

    @Override
    public void print() {
        System.out.println("anf-" + counter.getCount() + ", " +
                curId + "-" + counter.getPeopleCount(curId));
    }
}

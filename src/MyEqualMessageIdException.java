import com.oocourse.spec3.exceptions.EqualMessageIdException;

public class MyEqualMessageIdException extends EqualMessageIdException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId;

    public MyEqualMessageIdException(int id) {
        counter.incrementCount();
        counter.increMessageCount(id);
        curId = id;
    }

    @Override
    public void print() {
        System.out.println("emi-" + counter.getCount() + ", "
                + curId + "-" + counter.getMessageCount(curId));
    }
}

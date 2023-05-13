import com.oocourse.spec3.exceptions.MessageIdNotFoundException;

public class MyMessageIdNotFoundException extends MessageIdNotFoundException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId;

    public MyMessageIdNotFoundException(int id) {
        counter.incrementCount();
        counter.increMessageCount(id);
        curId = id;
    }

    @Override
    public void print() {
        System.out.println("minf-" + counter.getCount() + ", "
                + curId + "-" + counter.getMessageCount(curId));
    }
}

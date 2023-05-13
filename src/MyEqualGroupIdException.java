import com.oocourse.spec3.exceptions.EqualGroupIdException;

public class MyEqualGroupIdException extends EqualGroupIdException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId;

    public MyEqualGroupIdException(int id) {
        counter.incrementCount();
        counter.increGroupCount(id);
        curId = id;
    }

    @Override
    public void print() {
        System.out.println("egi-" + counter.getCount() + ", "
                + curId + "-" + counter.getGroupCount(curId));
    }
}

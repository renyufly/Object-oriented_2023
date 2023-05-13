import com.oocourse.spec3.exceptions.GroupIdNotFoundException;

public class MyGroupIdNotFoundException extends GroupIdNotFoundException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId;

    public MyGroupIdNotFoundException(int id) {
        counter.incrementCount();
        counter.increGroupCount(id);
        curId = id;
    }

    @Override
    public void print() {
        System.out.println("ginf-" + counter.getCount() + ", "
                + curId + "-" + counter.getGroupCount(curId));
    }
}


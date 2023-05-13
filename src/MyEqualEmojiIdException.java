import com.oocourse.spec3.exceptions.EqualEmojiIdException;

public class MyEqualEmojiIdException extends EqualEmojiIdException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId;

    public MyEqualEmojiIdException(int id) {
        counter.incrementCount();
        counter.increEmojiCount(id);
        this.curId = id;
    }

    @Override
    public void print() {
        System.out.println("eei-" + counter.getCount() + ", "
                            + curId + "-" + counter.getEmojiCount(curId));
    }

}

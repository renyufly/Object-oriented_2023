import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;

public class MyEmojiIdNotFoundException extends EmojiIdNotFoundException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId;

    public MyEmojiIdNotFoundException(int id) {
        counter.incrementCount();
        counter.increEmojiCount(id);
        this.curId = id;
    }

    @Override
    public void print() {
        System.out.println("einf-" + counter.getCount() + ", "
                          + curId + "-" + counter.getEmojiCount(curId));
    }

}
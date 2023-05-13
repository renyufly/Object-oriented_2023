import com.oocourse.spec3.exceptions.PathNotFoundException;

public class MyPathNotFoundException extends PathNotFoundException {
    private static ExceptionCounter counter = new ExceptionCounter();
    private int curId;

    public MyPathNotFoundException(int id) {
        counter.incrementCount();
        counter.increPeopleCount(id);
        this.curId = id;
    }

    @Override
    public void print() {
        System.out.println("pnf-" + counter.getCount() + ", "
                           + curId + "-" + counter.getPeopleCount(curId));
    }

}
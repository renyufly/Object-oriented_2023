import java.util.ArrayList;

public class Logistics {
    private ArrayList<String> logisticsShelf;   // 摆在后勤书架的书 [书号]

    public Logistics() {
        this.logisticsShelf = new ArrayList<>();
    }

    public void repairBook(String bookNumber) {
        this.logisticsShelf.add(bookNumber);
    }

    public void clearShelf() {
        this.logisticsShelf = new ArrayList<>();
    }

    public ArrayList<String> getLogisticsShelf() {
        return this.logisticsShelf;
    }

}

import java.util.ArrayList;
import java.util.HashMap;

public class ArrangeLibrarian {
    private HashMap<String, Integer> arrangeList;   // 收集的书籍<书号，数量>

    public ArrangeLibrarian() {
        this.arrangeList = new HashMap<>();
    }

    public HashMap<String, Integer> arrangeBook(ArrayList<String> borrowShelf,
                                                ArrayList<String> selfmachShelf,
                                                ArrayList<String> logisticsShelf,
                                                ArrayList<String> returnedOutBook) {
        HashMap<String, Integer> collected = new HashMap<>();
        for (String bookNum : borrowShelf) {
            if (!collected.containsKey(bookNum)) {
                collected.put(bookNum, 1);
            } else {
                int tmp = collected.get(bookNum);
                tmp++;
                collected.put(bookNum, tmp);
            }
        }
        for (String bookNum : selfmachShelf) {
            if (!collected.containsKey(bookNum)) {
                collected.put(bookNum, 1);
            } else {
                int tmp = collected.get(bookNum);
                tmp++;
                collected.put(bookNum, tmp);
            }
        }
        for (String bookNum : logisticsShelf) {
            if (!collected.containsKey(bookNum)) {
                collected.put(bookNum, 1);
            } else {
                int tmp = collected.get(bookNum);
                tmp++;
                collected.put(bookNum, tmp);
            }
        }
        for (String bookNum : returnedOutBook) {
            if (!collected.containsKey(bookNum)) {
                collected.put(bookNum, 1);
            } else {
                int tmp = collected.get(bookNum);
                tmp++;
                collected.put(bookNum, tmp);
            }
        }
        return collected;
    }

}

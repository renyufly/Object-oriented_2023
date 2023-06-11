import java.util.ArrayList;
import java.util.HashMap;

public class LibManage {   // 图书管理处
    private HashMap<String, Integer> purchaseList;  //校内购书清单  <[书号], 数量>
    private HashMap<String, ArrayList<String>> stuOutBook;  //校际间运过来的借书  <校名-学号，ABCDE B-0010>
    private ArrayList<String> returnedOutBook;   //归还的校际间的图书  [B-0010]  (存的书号已经是本校的了)

    public LibManage() {
        this.purchaseList = new HashMap<>();
        this.stuOutBook = new HashMap<>();
        this.returnedOutBook = new ArrayList<>();
    }

    public ArrayList<String> getReturnOutBook() {
        return this.returnedOutBook;  //待整理日时才会上架
    }

    public void clearReturnedOutBook() {
        this.returnedOutBook = new ArrayList<>();
    }

    public HashMap<String, ArrayList<String>> getStuOutBook() {
        return this.stuOutBook;
    }

    public int getPurchaseNum(String bookNum) {
        int ret = this.purchaseList.get(bookNum);
        if (ret < 3) {
            return 3;
        } else {
            return ret;
        }
    }

    public void addReturnedOut(String book) {
        this.returnedOutBook.add(book);   //第二天就要归还的
    }

    public void addStuOutBook(String stuid, String outSchool, String bookNum) {
        if (!stuOutBook.containsKey(stuid)) {
            ArrayList<String> strings = new ArrayList<>();
            stuOutBook.put(stuid, strings);
        }
        stuOutBook.get(stuid).add(outSchool + " " + bookNum);
    }

    public boolean isPermitOut(String stuid, String bookNum) {
        if (this.stuOutBook.containsKey(stuid)) {
            for (int i = 0; i < stuOutBook.get(stuid).size(); i++) {
                if (stuOutBook.get(stuid).get(i).split(" ")[1].equals(bookNum)) {  //书号相同
                    return false;   //不允许校际运输
                }
                if (bookNum.charAt(0) == 'B' &&
                        stuOutBook.get(stuid).get(i).split(" ")[1].charAt(0) == 'B') {
                    return false;
                } //B类书同段时间只能借一次
            }
        }
        return true;
    }

    public void addPurchase(String bookNum) {  //添加购书清单请求
        if (!this.purchaseList.containsKey(bookNum)) {
            this.purchaseList.put(bookNum, 1);
        } else {
            int tmp = this.purchaseList.get(bookNum);
            tmp++;
            this.purchaseList.put(bookNum, tmp);
        }
    }

    public void decPurchase(String bookNum) { //减少购书清单请求
        int tmp = this.purchaseList.get(bookNum);
        tmp--;
        this.purchaseList.put(bookNum, tmp);
    }

}

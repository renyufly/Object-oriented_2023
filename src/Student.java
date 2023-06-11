import java.util.HashMap;

public class Student {
    private HashMap<String, Boolean> ownBooks;  // 当前借在手里的书籍<ABCDE B-0010，是否损毁>  [是损毁为true]
    private HashMap<String, Integer> ownBookFrom;  //从哪借来的书 (0-LibManage, 1-Library, 2-Order)
    private boolean isGotBbook;     //   是否当前拥有B类书 [true表示拥有B书]
    private HashMap<String, String> orderList;   // <书号，书号> 预定的书籍清单  [B-0010]
    private String stuId;     // 学号 <BDCFE-45678912>
    private HashMap<String, Integer> ownBookTime;   // 当前借在手里图书借阅时长  <ABCDE B-0010, 15>

    public Student(String stuId) {
        this.ownBooks = new HashMap<>();
        this.ownBookFrom = new HashMap<>();
        this.isGotBbook = false;
        this.orderList = new HashMap<>();
        this.stuId = stuId;
        this.ownBookTime = new HashMap<>();
    }

    public String getStuId() {
        return this.stuId;
    }

    public void clearBbook(String bookNum) {
        this.orderList.remove(bookNum);
    }

    public void addOwnBooks(String bookNumber, String school, int opt) {
        ownBooks.put(school + " " + bookNumber, false);
        ownBookFrom.put(school + " " + bookNumber, opt);
        ownBookTime.put(school + " " + bookNumber, 0);  //首日是第0天，转日第1天
    }

    public int getOwnBookFrom(String bookNumber) {
        for (String book : this.ownBookFrom.keySet()) {
            if (book.split(" ")[1].equals(bookNumber)) {
                int ret = this.ownBookFrom.get(book);
                this.ownBookFrom.remove(book);
                return ret;
            }
        }
        return 0;
    }

    public void removeOwnBook(String bookNumber) {
        for (String book : this.ownBooks.keySet()) {
            if (book.split(" ")[1].equals(bookNumber)) {
                this.ownBooks.remove(book);
                this.ownBookTime.remove(book);  //
                break;
            }
        }
    }

    public boolean queryOwnBook(String bookNumber) {
        for (String book : this.ownBooks.keySet()) {
            if (book.split(" ")[1].equals(bookNumber)) {
                return true;
            }
        }
        return false;
    }

    public String getOwnBook(String bookNum) {   // 返回 [AVCSD B-0001]
        for (String book : this.ownBooks.keySet()) {
            if (book.split(" ")[1].equals(bookNum)) {
                return book;
            }
        }
        return null;
    }

    public void smearBook(String bookNum) {   // 损毁书籍
        for (String book : this.ownBooks.keySet()) {
            if (book.split(" ")[1].equals(bookNum)) {
                this.ownBooks.put(book, true);
                break;
            }
        }
    }

    public boolean isSmear(String bookNum) {  // 是否损毁
        for (String book : this.ownBooks.keySet()) {
            if (book.split(" ")[1].equals(bookNum)) {
                return ownBooks.get(book);
            }
        }
        return false;
    }

    public void setGotBbook(boolean opt) {
        this.isGotBbook = opt;
    }

    public boolean isGotBbook() {
        return this.isGotBbook;
    }

    public boolean isGotCbook(String bookNum) {
        for (String book : this.ownBooks.keySet()) {
            if (book.split(" ")[1].equals(bookNum)) {
                return true;
            }
        }
        return false;
    }

    public void addTime() {   //所有借阅书借阅时间加一天
        for (String bookNum: this.ownBookTime.keySet()) {
            int tmp = this.ownBookTime.get(bookNum);
            tmp++;
            this.ownBookTime.put(bookNum, tmp);
        }
    }

    public int getBookTime(String bookNum) {  //还书时返回借阅时间
        for (String book : this.ownBookTime.keySet()) {
            if (book.split(" ")[1].equals(bookNum)) {
                return ownBookTime.get(book);
            }
        }
        return 0;
    }

}

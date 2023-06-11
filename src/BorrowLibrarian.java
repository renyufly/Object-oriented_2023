import java.util.ArrayList;

public class BorrowLibrarian {
    //public HashMap<String, Boolean> bBook;  // <学号，是否可借B类书>
    private ArrayList<String> borrowShelf;   // 存的未借成功/归还的书号 [类别号-序列号] [被留在借还管理员处]

    public BorrowLibrarian() {
        this.borrowShelf = new ArrayList<>();
    }

    public boolean askBorrow(Student student, String bookNumber) {
        if (!student.isGotBbook()) {  //该学生可借B类书
            return true;
        }
        this.borrowShelf.add(bookNumber);
        return false;
    }

    public void returnBook(String bookNumber) {
        this.borrowShelf.add(bookNumber);
    }

    public void clearBorrowShelf() {
        this.borrowShelf = new ArrayList<>();
    }

    public ArrayList<String> getBorrowShelf() {
        return this.borrowShelf;
    }

}

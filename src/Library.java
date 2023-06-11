import java.util.ArrayList;
import java.util.HashMap;

public class Library {    //图书馆
    private String schoolName;   //所属校名
    private HashMap<String, Student> students;   // 借书的所有学生  <BCDFE-23456789, Student类>
    private HashMap<String, Book> books;   //书库 <类别号-序列号，Book类>
    private String currentDate;     // 当前日期
    private BorrowLibrarian borrowLibrarian;  // 借还管理员
    private OrderLibrarian orderLibrarian;   //预定管理员
    private Logistics logisticsLibrarian;   // 后勤管理员
    private ArrangeLibrarian arrangeLibrarian;  // 整理管理员
    private ArrayList<String> selfmachShelf;  // 留在自助机的书籍 [书号]
    private HashMap<String, Integer> collectedBook;  // 整理员收集到的书
    private LibManage libManage;     //图书管理处
    private int flagLib;
    private int flagRepair;
    private int flagRet;

    public Library(String schoolName) {
        this.schoolName = schoolName;
        this.students = new HashMap<>();
        this.books = new HashMap<>();
        this.currentDate = "2023-01-01";
        this.borrowLibrarian = new BorrowLibrarian();
        this.orderLibrarian = new OrderLibrarian();
        this.logisticsLibrarian = new Logistics();
        this.arrangeLibrarian = new ArrangeLibrarian();
        this.selfmachShelf = new ArrayList<>();
        this.collectedBook = new HashMap<>();
        this.libManage = new LibManage();
        this.flagLib = 0;
        this.flagRepair = 0;
        this.flagRet = 0;
    }

    public void addBook(String bookstr, String school) {
        Book mybook = new Book(bookstr, school);
        this.books.put(mybook.getBookNumber(), mybook);
    }

    public void addStudent(Student student) {
        this.students.put(student.getStuId(), student);
    }

    public Student getStudent(String stuId) {
        return this.students.get(stuId);
    }

    public LibManage getLibManage() {
        return this.libManage;
    }

    public boolean isContainStu(String stuid) {
        return this.students.containsKey(stuid);
    }

    public boolean isContainBook(String bookNum) {
        return this.books.containsKey(bookNum);
    }

    public boolean isBookOut(String bookNum) { //是否有可外借的余本
        if (this.books.containsKey(bookNum)) {
            if (books.get(bookNum).isPermitLendOut() && books.get(bookNum).isRemain()) {
                return true;
            }
        }
        return false;
    }

    public void allClear(String nowTime) {
        orderLibrarian.clearCnt(); //清除计数
        for (int i = 0; i < libManage.getReturnOutBook().size(); i++) {  //接收传入图书
            System.out.println(nowTime + " " + schoolName + "-" +
                    libManage.getReturnOutBook().get(i) +
                    " got received by purchasing department in " + schoolName);
            System.out.println("(State) " + nowTime + " " + libManage.getReturnOutBook().get(i) +
                    " transfers from normal to normal"); //
            this.selfmachShelf.add(libManage.getReturnOutBook().get(i));
            libManage.getReturnOutBook().remove(i);
            i--;
        }
        HashMap<String, ArrayList<String>> stuOutBook = libManage.getStuOutBook();
        for (String stuId : stuOutBook.keySet()) {
            for (int j = 0; j < stuOutBook.get(stuId).size(); j++) {
                String bookNum = stuOutBook.get(stuId).get(j); // ABCDE B-0012
                System.out.println(nowTime + " " + bookNum.split(" ")[0] + "-" +
                        bookNum.split(" ")[1] + " got received by purchasing department in "
                        + schoolName);
                System.out.println("(State) " + nowTime + " " + bookNum.split(" ")[1] +
                        " transfers from normal to normal"); //
            }
        }
    }

    public void lendStuOutBook(String nowTime) {
        HashMap<String, ArrayList<String>> stuOutBook = libManage.getStuOutBook();
        for (String stuId : stuOutBook.keySet()) {
            for (int j = 0; j < stuOutBook.get(stuId).size(); j++) {
                String bookNum = stuOutBook.get(stuId).get(j); // ABCDE B-0012
                students.get(stuId).addOwnBooks(bookNum.split(" ")[1], bookNum.split(" ")[0], 0);
                System.out.println(nowTime + " purchasing department lent " +
                        bookNum.split(" ")[0] + "-" + bookNum.split(" ")[1] + " to " + stuId);
                System.out.println("(State) " + nowTime + " " + bookNum.split(" ")[1] +
                        " transfers from normal to borrowedPurchase"); //
                System.out.println(nowTime + " " + stuId + " borrowed " + bookNum.split(" ")[0] +
                        "-" + bookNum.split(" ")[1] + " from purchasing department");
                if (bookNum.split(" ")[1].charAt(0) == 'B') {  //是B类书
                    students.get(stuId).setGotBbook(true);
                    orderLibrarian.clearBbookReq(stuId, students.get(stuId), libManage);
                } else {
                    orderLibrarian.clearSameBookReq(stuId, students.get(stuId),
                            libManage, bookNum.split(" ")[1]);
                }
                stuOutBook.get(stuId).remove(j);
                j--;
            }
        }
    }

    public void purchaseBook(String nowTime) {
        HashMap<String, String> hasPurBook = new HashMap<>();  // <书号，书号>
        for (int i = 0; i < orderLibrarian.getOrderReqArray().size(); i++) {
            String req = orderLibrarian.getOrderReqArray().get(i);
            if (req.charAt(req.length() - 1) == 'Y' && !hasPurBook.containsKey(req.split(" ")[1])) {
                int num = libManage.getPurchaseNum(req.split(" ")[1]);
                Book additionalBook = new Book(req.split(" ")[1], schoolName, num);
                this.books.put(req.split(" ")[1], additionalBook);
                hasPurBook.put(req.split(" ")[1], req.split(" ")[1]);
                System.out.println(nowTime + " " + schoolName + "-" + req.split(" ")[1] +
                        " got purchased by purchasing department in " + schoolName);
                for (int k = 0; k < num; k++) {
                    libManage.addReturnedOut(req.split(" ")[1]);
                }

            }
        }
    }

    public void collectBook(String curDate) { //整理图书
        collectedBook = arrangeLibrarian.arrangeBook(borrowLibrarian.getBorrowShelf(),
                selfmachShelf, logisticsLibrarian.getLogisticsShelf(),
                libManage.getReturnOutBook()); // 每三天整理书籍
        selfmachShelf = new ArrayList<>();    // 清空书架
        borrowLibrarian.clearBorrowShelf();
        logisticsLibrarian.clearShelf();
        libManage.clearReturnedOutBook();
        orderLibrarian.informGetBook(books, curDate, students, collectedBook);
    }

    public boolean querySelfMachine(String nowTime, String bookNumber,
                                    String stuId, String schoolName) {    //向自助机查询
        this.flagLib = 0;
        if (books.containsKey(bookNumber) && books.get(bookNumber).isRemain()) {     // [本校有剩余]
            Book curBook = books.get(bookNumber);
            if (curBook.getCategory().equals("B")) {  //借B类书
                if (borrowLibrarian.askBorrow(students.get(stuId), curBook.getBookNumber())) {
                    borrow(curBook, stuId);
                    students.get(stuId).setGotBbook(true);
                    System.out.println(nowTime + " borrowing and returning librarian lent " +
                            schoolName + "-" + bookNumber + " to " + stuId);
                    System.out.println("(State) " + nowTime + " " + bookNumber +
                            " transfers from normal to borrowed"); //
                    System.out.println(nowTime + " " + stuId + " borrowed " + schoolName + "-"
                            + bookNumber + " from borrowing and returning librarian");
                    orderLibrarian.clearBbookReq(stuId, students.get(stuId), libManage); //预定清除B类请求
                } else {
                    this.flagLib = 1;
                    curBook.setAvailaCopies(1);   //留在管理员处
                    System.out.println(nowTime + " borrowing and returning librarian refused " +
                            "lending " + schoolName + "-" + bookNumber + " to " + stuId);
                    System.out.println("(State) " + nowTime + " " + bookNumber + " transfers from "
                            + "normal to normal"); //
                }
            } else if (curBook.getCategory().equals("C")) {  // 借C类书
                if (!students.get(stuId).queryOwnBook(bookNumber)) { //同一书号只能有一个副本
                    borrow(curBook, stuId);
                    System.out.println(nowTime + " self-service machine lent " +
                            schoolName + "-" + bookNumber + " to " + stuId);
                    System.out.println("(State) " + nowTime + " " + bookNumber +
                            " transfers from normal to borrowed"); //
                    System.out.println(nowTime + " " + stuId + " borrowed " + schoolName + "-" +
                            bookNumber + " from self-service machine");
                } else {
                    this.flagLib = 1;
                    this.selfmachShelf.add(bookNumber);
                    System.out.println(nowTime + " self-service machine refused lending " +
                            schoolName + "-" + bookNumber + " to " + stuId);
                    System.out.println("(State) " + nowTime + " " + bookNumber + " transfers from "
                            + "normal to normal"); //
                    curBook.setAvailaCopies(1);
                }
            }  // 直接省略A类书
        } else {    // [本校没余本]-需预约
            if (bookNumber.charAt(0) != 'A') {
                return false;
            }
        }
        return true;
    }

    public void borrow(Book curBook, String stuId) {   //借书 [Hashmap中存的Book类是引用，拿出来修改里面也跟着被修改]
        curBook.setAvailaCopies(1);  // opt为 1 时表示“借书”，剩余数量--
        students.get(stuId).addOwnBooks(curBook.getBookNumber(), stuId.split("-")[0], 1);
    }

    public void outSchoolBorrow(String bookNum) {  //校际借书
        this.books.get(bookNum).setAvailaCopies(1);
    }

    public void orderBook(String stuId, String bookNumber, String nowTime, String school,
                          ArrayList<String> orderMessage) {
        if (orderLibrarian.isContinueOrder(stuId, bookNumber)) {  //不超次数且不同号
            if (bookNumber.charAt(0) == 'B') {
                if (!students.get(stuId).isGotBbook()) {
                    orderLibrarian.addOrderReq(stuId, bookNumber, false);  //预定管理员添预定清单
                    orderMessage.add(nowTime + " " + stuId + " ordered " + school + "-" +
                            bookNumber + " from ordering librarian");
                }
            } else if (bookNumber.charAt(0) == 'C') {
                if (!students.get(stuId).isGotCbook(bookNumber)) {
                    orderLibrarian.addOrderReq(stuId, bookNumber, false);
                    orderMessage.add(nowTime + " " + stuId + " ordered " + school + "-" +
                            bookNumber + " from ordering librarian");
                }
            }  // 直接省略A类书
        }
    }

    public void orderPurchase(String stuId, String bookNumber, String nowTime, String school,
                              ArrayList<String> orderMessage) {
        if (orderLibrarian.isContinueOrder(stuId, bookNumber)) {  //不超次数且不同号
            if (bookNumber.charAt(0) == 'B') {
                if (!students.get(stuId).isGotBbook()) {
                    orderLibrarian.addOrderReq(stuId, bookNumber, true);  //预定管理员添预定清单
                    libManage.addPurchase(bookNumber);   //添加购书清单请求
                    orderMessage.add(nowTime + " " + stuId + " ordered " + school + "-" +
                            bookNumber + " from ordering librarian");
                }
            } else if (bookNumber.charAt(0) == 'C') {
                if (!students.get(stuId).isGotCbook(bookNumber)) {
                    orderLibrarian.addOrderReq(stuId, bookNumber, true);
                    libManage.addPurchase(bookNumber);   //添加购书清单请求
                    orderMessage.add(nowTime + " " + stuId + " ordered " + school + "-" +
                            bookNumber + " from ordering librarian");
                }
            }  // 直接省略A类书
        }
    }

    public void lostBook(String bookNumber) {
        books.get(bookNumber).setTotalCopies(1);  //书对应总数减少
        if (books.get(bookNumber).getTotalCopies() == 0) {   //数量为0时图书馆删去此书
            this.books.remove(bookNumber);
        }
    }

    public void lostBookStudent(String bookNumber, String studentId) {  //丢失书-学生行为
        if (bookNumber.charAt(0) == 'B') {
            students.get(studentId).setGotBbook(false);
        }
        students.get(studentId).removeOwnBook(bookNumber);
        students.get(studentId).getOwnBookFrom(bookNumber);
    }

    public void returnBook(String nowTime, String stuid, String bookNumber,
                           ArrayList<String> outMessage,
                           HashMap<String, Library> libHashmap) {
        String bookName = students.get(stuid).getOwnBook(bookNumber);  //得到ABCDE B-0010
        this.flagRepair = 0;
        int tmpflag = 0;
        flagRet = students.get(stuid).getOwnBookFrom(bookNumber);
        if (bookName.split(" ")[0].equals(this.schoolName)) { //本校还书
            if (bookNumber.charAt(0) == 'B') {
                tmpflag = returnBoutMessage(stuid, bookNumber, nowTime, schoolName);
                if (flagRet == 0) {   //
                    System.out.println("(State) " + nowTime + " " + bookNumber +
                            " transfers from borrowedPurchase to normal");
                } else if (flagRet == 1) {
                    System.out.println("(State) " + nowTime + " " + bookNumber +
                            " transfers from borrowed to normal");
                } //
                if (tmpflag == 1) {
                    this.flagRepair = 1;
                    System.out.println(nowTime + " " + schoolName + "-" + bookNumber +
                            " got repaired by logistics division in " + schoolName);
                    System.out.println("(State) " + nowTime + " " + bookNumber +
                            " transfers from normal to normal"); //
                    logisticsLibrarian.repairBook(bookNumber);
                } else {
                    borrowLibrarian.returnBook(bookNumber);
                }
                students.get(stuid).setGotBbook(false);
                students.get(stuid).removeOwnBook(bookNumber);
            } else if (bookNumber.charAt(0) == 'C') {
                tmpflag = returnCoutMessage(stuid, bookNumber, nowTime, schoolName);
                if (flagRet == 0) {   //
                    System.out.println("(State) " + nowTime + " " + bookNumber +
                            " transfers from borrowedPurchase to normal");
                } else if (flagRet == 1) {
                    System.out.println("(State) " + nowTime + " " + bookNumber +
                            " transfers from borrowed to normal");
                } //
                if (tmpflag == 1) {
                    this.flagRepair = 1;
                    System.out.println(nowTime + " " + schoolName + "-" + bookNumber +
                            " got repaired by logistics division in " + schoolName);
                    System.out.println("(State) " + nowTime + " " + bookNumber +
                            " transfers from normal to normal"); //
                    logisticsLibrarian.repairBook(bookNumber);
                } else {
                    this.selfmachShelf.add(bookNumber);
                }
                students.get(stuid).removeOwnBook(bookNumber);
            }
        } else {  //校际还书-第二天回原学校
            returnOutBook(nowTime, stuid, bookNumber, outMessage, libHashmap);
        }
    }

    public int returnBoutMessage(String stuid, String bookNumber, String nowTime, String school) {
        int tmpflag = 0;
        if (students.get(stuid).isSmear(bookNumber)) {
            System.out.println(nowTime + " " + stuid +
                    " got punished by borrowing and returning librarian");
            System.out.println(nowTime + " borrowing and returning librarian received "
                    + stuid + "'s fine");
            tmpflag = 1;   //图书是否要被修复的标志
        }

        if (students.get(stuid).getBookTime(bookNumber) > 30) {
            System.out.println(nowTime + " " + stuid +
                    " got punished by borrowing and returning librarian");
            System.out.println(nowTime + " borrowing and returning librarian received "
                    + stuid + "'s fine");
        }

        System.out.println(nowTime + " " + stuid + " returned " + school + "-" +
                bookNumber + " to borrowing and returning librarian");
        System.out.println(nowTime + " borrowing and returning librarian collected " +
                school + "-" + bookNumber + " from " + stuid);
        return tmpflag;
    }

    public int returnCoutMessage(String stuid, String bookNumber, String nowTime, String school) {
        int tmpflag = 0;
        if (students.get(stuid).isSmear(bookNumber)) {
            System.out.println(nowTime + " " + stuid +
                    " got punished by borrowing and returning librarian");
            System.out.println(nowTime + " borrowing and returning librarian received "
                    + stuid + "'s fine");
            tmpflag = 1;
        }

        if (students.get(stuid).getBookTime(bookNumber) > 60) {
            System.out.println(nowTime + " " + stuid +
                    " got punished by borrowing and returning librarian");
            System.out.println(nowTime + " borrowing and returning librarian received "
                    + stuid + "'s fine");
        }

        System.out.println(nowTime + " " + stuid + " returned " + school + "-" +
                bookNumber + " to self-service machine");
        System.out.println(nowTime + " self-service machine collected " +
                school + "-" + bookNumber + " from " + stuid);
        return tmpflag;
    }

    public void returnOutBook(String nowTime, String stuid, String bookNumber,
                              ArrayList<String> outMessage,
                              HashMap<String, Library> libHashmap) {
        String bookName = students.get(stuid).getOwnBook(bookNumber);  //得到ABCDE B-0010
        int tmpflag = 0;
        String bookSchool = bookName.split(" ")[0];
        if (bookNumber.charAt(0) == 'B') {
            tmpflag = returnBoutMessage(stuid, bookNumber, nowTime, bookSchool);
            System.out.println("(State) " + nowTime + " " + bookNumber +
                    " transfers from borrowedOrder to normal");  //
            if (tmpflag == 1) {
                this.flagRepair = 1;
                System.out.println(nowTime + " " + bookSchool + "-" + bookNumber +
                        " got repaired by logistics division in " + schoolName);
                System.out.println("(State) " + nowTime + " " + bookNumber +
                        " transfers from normal to normal"); //
            }
            students.get(stuid).setGotBbook(false);
            students.get(stuid).removeOwnBook(bookNumber);
        } else if (bookNumber.charAt(0) == 'C') {
            tmpflag = returnCoutMessage(stuid, bookNumber, nowTime, bookSchool);
            System.out.println("(State) " + nowTime + " " + bookNumber +
                    " transfers from borrowedOrder to normal");  //
            if (tmpflag == 1) {
                this.flagRepair = 1;
            }
            if (flagRepair == 1) {
                System.out.println(nowTime + " " + bookSchool + "-" + bookNumber +
                        " got repaired by logistics division in " + schoolName);
                System.out.println("(State) " + nowTime + " " + bookNumber +
                        " transfers from normal to normal"); //
            }
            students.get(stuid).removeOwnBook(bookNumber);
        }
        libHashmap.get(bookSchool).getLibManage().addReturnedOut(bookNumber);  //
        String str = nowTime + " " + bookSchool + "-" + bookNumber +
                " got transported by purchasing department in " + schoolName;
        System.out.println("(State) " + nowTime + " " + bookNumber +
                " transfers from normal to normal");
        outMessage.add(str);   //运输出消息
    } //校际还书

    public void addAllStudentBookTime() {   //
        for (String stu : this.students.keySet()) {
            this.students.get(stu).addTime();
        }
    }

}


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, Library> libHashMap = new HashMap<>();  // <校名，Library>
        ArrayList<String> requests = new ArrayList<>();
        ArrayList<String> schoolOrder = new ArrayList<>();  //学校输入顺序—第一关键字
        int t = scanner.nextInt();  // 学校数量
        for (int i = 0; i < t; i++) {
            String schoolName = scanner.next();  //输入学校名字 DCFER
            schoolOrder.add(schoolName);
            Library myLib = new Library(schoolName);
            libHashMap.put(schoolName, myLib);
            int n = scanner.nextInt();
            scanner.nextLine();
            for (int j = 0; j < n; j++) {
                String bookStr = scanner.nextLine();  // [B-0001 10 N]
                myLib.addBook(bookStr, schoolName);
            }
        }
        int m = scanner.nextInt();
        scanner.nextLine();
        for (int j = 0; j < m; j++) {
            String req = scanner.nextLine();   // [YYYY-mm-dd] <学校名称>-<学号> <操作> <类别号-序列号>
            requests.add(req);
        }

        process(requests, libHashMap, schoolOrder); //  处理

    }

    public static void process(ArrayList<String> requests, HashMap<String, Library> libHashMap,
                               ArrayList<String> schoolOrder) {
        LocalDate date = LocalDate.parse("2023-01-01");  // 2023-01-01 日期处理，但没有两边的括号
        int arrcnt = 0;     //
        for (int i = 0; i < 365; i++) {    //循环365天
            if (requests.isEmpty()) {
                return;
            }
            String curDate = "[" + date + "]";
            arrange(libHashMap, curDate, arrcnt, schoolOrder);  //整理日动作
            arrcnt++;
            ArrayList<String> curNoRemain = new ArrayList<>(); // 记录本校无余本的等待借书请求
            ArrayList<String> outTransMessage = new ArrayList<>();  //当天闭关后的校际运输信息输出
            ArrayList<String> orderMessage = new ArrayList<>(); // 当天校内预定输出(要按关键字顺序)
            while (!requests.isEmpty() && requests.get(0).split(" ")[0].equals(curDate)) {
                String nowTime = requests.get(0).split(" ")[0];
                String studentId = requests.get(0).split(" ")[1];  //<学校名-学号>
                String operation = requests.get(0).split(" ")[2];
                String bookNumber = requests.get(0).split(" ")[3]; // B-0010
                String schoolName = studentId.split("-")[0];
                Library curLib = libHashMap.get(schoolName);   // 当前学生对应学校图书馆
                if (!curLib.isContainStu(studentId)) {
                    Student newstu = new Student(studentId);
                    curLib.addStudent(newstu);
                }
                if (operation.equals("borrowed")) {  // 借书
                    System.out.println(curDate + " " + studentId + " queried " +
                            bookNumber + " from self-service machine");
                    System.out.println(curDate +
                            " self-service machine provided information of " + bookNumber);
                    boolean ret = curLib.querySelfMachine(nowTime, bookNumber,
                            studentId, schoolName);
                    if (ret == false) {
                        curNoRemain.add(requests.get(0));  // 把本校无余本时的请求加进去
                    }
                } else if (operation.equals("smeared")) {  // 毁书
                    curLib.getStudent(studentId).smearBook(bookNumber);
                } else if (operation.equals("lost")) {  // 丢书
                    System.out.println(nowTime + " " + studentId +
                            " got punished by borrowing and returning librarian");
                    System.out.println(nowTime + " borrowing and returning librarian received "
                            + studentId + "'s fine");
                    String lostLib = curLib.getStudent(studentId).getOwnBook(bookNumber);
                    libHashMap.get(lostLib.split(" ")[0]).lostBook(bookNumber);
                    curLib.lostBookStudent(bookNumber, studentId);
                } else if (operation.equals("returned")) {  // 还书
                    curLib.returnBook(nowTime, studentId, bookNumber, outTransMessage, libHashMap);
                }
                requests.remove(0); //
                if ((requests.isEmpty()) || (!requests.isEmpty() &&
                        !requests.get(0).split(" ")[0].equals(curDate))) {
                    curNoRemainReq(curNoRemain, libHashMap,
                            outTransMessage, orderMessage); //处理本校无余本情况下的请求
                    closeLibOutMessage(schoolOrder, outTransMessage, orderMessage); //闭馆后输出预定&&转运
                } // 当天结束
            }  //处理当天请求
            date = date.plusDays(1);
        }
    }

    public static void arrange(HashMap<String, Library> libHashMap, String curDate,
                               int arrcnt, ArrayList<String> schoolOrder) {
        for (String school: libHashMap.keySet()) {  //所有学生当前借阅时间加一
            libHashMap.get(school).addAllStudentBookTime();
        }
        for (String schoolKey : libHashMap.keySet()) {
            libHashMap.get(schoolKey).allClear(curDate); //全部图书运入
        }
        for (String schoolKey : libHashMap.keySet()) {  //校际图书发放
            libHashMap.get(schoolKey).lendStuOutBook(curDate);
        }
        if (arrcnt % 3 == 0) {  //每到整理日
            for (int y = 0; y < schoolOrder.size(); y++) {  //学校名为第一关键字
                libHashMap.get(schoolOrder.get(y)).purchaseBook(curDate);  //购书
            }
            System.out.println(curDate + " arranging librarian arranged all the books");
            for (int y = 0; y < schoolOrder.size(); y++) {  //学校名为第一关键字: 整理&&发放预定
                libHashMap.get(schoolOrder.get(y)).collectBook(curDate);
            }
        }
    }

    public static void curNoRemainReq(ArrayList<String> curNoRemain,
                                      HashMap<String, Library> libHashMap,
                                      ArrayList<String> outTransMessage,
                                      ArrayList<String> orderMessage) {
        while (!curNoRemain.isEmpty()) {
            String curNowTime = curNoRemain.get(0).split(" ")[0];
            String stuId = curNoRemain.get(0).split(" ")[1];  //<学校名-学号>
            String curBookNumber = curNoRemain.get(0).split(" ")[3]; // B-0010
            String curSchoolName = stuId.split("-")[0];  //当前学生所在学校
            Library curLibrary = libHashMap.get(curSchoolName);   // 当前学生对应学校图书馆
            String outSchool = "";  //外借学校名

            int flagOut = 0;  //
            for (String school : libHashMap.keySet()) { //检查校外是否有余本外借
                if (!school.equals(curSchoolName)) {
                    if (libHashMap.get(school).isBookOut(curBookNumber)) {
                        flagOut = 1;  // 走校际借阅
                        outSchool = school;
                        break;
                    }
                }
            }

            if (flagOut == 1) {  //校际借阅
                Student curStu = curLibrary.getStudent(stuId);
                if ((curBookNumber.charAt(0) == 'B' && !curStu.isGotBbook()) ||
                        (curBookNumber.charAt(0) == 'C' &&
                                !curStu.isGotCbook(curBookNumber))) {
                    if (curLibrary.getLibManage().isPermitOut(stuId, curBookNumber)) {
                        String str1 = curNowTime + " " + outSchool + "-" +
                                curBookNumber +
                                " got transported by purchasing department in "
                                + outSchool;
                        System.out.println("(State) " + curNowTime + " " + curBookNumber +
                                " transfers from normal to normal"); //
                        outTransMessage.add(str1);
                        libHashMap.get(outSchool).outSchoolBorrow(curBookNumber);
                        curLibrary.getLibManage().addStuOutBook(stuId,
                                outSchool, curBookNumber);
                    }
                }
            } else if (flagOut == 0) { //在本校预定管理员预定
                if (curLibrary.isContainBook(curBookNumber)) {  //标准校内预定
                    curLibrary.orderBook(stuId, curBookNumber,
                            curNowTime, curSchoolName, orderMessage);
                } else {    //加购新书
                    curLibrary.orderPurchase(stuId, curBookNumber,
                            curNowTime, curSchoolName, orderMessage);
                }
            }
            curNoRemain.remove(0);
        }
    }

    public static void closeLibOutMessage(ArrayList<String> schoolOrder,
                                          ArrayList<String> outTransMessage,
                                          ArrayList<String> orderMessage) {
        for (int p = 0; p < schoolOrder.size(); p++) {   //按关键字顺序输出校内预定
            String curOrder = schoolOrder.get(p);
            for (int q = 0; q < orderMessage.size(); q++) {
                String ret = orderMessage.get(q);
                if (ret.split(" ")[1].split("-")[0].equals(curOrder)) {
                    System.out.println(orderMessage.get(q));
                    System.out.println(ret.split(" ")[0] +
                            " ordering librarian recorded " +
                            ret.split(" ")[1] + "'s order of " + ret.split(" ")[3]);
                    orderMessage.remove(q);
                    q--;
                }
            }
        }
        for (int k = 0; k < outTransMessage.size(); k++) {  //输出校际转运输出
            System.out.println(outTransMessage.get(k));
        }
    }

}

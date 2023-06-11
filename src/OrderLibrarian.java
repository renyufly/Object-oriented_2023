import java.util.ArrayList;
import java.util.HashMap;

public class OrderLibrarian {   //预定管理员
    private ArrayList<String> orderReqArray;    // 按顺序的预定请求 [DCFED-20001234 B-0001]
    // 是请求购买的预定请求就变成 [BDCFR-20012345 B-0010 Y]  [校名-学号 书号 (Y)]
    private HashMap<String, ArrayList<String>> orderRequest;   // 预定清单 <校名-学号，预定书号清单>
    private HashMap<String, Integer> orderCnt;    // 每个学生当天预定次数  （第二天清零）
    private int flag;

    public OrderLibrarian() {
        this.orderReqArray = new ArrayList<>();
        this.orderRequest = new HashMap<>();
        this.orderCnt = new HashMap<>();
        this.flag = 0;
    }

    public ArrayList<String> getOrderReqArray() {
        return this.orderReqArray;
    }

    public void addOrderReq(String stuId, String bookNumber, boolean isPurchase) {
        if (isPurchase) {  //是请求购买的预定请求就变成 [BDCFR-20012345 B-0010 Y]
            this.orderReqArray.add(stuId + " " + bookNumber + " Y");
        } else {
            this.orderReqArray.add(stuId + " " + bookNumber);
        }
        if (!orderRequest.containsKey(stuId)) {
            ArrayList<String> arrayList = new ArrayList<>();
            this.orderRequest.put(stuId, arrayList);
        }
        orderRequest.get(stuId).add(bookNumber);
        if (!orderCnt.containsKey(stuId)) {
            this.orderCnt.put(stuId, 1);
        } else {
            int tmp = this.orderCnt.get(stuId);
            tmp++;
            this.orderCnt.put(stuId, tmp);
        }
    }

    public boolean isContinueOrder(String stuid, String bookNum) {
        if (orderCnt.containsKey(stuid) && orderCnt.get(stuid) >= 3) {
            return false;     //不再接受该同学当天的预约
        }
        if (orderRequest.containsKey(stuid)) {
            for (int i = 0; i < orderRequest.get(stuid).size(); i++) {
                if (orderRequest.get(stuid).get(i).equals(bookNum)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void clearCnt() {    // 清零
        for (String stuid : this.orderCnt.keySet()) {
            this.orderCnt.replace(stuid, 0);
        }
    }

    public void clearBbookReq(String stuid, Student student, LibManage libManage) {     //清除B类书的预定
        for (int i = 0; i < orderReqArray.size(); i++) {
            if (orderReqArray.get(i).split(" ")[0].equals(stuid)
                    && orderReqArray.get(i).split(" ")[1].charAt(0) == 'B') {
                student.clearBbook(orderReqArray.get(i).split(" ")[1]);
                if (orderReqArray.get(i).charAt(orderReqArray.get(i).length() - 1) == 'Y') {
                    libManage.decPurchase(orderReqArray.get(i).split(" ")[1]);
                } //取消购书请求
                orderReqArray.remove(i);
                i--;
            }
        }
    }

    public void clearSameBookReq(String stuid, Student student,
                                 LibManage libManage, String bookNum) {
        for (int i = 0; i < orderReqArray.size(); i++) {
            if (orderReqArray.get(i).split(" ")[0].equals(stuid)
                    && orderReqArray.get(i).split(" ")[1].equals(bookNum)) {
                student.clearBbook(orderReqArray.get(i).split(" ")[1]);
                if (orderReqArray.get(i).charAt(orderReqArray.get(i).length() - 1) == 'Y') {
                    libManage.decPurchase(orderReqArray.get(i).split(" ")[1]);
                } //取消购书请求
                return;
            }
        }
    }

    public void informGetBook(HashMap<String, Book> books, String nowTime,
                              HashMap<String, Student> students,
                              HashMap<String, Integer> collectedBook) {
        for (int i = 0; i < orderReqArray.size(); i++) {   // [校名-学号 书号 (Y)]
            String stuid = orderReqArray.get(i).split(" ")[0];
            String bookNum = orderReqArray.get(i).split(" ")[1];
            this.flag = 0;
            if (collectedBook.containsKey(bookNum) && collectedBook.get(bookNum) >= 1) {  //整理的书有被预约
                this.flag = 1;
                int tmp = collectedBook.get(bookNum);
                tmp--;
                collectedBook.put(bookNum, tmp);
                System.out.println(nowTime + " ordering librarian lent " + stuid.split("-")[0] +
                        "-" + bookNum + " to " + stuid);
                System.out.println("(State) " + nowTime + " " + bookNum +
                         " transfers from normal to borrowedOrder"); //
                System.out.println(nowTime + " " + stuid + " borrowed " + stuid.split("-")[0] +
                        "-" + bookNum + " from ordering librarian");
                students.get(stuid).addOwnBooks(bookNum, stuid.split("-")[0], 2);
                orderReqArray.remove(i);
                i--;
                int cnt = 0;
                if (bookNum.charAt(0) == 'B') {   //是B类要取消该人剩余所有B类预约
                    students.get(stuid).setGotBbook(true);
                    int iterate = 0;  // 遍历次数
                    for (int j = 0; j < orderReqArray.size(); j++) {
                        if (orderReqArray.get(j).split(" ")[0].equals(stuid) &&
                                orderReqArray.get(j).split(" ")[1].charAt(0) == 'B') {
                            students.get(stuid).clearBbook(orderReqArray.get(j).split(" ")[1]);
                            orderReqArray.remove(j);
                            if (iterate <= i) {
                                cnt++;
                            }
                            j--;
                        }
                        iterate++;
                    }
                }
                i = i - cnt;
            }
        }
        for (String bookNum : collectedBook.keySet()) {
            if (collectedBook.get(bookNum) > 0) {
                int num = collectedBook.get(bookNum);
                for (int k = 0; k < num; k++) {
                    books.get(bookNum).setAvailaCopies(2); //剩余数量++
                }
            }
        }
    }

}
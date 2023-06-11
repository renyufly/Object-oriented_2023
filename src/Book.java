public class Book {
    private String category;  // 类别号
    private String bookNumber;  // 书号 [类别号-序列号]
    private int totalCopies;   //总副本数量
    private int availableCopies;   //剩余可借数量
    private String school;   // 所属学校
    private boolean isLendOut;   // 是否外借 （true是允许外借）

    public Book(String bookstr, String school) {
        this.category = bookstr.substring(0, 6).split("-")[0];
        this.bookNumber = bookstr.substring(0, 6);
        this.totalCopies = Integer.parseInt(bookstr.split(" ")[1]);
        this.availableCopies = this.totalCopies;
        this.school = school;
        if (bookstr.split(" ")[2].equals("Y")) {
            this.isLendOut = true;
        } else {
            this.isLendOut = false;
        }
    }

    public Book(String bookNumber, String school, int copies) {
        this.category = bookNumber.split("-")[0];
        this.bookNumber = bookNumber;
        this.totalCopies = copies;   //总副本数量
        this.availableCopies = 0;
        this.school = school;   // 所属学校
        this.isLendOut = true;  //新书默认允许外借
    }

    public boolean isRemain() {     //是否有剩余
        if (availableCopies >= 1) {
            return true;
        }
        return false;
    }

    public boolean isPermitLendOut() {   // 是否允许外借
        return this.isLendOut;
    }

    public String getCategory() {
        return this.category;
    }

    public String getBookNumber() {
        return this.bookNumber;
    }

    public void setAvailaCopies(int opt) {
        if (opt == 1) {    // opt为 1 时表示“借书”，剩余数量--
            this.availableCopies--;
        } else if (opt == 2) {   // opt为 2 时表示“还书”，剩余数量++
            this.availableCopies++;
        }
    }

    public String getSchool() {
        return this.school;
    }

    public void setTotalCopies(int opt) {
        if (opt == 1) {   // opt为 1 时表示“丢失”，总数量--
            this.totalCopies--;
        }
    }

    public int getTotalCopies() {
        return this.totalCopies;
    }

}

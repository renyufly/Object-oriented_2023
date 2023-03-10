public class Lexer {
    private final String input;

    private int pos = 0;

    private String curToken;

    public Lexer(String input) {
        this.input = exeStr(input);
        this.next();                    //创建时自动读取下一个符号
    }

    private String exeStr(String input) {      //处理简化输入字符串
        String input1 = input;
        input1 = input1.replaceAll("[ \t]+", "");
        input1 = input1.replaceAll("\\+\\+", "+");
        input1 = input1.replaceAll("--", "+");
        input1 = input1.replaceAll("\\+-", "-");
        input1 = input1.replaceAll("-\\+", "-");
        input1 = input1.replaceAll("\\*\\*", "^");     //用^代替**乘方，记得最后换回来 ！！！
        input1 = input1.replaceAll("\\^\\+", "^");     //把乘方后非负数的正号去掉

        return input1;
    }

    private String getNumber() {                 //获得数字
        StringBuilder sb = new StringBuilder();
        int flag = input.length();
        for (int i = pos; i < input.length(); i++) {
            if (!Character.isDigit(input.charAt(i))) {
                flag = i;
                break;
            }
        }
        int nozero = -1;
        for (int i = pos; i < flag; i++) {
            if (input.charAt(i) != '0') {
                nozero = i;                  //非前导0处
                break;
            }
        }
        if (nozero == -1) {
            sb.append(0);               //数字全0
            pos = flag;
        } else {
            pos = nozero;
            while (pos < flag) {
                sb.append(input.charAt(pos));       //去除前导0
                ++pos;
            }
        }
        return sb.toString();
    }

    public void next() {       //读取下一个非终结符
        if (pos == input.length()) {
            return;
        }
        char c = input.charAt(pos);
        if (Character.isDigit(c)) {   //读取到数字
            curToken = getNumber();
        } else if ("()+-*^xyz".indexOf(c) != -1) {   //符号: +, -, *, **[^], (, ), x, y, z
            pos += 1;
            curToken = String.valueOf(c);
        } else if ("fgh".indexOf(c) != -1) {          //读自定义函数
            StringBuilder sb = new StringBuilder();
            sb.append(c);   //第一个字符即自定义函数名
            pos = pos + 2;   //跳过紧跟的左括号
            int flag = 1;  //标记匹配括号数量，左括号+1，右括号-1
            for (int i = pos; i < input.length(); i++) {
                sb.append(input.charAt(i));
                if (input.charAt(i) == '(') {
                    flag++;
                } else if (input.charAt(i) == ')') {
                    flag--;
                }
                if (flag == 0) {    // 最终会读进去最后的右括号
                    pos = i + 1;
                    break;
                }
            }
            sb.deleteCharAt(sb.length() - 1);  //删去不要的最后一个右括号。 sb第一个字符即函数名
            curToken = sb.toString();
        } else if ("sc".indexOf(c) != -1) {     // s[in], c[os]
            StringBuilder sb = new StringBuilder();
            if (c == 's') {
                sb.append("sin");
            } else {
                sb.append("cos");
            }
            pos = pos + 4;
            matchBracket(sb);
            sb.deleteCharAt(sb.length() - 1);  //删去不要的最后一个右括号。 sb前三个个字符即三角函数名
            curToken = sb.toString();
        } else if (c == 'd') {      //识别求导算子 dx, dy, dz
            StringBuilder sb = new StringBuilder();
            sb.append(c);         //sb第一个字符为d
            pos = pos + 1;
            sb.append(input.charAt(pos));   //sb第二个字符为对谁偏导
            pos = pos + 2;
            matchBracket(sb);
            sb.deleteCharAt(sb.length() - 1);  //删去最后一个右括号。sb前两个字符为dx/dy/dz
            curToken = sb.toString();
        }
    }

    private void matchBracket(StringBuilder sb) {
        int flag = 1;      //匹配括号
        for (int i = pos; i < input.length(); i++) {
            sb.append(input.charAt(i));
            if (input.charAt(i) == '(') {
                flag++;
            } else if (input.charAt(i) == ')') {
                flag--;
            }
            if (flag == 0) {    // 最终会读进去最后的右括号
                pos = i + 1;
                break;
            }
        }
    }

    public String peek() {           //返回读取的非终结符
        return this.curToken;
    }
}

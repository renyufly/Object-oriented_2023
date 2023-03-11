import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String n = scanner.nextLine();
        HashMap<String, Custom> customFun = new HashMap<>();   // customFun存着自定义函数的定义
        for (int i = 0; i < Integer.parseInt(n); i++) {
            Custom function = new Custom(scanner.nextLine(), customFun);
            customFun.put(function.getName(), function);

        }
        String input = scanner.nextLine();       //next不能得到带空格的字符串

        input = input.replaceAll("[ \t]+", "");
        input = input.replaceAll("\\*\\+", "*");
        input = input.replaceAll("\\*-", "*(-1)*");
        input = repeat(input);

        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer, customFun);
        Expr expr = parser.parseExpr();     // 首先解析表达式，之后再递归下降分析

        String result = expr.toString();    //结果输出

        result = result.replaceAll("\\+-", "-");
        result = result.replaceAll("-\\+", "-");
        result = result.replaceAll("--", "+");
        result = result.replaceAll("\\+\\+", "+");
        result = result.replaceAll("-1\\*", "-");
        result = result.replaceAll("\\+1\\*", "+");
        StringBuilder sb = new StringBuilder();
        sb.append(result);
        if (sb.charAt(0) == '+') {
            sb.deleteCharAt(0);
        }
        if (sb.charAt(sb.length() - 1) == '+' || sb.charAt(sb.length() - 1) == '-') {
            sb.deleteCharAt(sb.length() - 1);
        }
        String sbstr = sb.toString();
        StringBuilder ret = new StringBuilder();
        int nozero = 0;
        if ((sbstr.charAt(0) != '0' && sbstr.length() > 1)
                || (sbstr.charAt(0) == '0' && sbstr.length() == 1)) {
            ret.append(sbstr.charAt(0));
            nozero = 1;
        }
        for (int i = 1; i < sbstr.length(); i++) {
            if ((i + 1 < sbstr.length()) && (sbstr.charAt(i) == '+' || sbstr.charAt(i) == '-')
                    && sbstr.charAt(i + 1) == '0') {
                i = i + 1;
            } else {
                ret.append(sbstr.charAt(i));
            }
        }
        if (ret.charAt(0) == '+') {
            ret.deleteCharAt(0);
        }
        System.out.println(ret.toString());
    }

    public static String repeat(String result) {
        String result1 = result;
        StringBuilder sb = new StringBuilder();
        sb.append(result1);

        result1 = sb.toString();

        result1 = result1.replaceAll("--", "+");
        result1 = result1.replaceAll("\\+\\+\\+\\+", "+");
        result1 = result1.replaceAll("\\+\\+\\+", "+");
        result1 = result1.replaceAll("\\+\\+", "+");
        result1 = result1.replaceAll("-\\+", "-");
        result1 = result1.replaceAll("\\+-", "-");

        result1 = result1.replaceAll("\\*\\+", "*");

        StringBuilder sb2 = new StringBuilder();
        sb2.append(result1);
        int len = sb2.length();
        for (int i = 0; i < len; i++) {
            if (sb2.charAt(i) == '+') {                //去除头部+号
                sb2.deleteCharAt(i);
                i = i - 1;
            } else {
                break;
            }
            len = sb2.length();
        }
        result1 = sb2.toString();
        return result1;
    }

}

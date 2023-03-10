import java.util.HashMap;

public class Custom {            //自定义函数类
    private String name;      //函数名
    private int paraCount;    //参数个数
    private String formula;   //自定义函数的公式
    private char[] parameter;    //形参名

    public Custom(String str, HashMap<String, Custom> customFun) {       //构造
        String str1 = new String(str);
        str1 = str1.replaceAll("[ \t]+", "");
        this.name = str1.substring(0, 1);       //函数名
        this.paraCount = 0;
        this.parameter = new char[]{'a', 'b', 'd'};
        String[] arr = str1.split("=");
        int flag = 0;
        for (int i = 2; i < arr[0].length() - 1; i++) {   //查找函数声明里的变量
            if (arr[0].charAt(i) != ',') {
                this.parameter[flag] = arr[0].charAt(i);
                flag++;
                this.paraCount++;
            }
        }
        StringBuilder sb = new StringBuilder();
        Lexer lexer1 = new Lexer(arr[1]);
        Parser parser1 = new Parser(lexer1, customFun);
        sb.append(parser1.parseExpr().toString());  //
        for (int j = 0; j < sb.length(); j++) {     //构造自定义函数公式，将里面的形参替换为flag1, flag2, flag3,方便之后替换
            if (sb.charAt(j) == this.parameter[0]) {
                sb.replace(j, j + 1, "flag1");
                j = j + 4;
            } else if (this.paraCount > 1 && sb.charAt(j) == this.parameter[1]) {
                sb.replace(j, j + 1, "flag2");
                j = j + 4;
            } else if (this.paraCount > 2 && sb.charAt(j) == this.parameter[2]) {
                sb.replace(j, j + 1, "flag3");
                j = j + 4;
            }
        }
        this.formula = sb.toString();
    }

    public String getName() {               // 获得函数名
        return this.name;
    }

    public Expr expand(String para, HashMap<String, Custom> customFun) {  //返回展开后的表达式字符串，para为传进整体参数
        //先对传入的字符串实参每个实参做expr解析，得到最简表达式
        if (this.paraCount == 1) {
            String str1 = exeExpr(para, customFun).toString();   //Expr转成String
            String ret = this.formula;       // 深拷贝 (因为字符串不可变)
            ret = ret.replaceAll("flag1", "(" + str1 + ")");   //替换要加括号
            return exeExpr(ret, customFun);
        } else if (this.paraCount == 2) {   //  5+x,g(,)
            //   StringBuilder sb = new StringBuilder();
            int comma = backComma(para);
            String ret = this.formula;
            String para1 = para.substring(0, comma);
            String para2 = para.substring(comma + 1);    //收集两个实参str
            String str1 = exeExpr(para1, customFun).toString();
            String str2 = exeExpr(para2, customFun).toString();
            ret = ret.replaceAll("flag1", "(" + str1 + ")");
            ret = ret.replaceAll("flag2", "(" + str2 + ")");
            return exeExpr(ret, customFun);
        } else {              //三个实参， 5+x,g(,),h()+6
            int comma = backComma(para);
            String para1 = para.substring(0, comma);
            int flag = 0;
            int comma1 = 0;
            for (int j = comma + 1; j < para.length(); j++) {
                if (flag == 0 && para.charAt(j) == ',') {
                    comma1 = j;             // 记录后两个实参间的逗号位置
                    break;
                }
                if (para.charAt(j) == '(') {
                    flag++;
                } else if (para.charAt(j) == ')') {
                    flag--;
                }
            }
            String para2 = para.substring(comma + 1, comma1);
            String para3 = para.substring(comma1 + 1);
            String ret = this.formula;
            String str1 = exeExpr(para1, customFun).toString();
            String str2 = exeExpr(para2, customFun).toString();
            String str3 = exeExpr(para3, customFun).toString();
            ret = ret.replaceAll("flag1", "(" + str1 + ")");
            ret = ret.replaceAll("flag2", "(" + str2 + ")");
            ret = ret.replaceAll("flag3", "(" + str3 + ")");
            return exeExpr(ret, customFun);
        }
    }

    private Expr exeExpr(String para1, HashMap<String, Custom> customFun1) {
        Lexer lexer1 = new Lexer(para1);
        Parser parser1 = new Parser(lexer1, customFun1);
        Expr expr1 = parser1.parseExpr();
        return expr1;
    }

    private int backComma(String para) {
        int flag = 0;
        int comma = 0;
        for (int i = 0; i < para.length(); i++) {
            if (flag == 0 && para.charAt(i) == ',') {  // para字符串的i位置是前两个实参之间的逗号
                comma = i;
                break;
            }
            if (para.charAt(i) == '(') {
                flag++;
            } else if (para.charAt(i) == ')') {
                flag--;
            }
        }
        return comma;
    }

}

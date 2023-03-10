import java.math.BigInteger;
import java.util.HashMap;

public class Parser {
    private final Lexer lexer;
    private final HashMap<String, Custom> customFun;  //定义好的自定义函数模板

    public Parser(Lexer lexer, HashMap<String, Custom> customFun) {
        this.lexer = lexer;
        this.customFun = customFun;
    }

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.customFun = new HashMap<>();
    }

    public Expr parseExpr() {          //解析表达式
        Expr expr = new Expr();
        String sign1 = "";
        if (lexer.peek().equals("+") || lexer.peek().equals("-")) {    //识别到表达式前面的正负号
            sign1 = lexer.peek();
            lexer.next();
            Term term = parseTerm();
            if (sign1.equals("-")) {
                term.negCoefficient();
            }

            expr.addTerm(term);
        } else {
            expr.addTerm(parseTerm());     //项
        }


        while (lexer.peek().equals("+") || lexer.peek().equals("-")) {
            String sign = lexer.peek();
            lexer.next();
            Term term = parseTerm();
            if (sign.equals("-")) {
                term.negCoefficient();
            }
            expr.addTerm(term);

        }

        return expr;
    }

    public Term parseTerm() {        //解析项
        Term term = new Term();
        term.addFactor(parseFactor());

        while (lexer.peek().equals("*")) {
            lexer.next();
            term.addFactor(parseFactor());
        }
        return term;
    }

    public Factor parseFactor() {      //解析因子
        if (lexer.peek().equals("(")) {         //表达式因子
            lexer.next();
            Factor expr = parseExpr();     // 只要有括号就执行解析，支持嵌套括号
            lexer.next();                //读掉右括号或其他
            if (lexer.peek().equals("^")) {          //识别表达式因子的指数
                lexer.next();
                int index = Integer.parseInt(lexer.peek());
                ((Expr) expr).setIndex(index);     // 设置指数
                lexer.next();       //读下一个符
            }
            return indexExpr((Expr) expr, ((Expr) expr).getIndex());  /*  若有指数，对表达式展开    */
        } else if (lexer.peek().equals("x") || lexer.peek().equals("y")
                || lexer.peek().equals("z")) {  // 是幂函数
            Pow power = new Pow(lexer.peek());
            lexer.next();
            if (lexer.peek().equals("^")) {
                lexer.next();
                int index = Integer.parseInt(lexer.peek());
                power.setIndex(index);
                lexer.next();
            }
            return power;
        } else if (lexer.peek().charAt(0) == 'f' || lexer.peek().charAt(0) == 'g'
                || lexer.peek().charAt(0) == 'h') {  //调用自定义函数
            Custom cus = this.customFun.get(lexer.peek().substring(0, 1));
            Factor expr = cus.expand(lexer.peek().substring(1), this.customFun);
            lexer.next();
            return expr;
        } else if (lexer.peek().charAt(0) == 's' || lexer.peek().charAt(0) == 'c') {       // 三角函数
            Trigo trigo = new Trigo(lexer.peek().substring(0, 3));
            trigo.getexpr(lexer.peek().substring(3), this.customFun);
            lexer.next();
            if (lexer.peek().equals("^")) {          //识别表达式因子的指数
                lexer.next();
                trigo.setIndex(Integer.parseInt(lexer.peek()));     // 设置指数
                lexer.next();       //读下一个符
            }
            return trigo;
        } else if(lexer.peek().charAt(0) == 'd') {       //求导因子
            String var = String.valueOf(this.lexer.peek().charAt(1));
            Lexer lexer1 = new Lexer(this.lexer.peek().substring(2));
            Parser parser1 = new Parser(lexer1, customFun);
            Expr expr1 = parser1.parseExpr();
            Derivation deri = new Derivation();
            lexer.next();
            return deri.differentiate(var, expr1);   //表达式求导
        } else {                               //数字因子
            if (lexer.peek().equals("-")) {
                lexer.next();
                BigInteger num = new BigInteger("-" + lexer.peek());
                lexer.next();
                return new Number(num);      //返回一个负数
            } else if (lexer.peek().equals("+")) {
                lexer.next();
            }
            BigInteger num = new BigInteger(lexer.peek());
            lexer.next();
            return new Number(num);       //返回数字
        }
    }

    private Expr indexExpr(Expr expr, BigInteger index1) {
        Expr retexpr = new Expr();
        BigInteger zero = new BigInteger("0");
        if (index1.equals(zero)) {
            Term one = new Term();
            retexpr.addTerm(one);          //指数为0，就是1
        } else {
            retexpr = (Expr) expr;
            for (int i = 1; i < Integer.valueOf(index1.toString()); i++) {
                Polymulti polymulti = new Polymulti();
                retexpr = polymulti.mulPoly(retexpr, (Expr) expr);
            }
        }
        return retexpr;
    }

}

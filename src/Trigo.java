import java.math.BigInteger;
import java.util.HashMap;

public class Trigo implements Factor {     // 三角函数
    private String name;     // sin or cos
    private Expr expr;       // 存内部表达式
    private BigInteger index;   //三角函数的指数

    public Trigo(String str) {
        this.name = str;
        this.index = new BigInteger("1");
    }

    public void setIndex(int index) {
        BigInteger index1 = BigInteger.valueOf(index);
        this.index = index1;
    }

    public void setIndex1(BigInteger index1) {
        this.index = new BigInteger(String.valueOf(index1));
    }

    public void setExpr(Expr expr) {
        this.expr = new Expr();
        this.expr = expr;
    }

    public void getexpr(String input, HashMap<String, Custom> customFun) {     // 获得三角函数内部的表达式
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer, customFun);
        this.expr = parser.parseExpr();
    }

    public String getName() {
        return this.name;
    }

    public Expr backExpr() {
        return this.expr;
    }

    public BigInteger getIndex() {
        return this.index;
    }

    @Override
    public boolean equals(Object trigo) {
        if (((Trigo) trigo).getIndex().equals(this.index)
                && ((Trigo) trigo).getName().equals(this.name)
                && ((Trigo) trigo).backExpr().equals(this.expr)) {
            return true;
        } else {
            return false;
        }
    }

}

import java.math.BigInteger;
import java.util.ArrayList;

public class Term {
    private final ArrayList<Factor> factors;    // 一个项里存着哪些因子

    private BigInteger coefficient;       // 整个项的系数

    public Term() {
        this.factors = new ArrayList<>();
        this.coefficient = new BigInteger("1");    //初始系数为1
    }

    public ArrayList<Factor> getFactors() {
        return this.factors;
    }

    public void setCoefficient(BigInteger cof) {
        this.coefficient = cof;
    }

    public BigInteger getCoefficient() {
        return this.coefficient;
    }

    public void negCoefficient() {   //系数取反
        BigInteger neg = new BigInteger("-1");
        this.coefficient = this.coefficient.multiply(neg);
    }

    public void addFactor(Factor factor) {     //添加因子
        BigInteger zero = new BigInteger("0");
        if (factor instanceof Number) {
            coefficient = coefficient.multiply(((Number) factor).getNum());  //因子是数就直接乘到系数
        } else {         //因子是幂函数就看有没有重复
            if (factor instanceof Pow) {
                if (!((Pow) factor).getIndex().equals(zero)) {      //指数为0就是1，不管
                    int flag = 0;
                    for (int i = 0; i < this.factors.size(); i++) {
                        if (this.factors.get(i) instanceof Pow && ((Pow) this.factors.get(i))
                                .getVarname().equals(((Pow) factor).getVarname())) {
                            ((Pow) this.factors.get(i)).setIndex(((Pow) this.factors.get(i))
                                    .getIndex().add(((Pow) factor).getIndex()));
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        this.factors.add(factor);
                    }
                }
            } else if (factor instanceof Trigo) {    //三角函数化简
                if( !((Trigo) factor).getIndex().equals(zero)) { //指数为0就不管
                    this.factors.add(factor);
                }
            } else {
                this.factors.add(factor);
            }
        }
    }

    public String toString() {
        bubbleSort(this.factors);
        StringBuilder sb = new StringBuilder();
        BigInteger test1 = new BigInteger("1");
        BigInteger zero = new BigInteger("0");
        if (!this.coefficient.equals(test1) || (this.coefficient.equals(test1)
                && this.factors.size() == 0)) {
            sb.append(this.coefficient);   // 首先打印系数
        }
        for (int i = 0; i < this.factors.size(); i++) {  //对每个因子查看类型选择输出
            Factor fact = this.factors.get(i);
            if (fact instanceof Pow) {
                if( !((Pow) fact).getIndex().equals(zero)) {  //指数不为0
                    if (i != 0 || (i == 0 && !this.coefficient.equals(test1))) {
                        sb.append("*");
                    }
                    sb.append(((Pow) fact).getVarname());
                    if (!((Pow) fact).getIndex().equals(test1)) {
                        sb.append("**");
                        sb.append(((Pow) fact).getIndex());
                    }
                }
            } else if (fact instanceof Trigo) {
                if( !((Trigo) fact).getIndex().equals(zero)) {
                    if (i != 0 || (i == 0 && !this.coefficient.equals(test1))) {
                        sb.append("*");
                    }
                    sb.append(((Trigo) fact).getName());
                    sb.append("(");
                    Expr expr = ((Trigo) fact).backExpr();  //返回三角函数里的函数，判断是否要加括号
                    String str = expr.toString();
                    int bracket = backBracket(str);
                    if (bracket == 1) {   //里面是表达式因子
                        sb.append("(");
                        sb.append(expr.toString());
                        sb.append(")");
                    } else {
                        sb.append(expr.toString());
                    }

                    sb.append(")");
                    if (!((Trigo) fact).getIndex().equals(test1)) {
                        sb.append("**");
                        sb.append(((Trigo) fact).getIndex());
                    }
                }
            } else if (fact instanceof Expr) {      // 表达式因子要和现在已打印出的项相乘，拆开括号
                if (i != 0) {
                    Lexer lexer = new Lexer(sb.toString());   // 将项里已结合好的字符串转成表达式
                    Parser parser = new Parser(lexer);
                    Expr expr = parser.parseExpr();
                    Polymulti poly = new Polymulti();
                    sb = new StringBuilder(poly.mulPoly(expr, (Expr) fact).toString());  //新替换之前表达式
                } else {
                    Lexer lexer = new Lexer(this.coefficient.toString());   // 将项里已结合好的字符串转成表达式
                    Parser parser = new Parser(lexer);
                    Expr expr = parser.parseExpr();
                    Polymulti poly = new Polymulti();
                    sb = new StringBuilder(poly.mulPoly(expr, (Expr) fact).toString());  //新替换之前表达式
                }
            }
        }
        return sb.toString();
    }

    private int backBracket(String str) {
        int bracket = 0;
        int flag = 0;
        for (int j = 0; j < str.length(); j++) {
            if (flag == 0 && ("+-".indexOf(str.charAt(j)) != -1 ||
                    (str.charAt(j) == '*' && str.charAt(j + 1) != '*'))) {
                bracket = 1;
                break;
            }
            if (str.charAt(j) == '(') {
                flag++;
            } else if (str.charAt(j) == ')') {
                flag--;
            }
        }
        return bracket;
    }

    private void bubbleSort(ArrayList<Factor> fact) {  //表达式挪到最后头
        int len = fact.size();
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                if (fact.get(j) instanceof Expr && (fact.get(j + 1) instanceof Pow
                        || fact.get(j + 1) instanceof Trigo)) {
                    if (fact.get(j + 1) instanceof Pow) {
                        Pow temp = new Pow(((Pow) fact.get(j + 1)).getVarname());
                        temp.setIndex(((Pow) fact.get(j + 1)).getIndex());
                        fact.set(j + 1, fact.get(j));
                        fact.set(j, temp);
                    } else {
                        Trigo temp = new Trigo(((Trigo) fact.get(j + 1)).getName());
                        temp.setIndex1(((Trigo) fact.get(j + 1)).getIndex());
                        temp.setExpr(((Trigo) fact.get(j + 1)).backExpr());
                        fact.set(j + 1, fact.get(j));
                        fact.set(j, temp);
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object term) {
        ArrayList<Integer> hasfind = new ArrayList<Integer>(); //记录已匹配编号
        int flag = 0;
        if (this.factors.size() == ((Term) term).getFactors().size()
                && this.coefficient.equals(((Term) term).getCoefficient())) {  //改bug
            for (int i = 0; i < this.factors.size(); i++) {
                int flag1 = 0;
                for (int j = 0; j < ((Term) term).getFactors().size(); j++) {
                    if (this.factors.get(i) instanceof Pow &&
                            ((Term) term).getFactors().get(j) instanceof Pow) {
                        if (this.factors.get(i).equals(((Term) term).getFactors().get(j))
                                && (!hasfind.contains(j))) {
                            hasfind.add(j);
                            flag1 = 1;
                            break;
                        }
                    } else if (this.factors.get(i) instanceof Trigo
                            && ((Term) term).getFactors().get(j) instanceof Trigo) {
                        if (this.factors.get(i).equals(((Term) term).getFactors().get(j))
                                && (!hasfind.contains(j))) {
                            hasfind.add(j);
                            flag1 = 1;
                            break;
                        }
                    } else if (this.factors.get(i) instanceof Expr
                            && ((Term) term).getFactors().get(j) instanceof Expr) {
                        if (this.factors.get(i).equals(((Term) term).getFactors().get(j))
                                && (!hasfind.contains(j))) {
                            hasfind.add(j);
                            flag1 = 1;
                            break;
                        }
                    }
                }
                if (flag1 == 0) {    //这一项没有匹配
                    flag = 0;
                    break;
                } else {
                    flag = 1;
                }
            }
            if (flag == 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }

}

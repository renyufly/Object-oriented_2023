import java.math.BigInteger;
import java.util.ArrayList;

public class Expr implements Factor {

    private ArrayList<Term> terms;     // 一个表达式里面存着有哪些项
    private BigInteger index;      //该表达式因子的指数
    private String sign;        // 该表达式整体的符号 （里面的各项之间的连接+-号都需要变号）

    public Expr() {               // 构造
        this.terms = new ArrayList<>();
        this.index = new BigInteger("1");         //初始指数
        this.sign = "+";    //默认正号
    }

    public Expr(Expr expr) {
        this.index = expr.getIndex();
        this.sign = expr.getSign();
        this.terms = new ArrayList<>(expr.getTerms());
    }

    public ArrayList<Term> getTerms() {
        return this.terms;
    }

    public void addTerm(Term term) {     //向表达式里添加项
        int flag = 0;
        for (int i = 0; i < this.terms.size(); i++) {
            if (this.terms.get(i).getFactors().size() == 0
                    && term.getFactors().size() == 0) {   //常数项
                this.terms.get(i).setCoefficient(this.terms.get(i).getCoefficient()
                        .add(term.getCoefficient()));
                flag = 1;
                break;
            } else if (this.terms.get(i).equals(term)) {   //重写term的equal
                this.terms.get(i).setCoefficient(this.terms.get(i)
                        .getCoefficient().add(term.getCoefficient()));
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            this.terms.add(term);
        }

    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return this.sign;
    }

    public void negTerm() {     //项变号
        for (int i = 0; i < this.terms.size(); i++) {
            this.terms.get(i).negCoefficient();
        }
    }

    public void setIndex(int index) {             // 设置指数
        BigInteger index1 = BigInteger.valueOf(index);
        this.index = index1;
    }

    public BigInteger getIndex() {
        return this.index;
    }

    public String toString() {    //
        StringBuilder sb = new StringBuilder();
        BigInteger zero = new BigInteger("0");
        if (this.sign.equals("-")) {
            for (int i = 0; i < this.terms.size(); i++) {
                this.terms.get(i).negCoefficient();
            }
        }

        int flag0 = 0;
        for (int j = 0; j < this.terms.size(); j++) {
            if (this.terms.get(j).getCoefficient().equals(zero)) {   //系数为0
                if (flag0 != 1) {
                    if (j != 0) {
                        sb.append("+");
                    }
                    sb.append("0");
                    flag0 = 1;
                }

            } else {
                if (j != 0) {
                    sb.append("+");
                }
                sb.append(this.terms.get(j).toString());
            }
            /*else /*if (this.terms.get(j).getCoefficient().compareTo(zero) > 0) {  //系数大于0
                if (j != 0) {
                    sb.append("+");
                }
                sb.append(this.terms.get(j).toString());
            } else
            }{
                sb.append(this.terms.get(j).toString());
            }  */
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object expr) {
        if (((Expr) expr).getSign().equals(this.sign) && ((Expr) expr)
                .getIndex().equals(this.index)) {
            if (this.terms.size() == ((Expr) expr).getTerms().size()) {
                ArrayList<Integer> hasfind = new ArrayList<>();
                int flag = 0;
                for (int i = 0; i < this.terms.size(); i++) {
                    int flag1 = 0;
                    for (int j = 0; j < ((Expr) expr).getTerms().size(); j++) {
                        if (this.terms.get(i).equals(((Expr) expr).getTerms().get(j))
                                && (!hasfind.contains(j))) {
                            hasfind.add(j);
                            flag1 = 1;
                            break;
                        }
                    }
                    if (flag1 == 0) {
                        flag = 0;
                        break;
                    } else {
                        flag = 1;
                    }
                }
                if (flag == 1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}

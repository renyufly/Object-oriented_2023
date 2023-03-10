import java.math.BigInteger;
import java.util.ArrayList;

public class Derivation {      //求导

    public Derivation() {

    }

    public Expr differentiate(String var, Expr expr) {   //对表达式求导 [var-对谁偏导]
        Expr ret = new Expr();    //结果表达式
        BigInteger zero = new BigInteger("0");
        BigInteger one = new BigInteger("1");
        for (int i = 0; i < expr.getTerms().size(); i++) {   //对表达式里每一项求导
            Term term = expr.getTerms().get(i);
            if (term.getFactors().size() == 0) {  //是常数项
                term.setCoefficient(zero); //项为0
                ret.addTerm(term);
            } else {
                bubbleSortTrigo(term.getFactors());
                int flag = term.getFactors().size();   //记录三角函数因子位置
                int isexpr = 0;    //记录是否表达式
                for (int j = 0; j < term.getFactors().size(); j++) {
                    Factor factor = term.getFactors().get(j);
                    if (factor instanceof Expr) {
                        Derivation der = new Derivation();
                        term.getFactors().set(j, der.differentiate(var, (Expr) factor));
                        isexpr = 1;
                    } else {
                        isexpr = 0;
                    }
                }
                for (int j = 0; j < term.getFactors().size(); j++) {
                    Factor factor = term.getFactors().get(j);
                    if (factor instanceof Trigo) {
                        flag = j;
                        break;
                    }
                }
                if (isexpr == 0) {
                    Term term1 = new Term();
                    Term term2 = new Term();
                    term1.setCoefficient(term.getCoefficient());
                    term2.setCoefficient(term.getCoefficient());
                    for (int k = flag; k < term.getFactors().size(); k++) {
                        term1.addFactor(term.getFactors().get(k));
                    }
                    deriPow(term1, term.getFactors(), flag, var);  //前幂函数
                    ret.addTerm(term1);
                    for (int p = 0; p < flag; p++) {
                        term2.addFactor(term.getFactors().get(p));
                    }
                    term2.addFactor(deriTrigo(term.getFactors(), flag, var));
                    ret.addTerm(term2);
                } else {
                    ret.addTerm(term);
                }

            }
        }
        return ret;
    }

    public void deriPow(Term term1, ArrayList<Factor> factors, int flag, String var) {
        BigInteger zero = new BigInteger("0");
        BigInteger one = new BigInteger("1");
        int flag1 = 0;
        for (int i = 0; i < flag; i++) {
            Factor factor = factors.get(i);
            if (factor instanceof Pow && ((Pow) factor).getVarname().equals(var)) {
                Pow pow = new Pow(var);
                BigInteger index = ((Pow) factor).getIndex();
                pow.setIndex(index.subtract(one));
                term1.addFactor(pow);
                BigInteger tempcoffi = term1.getCoefficient();
                term1.setCoefficient(tempcoffi.multiply(index));
                flag1 = 1;
            } else {
                term1.addFactor(factor);
            }
        }
        if (flag1 == 0) {
            term1.setCoefficient(zero);
        }
    }

    public Expr deriTrigo(ArrayList<Factor> factors, int flag, String var) {   //三角函数求导
        Expr ret = new Expr();
        Term term1 = new Term();
        BigInteger one = new BigInteger("1");
        BigInteger zero = new BigInteger("0");
        if (flag < factors.size()) {
            for (int i = flag + 1; i < factors.size(); i++) {
                term1.addFactor(factors.get(i));
            }
            if (factors.get(flag) instanceof Expr) {
                ret.addTerm(term1);
                Term term2 = new Term();
                term2.addFactor(factors.get(flag));
                term2.addFactor(deriTrigo(factors, flag + 1, var));
                ret.addTerm(term2);
            } else {
                Trigo trigo = (Trigo) factors.get(flag);
                if (trigo.getName().equals("sin")) {
                    if (trigo.getIndex().equals(one)) {   //指数为1
                        Expr triexpr = clonexpr(trigo.backExpr());
                        Trigo deritir = new Trigo("cos");
                        deritir.setExpr(triexpr);
                        term1.addFactor(deritir);
                        Expr tmpexp = clonexpr(triexpr);
                        term1.addFactor(differentiate(var, tmpexp));
                    } else {
                        BigInteger coff = term1.getCoefficient();
                        term1.setCoefficient(coff.multiply(trigo.getIndex()));
                        Trigo deri1 = new Trigo("sin");
                        Expr triexpr = clonexpr(trigo.backExpr());
                        deri1.setExpr(triexpr);
                        deri1.setIndex(Integer.valueOf(trigo.getIndex().subtract(one).toString()));
                        term1.addFactor(deri1);
                        Trigo deri2 = new Trigo("cos");
                        deri2.setExpr(triexpr);
                        term1.addFactor(deri2);
                        Expr tmpexp = clonexpr(triexpr);
                        term1.addFactor(differentiate(var, tmpexp));
                    }
                } else {   //cos函数 -> -sin
                    toSin(var, trigo, one, term1);
                }
                ret.addTerm(term1);
                Term term2 = new Term();
                term2.addFactor(trigo);
                term2.addFactor(deriTrigo(factors, flag + 1, var));
                ret.addTerm(term2);
            }

        } else {
            Number zero1 = new Number(zero);
            term1.addFactor(zero1);
            ret.addTerm(term1);
        }
        return ret;
    }

    private void toSin(String var, Trigo trigo, BigInteger one, Term term1) {
        if (trigo.getIndex().equals(one)) {   //指数为1
            Expr triexpr = clonexpr(trigo.backExpr());
            Trigo deritir = new Trigo("sin");
            term1.negCoefficient();
            deritir.setExpr(triexpr);
            term1.addFactor(deritir);
            Expr tmpexp = clonexpr(triexpr);
            term1.addFactor(differentiate(var, tmpexp));
        } else {
            BigInteger coff = term1.getCoefficient();
            term1.setCoefficient(coff.multiply(trigo.getIndex()));
            Trigo deri1 = new Trigo("cos");
            Expr triexpr = clonexpr(trigo.backExpr());
            deri1.setExpr(triexpr);
            deri1.setIndex(Integer.valueOf(trigo.getIndex().subtract(one).toString()));
            term1.addFactor(deri1);
            Trigo deri2 = new Trigo("sin");
            term1.negCoefficient();
            deri2.setExpr(triexpr);
            term1.addFactor(deri2);
            Expr tmpexp = clonexpr(triexpr);
            term1.addFactor(differentiate(var, tmpexp));
        }
    }

    private void bubbleSortTrigo(ArrayList<Factor> fact) {  //三角函数挪到最后头
        int len = fact.size();
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                if (fact.get(j) instanceof Trigo && fact.get(j + 1) instanceof Pow) {
                    Pow temp = new Pow(((Pow) fact.get(j + 1)).getVarname());
                    temp.setIndex(((Pow) fact.get(j + 1)).getIndex());
                    fact.set(j + 1, fact.get(j));
                    fact.set(j, temp);
                }
            }
        }
    }

    private Expr clonexpr(Expr expr1) {    //表达式深克隆
        Expr ret = new Expr();
        for (int i = 0; i < expr1.getTerms().size(); i++) {
            Term term = new Term();
            term.setCoefficient(expr1.getTerms().get(i).getCoefficient());
            for (int j = 0; j < expr1.getTerms().get(i).getFactors().size(); j++) {
                Factor factor = expr1.getTerms().get(i).getFactors().get(j);
                if (factor instanceof Pow) {
                    Pow gac = new Pow(((Pow) factor).getVarname());
                    gac.setIndex(((Pow) factor).getIndex());
                    term.addFactor(gac);
                } else if (factor instanceof Trigo) {
                    Trigo gac = new Trigo(((Trigo) factor).getName());
                    gac.setIndex1(((Trigo) factor).getIndex());
                    Expr triexpr = clonexpr(((Trigo) factor).backExpr());
                    gac.setExpr(triexpr);
                    term.addFactor(gac);
                } else {
                    Expr gac = clonexpr((Expr) factor);
                    term.addFactor(gac);
                }
            }
            ret.addTerm(term);
        }
        return ret;
    }

}

public class Polymulti {      //多项式乘法
    public Polymulti() {

    }

    public Expr mulPoly(Expr expr1, Expr expr2) {     //两个最简表达式相乘，每项相乘即可，注意中间连接的符号
        Expr expr0 = new Expr();
        if ((expr1.getSign().equals("-") && expr2.getSign().equals("+"))
                || (expr1.getSign().equals("+") && expr2.getSign().equals("-"))) {
            expr0.setSign("-");
        } else {
            expr0.setSign("+");
        }
        for (int i = 0; i < expr1.getTerms().size(); i++) {
            for (int j = 0; j < expr2.getTerms().size(); j++) {
                Term term2 = new Term();
                term2.setCoefficient(expr2.getTerms().get(j).getCoefficient());
                for (int q = 0; q < expr2.getTerms().get(j).getFactors().size(); q++) {
                    exeNewTerm(expr2.getTerms().get(j).getFactors().get(q), term2);
                }

                Term term = new Term();
                term.setCoefficient(expr1.getTerms().get(i)
                        .getCoefficient().multiply(term2.getCoefficient()));  //系数 + 因子容器
                for (int p = 0; p < expr1.getTerms().get(i).getFactors().size(); p++) {  //深克隆
                    if (expr1.getTerms().get(i).getFactors().get(p) instanceof Pow) {
                        Pow gac = new Pow(((Pow) expr1.getTerms()
                                .get(i).getFactors().get(p)).getVarname());
                        gac.setIndex(((Pow) expr1.getTerms().get(i)
                                .getFactors().get(p)).getIndex());
                        term.addFactor(gac);
                    } else if (expr1.getTerms().get(i).getFactors().get(p) instanceof Trigo) {
                        Trigo gac = new Trigo(((Trigo) expr1.getTerms()
                                .get(i).getFactors().get(p)).getName());
                        gac.setIndex1(((Trigo) expr1.getTerms()
                                .get(i).getFactors().get(p)).getIndex());
                        gac.setExpr(((Trigo) expr1.getTerms().get(i)
                                .getFactors().get(p)).backExpr());
                        term.addFactor(gac);
                    } else {
                        Expr gac = new Expr((Expr) expr1.getTerms().get(i).getFactors().get(p));
                        term.addFactor(gac);
                    }
                }
                for (int k = 0; k < term2.getFactors().size(); k++) {
                    term.addFactor(term2.getFactors().get(k));
                }
                expr0.addTerm(term);
            }
        }

        return expr0;
    }

    private void exeNewTerm(Factor factor, Term term) {
        if (factor instanceof Pow) {
            Pow fac = new Pow(((Pow) factor).getVarname());
            fac.setIndex(((Pow) factor).getIndex());
            term.addFactor(fac);
        } else if (factor  instanceof Trigo) {
            Trigo fac = new Trigo(((Trigo) factor).getName());
            fac.setIndex1(((Trigo) factor).getIndex());
            fac.setExpr(((Trigo) factor).backExpr());
            term.addFactor(fac);
        } else {
            Expr fac = new Expr(((Expr)factor));
            term.addFactor(fac);
        }
    }

}

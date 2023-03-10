import java.math.BigInteger;

public class Pow implements Factor {        // 幂函数
    private String varname;      // 自变量
    private BigInteger index;    // 指数

    public Pow() {
        this.varname = "";
        this.index = new BigInteger("1");
    }

    public Pow(String varname) {
        this.varname = varname;
        this.index = new BigInteger("1");
    }

    public void setIndex(int index1) {      // 设置指数
        BigInteger index = BigInteger.valueOf(index1);
        this.index = index;
    }

    public void setIndex(BigInteger index1) {
        this.index = index1;
    }

    public String getVarname() {
        return this.varname;
    }

    public BigInteger getIndex() {
        return this.index;
    }

    @Override
    public boolean equals(Object pow) {
        if (((Pow) pow).getVarname().equals(this.varname)
                && ((Pow) pow).getIndex().equals(this.index)) {
            return true;
        } else {
            return false;
        }
    }

}

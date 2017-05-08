package ransomts.foobardarts;

/**
 * Created by tim on 5/7/17.
 */

public class Tuple<L, R> {
    L a;
    R b;

    public Tuple(L a, R b) {
        setA(a);
        setB(b);
    }

    public Tuple() {
        this.a = null;
        this.b = null;
    }

    public L getA() {
        return a;
    }

    public R getB() {
        return b;
    }

    public void setA(L a) {
        this.a = a;
    }

    public void setB(R b) {
        this.b = b;
    }
}

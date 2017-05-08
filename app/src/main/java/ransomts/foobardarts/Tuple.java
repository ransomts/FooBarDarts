package ransomts.foobardarts;

/**
 * Created by tim on 5/7/17.
 */

public class Tuple {
    Object a;
    Object b;

    public Tuple(Object a, Object b) {
        setA(a);
        setB(b);
    }

    public Tuple() {
        this.a = null;
        this.b = null;
    }

    public Object getA() {
        return a;
    }

    public Object getB() {
        return b;
    }

    public void setA(Object a) {
        this.a = a;
    }

    public void setB(Object b) {
        this.b = b;
    }
}

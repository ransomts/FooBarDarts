package ransomts.foobardarts;

import java.util.HashMap;

/**
 * Java doesn't have a Tuple class for some reason, yay!!!
 */

public class Tuple<L, R> {
    L a;
    R b;

    public Tuple(L a, R b) {
        setA(a);
        setB(b);
    }

    public Tuple(HashMap<L, R> hashMap) {
        for (L left : hashMap.keySet()) {
            setA(left);
            setB(hashMap.get(getA()));
        }
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

    public HashMap<L, R> toHashMap() {
        HashMap<L, R> temp = new HashMap<>(1);
        temp.put(a,b);
        return temp;
    }
}

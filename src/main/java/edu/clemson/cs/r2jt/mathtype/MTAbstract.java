package edu.clemson.cs.r2jt.mathtype;

public abstract class MTAbstract<T> extends MTType {

    @SuppressWarnings("unchecked")
    public final boolean equals(Object o) {
        boolean result = this.getClass().isAssignableFrom(o.getClass());

        if (result) {
            result = valueEqual((T) o);
        }

        return result;
    }

    public abstract boolean valueEqual(T t);
}

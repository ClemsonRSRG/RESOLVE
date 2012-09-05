package edu.clemson.cs.r2jt.mathtype;

/**
 * <p>A proper type.  Any type that does not depend on other types.  I.e., it
 * is atomic.</p>
 */
public class MTProper extends MTType {

    public boolean isOfKindType() {
        return false;
    }
}

package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.absyn.*;

public class MathTypeResolver {

    public static MTType getType(Exp e, IdentifierResolver scope) {
        return new MTProper();
    }
}

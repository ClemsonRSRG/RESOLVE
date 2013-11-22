package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

/**
 * <p>A <code>Dec</code> refers to anything that is able to be declared;
 * hence the name "Dec". This might include anything from an operation
 * declaration to a facility module declaration -- and any number of things
 * in between.</p>
 */
public abstract class Dec extends ResolveConceptualElement implements Cloneable {

    protected MTType myMathType = null;

    //protected MTType myMathTypeValue = null;

    public abstract void accept(ResolveConceptualVisitor v);

    public abstract String asString(int indent, int increment);

    /**
     * <p>Returns the name of this <code>Dec</code>.</p>
     *
     * @return The name of the declaration.
     */
    public abstract PosSymbol getName();

    /**
     * <p>Return an indented string representation of this <code>Dec</code>.</p>
     *
     * @param indent The desired level of indentation.</p>
     *
     * @return A string.
     */
    public String toString(int indent) {
        return new String();
    }

    public Location getLocation() {
        return getName().getLocation();
    }

    /**
     * <p>Returns an <code>MTType</code> representing the math type for this
     * <code>Dec</code>.</p>
     */
    public MTType getMathType() {
        return myMathType;
    }

    public void setMathType(MTType mt) {
        if (mt == null) {
            throw new RuntimeException("Trying to set null type on "
                    + this.getClass());
        }

        this.myMathType = mt;
    }

    //	public MTType getMathTypeValue() {
    //		return myMathTypeValue;
    //	}
    //	public void setMathTypeValue(MTType mathTypeValue) {
    //		this.myMathTypeValue = mathTypeValue;
    //	}

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError("But we are Cloneable!!!");
        }
    }
}

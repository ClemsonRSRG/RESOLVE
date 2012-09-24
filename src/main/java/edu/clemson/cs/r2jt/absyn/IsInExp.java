package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.type.BooleanType;
import edu.clemson.cs.r2jt.type.ConstructedType;
import edu.clemson.cs.r2jt.type.IndirectType;
import edu.clemson.cs.r2jt.type.Type;

public class IsInExp extends Exp {

    //private ErrorHandler err = ErrorHandler.getInstance();

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int IS_IN = 1;
    public static final int IS_NOT_IN = 2;

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The left member. */
    private Exp left;

    /** The operator member. */
    private int operator;

    /** The right member. */
    private Exp right;

    private Type retType = BooleanType.INSTANCE;

    // ===========================================================
    // Constructors
    // ===========================================================

    public IsInExp() {};

    public IsInExp(Location location, Exp left, int operator, Exp right) {
        this.location = location;
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the left variable. */
    public Exp getLeft() {
        return left;
    }

    /** Returns the value of the operator variable. */
    public int getOperator() {
        return operator;
    }

    /** Returns the value of the right variable. */
    public Exp getRight() {
        return right;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the left variable to the specified value. */
    public void setLeft(Exp left) {
        this.left = left;
    }

    /** Sets the operator variable to the specified value. */
    public void setOperator(int operator) {
        this.operator = operator;
    }

    /** Sets the right variable to the specified value. */
    public void setRight(Exp right) {
        this.right = right;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public String getOperatorAsString() {
        String retval;

        switch (operator) {
        case IS_IN:
            retval = "is_in";
            break;
        case IS_NOT_IN:
            retval = "is_not_in";
            break;
        default:
            throw new RuntimeException("Invalid operator code.");
        }

        return retval;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        Exp retval =
                new EqualsExp(location, substitute(left, substitutions),
                        operator, substitute(right, substitutions));

        retval.setType(type);

        return retval;
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitIsInExp(this);
    }

    /** Accepts a TypeResolutionVisitor. */
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return getKnownType(v);
        //return v.getEqualsExpType(this);
    }

    /** Returns the known type for this class. */
    private Type getKnownType(TypeResolutionVisitor v)
            throws TypeResolutionException {

        v.getMathExpType(this.getLeft());

        //System.out.println("IS_IN type check here");
        //Type t1 = v.getMathExpType(this.getLeft());
        Type t2 = v.getMathExpType(this.getRight());

        // if t2 made using powerset_expression, need to convert it
        // to the math type (ConstructedType)
        if (t2 instanceof IndirectType) {
            t2 = t2.toMath();
        }

        // if t2 made using set_constructor
        if (t2 instanceof ConstructedType) {
            if (((ConstructedType) t2).getName().getName().equalsIgnoreCase(
                    "Set")
                    || ((ConstructedType) t2).getArgs().size() != 1) {
                // Manually set the return type of is_in statement
                Type b = retType;
                this.setType(b);
                return b;
            }
        }
        // t2 is not a set at all
        else {
            //String msg = "The second parameter to is_in must be a set, found: " + this.getRight().toString();
            //err.error(this.getLocation(), msg);
        }
        return retType;
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("EqualsExp\n");

        if (left != null) {
            sb.append(left.asString(indent + increment, increment));
        }

        printSpace(indent + increment, sb);
        sb.append(printConstant(operator) + "\n");

        if (right != null) {
            sb.append(right.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {
        //Environment   env	= Environment.getInstance();
        //if(env.isabelle()){return toIsabelleString(indent);};    	

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (left != null) {
            sb.append(left.toString(0));
        }

        if (operator == 1)
            sb.append(" is_in ");
        else {
            sb.append(" is_not_in ");
        }

        if (right != null) {
            sb.append(right.toString(0));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toIsabelleString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (left != null) {
            sb.append(left.toString(0));
        }

        if (operator == 1)
            sb.append(" = ");
        else
            sb.append(" ~= ");

        if (right != null) {
            sb.append(right.toString(0));
        }

        return sb.toString();
    }

    public Exp replace(Exp old, Exp replacement) {
        if (!(old instanceof EqualsExp)) {
            IsInExp newExp = new IsInExp();
            newExp.setLeft((Exp) left.clone());
            newExp.setRight((Exp) right.clone());
            newExp.setOperator(this.operator);
            newExp.setType(type);
            newExp.setLocation(this.location);
            Exp lft = left.replace(old, replacement);
            Exp rgt = right.replace(old, replacement);
            if (lft != null)
                newExp.setLeft(lft);
            if (rgt != null)
                newExp.setRight(rgt);
            return newExp;
        }
        else {}
        //
        return this;
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Boolean found = false;
        if (left != null) {
            found = left.containsVar(varName, IsOldExp);
        }
        if (!found && right != null) {
            found = right.containsVar(varName, IsOldExp);
        }
        return found;
    }

    private String printConstant(int k) {
        StringBuffer sb = new StringBuffer();
        switch (k) {
        case 1:
            sb.append("IS_IN");
            break;
        case 2:
            sb.append("IS_NOT_IN");
            break;
        default:
            sb.append(k);
        }
        return sb.toString();
    }

    public Object clone() {
        IsInExp clone = new IsInExp();
        clone.setLeft((Exp) this.getLeft().clone());
        clone.setRight((Exp) this.getRight().clone());
        if (this.location != null)
            clone.setLocation((Location) this.getLocation().clone());
        clone.setOperator(this.getOperator());
        clone.setType(type);
        return clone;
    }

    public Exp remember() {
        if (left != null)
            left = left.remember();
        if (left != null)
            right = right.remember();

        return this;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(left);
        list.add(right);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        switch (index) {
        case 0:
            left = e;
            break;
        case 1:
            right = e;
            break;
        }
    }
}
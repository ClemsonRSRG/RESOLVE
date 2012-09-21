package edu.clemson.cs.r2jt.vcgeneration;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.Statement;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.*;

/**
 * TODO: Write a description of this module
 */
public class VerificationStatement implements Cloneable {

    // ===========================================================
    // Global Variables 
    // ===========================================================

    private int type;
    private Object assertion;
    static final public int ASSUME = 1;
    static final public int CODE = 2;
    static final public int VARIABLE = 3;
    static final public int CONFIRM = 4;
    static final public int REMEMBER = 5;
    static final public int CHANGE = 6;

    // ===========================================================
    // Constructors
    // ===========================================================

    VerificationStatement(int type, Object assertion) {
        if (((type == ASSUME || type == CONFIRM) && (assertion instanceof Exp))
                || ((type == CODE) && (assertion instanceof Statement))
                || (type == VARIABLE && assertion instanceof VarDec)
                || (type == CHANGE)) {
            this.type = type;
            this.assertion = assertion;
        }
        else if (type == REMEMBER) {
            this.type = type;
        }
    }

    VerificationStatement() {}

    @Override
    public Object clone() {
        try {
            VerificationStatement clone = new VerificationStatement();
            if ((type == ASSUME) || (type == CONFIRM)) {
                if (assertion instanceof Exp) {
                    clone =
                            new VerificationStatement(type, ((Exp) assertion)
                                    .clone());
                }
                return clone;
            }
            else if (type == CODE) {
                if (assertion instanceof Statement) {
                    clone =
                            new VerificationStatement(type,
                                    ((Statement) assertion).clone());
                }
                return clone;
            }
            else if (type == VARIABLE) {
                if (assertion instanceof VarDec) {
                    clone =
                            new VerificationStatement(type,
                                    ((VarDec) assertion).clone());
                }
                return clone;
            }
            else if (type == REMEMBER) {
                clone = new VerificationStatement(type, null);
                return clone;
            }
            else if (type == CHANGE) {
                if (assertion instanceof List<?>) {
                    clone =
                            new VerificationStatement(type,
                                    ((List<?>) assertion).clone());
                    return clone();
                }
                else {
                    return super.clone();
                }
            }
            else {
                return super.clone();
            }
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError("But we are Cloneable!!!");
        }
    }

    VerificationStatement(int type) {
        if (type == REMEMBER) {
            this.type = type;
        }
    }

    public VerificationStatement copy() {
        return new VerificationStatement(this.getType(), this.getAssertion());
    }

    static public int getAssumeType() {
        return ASSUME;
    }

    static public int getCodeType() {
        return CODE;
    }

    static public int getVariableType() {
        return VARIABLE;
    }

    static public int getConfirmType() {
        return CONFIRM;
    }

    static public int getRememberType() {
        return REMEMBER;
    }

    public int getType() {
        return type;
    }

    public void changeAssertion(Object assertion) {
        this.assertion = assertion;
    }

    public Object getAssertion() {
        return assertion;
    }

    public String toString() {
        String str = new String();
        switch (type) {
        case 1:
            str = "Assume";
            break;
        case 2:
            str = "Code";
            break;
        case 3:
            str = "Variable";
            break;
        case 4:
            str = "Confirm";
            break;
        case 5:
            str = "Remember";
            break;
        case 6:
            str = "Change";
            break;
        default:
            str = "Default";
            break;
        }
        return str;
    }
}
/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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

    // Type of Verification Statement
    private int myType;

    // Each assertion object
    private Object myAssertion;

    // Code for each type of Verification Statement
    public static final int CODE = 1;
    public static final int VARIABLE = 2;
    public static final int REMEMBER = 3;
    public static final int CHANGE = 4;

    // ===========================================================
    // Constructors
    // ===========================================================

    // Default constructor
    public VerificationStatement() {}

    // Our defined constructor
    public VerificationStatement(int type, Object assertion) {
        if (((type == CODE) && (assertion instanceof Statement))
                || (type == VARIABLE && assertion instanceof VarDec)
                || (type == CHANGE)) {
            myType = type;
            myAssertion = assertion;
        }
        else if (type == REMEMBER) {
            myType = type;
        }
    }

    // Copy constructor
    public VerificationStatement copy() {
        return new VerificationStatement(this.getType(), this.getAssertion());
    }

    // Clone the object
    @Override
    public Object clone() {
        try {
            VerificationStatement clone = new VerificationStatement();
            if (myType == CODE) {
                if (myAssertion instanceof Statement) {
                    clone =
                            new VerificationStatement(myType,
                                    ((Statement) myAssertion).clone());
                }
                return clone;
            }
            else if (myType == VARIABLE) {
                if (myAssertion instanceof VarDec) {
                    clone =
                            new VerificationStatement(myType,
                                    ((VarDec) myAssertion).clone());
                }
                return clone;
            }
            else if (myType == REMEMBER) {
                clone = new VerificationStatement(myType, null);
                return clone;
            }
            else if (myType == CHANGE) {
                if (myAssertion instanceof List<?>) {
                    clone =
                            new VerificationStatement(myType,
                                    ((List<?>) myAssertion).clone());
                    return clone;
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

    // ===========================================================
    // Mutator/Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /**
     * <p>Returns the type of the assertion.</p>
     *
     * @return The type of <code>VerificationStatement</code>.
     */
    public int getType() {
        return myType;
    }

    /**
     * <p>Returns the assertion object.</p>
     *
     * @return The object containing an <code>ResolveConceptualElement</code>.
     */
    public Object getAssertion() {
        return myAssertion;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /**
     * <p>Sets the <code>ResolveConceptualElement</code>
     * <code>Object</code>.</p>
     *
     * @param assertion The corresponding assertion <code>Object</code>.
     */
    public void setAssertion(Object assertion) {
        myAssertion = assertion;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Returns the type of the <code>VerificationStatement</code>
     * as a String.</p>
     *
     * @return The string form of the type.
     */
    public String toString() {
        String str = new String();
        switch (myType) {
        case 1:
            str = "Code";
            break;
        case 2:
            str = "Variable";
            break;
        case 3:
            str = "Remember";
            break;
        case 4:
            str = "Change";
            break;
        default:
            str = "Default";
            break;
        }
        return str;
    }
}
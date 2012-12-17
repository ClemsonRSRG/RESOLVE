/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the Clemson University nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University. Contributors to the initial version are:
 * 
 * Steven Atkinson
 * Greg Kulczycki
 * Kunal Chopra
 * John Hunt
 * Heather Keown
 * Ben Markle
 * Kim Roche
 * Murali Sitaraman
 */
package edu.clemson.cs.r2jt.verification;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.Statement;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.*;

public class VerificationStatement implements Cloneable {

    private int type;
    private Object assertion;

    static final public int ASSUME = 1;
    static final public int CODE = 2;
    static final public int VARIABLE = 3;
    static final public int CONFIRM = 4;
    static final public int REMEMBER = 5;
    static final public int CHANGE = 6;

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

    VerificationStatement() {

    }

    public Object clone() {
        try {
            VerificationStatement clone = new VerificationStatement();
            if ((type == ASSUME) || (type == CONFIRM)) {
                if (assertion instanceof Exp)
                    clone =
                            new VerificationStatement(type, Exp
                                    .clone(((Exp) assertion)));
                return clone;
            }
            else if (type == CODE) {
                if (assertion instanceof Statement)
                    clone =
                            new VerificationStatement(type,
                                    ((Statement) assertion).clone());
                return clone;
            }
            else if (type == VARIABLE) {
                if (assertion instanceof VarDec)
                    clone =
                            new VerificationStatement(type,
                                    ((VarDec) assertion).clone());
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
                    return this.clone();
                }
                else
                    return super.clone();
            }
            else
                return super.clone();
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

    /**
     * <p>I need a convenient way of printing out a full piece of assertive
     * code, but I'm hesitant to change toString() since, knowing this code
     * base, something depends on it.</p>
     */
    public String allInfo() {
        String result = toString();
        result += assertion;
        return result;
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

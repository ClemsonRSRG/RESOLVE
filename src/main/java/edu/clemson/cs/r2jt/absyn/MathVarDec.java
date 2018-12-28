/*
 * MathVarDec.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.data.PosSymbol;

public class MathVarDec extends Dec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The ty member. */
    private Ty ty;

    private boolean confirm = false;

    // ===========================================================
    // Constructors
    // ===========================================================

    public MathVarDec() {};

    public MathVarDec(PosSymbol name, Ty ty) {
        this.name = name;
        this.ty = ty;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the ty variable. */
    public Ty getTy() {
        return ty;
    }

    public boolean getConfirm() {
        return confirm;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the ty variable to the specified value. */
    public void setTy(Ty ty) {
        this.ty = ty;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitMathVarDec(this);
    }

    public String toString() {
        return name.getName() + " : " + ty.toString();
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("MathVarDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (ty != null) {
            sb.append(ty.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public String toString(int indent) {
        //Environment   env	= Environment.getInstance();
        //if(env.isabelle()){return toIsabelleString(indent);};

        String str = new String();

        if (name != null) {
            String strName = name.toString();
            int index = 0;
            int num = 0;
            while ((strName.charAt(index)) == '?') {
                num++;
                index++;
            }
            str = str.concat(strName.substring(index, strName.length()));
            for (int i = 0; i < num; i++) {
                str = str.concat("'");
            }
        }

        str = str.concat(":");

        if (ty != null) {
            if (ty instanceof NameTy)
                str = str.concat(((NameTy) ty).getName().toString());
            else
                str = str.concat(ty.toString(0));
        }
        return str;
    }

    /*public String toIsabelleString(int indent) {
       
        VarExp tmp = new VarExp(null, null, name);
    	 String str = tmp.toIsabelleString(0);
        str = str.concat(":");
        if(ty instanceof NameTy)
        	str = str.concat(((NameTy)ty).getName().toString());
        else
        	str = str.concat(ty.toString(0));
        return str;    
    }*/

    public void prettyPrint() {
        System.out.print(name.getName() + ": ");
        ty.prettyPrint();
    }

    public MathVarDec copy() {
        PosSymbol newName = name.copy();

        Ty newTy = null;
        if (ty != null) {
            newTy = Ty.copy(ty);
        }
        return new MathVarDec(newName, newTy);
    }

}

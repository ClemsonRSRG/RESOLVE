/*
 * This softare is released under the new BSD 2006 license.
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
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
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
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */
/*
 * MathVarDec.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.init.Environment;

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

    public MathVarDec(
            PosSymbol name,
            Ty ty)
    {
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
    public PosSymbol getName() { return name; }

    /** Returns the value of the ty variable. */
    public Ty getTy() { return ty; }
    
    public boolean getConfirm() { return confirm; }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) { this.name = name; }

    /** Sets the ty variable to the specified value. */
    public void setTy(Ty ty) { this.ty = ty; }
    
    public void setConfirm(boolean confirm) { this.confirm = confirm; }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitMathVarDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("MathVarDec\n");

        if (name != null) {
            sb.append(name.asString(indent+increment,increment));
        }

        if (ty != null) {
            sb.append(ty.asString(indent+increment,increment));
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
        	while((strName.charAt(index))== '?'){
        		num++;
        		index++;
        	}
        	str = str.concat(strName.substring(index, strName.length()));
        	for(int i=0;i<num;i++){ 
        		str = str.concat("'");
        	}
        }
        
        
        str = str.concat(":");
        if(ty instanceof NameTy)
        	str = str.concat(((NameTy)ty).getName().toString());
        else
        	str = str.concat(ty.toString(0));
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
    	Ty newTy = ty.copy();
    	return new MathVarDec(newName, newTy);
    }

}

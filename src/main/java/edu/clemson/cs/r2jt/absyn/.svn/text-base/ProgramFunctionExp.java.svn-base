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
 * ProgramFunctionExp.java
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
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.Iterator;

public class ProgramFunctionExp extends ProgramExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The qualifier member. */
    private PosSymbol qualifier;

    /** The name member. */
    private PosSymbol name;

    /** The arguments member. */
    private List<ProgramExp> arguments;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProgramFunctionExp() {};

    public ProgramFunctionExp(
            Location location,
            PosSymbol qualifier,
            PosSymbol name,
            List<ProgramExp> arguments)
    {
        this.location = location;
        this.qualifier = qualifier;
        this.name = name;
        this.arguments = arguments;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
    	Exp retval;
    	
    	List<ProgramExp> newArguments = new List<ProgramExp>();
    	for (ProgramExp a : arguments) {
    		newArguments.add((ProgramExp) substitute(a, substitutions));
    	}
    	
    	retval =
    		new ProgramFunctionExp(location, qualifier, name, newArguments);
    	retval.setType(type);
    	
    	return retval;
    }
    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() { return location; }

    /** Returns the value of the qualifier variable. */
    public PosSymbol getQualifier() { return qualifier; }

    /** Returns the value of the name variable. */
    public PosSymbol getName() { return name; }

    /** Returns the value of the arguments variable. */
    public List<ProgramExp> getArguments() { return arguments; }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) { this.location = location; }

    /** Sets the qualifier variable to the specified value. */
    public void setQualifier(PosSymbol qualifier) { this.qualifier = qualifier; }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) { this.name = name; }

    /** Sets the arguments variable to the specified value. */
    public void setArguments(List<ProgramExp> arguments) { this.arguments = arguments; }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitProgramFunctionExp(this);
    }

    /** Accepts a TypeResolutionVisitor. */
    public Type accept(TypeResolutionVisitor v)
        throws TypeResolutionException {
        return v.getProgramFunctionExpType(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ProgramFunctionExp\n");

        if (qualifier != null) {
            sb.append(qualifier.asString(indent+increment,increment));
        }

        if (name != null) {
            sb.append(name.asString(indent+increment,increment));
        }

        if (arguments != null) {
            sb.append(arguments.asString(indent+increment,increment));
        }

        return sb.toString();
    }
    
    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (qualifier != null) {
            sb.append(qualifier.getName().toString());
            sb.append(".");
        }

        if (name != null) {
            sb.append(name.getName().toString());
        }

        if (arguments != null) {
        	sb.append("(" + argumentsToString(arguments) + ")");
        }

        return sb.toString();
    }
    
    String argumentsToString(List<ProgramExp> arguments) {
    	String str = new String();
    	Iterator i = arguments.iterator();
		while(i.hasNext()) {
			ProgramExp exp = (ProgramExp)i.next();
			str = str.concat(exp.toString(0));
			if(i.hasNext())
				str = str.concat(", ");
		}
        return str;
    }
    
    
    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Iterator<ProgramExp> i = arguments.iterator();
        while(i.hasNext()) {
            ProgramExp temp = i.next();
            if(temp != null) {
                if(temp.containsVar(varName,IsOldExp)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public List<Exp> getSubExpressions() {
    	List<Exp> list = new List<Exp>();
    	Iterator<ProgramExp> argIt = arguments.iterator();
    	while(argIt.hasNext()) {
    		list.add((Exp)(argIt.next()));
    	}
    	return list;
    }

    public void setSubExpression(int index, Exp e) {
    	arguments.set(index, (ProgramExp) e);
    }
}

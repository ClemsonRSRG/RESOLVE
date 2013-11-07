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
 * Nighat Yasmin
 */
/*
 * PerformanceEModuleDec.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class PerformanceEModuleDec extends ModuleDec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The ProfileNames member. */
    private PosSymbol name;

    /** The ProfileNames member. */
    private PosSymbol profileName1;

    /** The ProfileNames member. */
    private PosSymbol profileName2;

    /** The ProfileNames member. */
    private PosSymbol profileName3;

    /** The Profile's concept name member. */
    private PosSymbol profilecName;

    /** The Profile's concept's profile name member. */
    private PosSymbol profilecpName;

    /** The parameters member. */
    private List<ModuleParameterDec> parameters;

    /** The usesItems member. */
    private List<UsesItem> usesItems;

    /** The requires member. */
    private Exp requires;

    /** The decs member. */
    private List<Dec> decs;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PerformanceEModuleDec() {};

    public PerformanceEModuleDec(PosSymbol profileName1,
            PosSymbol profileName2, PosSymbol profileName3,
            PosSymbol profilecName, PosSymbol profilecpName,
            List<ModuleParameterDec> parameters, List<UsesItem> usesItems,
            Exp requires, List<Dec> decs) {
        this.profileName1 = profileName1;
        this.profileName2 = profileName2;
        this.profileName3 = profileName3;
        this.profilecName = profilecName;
        this.profilecpName = profilecpName;
        this.parameters = parameters;
        this.usesItems = usesItems;
        this.requires = requires;
        this.decs = decs;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the profileNames variable. */
    public PosSymbol getName() {
        return profileName1;
    }

    /** Returns the value of the profileNames variable. */
    public PosSymbol getProfileName1() {
        return profileName1;
    }

    /** Returns the value of the profileNames variable. */
    public PosSymbol getProfileName2() {
        return profileName2;
    }

    /** Returns the value of the profileNames variable. */
    public PosSymbol getProfileName3() {
        return profileName3;
    }

    /** Returns the value of the Profile's concept name variable. */
    public PosSymbol getProfilecName() {
        return profilecName;
    }

    /** Returns the value of the Profile's concept's profile name variable. */
    public PosSymbol getProfilecpName() {
        return profilecpName;
    }

    /** Returns the value of the parameters variable. */
    public List<ModuleParameterDec> getParameters() {
        return parameters;
    }

    /** Returns the value of the usesItems variable. */
    public List<UsesItem> getUsesItems() {
        return usesItems;
    }

    /** Returns the value of the requires variable. */
    public Exp getRequires() {
        return requires;
    }

    /** Returns the value of the decs variable. */
    public List<Dec> getDecs() {
        return decs;
    }

    /** Returns a list of procedures in this realization. */
    public List<Symbol> getLocalProcedureNames() {
        List<Symbol> retval = new List<Symbol>();
        Iterator<Dec> it = decs.iterator();
        while (it.hasNext()) {
            Dec d = it.next();
            if (d instanceof ProcedureDec) {
                retval.add(d.getName().getSymbol());
            }
        }
        return retval;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the profileName1 variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = profileName1;
    }

    /** Sets the profileName1 variable to the specified value. */
    public void setProfileNames1(PosSymbol profileName1) {
        this.profileName1 = profileName1;
    }

    /** Sets the profileName2 variable to the specified value. */
    public void setProfileName2(PosSymbol profileName2) {
        this.profileName2 = profileName2;
    }

    /** Sets the profileName3 variable to the specified value. */
    public void setProfileName3(PosSymbol profileName3) {
        this.profileName3 = profileName3;
    }

    /** Sets the Profile's concept name variable to the specified value. */
    public void setProfilecName(PosSymbol profilecName) {
        this.profilecName = profilecName;
    }

    /** Sets the Profile's concept's profile name variable to the specified value. */
    public void setProfilecpName(PosSymbol profilecpName) {
        this.profilecpName = profilecpName;
    }

    /** Sets the parameters variable to the specified value. */
    public void setParameters(List<ModuleParameterDec> parameters) {
        this.parameters = parameters;
    }

    /** Sets the usesItems variable to the specified value. */
    public void setUsesItems(List<UsesItem> usesItems) {
        this.usesItems = usesItems;
    }

    /** Sets the requires variable to the specified value. */
    public void setRequires(Exp requires) {
        this.requires = requires;
    }

    /** Sets the decs variable to the specified value. */
    public void setDecs(List<Dec> decs) {
        this.decs = decs;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitPerformanceEModuleDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("PerformanceEModuleDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (profileName1 != null) {
            sb.append(profileName1.asString(indent + increment, increment));
        }

        if (profileName2 != null) {
            sb.append(profileName2.asString(indent + increment, increment));
        }

        if (profileName3 != null) {
            sb.append(profileName3.asString(indent + increment, increment));
        }

        if (profilecName != null) {
            sb.append(profilecName.asString(indent + increment, increment));
        }

        if (profilecpName != null) {
            sb.append(profilecpName.asString(indent + increment, increment));
        }

        if (parameters != null) {
            sb.append(parameters.asString(indent + increment, increment));
        }

        if (usesItems != null) {
            sb.append(usesItems.asString(indent + increment, increment));
        }

        if (requires != null) {
            sb.append(requires.asString(indent + increment, increment));
        }

        if (decs != null) {
            sb.append(decs.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}

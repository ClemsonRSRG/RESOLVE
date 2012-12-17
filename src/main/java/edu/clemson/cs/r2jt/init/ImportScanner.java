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
/*
 * ImportScanner.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.init;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.translation.Translator;

/**
 * This class scans the module dec for imported modules: associates, uses items,
 * and modules used in facility declarations. It adds the modules (and their
 * location) to a list of imports that is returned to the controller.
 */
public class ImportScanner extends ResolveConceptualVisitor {

    // ===========================================================
    // Variables
    // ===========================================================

    private List<Import> imports = new List<Import>();

    private final CompileEnvironment myInstanceEnvironment;

    //private Environment env = Environment.getInstance();

    // ===========================================================
    // Constructors
    // ===========================================================

    public ImportScanner(CompileEnvironment e) {
        myInstanceEnvironment = e;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public List<Import> getImportList(ModuleDec dec) {
        visitModuleDec(dec);
        return imports;
    }

    public void visitModuleDec(ModuleDec dec) {

        // This procedure finds all the Std_Fac dependencies and adds them to a
        // list in the CompileEnvironment class. It then determines whether the
        // Std_Fac should be automatically included in the dec's UsesList and
        // annexes them to be added to the ImportList based on whether it is a
        // dependency of the Std_Fac
        // NOTE: If you are adding more Std_Facs they should be added to the stdUses array in CompileEnvironment
        // TODO: This mechanism is a duplicated in Populator.visitModuleDec(),
        // perhaps combine these somehow? -JCK
        String[] stdUses = myInstanceEnvironment.getStdUses();
        String decName = dec.getName().getName();
        List<List<UsesItem>> listOfDependLists =
                myInstanceEnvironment.getStdUsesDepends();
        // System.out.println("list: " + listOfDependLists.toString());
        for (int i = 0; i < stdUses.length; i++) {
            if (decName.equals("Std_" + stdUses[i] + "_Fac")
                    || decName.equals(stdUses[i] + "_Template")
                    || decName.equals(stdUses[i] + "_Theory")) {
                if (decName.equals(stdUses[i] + "_Template")
                        || decName.equals(stdUses[i] + "_Theory")) {
                    // Set the dependencies
                    listOfDependLists.get(i).addAllUnique((dec.getUsesItems()));
                    myInstanceEnvironment.setStdUsesDepends(listOfDependLists);
                }
                dec.accept(this);
                return;
            }
            else {
                // Include this std UsesItem unless it is a Dependency
                if (!listOfDependLists.isEmpty()) {
                    List<UsesItem> dependencies = listOfDependLists.get(i);
                    if (dependencies != null) {
                        Iterator<UsesItem> it = dependencies.iterator();
                        while (it.hasNext()) {
                            if (it.next().getName().getName().equals(decName)) {
                                // This is a dependency do NOT annex/add
                                dec.accept(this);
                                return;
                            }
                        }
                    }
                }
                // Update the dec's UsesItemList
                PosSymbol facSymbol =
                        new PosSymbol(null, Symbol.symbol("Std_" + stdUses[i]
                                + "_Fac"));
                List<UsesItem> decUses = dec.getUsesItems();
                if (decUses == null) {
                    decUses = new List<UsesItem>();
                }
                for (int j = 0; j < decUses.size(); j++) {
                    if (decUses.get(j).getName().getName().equals(
                            facSymbol.getName())) {
                        decUses.remove(j);
                    }
                }
                decUses.add(new UsesItem(facSymbol));
                dec.setUsesItems(decUses);

                // Add the StdDec to the ImportList by annexing
                annexUsesItem(new PosSymbol(null, Symbol.symbol("Std_"
                        + stdUses[i] + "_Fac")));
            }
        }

        dec.accept(this);
    }

    // -----------------------------------------------------------
    // Annex module dec imports
    // -----------------------------------------------------------

    public void visitProofModuleDec(ProofModuleDec dec) {
        visitUsesItemList(dec.getUsesItems());
    }

    public void visitMathModuleDec(MathModuleDec dec) {
        visitUsesItemList(dec.getUsesItems());
    }

    public void visitConceptModuleDec(ConceptModuleDec dec) {
        visitUsesItemList(dec.getUsesItems());
    }

    public void visitEnhancementModuleDec(EnhancementModuleDec dec) {
        annexConceptModule(dec.getConceptName());
        visitUsesItemList(dec.getUsesItems());
    }

    public void visitConceptBodyModuleDec(ConceptBodyModuleDec dec) {
        visitModuleParameterList(dec.getParameters());
        annexConceptModule(dec.getConceptName());
        Iterator<PosSymbol> i = dec.getEnhancementNames().iterator();
        while (i.hasNext()) {
            PosSymbol eName = i.next();
            annexEnhancementModule(eName, dec.getConceptName());
        }
        if (dec.getProfileName() != null) {
            annexPerformanceProfile(dec.getProfileName());
        }
        visitUsesItemList(dec.getUsesItems());
        if (dec.getFacilityInit() != null) {
            visitInitItem(dec.getFacilityInit());
        }
        if (dec.getFacilityFinal() != null) {
            visitFinalItem(dec.getFacilityFinal());
        }
        visitDecList(dec.getDecs());
    }

    public void visitEnhancementBodyModuleDec(EnhancementBodyModuleDec dec) {
        if (dec.getProfileName() != null) {
            annexPerformanceProfile(dec.getProfileName());
        }
        visitModuleParameterList(dec.getParameters());
        annexEnhancementModule(dec.getEnhancementName(), dec.getConceptName());
        annexConceptModule(dec.getConceptName());
        visitEnhancementBodyItemList(dec.getEnhancementBodies(), dec
                .getConceptName());
        visitUsesItemList(dec.getUsesItems());
        if (dec.getFacilityInit() != null) {
            visitInitItem(dec.getFacilityInit());
        }
        if (dec.getFacilityFinal() != null) {
            visitFinalItem(dec.getFacilityFinal());
        }
        visitDecList(dec.getDecs());
    }

    public void visitFacilityModuleDec(FacilityModuleDec dec) {
        visitUsesItemList(dec.getUsesItems());
        if (dec.getFacilityInit() != null) {
            visitInitItem(dec.getFacilityInit());
        }
        if (dec.getFacilityFinal() != null) {
            visitFinalItem(dec.getFacilityFinal());
        }
        visitDecList(dec.getDecs());
    }

    public void visitShortFacilityModuleDec(ShortFacilityModuleDec dec) {
        assert dec.getDec() != null : "dec.getDec() is null";
        visitFacilityDec(dec.getDec());
    }

    // -----------------------------------------------------------
    // Annex module parameter imports
    // -----------------------------------------------------------

    public void visitRealizationParamDec(RealizationParamDec dec) {
        annexConceptModule(dec.getConceptName());
    }

    // -----------------------------------------------------------
    // Annex uses clause imports
    // -----------------------------------------------------------

    public void visitUsesItem(UsesItem item) {
        // A simple check to ensure that the Standard Facilities are not annexed
        // a second time because they are implicitly included in the
        // VisitModuleDec()
        // Similar check found in Populator.visitUsesItem()
        String[] stdUses = myInstanceEnvironment.getStdUses();
        String itemName = item.getName().getName();
        boolean doAnnex = true;
        for (int i = 0; i < stdUses.length; i++) {
            if (itemName.equals("Std_" + stdUses[i] + "_Fac")) {
                doAnnex = false;
                break;
            }
        }
        if (doAnnex) {
            annexUsesItem(item.getName());
        }
    }

    // -----------------------------------------------------------
    // Annex module level declaration imports
    // -----------------------------------------------------------

    public void visitFacilityTypeDec(FacilityTypeDec dec) {
        if (dec.getInitialization() != null) {
            visitInitItem(dec.getInitialization());
        }
        if (dec.getFinalization() != null) {
            visitFinalItem(dec.getFinalization());
        }
    }

    public void visitRepresentaionDec(RepresentationDec dec) {
        if (dec.getInitialization() != null) {
            visitInitItem(dec.getInitialization());
        }
        if (dec.getFinalization() != null) {
            visitFinalItem(dec.getFinalization());
        }
    }

    public void visitFacilityOperationDec(FacilityOperationDec dec) {
        Iterator<FacilityDec> i = dec.getFacilities().iterator();
        while (i.hasNext()) {
            FacilityDec dec2 = i.next();
            visitFacilityDec(dec2);
        }
    }

    public void visitProcedureDec(ProcedureDec dec) {
        Iterator<FacilityDec> i = dec.getFacilities().iterator();
        while (i.hasNext()) {
            FacilityDec dec2 = i.next();
            visitFacilityDec(dec2);
        }
    }

    public void visitFacilityDec(FacilityDec dec) {
        annexConceptModule(dec.getConceptName());
        visitEnhancementItemList(dec.getEnhancements(), dec.getConceptName());
        if (!dec.getBodyName().equals("Std_Character_Realiz")
                && !dec.getBodyName().equals("Std_Char_Str_Realiz")
                && !dec.getBodyName().equals("Std_Boolean_Realiz")
                && !dec.getBodyName().equals("Std_Integer_Realiz")) {
            annexConceptBodyModule(dec.getBodyName(), dec.getConceptName());
        }
        //if(dec.getProfileName() != null){
        //annexPerformanceProfile(dec.getProfileName());
        //}
        visitEnhancementBodyItemList(dec.getEnhancementBodies(), dec
                .getConceptName());
    }

    // -----------------------------------------------------------
    // Annex initialization and finalization imports
    // -----------------------------------------------------------

    public void visitInitItem(InitItem item) {
        Iterator<FacilityDec> i = item.getFacilities().iterator();
        while (i.hasNext()) {
            FacilityDec dec = i.next();
            visitFacilityDec(dec);
        }
    }

    public void visitFinalItem(FinalItem item) {
        Iterator<FacilityDec> i = item.getFacilities().iterator();
        while (i.hasNext()) {
            FacilityDec dec = i.next();
            visitFacilityDec(dec);
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void visitModuleParameterList(List<ModuleParameterDec> pars) {
        Iterator<ModuleParameterDec> i = pars.iterator();
        while (i.hasNext()) {
            Dec par = i.next().getWrappedDec();
            if (par instanceof RealizationParamDec) {
                visitRealizationParamDec((RealizationParamDec) par);
            }
        }
    }

    private void visitEnhancementItemList(List<EnhancementItem> items,
            PosSymbol cName) {
        Iterator<EnhancementItem> i = items.iterator();
        while (i.hasNext()) {
            EnhancementItem item = i.next();
            annexEnhancementModule(item.getName(), cName);
        }
    }

    private void visitEnhancementBodyItemList(List<EnhancementBodyItem> items,
            PosSymbol cName) {
        Iterator<EnhancementBodyItem> i = items.iterator();
        while (i.hasNext()) {
            EnhancementBodyItem item = i.next();
            assert item != null : "item is null";
            //if(item.getProfileName() != null){
            //annexPerformanceProfile(item.getProfileName());
            //}
            annexEnhancementModule(item.getName(), cName);
            annexEnhancementBodyModule(item.getBodyName(), item.getName(),
                    cName);
        }
    }

    private void visitUsesItemList(List<UsesItem> items) {
        if (items == null)
            return;
        Iterator<UsesItem> i = items.iterator();
        while (i.hasNext()) {
            UsesItem item = i.next();
            visitUsesItem(item);
        }
    }

    private void visitDecList(List<Dec> decs) {
        Iterator<Dec> i = decs.iterator();
        while (i.hasNext()) {
            Dec dec = i.next();
            dec.accept(this);
        }
    }

    // -----------------------------------------------------------
    // Annexation Methods
    // -----------------------------------------------------------

    private void annexConceptModule(PosSymbol cName) {
        ModuleID id = ModuleID.createConceptID(cName);
        Import pid = new Import(cName.getLocation(), id);
        imports.add(pid);
    }

    private void annexEnhancementModule(PosSymbol eName, PosSymbol cName) {
        ModuleID id = ModuleID.createEnhancementID(eName, cName);
        Import pid = new Import(eName.getLocation(), id);
        imports.add(pid);
    }

    private void annexConceptBodyModule(PosSymbol bName, PosSymbol cName) {
        ModuleID id = ModuleID.createConceptBodyID(bName, cName);
        Import pid = new Import(bName.getLocation(), id);
        imports.add(pid);
    }

    private void annexEnhancementBodyModule(PosSymbol bName, PosSymbol eName,
            PosSymbol cName) {
        ModuleID id = ModuleID.createEnhancementBodyID(bName, eName, cName);
        Import pid = new Import(bName.getLocation(), id);
        imports.add(pid);
    }

    private void annexPerformanceProfile(PosSymbol profileName) {
        ModuleID id = ModuleID.createPerformanceID(profileName);
        Import pid = new Import(profileName.getLocation(), id);
        imports.add(pid);
    }

    private void annexUsesItem(PosSymbol name) {
        ModuleID id = ModuleID.createUsesItemID(name);
        Import pid = new Import(name.getLocation(), id);
        imports.add(pid);
    }
}

/**
 * ImportScanner.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.init;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.data.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class scans the module dec for imported modules: associates, uses items,
 * and modules used in facility declarations. It adds the modules (and their
 * location) to a list of imports that is returned to the controller.
 */
public class ImportScanner extends ResolveConceptualVisitor {

    // ===========================================================
    // Variables
    // ===========================================================

    //Modules with names matching a name in this set will not get any default
    //imports
    public static final Set<String> NO_DEFAULT_IMPORT_MODULES;

    static {
        Set<String> noDefault = new HashSet<String>();

        noDefault.add("Basic_Function_Properties_Theory");
        noDefault.add("Monogenerator_Theory");

        NO_DEFAULT_IMPORT_MODULES = Collections.unmodifiableSet(noDefault);
    }

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

        if (!NO_DEFAULT_IMPORT_MODULES.contains(decName)) {

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
                        listOfDependLists.get(i).addAllUnique(
                                (dec.getUsesItems()));
                        myInstanceEnvironment
                                .setStdUsesDepends(listOfDependLists);
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
                                if (it.next().getName().getName().equals(
                                        decName)) {
                                    // This is a dependency do NOT annex/add
                                    dec.accept(this);
                                    return;
                                }
                            }
                        }
                    }
                    // Update the dec's UsesItemList
                    PosSymbol facSymbol =
                            new PosSymbol(null, Symbol.symbol("Std_"
                                    + stdUses[i] + "_Fac"));
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

    // -- YS --NY
    public void visitPerformanceCModuleDec(PerformanceCModuleDec dec) {
        annexConceptModule(dec.getProfilecName());
        visitUsesItemList(dec.getUsesItems());
    }

    // -- YS NY
    public void visitPerformanceEModuleDec(PerformanceEModuleDec dec) {
        annexConceptModule(dec.getProfilecName());
        annexEnhancementModule(dec.getProfileName3(), dec.getProfilecName());
        visitUsesItemList(dec.getUsesItems());

        if (dec.getProfilecpName() != null) {
            annexPerformanceProfile(dec.getProfilecpName());
        }
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

        if (dec.getExternallyRealizedFlag() == false) {
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

/**
 * FacilityEntry.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2.entry;

import edu.clemson.cs.r2jt.absynnew.EnhancementPairAST;
import edu.clemson.cs.r2jt.absynnew.decl.FacilityAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.ModuleParameterization;
import edu.clemson.cs.r2jt.typeandpopulate2.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate2.SpecRealizationPairing;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import org.antlr.v4.runtime.Token;

import java.util.*;

public class FacilityEntry extends SymbolTableEntry {

    private final SpecRealizationPairing myType;

    private final List<ModuleParameterization> myEnhancements =
            new LinkedList<ModuleParameterization>();

    private final ScopeRepository mySourceRepository;

    private Map<ModuleParameterization, ModuleParameterization> myEnhancementRealizations =
            new HashMap<ModuleParameterization, ModuleParameterization>();

    public FacilityEntry(FacilityAST facility, ModuleIdentifier sourceModule,
            ScopeRepository sourceRepository) {
        super(facility.getName().getText(), facility, sourceModule);

        mySourceRepository = sourceRepository;

        ModuleParameterization spec =
                new ModuleParameterization(new ModuleIdentifier(facility
                        .getConceptName().getText()), facility
                        .getConceptArguments(), this, mySourceRepository);

        ModuleParameterization realization = null;
        if (facility.getBodyName() != null) {
            realization =
                    new ModuleParameterization(new ModuleIdentifier(facility
                            .getBodyName().getText()), facility
                            .getBodyArguments(), this, mySourceRepository);
        }

        myType = new SpecRealizationPairing(spec, realization);

        //These are realized by the concept realization
        //Todo
        /*for (EnhancementItem realizationEnhancement : facility
                .getEnhancements()) {

            spec =
                    new ModuleParameterization(new ModuleIdentifier(
                            realizationEnhancement.getName().getName()),
                            realizationEnhancement.getParams(), this,
                            mySourceRepository);

            myEnhancements.add(spec);
            myEnhancementRealizations.put(spec, realization);
        }*/

        //These are realized by individual enhancement realizations
        for (EnhancementPairAST enhancement : facility.getEnhancementPairs()) {

            spec =
                    new ModuleParameterization(new ModuleIdentifier(enhancement
                            .getSpecName().getText()), enhancement
                            .getSpecArguments(), this, mySourceRepository);

            realization =
                    new ModuleParameterization(new ModuleIdentifier(enhancement
                            .getImplName().getText()), enhancement
                            .getImplArguments(), this, mySourceRepository);

            myEnhancements.add(spec);
            myEnhancementRealizations.put(spec, realization);
        }
    }

    public SpecRealizationPairing getFacility() {
        return myType;
    }

    public List<ModuleParameterization> getEnhancements() {
        return Collections.unmodifiableList(myEnhancements);
    }

    /**
     * <p>Do not assume that each enhancement is realized by a corresponding,
     * individual realization.  Some enhancements may be realized by the base
     * concept, in which case this method will return that module.</p>
     * 
     * <p>For example, <code>Stack_Template</code> might have an enhancement
     * <code>Get_Nth_Ability</code>, which replicates and returns the 
     * <em>n</em>th element from the top of the stack.  There may be an 
     * <code>Obvious_Get_Nth_Realization</code> that does what you'd expect:
     * repeatedly pop some elements off the top to get to the requested element,
     * replicate it, then push everything back on.  Using this enhancement would
     * be a fine choice for extending <code>Stack_Template</code> as realized
     * by <code>Pointer_Realization</code>.  However, if we happen to know that
     * we're using <code>Array_Based_Realization</code>, we can do much better.
     * For this purpose, <code>Array_Based_Realization</code> can directly
     * incorporate enhancements and provide a direct realization that takes
     * advantage of implementation details.  I.e., <code>Get_Nth()</code> would
     * be included as a procedure inside <code>Array_Based_Realization</code>.
     * In such a case, we would declare a facility like this:</p>
     * 
     * <pre>
     * Facility Indexible_Stack is Stack_Template enhanced with Get_Nth_Ability
     *     realized by Array_Based_Realization;
     * </pre>
     * 
     * <p>And, calling this method to ask the realization for 
     * <code>Get_Nth_Ability</code> would return 
     * <code>Array_Based_Realization</code>.</p>
     * 
     * @param enhancement
     * @return
     */
    public ModuleParameterization getEnhancementRealization(
            ModuleParameterization enhancement) {

        return myEnhancementRealizations.get(enhancement);
    }

    @Override
    public FacilityEntry toFacilityEntry(Token l) {
        return this;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a facility";
    }

    @Override
    public FacilityEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        //TODO : This is probably wrong.  One of the parameters to a module
        //       used in the facility could be a generic, in which case it 
        //       should be replaced with the corresponding concrete type--but
        //       how?
        return this;
    }
}

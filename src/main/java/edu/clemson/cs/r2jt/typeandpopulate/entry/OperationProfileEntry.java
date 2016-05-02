/**
 * OperationProfileEntry.java
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
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.PerformanceOperationDec;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import java.util.Map;

/**
 *
 * @author ys
 */
public class OperationProfileEntry extends SymbolTableEntry {

    private final OperationEntry myCorrespondingOperation;
    private final Exp myEnsures;
    private final Exp myDuration;
    private final Exp myManipDisp;

    public OperationProfileEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, OperationEntry correspondingOperation) {

        super(name, definingElement, sourceModule);

        PerformanceOperationDec dec = (PerformanceOperationDec) definingElement;
        myCorrespondingOperation = correspondingOperation;
        myEnsures = dec.getEnsures();
        myDuration = dec.getDuration();
        myManipDisp = dec.getMainp_disp();
    }

    public OperationEntry getCorrespondingOperation() {
        return myCorrespondingOperation;
    }

    public Exp getEnsuresClause() {
        return myEnsures;
    }

    public Exp getDurationClause() {
        return myDuration;
    }

    public Exp getManipDispClause() {
        return myManipDisp;
    }

    @Override
    public String getEntryTypeDescription() {
        return "the profile of an operation";
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OperationProfileEntry toOperationProfileEntry(Location l) {
        return this;
    }
}

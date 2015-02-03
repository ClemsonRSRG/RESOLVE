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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
package edu.clemson.cs.r2jt.typeandpopulate.programtypes;

import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.Utilities;
import edu.clemson.cs.r2jt.typeandpopulate.MTNamed;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>Represents a <em>type family</em> as would be introduced inside a concept.
 * This is an abstract program type without a realization and without parameters
 * instantiated.</p>
 */
public class PTFamily extends PTType {

    private final MTType myModel;
    private final String myName;
    private final String myExemplarName;
    private final PExp myConstraint;
    private final PExp myInitializationRequires;
    private final PExp myInitializationEnsures;
    private final PExp myFinalizationRequires;
    private final PExp myFinalizationEnsures;

    public PTFamily(MTType model, String familyName, String exemplarName,
            Exp constraint, Exp initializationRequires,
            Exp initializationEnsures, Exp finalizationRequires,
            Exp finalizationEnsures) {
        this(model, familyName, exemplarName, normalize(model.getTypeGraph(),
                constraint), normalize(model.getTypeGraph(),
                initializationRequires), normalize(model.getTypeGraph(),
                initializationEnsures), normalize(model.getTypeGraph(),
                finalizationRequires), normalize(model.getTypeGraph(),
                finalizationEnsures));
    }

    private static PExp normalize(TypeGraph g, Exp original) {
        if (original == null) {
            original = g.getTrueVarExp();
        }

        original = Utilities.applyQuantification(original);

        return PExp.buildPExp(original);
    }

    public PTFamily(MTType model, String familyName, String exemplarName,
            PExp constraint, PExp initializationRequires,
            PExp initializationEnsures, PExp finalizationRequires,
            PExp finalizationEnsures) {
        super(model.getTypeGraph());

        myName = familyName;
        myModel = model;
        myExemplarName = exemplarName;
        myConstraint = constraint;
        myInitializationRequires = initializationRequires;
        myInitializationEnsures = initializationEnsures;
        myFinalizationRequires = finalizationRequires;
        myFinalizationEnsures = finalizationEnsures;
    }

    public String getName() {
        return myName;
    }

    public String getExemplarName() {
        return myExemplarName;
    }

    public PExp getConstraint() {
        return myConstraint;
    }

    public PExp getInitializationRequires() {
        return myInitializationRequires;
    }

    public PExp getInitializationEnsures() {
        return myInitializationEnsures;
    }

    public PExp getFinalizationRequires() {
        return myFinalizationRequires;
    }

    public PExp getFinalizationEnsures() {
        return myFinalizationEnsures;
    }

    @Override
    public MTType toMath() {
        return myModel;
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        Map<String, MTType> stringToMathType =
                SymbolTableEntry.buildMathTypeGenerics(genericInstantiations);

        @SuppressWarnings("unchecked")
        Map<MTType, MTType> mathTypeToMathType =
                (Map<MTType, MTType>) (Map<?, MTType>) MTNamed.toMTNamedMap(
                        getTypeGraph(), stringToMathType);

        MTType newModel =
                myModel.getCopyWithVariablesSubstituted(stringToMathType);

        PExp newConstraint =
                myConstraint.withTypesSubstituted(mathTypeToMathType);

        PExp newInitializationRequires =
                myInitializationRequires
                        .withTypesSubstituted(mathTypeToMathType);

        PExp newInitializationEnsures =
                myInitializationEnsures
                        .withTypesSubstituted(mathTypeToMathType);

        PExp newFinalizationRequires =
                myFinalizationRequires.withTypesSubstituted(mathTypeToMathType);

        PExp newFinalizationEnsures =
                myFinalizationEnsures.withTypesSubstituted(mathTypeToMathType);

        return new PTFamily(newModel, myName, myExemplarName, newConstraint,
                newInitializationRequires, newInitializationEnsures,
                newFinalizationRequires, newFinalizationEnsures);
    }

    @Override
    public boolean equals(Object o) {
        boolean result = (o instanceof PTFamily);

        if (result) {
            PTFamily oAsPTFamily = (PTFamily) o;

            result =
                    (myModel.equals(oAsPTFamily.myModel))
                            && (myName.equals(oAsPTFamily.myName))
                            && (myExemplarName
                                    .equals(oAsPTFamily.myExemplarName))
                            && (myConstraint.equals(oAsPTFamily.myConstraint))
                            && (myInitializationRequires
                                    .equals(oAsPTFamily.myInitializationRequires))
                            && (myInitializationEnsures
                                    .equals(oAsPTFamily.myInitializationEnsures))
                            && (myFinalizationRequires
                                    .equals(oAsPTFamily.myFinalizationRequires))
                            && (myFinalizationEnsures
                                    .equals(oAsPTFamily.myFinalizationEnsures));
        }

        return result;
    }

    @Override
    public String toString() {
        return myName;
    }
}

/**
 * PTFamily.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2.programtypes;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.rewriteprover.Utilities;
import edu.clemson.cs.r2jt.rewriteprover.absyn2.PExpr;
import edu.clemson.cs.r2jt.typeandpopulate2.MTNamed;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

import java.util.Map;

/**
 * <p>Represents a <em>type family</em> as would be introduced inside a concept.
 * This is an abstract program type without a realization and without parameters
 * instantiated.</p>
 */
public class PTFamily extends PTType {

    private final MTType myModel;
    private final String myName;
    private final String myExemplarName;
    private final PExpr myConstraint;
    private final PExpr myInitializationRequires;
    private final PExpr myInitializationEnsures;
    private final PExpr myFinalizationRequires;
    private final PExpr myFinalizationEnsures;

    public PTFamily(MTType model, String familyName, String exemplarName,
            ExprAST constraint, ExprAST initializationRequires,
            ExprAST initializationEnsures, ExprAST finalizationRequires,
            ExprAST finalizationEnsures) {
        this(model, familyName, exemplarName, normalize(model.getTypeGraph(),
                constraint), normalize(model.getTypeGraph(),
                initializationRequires), normalize(model.getTypeGraph(),
                initializationEnsures), normalize(model.getTypeGraph(),
                finalizationRequires), normalize(model.getTypeGraph(),
                finalizationEnsures));
    }

    private static PExpr normalize(TypeGraph g, ExprAST original) {
        if (original == null) {
            original = g.getTrueVarExp();
        }

        original = Utilities.applyQuantification(original);

        return PExpr.buildPExp(original);
    }

    public PTFamily(MTType model, String familyName, String exemplarName,
            PExpr constraint, PExpr initializationRequires,
            PExpr initializationEnsures, PExpr finalizationRequires,
            PExpr finalizationEnsures) {
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

    public PExpr getConstraint() {
        return myConstraint;
    }

    public PExpr getInitializationRequires() {
        return myInitializationRequires;
    }

    public PExpr getInitializationEnsures() {
        return myInitializationEnsures;
    }

    public PExpr getFinalizationRequires() {
        return myFinalizationRequires;
    }

    public PExpr getFinalizationEnsures() {
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

        PExpr newConstraint =
                myConstraint.withTypesSubstituted(mathTypeToMathType);

        PExpr newInitializationRequires =
                myInitializationRequires
                        .withTypesSubstituted(mathTypeToMathType);

        PExpr newInitializationEnsures =
                myInitializationEnsures
                        .withTypesSubstituted(mathTypeToMathType);

        PExpr newFinalizationRequires =
                myFinalizationRequires.withTypesSubstituted(mathTypeToMathType);

        PExpr newFinalizationEnsures =
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

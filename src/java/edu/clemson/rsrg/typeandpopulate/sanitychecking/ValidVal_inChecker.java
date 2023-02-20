/*
 * ValidVal_inChecker.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.DotExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.rsrg.absyn.rawtypes.Ty;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.rsrg.typeandpopulate.query.NameQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.List;

/**
 * <p>
 * This is a sanity checker for making sure the {@code Val_in} function application is valid.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class ValidVal_inChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>
     * The symbol table we are currently building.
     * </p>
     */
    private final MathSymbolTableBuilder myBuilder;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates a sanity checker for checking whether or not the we have a valid {@code Val_in} function.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param b
     *            The current scope repository builder.
     */
    public ValidVal_inChecker(TypeGraph g, MathSymbolTableBuilder b) {
        myTypeGraph = g;
        myBuilder = b;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method indicates whether or not this is a valid application of the {@code Val_in} function.
     * </p>
     *
     * @param lastSegment
     *            The segment that contains the type information for the {@code Val_in} function.
     * @param argExp
     *            The argument expression passed to the {@code Val_in} function.
     */
    public final void validArgument(Exp lastSegment, Exp argExp) {
        // This is the only kind allowed: Foo.Val_in(recp.f)
        // So anything that isn't a Receptacle or isn't a DotExp of size 2
        // is definitely not allowed
        if (!(argExp instanceof DotExp) || ((DotExp) argExp).getSegments().size() != 2
                || !argExp.getMathType().equals(myTypeGraph.RECEPTACLES)) {
            // This is definitely not a Receptacle
            throw new SourceErrorException("Not a Receptacle", argExp.getLocation());
        } else {
            List<Exp> argExpSegments = ((DotExp) argExp).getSegments();
            Exp segArg1 = argExpSegments.get(0);
            Exp segArg2 = argExpSegments.get(1);

            if (!(segArg1 instanceof VarExp) || !(segArg2 instanceof VarExp)
                    || !((VarExp) segArg1).getName().getName().equals("recp")) {
                // This is definitely not a Receptacle
                throw new SourceErrorException("Not a Receptacle", argExp.getLocation());
            } else {
                VarExp segArg2AsVarExp = (VarExp) segArg2;

                try {
                    SymbolTableEntry symbolEntry = myBuilder.getInnermostActiveScope()
                            .queryForOne(new NameQuery(null, segArg2AsVarExp.getName(),
                                    MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                                    MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, true));

                    // Make sure the types are the same
                    MathSymbolEntry mathSymbolEntry = symbolEntry.toMathSymbolEntry(segArg2.getLocation());
                    if (!lastSegment.getMathTypeValue().equals(mathSymbolEntry.getType())) {
                        throw new SourceErrorException("Parameters do not "
                                + "match function range.\n\nExpecting a Receptacle with math type: "
                                + lastSegment.getMathTypeValue() + "\nFound a Receptacle with math type: "
                                + mathSymbolEntry.getType(), segArg2AsVarExp.getLocation());
                    } else {
                        // YS: Now comes the hard part. We could have two things that have the same
                        // math type, but from different program types. So we will need to make sure
                        // the program types match.
                        VarExp lastSegmentAsVarExp = (VarExp) lastSegment;
                        if (symbolEntry instanceof MathSymbolEntry) {
                            // This must be an exemplar!
                            TypeFamilyDec dec = (TypeFamilyDec) mathSymbolEntry.getDefiningElement();
                            if (!dec.getName().equals(lastSegmentAsVarExp.getName())) {
                                throw new SourceErrorException(
                                        "Parameters do not "
                                                + "match function range.\n\nExpecting a Receptacle with program type: "
                                                + lastSegmentAsVarExp.getName()
                                                + "\nFound a Receptacle with program type: " + dec.getName().getName(),
                                        segArg2AsVarExp.getLocation());
                            }
                        } else if (symbolEntry instanceof ProgramParameterEntry) {
                            // This is a program parameter!
                            ProgramParameterEntry programParamEntry = symbolEntry
                                    .toProgramParameterEntry(segArg2.getLocation());

                            ParameterVarDec dec = (ParameterVarDec) programParamEntry.getDefiningElement();
                            Ty decTy = dec.getTy();
                            if (!(decTy instanceof NameTy)
                                    || !((NameTy) decTy).getName().equals(lastSegmentAsVarExp.getName())) {
                                throw new SourceErrorException(
                                        "Parameters do not "
                                                + "match function range.\n\nExpecting a Receptacle with program type: "
                                                + lastSegmentAsVarExp.getName()
                                                + "\nFound a Receptacle with program type: " + decTy.toString(),
                                        segArg2AsVarExp.getLocation());
                            }
                        } else {
                            throw new SourceErrorException(
                                    "This type of entry isn't allowed: " + symbolEntry.getEntryTypeDescription(),
                                    segArg2AsVarExp.getLocation());
                        }
                    }
                } catch (NoSuchSymbolException nsse2) {
                    throw new SourceErrorException("No such symbol: " + segArg2AsVarExp.getName(),
                            segArg2AsVarExp.getLocation());
                } catch (DuplicateSymbolException dse) {
                    throw new SourceErrorException("Duplicate symbol: " + segArg2AsVarExp.getName(),
                            segArg2AsVarExp.getLocation());
                }
            }
        }
    }

}

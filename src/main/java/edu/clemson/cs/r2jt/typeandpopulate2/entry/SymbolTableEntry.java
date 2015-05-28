/**
 * SymbolTableEntry.java
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
package edu.clemson.cs.r2jt.typeandpopulate2.entry;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.misc.SrcErrorException;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import org.antlr.v4.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public abstract class SymbolTableEntry {

    public enum Quantification {
        NONE {

            @Override
            public String toString() {
                return "None";
            }
        },
        UNIVERSAL {

            @Override
            public String toString() {
                return "Universal";
            }
        },
        EXISTENTIAL {

            @Override
            public String toString() {
                return "Existential";
            }
        }
    }

    private final String myName;
    private final ResolveAST myDefiningElement;
    private final ModuleIdentifier mySourceModuleIdentifier;

    public SymbolTableEntry(String name, ResolveAST definingElement,
            ModuleIdentifier sourceModule) {
        myName = name;
        myDefiningElement = definingElement;
        mySourceModuleIdentifier = sourceModule;
    }

    public ModuleIdentifier getSourceModuleIdentifier() {
        return mySourceModuleIdentifier;
    }

    public String getName() {
        return myName;
    }

    public ResolveAST getDefiningElement() {
        return myDefiningElement;
    }

    public RepresentationTypeEntry toRepresentationTypeEntry(Token l) {
        throw new SrcErrorException("Expecting a program type "
                + "representation.  Found " + getEntryTypeDescription() + ".",
                l);
    }

    public MathSymbolEntry toMathSymbolEntry(Token l) {
        throw new SrcErrorException("expecting a math symbol; found "
                + getEntryTypeDescription(), l);
    }

    public ProgramTypeEntry toProgramTypeEntry(Token l) {
        throw new SrcErrorException("expecting a program type; found "
                + getEntryTypeDescription(), l);
    }

    public FacilityEntry toFacilityEntry(Token l) {
        throw new SrcErrorException("expecting a facility; found "
                + getEntryTypeDescription(), l);
    }

    public ProgramVariableEntry toProgramVariableEntry(Token l) {
        throw new SrcErrorException("expecting a program variable;  "
                + "found " + getEntryTypeDescription(), l);
    }

    public ProgramParameterEntry toProgramParameterEntry(Token l) {
        throw new SrcErrorException("expecting a program parameter;  "
                + "found " + getEntryTypeDescription(), l);
    }

    public OperationEntry toOperationEntry(Token l) {
        throw new SrcErrorException("expecting an operation; found "
                + getEntryTypeDescription(), l);
    }

    public ProgramTypeDefinitionEntry toProgramTypeDefinitionEntry(Token l) {
        throw new SrcErrorException("expecting a program type definition; "
                + "found " + getEntryTypeDescription(), l);
    }

    /*public OperationProfileEntry toOperationProfileEntry(Location l) {
        throw new SourceErrorException("Expecting a operation profile.  Found "
                + getEntryTypeDescription(), l);
    }*/

    public ProcedureEntry toProcedureEntry(Token l) {
        throw new SrcErrorException("Expecting a procedure.  Found "
                + getEntryTypeDescription(), l);
    }

    /*public ShortFacilityEntry toShortFacilityEntry(Location l) {
        throw new SourceErrorException("Expecting a short facility module.  "
                + "Found " + getEntryTypeDescription(), l);
    }*/

    public AbstractProgramEntry toProgrammaticEntry(Token l) {
        throw new SrcErrorException("expecting a programmatic entry; found "
                + getEntryTypeDescription(), l);
    }

    public TheoremEntry toTheoremEntry(Token l) {
        throw new SrcErrorException("expecting a theorem; found "
                + getEntryTypeDescription(), l);
    }

    public abstract String getEntryTypeDescription();

    public abstract SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility);

    public static Map<String, MTType> buildMathTypeGenerics(
            Map<String, PTType> genericInstantiations) {

        Map<String, MTType> genericMathematicalInstantiations =
                new HashMap<String, MTType>();

        for (Map.Entry<String, PTType> instantiation : genericInstantiations
                .entrySet()) {

            genericMathematicalInstantiations.put(instantiation.getKey(),
                    instantiation.getValue().toMath());
        }

        return genericMathematicalInstantiations;
    }

}

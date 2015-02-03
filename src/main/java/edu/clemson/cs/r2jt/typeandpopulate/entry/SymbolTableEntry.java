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
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import java.util.HashMap;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.absyn.VarExp;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

/**
 * <p>Checklist for subclassing <code>SymbolTableEntry</code>:</p>
 * 
 * <ul>
 * 		<li>Create subclass.</li>
 * 		<li>Add "toXXX()" method in this parent class.</li>
 * 		<li>Override it in subclass.</li>
 *              <li>Consider if entry can be coerced to other kinds of entries,
 *                  and override those toXXXs as well. (See ProgramVariableEntry
 *                  as an example.</li>
 * </ul>
 */
public abstract class SymbolTableEntry {

    public enum Quantification {
        NONE {

            @Override
            public String toString() {
                return "None";
            }

            @Override
            public int toVarExpQuantificationCode() {
                return VarExp.NONE;
            }
        },
        UNIVERSAL {

            @Override
            public String toString() {
                return "Universal";
            }

            @Override
            public int toVarExpQuantificationCode() {
                return VarExp.FORALL;
            }
        },
        EXISTENTIAL {

            @Override
            public String toString() {
                return "Existential";
            }

            @Override
            public int toVarExpQuantificationCode() {
                return VarExp.EXISTS;
            }
        };

        public abstract int toVarExpQuantificationCode();
    }

    private final String myName;
    private final ResolveConceptualElement myDefiningElement;
    private final ModuleIdentifier mySourceModuleIdentifier;

    public SymbolTableEntry(String name,
            ResolveConceptualElement definingElement,
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

    public ResolveConceptualElement getDefiningElement() {
        return myDefiningElement;
    }

    public FacilityTypeRepresentationEntry toFacilityTypeRepresentationEntry(
            Location l) {
        throw new SourceErrorException("Expecting a facility type "
                + "representation.  Found " + getEntryTypeDescription() + ".",
                l);
    }

    public RepresentationTypeEntry toRepresentationTypeEntry(Location l) {
        throw new SourceErrorException("Expecting a program type "
                + "representation.  Found " + getEntryTypeDescription() + ".",
                l);
    }

    public MathSymbolEntry toMathSymbolEntry(Location l) {
        throw new SourceErrorException("Expecting a math symbol.  Found "
                + getEntryTypeDescription() + ".", l);
    }

    public ProgramTypeEntry toProgramTypeEntry(Location l) {
        throw new SourceErrorException("Expecting a program type.  Found "
                + getEntryTypeDescription() + ".", l);
    }

    public FacilityEntry toFacilityEntry(Location l) {
        throw new SourceErrorException("Expecting a facility.  Found "
                + getEntryTypeDescription(), l);
    }

    public ProgramParameterEntry toProgramParameterEntry(Location l) {
        throw new SourceErrorException("Expecting a program parameter.  "
                + "Found " + getEntryTypeDescription(), l);
    }

    public ProgramVariableEntry toProgramVariableEntry(Location l) {
        throw new SourceErrorException("Expecting a program variable.  "
                + "Found " + getEntryTypeDescription(), l);
    }

    public OperationEntry toOperationEntry(Location l) {
        throw new SourceErrorException("Expecting an operation.  Found "
                + getEntryTypeDescription(), l);
    }

    public OperationProfileEntry toOperationProfileEntry(Location l) {
        throw new SourceErrorException("Expecting a operation profile.  Found "
                + getEntryTypeDescription(), l);
    }

    public ProcedureEntry toProcedureEntry(Location l) {
        throw new SourceErrorException("Expecting a procedure.  Found "
                + getEntryTypeDescription(), l);
    }

    public ShortFacilityEntry toShortFacilityEntry(Location l) {
        throw new SourceErrorException("Expecting a short facility module.  "
                + "Found " + getEntryTypeDescription(), l);
    }

    public ProgramTypeDefinitionEntry toProgramTypeDefinitionEntry(Location l) {
        throw new SourceErrorException("Expecting a program type definition.  "
                + "Found " + getEntryTypeDescription(), l);
    }

    public TheoremEntry toTheoremEntry(Location l) {
        throw new SourceErrorException("Expecting a theorem.  " + "Found "
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

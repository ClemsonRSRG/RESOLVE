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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.typeandpopulate.MTNamed;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProgramParameterEntry extends SymbolTableEntry {

    public static enum ParameterMode {
        ALTERS {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { ALTERS, CLEARS };
            }
        },
        UPDATES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { UPDATES, CLEARS, RESTORES,
                        PRESERVES };
            }
        },
        REPLACES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { REPLACES, CLEARS };
            }
        },
        CLEARS {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { CLEARS };
            }
        },
        RESTORES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { RESTORES, PRESERVES };
            }
        },
        PRESERVES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { PRESERVES };
            }
        },
        EVALUATES {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { EVALUATES };
            }
        },
        TYPE {

            @Override
            public ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { TYPE };
            }
        };

        public boolean canBeImplementedWith(ParameterMode o) {
            return contains(getValidImplementationModes(), o);
        }

        private static boolean contains(Object[] os, Object o) {
            boolean result = false;

            int i = 0;
            int osLength = os.length;
            while (!result && i < osLength) {
                result = os[i].equals(o);
                i++;
            }

            return result;
        }

        public abstract ParameterMode[] getValidImplementationModes();
    }

    public final static Map<Mode, ParameterMode> OLD_TO_NEW_MODE;

    static {
        Map<Mode, ParameterMode> mapping = new HashMap<Mode, ParameterMode>();

        mapping.put(Mode.ALTERS, ParameterMode.ALTERS);
        mapping.put(Mode.UPDATES, ParameterMode.UPDATES);
        mapping.put(Mode.REPLACES, ParameterMode.REPLACES);
        mapping.put(Mode.CLEARS, ParameterMode.CLEARS);
        mapping.put(Mode.RESTORES, ParameterMode.RESTORES);
        mapping.put(Mode.PRESERVES, ParameterMode.PRESERVES);
        mapping.put(Mode.EVALUATES, ParameterMode.EVALUATES);

        OLD_TO_NEW_MODE = Collections.unmodifiableMap(mapping);
    }

    private final PTType myDeclaredType;
    private final ParameterMode myPassingMode;
    private final TypeGraph myTypeGraph;

    private final MathSymbolEntry myMathSymbolAlterEgo;
    private final ProgramVariableEntry myProgramVariableAlterEgo;

    public ProgramParameterEntry(TypeGraph g, String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType type, ParameterMode mode) {
        super(name, definingElement, sourceModule);

        myTypeGraph = g;
        myDeclaredType = type;
        myPassingMode = mode;

        MTType typeValue = null;
        if (mode == ParameterMode.TYPE) {
            typeValue = new PTGeneric(type.getTypeGraph(), name).toMath();
        }

        //TODO: Probably need to recajigger this to correctly account for any
        //      generics in the defining context
        myMathSymbolAlterEgo =
                new MathSymbolEntry(type.getTypeGraph(), name,
                        Quantification.NONE, definingElement, type.toMath(),
                        typeValue, null, null, sourceModule);

        myProgramVariableAlterEgo =
                new ProgramVariableEntry(getName(), getDefiningElement(),
                        getSourceModuleIdentifier(), myDeclaredType);
    }

    @Override
    public ProgramParameterEntry toProgramParameterEntry(Location l) {
        return this;
    }

    @Override
    public MathSymbolEntry toMathSymbolEntry(Location l) {
        return myMathSymbolAlterEgo;
    }

    @Override
    public ProgramVariableEntry toProgramVariableEntry(Location l) {
        return myProgramVariableAlterEgo;
    }

    @Override
    public ProgramTypeEntry toProgramTypeEntry(Location l) {

        ProgramTypeEntry result;

        if (!myPassingMode.equals(ParameterMode.TYPE)) {
            //This will throw an appropriate error
            result = super.toProgramTypeEntry(l);
        }
        else {
            result =
                    new ProgramTypeEntry(myTypeGraph, getName(),
                            getDefiningElement(), getSourceModuleIdentifier(),
                            new MTNamed(myTypeGraph, getName()), new PTGeneric(
                                    myTypeGraph, getName()));
        }

        return result;
    }

    public ParameterMode getParameterMode() {
        return myPassingMode;
    }

    public PTType getDeclaredType() {
        return myDeclaredType;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a program parameter";
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        return new ProgramParameterEntry(myTypeGraph, getName(),
                getDefiningElement(), getSourceModuleIdentifier(),
                myDeclaredType.instantiateGenerics(genericInstantiations,
                        instantiatingFacility), myPassingMode);
    }

    @Override
    public String toString() {
        return "" + myPassingMode + myDeclaredType;
    }
}

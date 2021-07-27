/*
 * ProgramParameterEntry.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.entry;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTNamed;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Map;

/**
 * <p>
 * This creates a symbol table entry for a program parameter declaration.
 * </p>
 *
 * @version 2.0
 */
public class ProgramParameterEntry extends SymbolTableEntry {

    // ===========================================================
    // Parameter Modes
    // ===========================================================

    /**
     * <p>
     * This defines the various different parameter modes that a
     * {@link ParameterVarDec} can have.
     * </p>
     *
     * @version 2.0
     */
    public enum ParameterMode {

        ALTERS {

            /**
             * <p>
             * This method returns all the parameter modes that can be used to
             * implement a particular
             * mode.
             * </p>
             *
             * @return An array of valid implementation {@link ParameterMode}s.
             */
            @Override
            public final ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { ALTERS, CLEARS };
            }
        },
        UPDATES {

            /**
             * <p>
             * This method returns all the parameter modes that can be used to
             * implement a particular
             * mode.
             * </p>
             *
             * @return An array of valid implementation {@link ParameterMode}s.
             */
            @Override
            public final ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { UPDATES, CLEARS, RESTORES,
                        PRESERVES };
            }
        },
        REPLACES {

            /**
             * <p>
             * This method returns all the parameter modes that can be used to
             * implement a particular
             * mode.
             * </p>
             *
             * @return An array of valid implementation {@link ParameterMode}s.
             */
            @Override
            public final ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { REPLACES, CLEARS };
            }
        },
        CLEARS {

            /**
             * <p>
             * This method returns all the parameter modes that can be used to
             * implement a particular
             * mode.
             * </p>
             *
             * @return An array of valid implementation {@link ParameterMode}s.
             */
            @Override
            public final ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { CLEARS };
            }
        },
        RESTORES {

            /**
             * <p>
             * This method returns all the parameter modes that can be used to
             * implement a particular
             * mode.
             * </p>
             *
             * @return An array of valid implementation {@link ParameterMode}s.
             */
            @Override
            public final ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { RESTORES, PRESERVES };
            }
        },
        PRESERVES {

            /**
             * <p>
             * This method returns all the parameter modes that can be used to
             * implement a particular
             * mode.
             * </p>
             *
             * @return An array of valid implementation {@link ParameterMode}s.
             */
            @Override
            public final ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { PRESERVES };
            }
        },
        EVALUATES {

            /**
             * <p>
             * This method returns all the parameter modes that can be used to
             * implement a particular
             * mode.
             * </p>
             *
             * @return An array of valid implementation {@link ParameterMode}s.
             */
            @Override
            public final ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { EVALUATES };
            }
        },
        TYPE {

            /**
             * <p>
             * This method returns all the parameter modes that can be used to
             * implement a particular
             * mode.
             * </p>
             *
             * @return An array of valid implementation {@link ParameterMode}s.
             */
            @Override
            public final ParameterMode[] getValidImplementationModes() {
                return new ParameterMode[] { TYPE };
            }
        };

        /**
         * <p>
         * This method checks if a parameter mode can be implemented using
         * {@code o}.
         * </p>
         *
         * @param o A {@link ParameterMode} to check.
         *
         * @return {@code true} if {@code o} can be used to implement this mode,
         *         {@code false}
         *         otherwise.
         */
        public boolean canBeImplementedWith(ParameterMode o) {
            return contains(getValidImplementationModes(), o);
        }

        /**
         * <p>
         * A helper method to check whether we can implement this mode using
         * {@code o}.
         * </p>
         *
         * @param os An array of acceptable parameter modes.
         * @param o A {@link ParameterMode} to check.
         *
         * @return {@code true} if this mode contains {@code o}, {@code false}
         *         otherwise.
         */
        private static boolean contains(ParameterMode[] os, ParameterMode o) {
            boolean result = false;

            int i = 0;
            int osLength = os.length;
            while (!result && i < osLength) {
                result = os[i].equals(o);
                i++;
            }

            return result;
        }

        /**
         * <p>
         * This method returns all the parameter modes that can be used to
         * implement a particular mode.
         * </p>
         *
         * @return An array of valid implementation {@link ParameterMode}s.
         */
        public abstract ParameterMode[] getValidImplementationModes();
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The program type declared for this entry.
     * </p>
     */
    private final PTType myDeclaredType;

    /**
     * <p>
     * The parameter mode for this entry.
     * </p>
     */
    private final ParameterMode myPassingMode;

    /**
     * <p>
     * The current type graph object in use.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>
     * The mathematical symbol entry associated with this entry.
     * </p>
     */
    private final MathSymbolEntry myMathSymbolAlterEgo;

    /**
     * <p>
     * The program variable entry associated with this entry.
     * </p>
     */
    private final ProgramVariableEntry myProgramVariableAlterEgo;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a program parameter declaration.
     * </p>
     *
     * @param g The current type graph.
     * @param name Name associated with this entry.
     * @param definingElement The element that created this entry.
     * @param sourceModule The module where this entry was created from.
     * @param type The program type declared for this entry.
     * @param mode The parameter mode for this entry.
     */
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

        // TODO: Probably need to recajigger this to correctly account for any
        // generics in the defining context
        myMathSymbolAlterEgo = new MathSymbolEntry(type.getTypeGraph(), name,
                Quantification.NONE, definingElement, type.toMath(), typeValue,
                null, null, sourceModule);

        myProgramVariableAlterEgo =
                new ProgramVariableEntry(getName(), getDefiningElement(),
                        getSourceModuleIdentifier(), myDeclaredType);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the program type associated with this entry.
     * </p>
     *
     * @return A {@link PTType} representation object.
     */
    public final PTType getDeclaredType() {
        return myDeclaredType;
    }

    /**
     * <p>
     * This method returns a description associated with this entry.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getEntryTypeDescription() {
        return "a program parameter";
    }

    /**
     * <p>
     * This method returns the parameter mode associated with this entry.
     * </p>
     *
     * @return A {@link ParameterMode} representation object.
     */
    public final ParameterMode getParameterMode() {
        return myPassingMode;
    }

    /**
     * <p>
     * This method converts a generic {@link SymbolTableEntry} to an entry that
     * has all the generic
     * types and variables replaced with actual values.
     * </p>
     *
     * @param genericInstantiations Map containing all the instantiations.
     * @param instantiatingFacility Facility that instantiated this type.
     *
     * @return A {@link ProgramParameterEntry} that has been instantiated.
     */
    @Override
    public final ProgramParameterEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        return new ProgramParameterEntry(myTypeGraph, getName(),
                getDefiningElement(), getSourceModuleIdentifier(),
                myDeclaredType.instantiateGenerics(genericInstantiations,
                        instantiatingFacility),
                myPassingMode);
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a
     * {@link MathSymbolEntry}.
     * </p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link MathSymbolEntry} if possible. Otherwise, it throws a
     *         {@link SourceErrorException}.
     */
    @Override
    public final MathSymbolEntry toMathSymbolEntry(Location l) {
        return myMathSymbolAlterEgo;
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a
     * {@link ProgramParameterEntry}.
     * </p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link ProgramParameterEntry} if possible. Otherwise, it throws
     *         a
     *         {@link SourceErrorException}.
     */
    @Override
    public final ProgramParameterEntry toProgramParameterEntry(Location l) {
        return this;
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a
     * {@link ProgramTypeEntry}.
     * </p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link ProgramTypeEntry} if possible. Otherwise, it throws a
     *         {@link SourceErrorException}.
     */
    @Override
    public final ProgramTypeEntry toProgramTypeEntry(Location l) {

        ProgramTypeEntry result;

        if (!myPassingMode.equals(ParameterMode.TYPE)) {
            // This will throw an appropriate error
            result = super.toProgramTypeEntry(l);
        }
        else {
            result = new ProgramTypeEntry(myTypeGraph, getName(),
                    getDefiningElement(), getSourceModuleIdentifier(),
                    new MTNamed(myTypeGraph, getName()),
                    new PTGeneric(myTypeGraph, getName()));
        }

        return result;
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a
     * {@link ProgramVariableEntry}.
     * </p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link ProgramVariableEntry} if possible. Otherwise, it throws
     *         a
     *         {@link SourceErrorException}.
     */
    @Override
    public final ProgramVariableEntry toProgramVariableEntry(Location l) {
        return myProgramVariableAlterEgo;
    }

    /**
     * <p>
     * This method returns the object in string format.
     * </p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return "" + myPassingMode + myDeclaredType;
    }

}

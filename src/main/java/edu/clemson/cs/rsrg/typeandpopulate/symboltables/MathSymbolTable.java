/**
 * MathSymbolTable.java
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
package edu.clemson.cs.rsrg.typeandpopulate.symboltables;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;

/**
 * TODO: Refactor this class
 */
public class MathSymbolTable extends ScopeRepository {

    /**
     * <p>When starting a search from a particular scope, specifies how any
     * available facilities should be searched.</p>
     *
     * <p>Available facilities are those facilities defined in a module searched
     * by the search's <code>ImportStrategy</code> (which necessarily always
     * includes the source module).</p>
     *
     * <p>Note that facilities cannot be recursively searched.  Imports and
     * facilities appearing in available facilities will not be searched.</p>
     */
    public enum FacilityStrategy {

        /**
         * <p>Indicates that available facilities should not be searched.  The
         * default strategy.</p>
         */
        FACILITY_IGNORE,

        /**
         * <p>Indicates that available facilities should be searched with
         * generic types instantiated.  That is, any types used by symbols
         * inside the facility should be updated to reflect the particular
         * instantiation of the generic types.</p>
         */
        FACILITY_INSTANTIATE,

        /**
         * <p>Indicates that available facilities should be searched with
         * generic types intact.  That is, any types used by symbols inside the
         * facility will appear exactly as listed in the source file--including
         * references to generics--even if we could use information from the
         * facility to "fill them in."</p>
         */
        FACILITY_GENERIC
    }

    /**
     * <p>When starting a search from a particular scope, specifies which
     * additional modules should be searched, based on any imported modules.</p>
     *
     * <p>Imported modules are those listed in the <em>uses</em> clause of the
     * source module scope in which the scope is introduced.  For searches
     * originating directly in a module scope, the source module scope is the
     * scope itself.  In addition to those scopes directly imported in the
     * <em>uses</em> clause, any modules implicitly imported will also be
     * searched.  Implicitly imported modules include the standard modules
     * (<code>Std_Boolean_Fac</code>, etc.), and any modules named in the header
     * of the source module (e.g., an enhancement realization implicitly imports
     * it's associate enhancement and concept.)</p>
     */
    public enum ImportStrategy {

        /**
         * <p>Indicates that imported modules should not be searched. The
         * default strategy.</p>
         */
        IMPORT_NONE {

            public ImportStrategy cascadingStrategy() {
                return IMPORT_NONE;
            }

            public boolean considerImports() {
                return false;
            }
        },

        /**
         * <p>Indicates that only those modules imported directly from the
         * source module should be searched.</p>
         */
        IMPORT_NAMED {

            public ImportStrategy cascadingStrategy() {
                return IMPORT_NONE;
            }

            public boolean considerImports() {
                return true;
            }
        },

        /**
         * <p>Indicates that the search should recursively search the closure
         * of all imports and their own imports.</p>
         */
        IMPORT_RECURSIVE {

            public ImportStrategy cascadingStrategy() {
                return IMPORT_RECURSIVE;
            }

            public boolean considerImports() {
                return true;
            }
        };

        /**
         * <p>Returns the strategy that should be used to recursively search
         * any imported modules.</p>
         *
         * @return The strategy that should be used to recursively search any
         *         imported modules.
         */
        public abstract ImportStrategy cascadingStrategy();

        /**
         * <p>Returns <code>true</code> <strong>iff</strong> this strategy
         * requires searching directly imported modules.</p>
         *
         * @return <code>true</code> <strong>iff</strong> this strategy
         *         requires searching directly imported modules.
         */
        public abstract boolean considerImports();
    }

    /**
     * <p>Returns the {@link ModuleScope} associated with the given
     * {@link ModuleIdentifier}.</p>
     *
     * @param module The module identifier.
     * @throws NoSuchSymbolException If no scope has been opened for the named
     *                               module.
     * @returns The associated module scope.
     */
    @Override
    public ModuleScope getModuleScope(ModuleIdentifier module)
            throws NoSuchSymbolException {
        return null;
    }

    /**
     * <p>Returns the {@link Scope} introduced and bounded by the given
     * defining element.</p>
     *
     * @param e defining element.
     * @throws NoSuchSymbolException If no scope has been opened for the given
     *                               defining element.
     * @returns The associated scope.
     */
    @Override
    public Scope getScope(ResolveConceptualElement e) {
        return null;
    }

    /**
     * <p>Returns the {@link TypeGraph} that relates the types found in this
     * <code>ScopeRepository</code>.</p>
     *
     * @return The <code>TypeGraph</code>.
     */
    @Override
    public TypeGraph getTypeGraph() {
        return null;
    }

}
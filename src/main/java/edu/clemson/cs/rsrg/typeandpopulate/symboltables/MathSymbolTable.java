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
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.PrecisModuleDec;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A <code>MathSymbolTable</code> represents an immutable mapping from
 * those nodes in the AST that define a scope to {@link FinalizedScope} objects
 * representing those scopes and containing the symbols defined therein.</p>
 *
 * <p><code>Scope</code>s that were introduced at the module-level (e.g.,
 * the scope defined by a {@link PrecisModuleDec}) will have an
 * associated <code>Scope</code> that is further refined into an instance of
 * {@link FinalizedModuleScope}. As a convenience, such module scopes may
 * be retrieved in a type-safe way with a call to
 * {@link #getModuleScope(ModuleIdentifier)}.</p>
 *
 * <p>Note that there are no public constructors for <code>MathSymbolTable</code>.
 * New instances should be acquired from a call to {@link MathSymbolTableBuilder#seal()}.</p>
 *
 * @version 2.0
 */
public class MathSymbolTable extends ScopeRepository {

    // ===========================================================
    // Strategies
    // ===========================================================

    /**
     * <p>When starting a search from a particular scope, specifies how any
     * available facilities should be searched.</p>
     *
     * <p>Available facilities are those facilities defined in a module searched
     * by the search's <code>ImportStrategy</code> (which necessarily always
     * includes the source module).</p>
     *
     * <p>Note that facilities cannot be recursively searched. Imports and
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
     * scope itself. In addition to those scopes directly imported in the
     * <em>uses</em> clause, any modules implicitly imported will also be
     * searched. Implicitly imported modules include the standard modules
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

            /**
             * <p>Returns the strategy that should be used to recursively search
             * any imported modules.</p>
             *
             * @return The strategy that should be used to recursively search any
             *         imported modules.
             */
            @Override
            public ImportStrategy cascadingStrategy() {
                return IMPORT_NONE;
            }

            /**
             * <p>Returns <code>true</code> <strong>iff</strong> this strategy
             * requires searching directly imported modules.</p>
             *
             * @return <code>true</code> <strong>iff</strong> this strategy
             *         requires searching directly imported modules.
             */
            @Override
            public boolean considerImports() {
                return false;
            }
        },

        /**
         * <p>Indicates that only those modules imported directly from the
         * source module should be searched.</p>
         */
        IMPORT_NAMED {

            /**
             * <p>Returns the strategy that should be used to recursively search
             * any imported modules.</p>
             *
             * @return The strategy that should be used to recursively search any
             *         imported modules.
             */
            @Override
            public ImportStrategy cascadingStrategy() {
                return IMPORT_NONE;
            }

            /**
             * <p>Returns <code>true</code> <strong>iff</strong> this strategy
             * requires searching directly imported modules.</p>
             *
             * @return <code>true</code> <strong>iff</strong> this strategy
             *         requires searching directly imported modules.
             */
            @Override
            public boolean considerImports() {
                return true;
            }
        },

        /**
         * <p>Indicates that the search should recursively search the closure
         * of all imports and their own imports.</p>
         */
        IMPORT_RECURSIVE {

            /**
             * <p>Returns the strategy that should be used to recursively search
             * any imported modules.</p>
             *
             * @return The strategy that should be used to recursively search any
             *         imported modules.
             */
            @Override
            public ImportStrategy cascadingStrategy() {
                return IMPORT_RECURSIVE;
            }

            /**
             * <p>Returns <code>true</code> <strong>iff</strong> this strategy
             * requires searching directly imported modules.</p>
             *
             * @return <code>true</code> <strong>iff</strong> this strategy
             *         requires searching directly imported modules.
             */
            @Override
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

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final Map<ResolveConceptualElement, FinalizedScope> myScopes =
            new HashMap<>();

    private final Map<ModuleIdentifier, FinalizedModuleScope> myModuleScopes =
            new HashMap<>();

    private final TypeGraph myTypeGraph = null;

    // ===========================================================
    // Constructors
    // ===========================================================


    // ===========================================================
    // Public Methods
    // ===========================================================

    @Override
    public ModuleScope getModuleScope(ModuleIdentifier module)
            throws NoSuchSymbolException {
        return null;
    }

    @Override
    public Scope getScope(ResolveConceptualElement e) {
        return null;
    }

    @Override
    public TypeGraph getTypeGraph() {
        return null;
    }

}
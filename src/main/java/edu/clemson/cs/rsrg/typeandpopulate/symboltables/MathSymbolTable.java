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
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.PrecisModuleDec;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchModuleException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

    /** <p>A map of non-module scopes.</p> */
    private final Map<ResolveConceptualElement, FinalizedScope> myScopes =
            new HashMap<>();

    /** <p>A map of module scopes</p> */
    private final Map<ModuleIdentifier, FinalizedModuleScope> myModuleScopes =
            new HashMap<>();

    /** <p>The current type graph.</p> */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

	MathSymbolTable(TypeGraph g, Map<ResolveConceptualElement, ScopeBuilder> scopes, ScopeBuilder root) throws NoSuchModuleException {
        myTypeGraph = g;

        List<ImportRequest> importedModules = new LinkedList<>();

        seal(root, importedModules);

        for (ImportRequest request : importedModules) {
            if (!myModuleScopes.containsKey(request.importedModule)) {
                throw new NoSuchModuleException(request.sourceModule,
                        request.importedModule);
            }
        }
    }

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
    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }
	
	// ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This creates an {@link ImportRequest} for each imported
     * module.</p>
     *
     * @param source The source module's identifier.
     * @param imports The list of modules imported by <code>source</code>.
     *
     * @return A list of {@link ImportRequest}.
     */
    private static List<ImportRequest> buildImportRequests(ModuleIdentifier source, List<ModuleIdentifier> imports) {
        List<ImportRequest> result = new LinkedList<>();

        for (ModuleIdentifier imported : imports) {
            result.add(new ImportRequest(source, imported));
        }

        return result;
    }

    /**
     * <p>This method makes sure that we do not attempt to import more modules
     * after we invoke the constructor.</p>
     *
     * @param b The current scope repository builder.
     * @param importedModules The list of imported modules.
     */
	private void seal(ScopeBuilder b, List<ImportRequest> importedModules) {
        FinalizedScope result = b.seal(this);
        FinalizedModuleScope resultAsModuleScope;
        ModuleIdentifier resultIdentifier;

        ResolveConceptualElement definingElement = b.getDefiningElement();
        if (definingElement != null) {
            myScopes.put(definingElement, result);

            if (result instanceof FinalizedModuleScope) {
                resultAsModuleScope = (FinalizedModuleScope) result;
                resultIdentifier =
                        new ModuleIdentifier((ModuleDec) b.getDefiningElement());

                myModuleScopes.put(resultIdentifier, resultAsModuleScope);

                importedModules.addAll(buildImportRequests(resultIdentifier,
                        resultAsModuleScope.getImports()));
            }
        }

        for (ScopeBuilder curChild : b.children()) {
            curChild.setParent(result);
            seal(curChild, importedModules);
        }
    }

	// ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>An helper construct that keeps track of both {@link ModuleIdentifier}
     * for the source and imported modules.</p>
     */
	private static class ImportRequest {
		
		// ===========================================================
		// Member Fields
		// ===========================================================

        /** <p>The source module.</p> */
        final ModuleIdentifier sourceModule;

        /** <p>The imported module.</p> */
        final ModuleIdentifier importedModule;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This creates a helper construct that keeps track of imported
         * module for the specified source module.</p>
         *
         * @param source The source module's identifier.
         * @param imported The imported module's identifier.
         */
        ImportRequest(ModuleIdentifier source, ModuleIdentifier imported) {
            sourceModule = source;
            importedModule = imported;
        }

    }

}
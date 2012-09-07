package edu.clemson.cs.r2jt.mathtype;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;

/**
 * <p>A <code>MathSymbolTable</code> represents an immutable mapping from
 * those nodes in the AST that define a scope to {@link Scope Scope} objects
 * representing those scopes and containing the symbols defined therein.</p>
 * 
 * <p><code>Scope</code>s that were introduced at the module-level (e.g.,
 * the scope defined by a 
 * {@link edu.clemson.cs.r2jt.absyn.MathModuleDec MathModuleDec}) will have an
 * associated <code>Scope</code> that is further refined into an instance of
 * {@link ModuleScope ModuleScope}.  As a convenience, such module scopes may
 * be retrieved in a type-safe way with a call to 
 * {@link #getModuleScope(ModuleIdentifier) getModuleScope()}.</p>
 * 
 * <p>Note that there are no public constructors for 
 * <code>MathSymbolTable</code>.  New instances should be acquired from a call 
 * to {@link MathSymbolTableBuilder#seal() MathSymbolTableBuilder.seal()}.</p>
 */
public class MathSymbolTable implements ImportRepository {

    /**
     * <p>Defines the search strategy to be used when searching a scope with
     * respect to any modules that may be imported from that scope's containing
     * module.</p>
     */
    public static enum ImportStrategy {

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
         * <p>Indicates that only directly imported modules should be searched.
         * </p>
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

    private final Map<ResolveConceptualElement, Scope> myScopes =
            new HashMap<ResolveConceptualElement, Scope>();

    private final Map<ModuleIdentifier, ModuleScope> myModuleScopes =
            new HashMap<ModuleIdentifier, ModuleScope>();

    MathSymbolTable(Map<ResolveConceptualElement, ScopeBuilder> scopes,
            ScopeBuilder root) throws NoSuchModuleException {

        List<ImportRequest> importedModules = new LinkedList<ImportRequest>();

        seal(root, importedModules);

        for (ImportRequest request : importedModules) {
            if (!myModuleScopes.containsKey(request.importedModule)) {
                throw new NoSuchModuleException(request.sourceModule,
                        request.importedModule);
            }
        }
    }

    private void seal(ScopeBuilder b, List<ImportRequest> importedModules) {

        Scope result = b.seal(this);
        ModuleScope resultAsModuleScope;
        ModuleIdentifier resultIdentifier;

        ResolveConceptualElement definingElement = b.getDefiningElement();
        if (definingElement != null) {
            myScopes.put(definingElement, result);

            if (result instanceof ModuleScope) {
                resultAsModuleScope = (ModuleScope) result;
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

    private static List<ImportRequest> buildImportRequests(
            ModuleIdentifier source, List<ModuleIdentifier> imports) {

        List<ImportRequest> result = new LinkedList<ImportRequest>();

        for (ModuleIdentifier imported : imports) {
            result.add(new ImportRequest(source, imported));
        }

        return result;
    }

    /**
     * <p>Returns the <code>Scope</code> associated with <code>e</code>.  If
     * there is not associated scope, throws a 
     * {@link NoSuchScopeException NoSuchScopeException}.</p>
     * 
     * @param e The node in the AST for which to retrieve an associated 
     *          <code>Scope</code>.
     *          
     * @return The associated scope.
     * 
     * @throws NoSuchScopeException If there is no <code>Scope</code> associated
     *                              with the given AST node.
     */
    public Scope getScope(ResolveConceptualElement e) {
        if (!myScopes.containsKey(e)) {
            throw new NoSuchScopeException(e);
        }

        return myScopes.get(e);
    }

    /**
     * <p>Returns the <code>ModuleScope</code> associated with 
     * <code>module</code>.  If there is no module by that name, throws a 
     * {@link NoSuchSymbolException NoSuchSymbolException}.</p>
     * 
     * <p>Barring the type of the <code>Exception</code> thrown, for all
     * <code>ModuleDec</code>s, <em>d</em>, if 
     * <code>module.equals(new ModuleIdentifier(d))<code>, then 
     * <code>getModuleScope(module)</code> is equivalent to
     * <code>(ModuleScope) getScope(d)</code>.</p>
     * 
     * @param e The node in the AST for which to retrieve an associated 
     *          <code>Scope</code>.
     *          
     * @return The associated scope.
     * 
     * @throws NoSuchScopeException If there is no <code>Scope</code> associated
     *                              with the given AST node.
     */
    public ModuleScope getModuleScope(ModuleIdentifier module)
            throws NoSuchSymbolException {

        if (!myModuleScopes.containsKey(module)) {
            throw new NoSuchSymbolException("" + module);
        }

        return myModuleScopes.get(module);
    }

    private static class ImportRequest {

        public final ModuleIdentifier sourceModule;
        public final ModuleIdentifier importedModule;

        public ImportRequest(ModuleIdentifier source, ModuleIdentifier imported) {

            sourceModule = source;
            importedModule = imported;
        }
    }
}

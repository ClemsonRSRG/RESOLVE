package edu.clemson.cs.r2jt.mathtype;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.utilities.HardCoded;

/**
 * <p>A <code>MathSymbolTableBuilder</code> is a factory for producing immutable
 * {@link MathSymbolTable MathSymbolTable}s.  It's behavior directly mirrors
 * <code>MathSymbolTable</code>, so that it can be used as a working symbol
 * table while it is built.</p>
 * 
 * <p>Once the building process is complete, {@link #seal() seal()} should
 * be called to return a <code>MathSymbolTable</code> that is equivalent to
 * the working symbol table represented by this 
 * <code>MathSymbolTableBuilder</code>.</p>
 */
public class MathSymbolTableBuilder extends ScopeRepository {

    private static final Scope DUMMY_RESOLVER = new DummyIdentifierResolver();

    private final Deque<ScopeBuilder> myLexicalScopeStack =
            new LinkedList<ScopeBuilder>();

    private final Map<ResolveConceptualElement, ScopeBuilder> myScopes =
            new HashMap<ResolveConceptualElement, ScopeBuilder>();

    private final Map<ModuleIdentifier, ModuleScopeBuilder> myModuleScopes =
            new HashMap<ModuleIdentifier, ModuleScopeBuilder>();

    private ModuleScopeBuilder myCurModuleScope = null;

    private final TypeGraph myTypeGraph;

    /**
     * <p>Creates a new, empty <code>MathSymbolTableBuilder</code> with no
     * open scopes.</p>
     */
    public MathSymbolTableBuilder() {

        myTypeGraph = new TypeGraph();

        //The only things in global scope are built-in things
        ScopeBuilder globalScope =
                new ScopeBuilder(this, myTypeGraph, null, DUMMY_RESOLVER,
                        ModuleIdentifier.GLOBAL);

        HardCoded.addBuiltInSymbols(myTypeGraph, globalScope);

        myLexicalScopeStack.push(globalScope);

        //Some IDEs (rightly) complain about leaking a "this" pointer inside the
        //constructor, but we know what we're doing--this is the last thing in
        //the constructor and thus the object is fully initialized.  The weird 
        //intermediate variable suppresses the warning
        MathSymbolTableBuilder thisObject = this;
        HardCoded.addBuiltInRelationships(myTypeGraph, thisObject);
    }

    @Override
    public TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    /**
     * <p>Opens a new working module scope defined by the given 
     * <code>ModuleDec</code>.</p>
     * 
     * @param definingElement The <code>ModuleDec</code> that defines this 
     *            scope.
     *            
     * @return The newly opened {@link ModuleScopeBuilder}.
     * 
     * @throws IllegalStateException If a module scope is already open.
     * @throws IllegalArgumentException If <code>definingElement</code> is
     *             <code>null</code>.
     */
    public ModuleScopeBuilder startModuleScope(ModuleDec definingElement) {

        if (definingElement == null) {
            throw new IllegalArgumentException("definingElement may not be "
                    + "null.");
        }

        if (myCurModuleScope != null) {
            throw new IllegalStateException("Module scope already open.");
        }

        ScopeBuilder parent = myLexicalScopeStack.peek();

        ModuleScopeBuilder s =
                new ModuleScopeBuilder(myTypeGraph, definingElement, parent,
                        this);

        myCurModuleScope = s;

        addScope(s, parent);
        myModuleScopes.put(s.getModuleIdentifier(), s);

        return s;
    }

    /**
     * <p>Adds an import link from the currently open module scope to some other
     * module.  It is not necessary that a scope for the imported module already
     * exist.</p>
     * 
     * @param module The module to be imported.
     * 
     * @throws IllegalStateException If no module scope is currently open.
     */
    public void addModuleImport(ModuleIdentifier module) {
        checkModuleScopeOpen();
        myCurModuleScope.addImport(module);
    }

    /**
     * <p>Returns the working scope associated with the given module.  Any 
     * module scope that has been opened can be retrieved, even if it has not
     * yet been closed.</p>
     * 
     * @returns The associated working module scope.
     * 
     * @throws NoSuchSymbolException If no working scope has been opened for
     *             the named module.
     */
    @Override
    public ModuleScopeBuilder getModuleScope(ModuleIdentifier module)
            throws NoSuchSymbolException {

        if (!myModuleScopes.containsKey(module)) {
            throw new NoSuchSymbolException("" + module);
        }

        return myModuleScopes.get(module);
    }

    public ScopeBuilder getScope(ResolveConceptualElement e) {
        if (!myScopes.containsKey(e)) {
            throw new NoSuchScopeException(e);
        }

        return myScopes.get(e);
    }

    /**
     * <p>Starts a new working scope to represent the scope defined by
     * <code>definingElement</code>.  It's parent will be the last unclosed
     * working scope (including unclosed working module scopes) and who's
     * root parent is the currently open working module scope.</p>
     * 
     * @param definingElement The AST node that defined this scope.
     * 
     * @return The newly opened working scope.
     * 
     * @throws IllegalArgumentException If <code>definingElement</code> is
     *             <code>null</code>.
     * @throws IllegalStateException If no module scope is currently open.
     */
    public ScopeBuilder startScope(ResolveConceptualElement definingElement) {

        if (definingElement == null) {
            throw new IllegalArgumentException("definingElement may not be "
                    + "null.");
        }

        checkModuleScopeOpen();

        ScopeBuilder parent = myLexicalScopeStack.peek();

        ScopeBuilder s =
                new ScopeBuilder(this, myTypeGraph, definingElement, parent,
                        myCurModuleScope.getModuleIdentifier());

        addScope(s, parent);

        return s;
    }

    private void addScope(ScopeBuilder s, ScopeBuilder parent) {
        parent.addChild(s);
        myLexicalScopeStack.push(s);
        myScopes.put(s.getDefiningElement(), s);
    }

    /**
     * <p>Closes the most recently opened, unclosed working scope, including
     * those opened with <code>startModuleScope()</code>.</p>
     * 
     * @return The new innermost active scope after the former one was closed
     *         by this call.  If the scope that was closed was the module scope,
     *         returns <code>null</code>.
     */
    public ScopeBuilder endScope() {
        checkScopeOpen();
        myLexicalScopeStack.pop();

        ScopeBuilder result;

        if (myLexicalScopeStack.size() == 1) {
            result = null;
            myCurModuleScope = null;
        }
        else {
            result = myLexicalScopeStack.peek();
        }

        return result;
    }

    /**
     * <p>Returns the most recently opened, unclosed working scope.</p>
     * 
     * @return The most recently opened, unclosed working scope.
     * 
     * @throws IllegalStateException If there are no open scopes.
     */
    public ScopeBuilder getInnermostActiveScope() {
        checkScopeOpen();
        return myLexicalScopeStack.peek();
    }

    /**
     * <p>Returns an immutable snapshot of the working symbol table represented
     * by this <code>MathSymbolTableBuilder</code> as a
     * <code>MathSymbolTable</code>.</p>
     * 
     * @return The snapshot.
     * 
     * @throws IllegalStateException If there are any open scopes.
     * @throws NoSuchModuleException If any module claims to import a module
     *             for which there is no associated scope.
     */
    public MathSymbolTable seal() throws NoSuchModuleException {

        if (myLexicalScopeStack.size() > 1) {
            throw new IllegalStateException("There are open scopes.");
        }

        return new MathSymbolTable(myTypeGraph, myScopes, myLexicalScopeStack
                .peek());
    }

    private void checkModuleScopeOpen() {
        if (myCurModuleScope == null) {
            throw new IllegalStateException("No open module scope.");
        }
    }

    private void checkScopeOpen() {
        if (myLexicalScopeStack.size() == 1) {
            throw new IllegalStateException("No open scope.");
        }
    }
}

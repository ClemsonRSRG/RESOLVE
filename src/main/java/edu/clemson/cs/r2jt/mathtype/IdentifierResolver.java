package edu.clemson.cs.r2jt.mathtype;

import java.util.List;

public abstract class IdentifierResolver {

    /**
     * <p>For a given symbol name, <code>name</code>, returns information about
     * the declaration of the innermost introduction of that symbol.  I.e., 
     * if such a symbol were introduced inside this <code>Scope</code>, returns
     * information on that declaration.  If there is no such declaration,
     * recursively searches each parent scope, returning information about the
     * first declaration encountered.</p>
     * 
     * <p>If no declarations of the given symbol are found in the parent
     * hierarchy, this method does not search any modules imported from this
     * <code>Scope</code>'s root <code>ModuleScope</code>'s import.  That is,
     * the behavior of this method is exactly equivalent to:</p>
     * 
     * <pre>
     * getInnermostBinding(name, MathSymbolTable.ImportStrategy.IMPORT_NONE)
     * </pre>
     * 
     * @param name The symbol to find.
     * @param importStrategy The strategy to use when searching imported modules
     *            if no declaration can be found in this scope.
     * 
     * @return Information about the innermost declaration of a symbol with
     *         the given name.
     * 
     * @throws NoSuchSymbolException If there is no declaration for the given
     *             symbol in this scope or any of the imports as permitted by 
     *             the given <code>ImportStrategy</code>.
     */
    public final MathSymbolTableEntry getInnermostBinding(String name)
            throws NoSuchSymbolException {

        MathSymbolTableEntry result;

        try {
            result =
                    getInnermostBinding(name,
                            MathSymbolTable.ImportStrategy.IMPORT_NONE);
        }
        catch (DuplicateSymbolException dse) {
            //Not possible with strategy IMPORT_NONE
            throw new RuntimeException();
        }

        return result;
    }

    /**
     * <p>For a given symbol name, <code>name</code>, returns information about
     * the declaration of the innermost introduction of that symbol.  I.e., 
     * if such a symbol were introduced inside this <code>Scope</code>, returns
     * information on that declaration.  If there is no such declaration,
     * recursively searches each parent scope, returning information about the
     * first declaration encountered.</p>
     * 
     * <p>If no declarations of the given symbol are found in the parent
     * hierarchy, this method will search the root <code>ModuleScope</code>'s
     * imported modules as defined by the given 
     * {@link MathSymbolTable.ImportStrategy ImportStrategy}.</p>
     * 
     * @param name The symbol to find.
     * @param importStrategy The strategy to use when searching imported modules
     *            if no declaration can be found in this scope.
     * 
     * @return Information about the innermost declaration of a symbol with
     *         the given name.
     * 
     * @throws NoSuchSymbolException If there is no declaration for the given
     *             symbol in this scope or any of the imports as permitted by 
     *             the given <code>ImportStrategy</code>.
     * @throws DuplicateSymbolException If the given symbol appears in two or
     *             more imports as permitted by the given
     *             <code>ImportStrategy</code>.
     */
    public abstract MathSymbolTableEntry getInnermostBinding(String name,
            MathSymbolTable.ImportStrategy importStrategy)
            throws NoSuchSymbolException,
                DuplicateSymbolException;

    /**
     * <p>For a given symbol name, <code>name</code>, returns information about
     * all declarations with that name in scope, in order from innermost
     * (i.e., declarations directly inside this <code>Scope</code>) to 
     * outermost, (i.e., declarations inside parent <code>Scope</code>s).</p>
     * 
     * <p>If no matching declarations are found, either in the parent hierarchy
     * or permitted imported modules, returns an empty <code>List</code>.</p>
     * 
     * <p>The behavior of this method is exactly equivalent to:</p>
     * 
     * <pre>
     * getAllBindings(name, MathSymbolTable.ImportStrategy.IMPORT_NONE)
     * </pre>
     * 
     * @param name The symbol to find.
     * @param importStrategy The strategy to use when searching imported 
     *            modules.
     * 
     * @return A list of information about each declaration found.
     */
    public final List<MathSymbolTableEntry> getAllBindings(String name) {
        return getAllBindings(name, MathSymbolTable.ImportStrategy.IMPORT_NONE);
    }

    /**
     * <p>For a given symbol name, <code>name</code>, returns information about
     * all declarations with that name in scope, in order from innermost
     * (i.e., declarations directly inside this <code>Scope</code>) to 
     * outermost, (i.e., declarations inside parent <code>Scope</code>s).</p>
     * 
     * <p>After searching the full hierarchy of parent scopes, this method will
     * search the root <code>ModuleScope</code>'s imported modules as defined by
     * the given {@link MathSymbolTable.ImportStrategy ImportStrategy}.  Any
     * matching declarations will be added at the end of the list.  The order in
     * which the imported modules will be searched is undefined.</p>
     * 
     * <p>If no matching declarations are found, either in the parent hierarchy
     * or permitted imported modules, returns an empty <code>List</code>.</p>
     * 
     * @param name The symbol to find.
     * @param importStrategy The strategy to use when searching imported 
     *            modules.
     * 
     * @return A list of information about each declaration found.
     */
    public abstract List<MathSymbolTableEntry> getAllBindings(String name,
            MathSymbolTable.ImportStrategy importStrategy);

    /**
     * <p>For a given symbol name, <code>name</code>, populates a 
     * <code>List</code> of <code>MathSymbolTableEntry</code> with information
     * about all declarations with that name in scope, in order from innermost
     * (i.e., declarations directly inside this <code>Scope</code>) to 
     * outermost, (i.e., declarations inside parent <code>Scope</code>s).  If
     * there are already elements in the given <code>List</code>, the results
     * of the search will be appended to the end of them.</p>
     * 
     * <p>If no matching declarations are found, either in the parent hierarchy
     * or permitted imported modules, the provided <code>List</code> will
     * remain unmodified.</p>
     * 
     * <p>The behavior of this method is exactly equivalent to:</p>
     * 
     * <pre>
     * buildAllBindings(name, accumulator, 
     *                  MathSymbolTable.ImportStrategy.IMPORT_NONE);
     * </pre>
     * 
     * @param name The symbol to find.
     * @param accumulator The list to populate.
     * @param importStrategy The strategy to use when searching imported 
     *            modules.
     */
    public final void buildAllBindingsList(String name,
            List<MathSymbolTableEntry> accumulator) {

        buildAllBindingsList(name, accumulator,
                MathSymbolTable.ImportStrategy.IMPORT_NONE);
    }

    /**
     * <p>For a given symbol name, <code>name</code>, populates a 
     * <code>List</code> of <code>MathSymbolTableEntry</code> with information
     * about all declarations with that name in scope, in order from innermost
     * (i.e., declarations directly inside this <code>Scope</code>) to 
     * outermost, (i.e., declarations inside parent <code>Scope</code>s).  If
     * there are already elements in the given <code>List</code>, the results
     * of the search will be appended to the end of them.</p>
     * 
     * <p>After searching the full hierarchy of parent scopes, this method will
     * search the root <code>ModuleScope</code>'s imported modules as defined by
     * the given {@link MathSymbolTable.ImportStrategy ImportStrategy}.  Any
     * matching declarations will be added at the end of the list.  The order in
     * which the imported modules will be searched is undefined.</p>
     * 
     * <p>If no matching declarations are found, either in the parent hierarchy
     * or permitted imported modules, the provided <code>List</code> will
     * remain unmodified.</p>
     * 
     * @param name The symbol to find.
     * @param accumulator The list to populate.
     * @param importStrategy The strategy to use when searching imported 
     *            modules.
     */
    public abstract void buildAllBindingsList(String name,
            List<MathSymbolTableEntry> accumulator,
            MathSymbolTable.ImportStrategy importStrategy);
}

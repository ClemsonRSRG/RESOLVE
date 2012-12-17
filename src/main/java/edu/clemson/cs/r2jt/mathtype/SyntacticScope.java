package edu.clemson.cs.r2jt.mathtype;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;

/**
 * <p>A <code>SyntacticScope</code> corresponds to a RESOLVE scope that
 * arises because of a concrete piece of source code (rather than, for example,
 * the built-in global scope, or the top-level dummy scope), represented by
 * a {@link edu.clemson.cs.r2jt.absyn.ResolveConceptualElement 
 *     ResolveConceptualElement} called its <em>defining element</em>.  Such a
 * scope therefore exists in a <em>lexical hierarchy</em> of those scopes
 * introduced by its defining element's parent elements, necessarily passing
 * through a {@link ModuleScope ModuleScope} (belonging to this scope's 
 * <em>source module</em>) before reaching the top-level dummy scope.  If this
 * scope corresponds to a module, it is its own source module.</p>
 *     
 * <p>The symbols in this scope's lexical parents are therefore implicitly 
 * available from within this scope.  Optionally, symbols from the transitive
 * closure of all modules imported by this scope's source module (its 
 * <em>recursive imports</em>) are available, as are any symbols found in 
 * facilities defined in the source module or any modules directly imported by
 * the source module (its <em>named imports</em>, which do not recursively 
 * include any modules imported from within named imports).</p>
 * 
 * <p>Note that this class has no public constructor.  Instances of this class
 * can be retrieved from a {@link ScopeRepository SymbolTable} or constructed via
 * some of the methods of {@link MathSymbolTableBuilder MathSymbolTableBuilder}.
 * </p>
 */
public abstract class SyntacticScope extends AbstractScope {

    protected final ResolveConceptualElement myDefiningElement;
    protected Scope myParent;
    protected final ModuleIdentifier myRootModule;
    protected final BaseSymbolTable myBindings;
    private final ScopeRepository mySource;

    /*package private*/SyntacticScope(ScopeRepository source,
            ResolveConceptualElement definingElement, Scope parent,
            ModuleIdentifier enclosingModule, BaseSymbolTable bindings) {

        mySource = source;
        myDefiningElement = definingElement;
        myParent = parent;
        myRootModule = enclosingModule;
        myBindings = bindings;
    }

    @Override
    public <E extends SymbolTableEntry> List<E> query(
            MultimatchSymbolQuery<E> query) {

        return query.searchFromContext(this, mySource);
    }

    @Override
    public <E extends SymbolTableEntry> E queryForOne(SymbolQuery<E> query)
            throws NoSuchSymbolException,
                DuplicateSymbolException {

        List<E> results = query.searchFromContext(this, mySource);

        if (results.isEmpty()) {
            throw new NoSuchSymbolException();
        }
        else if (results.size() > 1) {
            throw new DuplicateSymbolException();
        }

        return results.get(0);
    }

    /**
     * <p>Returns this scopes defining element.</p>
     * 
     * @return The defining element.
     */
    public ResolveConceptualElement getDefiningElement() {
        return myDefiningElement;
    }

    /*package private*/final Scope getParent() {
        return myParent;
    }

    public ModuleIdentifier getRootModule() {
        return myRootModule;
    }

    public ScopeRepository getSourceRepository() {
        return mySource;
    }

    @Override
    public <E extends SymbolTableEntry> boolean addMatches(
            TableSearcher<E> searcher, List<E> matches,
            Set<Scope> searchedScopes,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility)
            throws DuplicateSymbolException {

        boolean finished = false;

        if (!searchedScopes.contains(this)) {
            searchedScopes.add(this);

            SymbolTable symbolTableView = myBindings;

            if (instantiatingFacility != null) {
                symbolTableView =
                        new InstantiatedSymbolTable(myBindings,
                                genericInstantiations, instantiatingFacility);
            }

            finished = searcher.addMatches(symbolTableView, matches);

            if (!finished) {
                finished =
                        myParent.addMatches(searcher, matches, searchedScopes,
                                genericInstantiations, instantiatingFacility);
            }
        }

        return finished;
    }

    @Override
    public List<ProgramParameterEntry> getFormalParameterEntries() {
        List<ProgramParameterEntry> result =
                new LinkedList<ProgramParameterEntry>();

        Iterator<ProgramParameterEntry> formalBindings =
                myBindings.iterateByType(ProgramParameterEntry.class);

        while (formalBindings.hasNext()) {
            result.add(formalBindings.next());
        }

        return result;
    }
}

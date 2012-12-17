package edu.clemson.cs.r2jt.mathtype;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>A <code>Scope</code> represents a mapping from symbol names to symbol
 * table entries.  Each entry inherits from 
 * {@link SymbolTableEntry SymbolTableEntry}, but differing concrete subclasses
 * represent different kinds of entries.</p>
 * 
 * <p>This interface defines no mutator methods, but specific concrete 
 * subclasses may be mutable.</p>
 * 
 * <p>A scope may possibly exist in a context in which more symbols are 
 * available than just those introduced directly inside the scope.  For example,
 * a scope may be a child scope of another, or may exist within a RESOLVE module
 * that imports other modules (and, thus, their contained symbols).  The methods
 * of <code>Scope</code> provide options for searching these possible additional
 * available scopes in different ways.</p>
 */
public interface Scope {

    /**
     * <p>Searches for symbols by the given query, using this <code>Scope</code>
     * as the source scope of the search, i.e. the scope that is the context
     * from which the search was triggered.</p>
     * 
     * @param query The query to use.
     * 
     * @return A list of all symbols matching the given query.
     */
    public <E extends SymbolTableEntry> List<E> query(
            MultimatchSymbolQuery<E> query);

    /**
     * <p>Searches for a symbol using the given query, using this 
     * <code>Scope</code> as the source scope of the search, i.e. the scope
     * that is the context from which the search was triggered.  This method
     * works much like {@link #query(BaseMultimatchSymbolQuery) query()} except that
     * it expects exactly one match, as determined by the given query.  If the
     * given query matches more than one entry, or itself throws a 
     * {@link DuplicateSymbolException DuplicateSymbolException}, then this
     * method will throw a <code>DuplicateSymbolException</code>.</p>
     * 
     * @param query The query to use.
     * 
     * @return The single symbol that is matched.
     * 
     * @throws NoSuchSymbolException If the query matches no symbol table 
     * 		       entries.
     * @throws DuplicateSymbolException If the query matches more than one
     * 			   symbol table entry.
     */
    public <E extends SymbolTableEntry> E queryForOne(SymbolQuery<E> query)
            throws NoSuchSymbolException,
                DuplicateSymbolException;

    /**
     * <p>Begins applying the given {@link TableSearcher TableSearcher} up the 
     * lexical hierarchy that ends in this scope, starting with this scope, thus
     * adding any matches to <code>matches</code>.  For the purposes of the
     * search and the returned results, any generics will be instantiated 
     * appropriately according to <code>genericInstantiations</code>, but this 
     * scope will not be permanently modified.</p>
     * 
     * <p>The search will continue upward toward the top-level global scope 
     * until one of the following happens:</p>
     * 
     * <ul>
     * 		<li>The top-level scope is searched.</li>
     * 		<li>A scope is reached that is already in 
     * 		    <code>searchedScopes</code>.</li>
     * 		<li><code>searcher</code>'s {@link 
     * 		    TableSearcher#addMatches(SymbolTable, List) addMatches}() method
     * 		    returns <code>true</code>, indicating the list is complete.</li>
     * 		<li>The <code>addMatches()</code> method throws a
     * 		    {@link DuplicateSymbolException DuplicateSymbolException}.</li>
     * </ul>
     * 
     * <p>In the first three cases, the method returns normally, with any 
     * matches added to <code>matches</code>.  If no matches are found, 
     * <code>matches</code> will simply be left unchanged.</p>
     * 
     * <p>In the last case, this method will throw a 
     * <code>DuplicateSymbolException</code>.</p>
     * 
     * <p>Regardless of how this method terminates, any searched scopes will be
     * added to <code>searchedScopes</code>.</p>
     * 
     * @param searcher The searcher to be used to match symbol table entries.
     * @param matches A non-<code>null</code> accumulator of matches.
     * @param searchedScopes A set of already-searched scopes.
     * @param genericInstantiations A mapping from generic names to instantiated
     *            types, which will be applied when searching and returning
     *            results.
     * @param instantiatingFacility A pointer to the symbol table entry 
     * 			  corresponding to the facility that provided the instantiation
     * 			  of the generics given by <code>genericInstantiations</code>, 
     *            or <code>null</code> if we are not searching an instantiated 
     *            scope.  If this parameter is <code>null</code> then 
     *            <code>genericInstantiations.isEmpty() == true</code>, but the
     *            reverse is not necessarily the case.
     *            
     * @return <code>true</code> if <code>matches</code> now represents a
     *         final list of search results&mdash;i.e., no further scopes
     *         should be considered.  <code>false</code> indicates that
     *         the search should continue, provided there are additional
     *         un-searched scopes.
     */
    public <E extends SymbolTableEntry> boolean addMatches(
            TableSearcher<E> searcher, List<E> matches,
            Set<Scope> searchedScopes,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility)
            throws DuplicateSymbolException;

    /**
     * <p>A simple variation on {@link #addMatches addMatches}() that creates
     * and returns a new list rather than requiring an accumulator and starts
     * with an empty set of searched scopes and instantiations.</p>
     * 
     * @param searcher
     * @return
     */
    public <E extends SymbolTableEntry> List<E> getMatches(
            TableSearcher<E> searcher) throws DuplicateSymbolException;

    /**
     * <p>Returns a list of {@link ProgramParameterEntry ProgramParameterEntry}s
     * contained directly in this scope.  These correspond to the formal 
     * parameters defined by the syntactic element that introduced the scope.
     * </p>
     * 
     * <p>If there are no parameters, or the syntactic element is not of the 
     * sort that can define parameters, returns an empty list.</p>
     * 
     * @return Entries for the parameters of the current scope.
     */
    public List<ProgramParameterEntry> getFormalParameterEntries();
}

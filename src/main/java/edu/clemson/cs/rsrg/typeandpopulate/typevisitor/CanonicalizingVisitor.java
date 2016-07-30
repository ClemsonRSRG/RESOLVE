/**
 * CanonicalizingVisitor.java
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
package edu.clemson.cs.rsrg.typeandpopulate.typevisitor;

import edu.clemson.cs.rsrg.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTBigUnion;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTNamed;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships.EqualsPredicate;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships.IsInPredicate;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships.TypeRelationshipPredicate;
import java.util.*;

/**
 * <p>This class visits the named types to see if any of the
 * given names needs to be replaced with their canonical names.</p>
 *
 * @version 2.0
 */
public class CanonicalizingVisitor extends MutatingVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The number of quantified variables we have encountered.</p> */
    private int myQuantifiedVariableCount = 1;

    /** <p>A map that stores canonical names and their original names.</p> */
    private final Map<String, String> myCanonicalToEnvironmentOriginal = new HashMap<>();

    /** <p>A list of type relationships.</p> */
    private final List<TypeRelationshipPredicate> myPredicates = new LinkedList<>();

    /** <p>This stores the root mathematical type.</p> */
    private MTType myRoot;

    /** <p>A flag that indicates that we have used this visitor instance.</p> */
    private boolean mySpentFlag = false;

    /** <p>A suffix string for the predicate.</p> */
    private final String myPredicateSuffix;

    /** <p>The current type graph object in use.</p> */
    private final TypeGraph myTypeGraph;

    /** <p>The searching scope.</p> */
    private final Scope myEnvironment;

    /** <p>Annotations for each of the entries.</p> */
    private final Map<SymbolTableEntry, Map<Object, Object>> myEnvironmentAnnotations = new HashMap<>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a visitor used to replace all names to
     * their canonical forms.</p>
     *
     * @param g The current type graph.
     * @param environment The searching scope.
     * @param predicateSuffix A suffix string for the predicate.
     */
    public CanonicalizingVisitor(TypeGraph g, Scope environment, String predicateSuffix) {
        myTypeGraph = g;
        myPredicateSuffix = "_" + predicateSuffix;
        myEnvironment = environment;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTNamed} by replacing all canonical types in
     * <code>t</code>.</p>
     *
     * @param t A math type.
     */
    @Override
    public final void beginMTNamed(MTNamed t) {
        String canonicalName = "c" + myQuantifiedVariableCount;

        MTType originalBinding = null;
        try {
            originalBinding = getInnermostBinding(t.getName());
        }
        catch (NoSuchElementException e) {
            //The variable is unbound.  Must be in the environment
            try {
                //We cast rather than call toMathSymbolEntry() because this
                //would represent an error in the compiler code rather than the
                //RESOLVE source: you shouldn't be able to canonicalize anything
                //containing non-math-symbol pieces
                MathSymbolEntry entry =
                        (MathSymbolEntry) myEnvironment
                                .queryForOne(new UnqualifiedNameQuery(t.getName()));

                if (entry.getQuantification().equals(
                        SymbolTableEntry.Quantification.UNIVERSAL)) {
                    originalBinding = entry.getType();
                    myCanonicalToEnvironmentOriginal.put(canonicalName
                            + myPredicateSuffix, t.getName());
                }
            }
            catch (NoSuchSymbolException | DuplicateSymbolException nsse) {
                //Shouldn't be possible--we'd've noticed it before now
                throw new RuntimeException(nsse);
            }
        }

        //if originalBinding is null, then we name some
        //non-universally-quantified thing, so we do nothing
        if (originalBinding != null) {
            //Construct a canonical variable to represent this thing we've just
            //encountered
            MTNamed canonicalType = new MTNamed(myTypeGraph, canonicalName);
            MTNamed canonicalTypeWithSuffix =
                    new MTNamed(myTypeGraph, canonicalName + myPredicateSuffix);
            myQuantifiedVariableCount++;

            //We're going to weaken it's declared type all the way to MType, so
            //make a note of its original declared type
            myPredicates.add(new IsInPredicate(myTypeGraph,
                    canonicalTypeWithSuffix, originalBinding));

            //If we've already encountered this particular variable, we're going
            //to remove that information as we canonicalize, so we make a note
            //that this new canonical variable should equal the last one
            MTNamed lastReplace =
                    (MTNamed) getInnermostBindingAnnotation(t.getName(),
                            "LastReplace");

            if (lastReplace != null) {
                myPredicates.add(new EqualsPredicate(myTypeGraph, lastReplace,
                        canonicalTypeWithSuffix));
            }
            annotateInnermostBinding(t.getName(), "LastReplace",
                    canonicalTypeWithSuffix);

            //Replace "t" with "canonical" in the final expression
            replaceWith(canonicalType);
        }
    }

    /**
     * <p>This method returns the list of type relationships
     * that we have encountered/formed.</p>
     *
     * @return A list of {@link TypeRelationshipPredicate}.
     */
    public final List<TypeRelationshipPredicate> getTypePredicates() {
        return Collections.unmodifiableList(myPredicates);
    }

    /**
     * <p>This method returns a map of canonical names to their
     * original names.</p>
     *
     * @return A map of name mappings.
     */
    public final Map<String, String> getCanonicalToEnvironmentOriginalMapping() {
        return Collections.unmodifiableMap(myCanonicalToEnvironmentOriginal);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>This method annotates the inner most binding for the
     * given variable name.</p>
     *
     * @param name A variable name.
     * @param key The binding key.
     * @param value The binding value.
     */
    @Override
    protected final void annotateInnermostBinding(String name, Object key, Object value) {
        try {
            super.annotateInnermostBinding(name, key, value);
        }
        catch (NoSuchElementException e) {
            try {
                SymbolTableEntry entry =
                        myEnvironment
                                .queryForOne(new UnqualifiedNameQuery(name));

                Map<Object, Object> annotations =
                        myEnvironmentAnnotations.get(entry);
                if (annotations == null) {
                    annotations = new HashMap<>();
                    myEnvironmentAnnotations.put(entry, annotations);
                }
                annotations.put(key, value);
            }
            catch (NoSuchSymbolException | DuplicateSymbolException nsse) {
                //Shouldn't be possible--we'd've noticed it before now
                throw new NoSuchElementException(name);
            }
        }
    }

    /**
     * <p>This method adds additional logic to bound
     * {@code t} after we visit it.</p>
     *
     * @param t A math type.
     */
    @Override
    protected final void boundEndMTBigUnion(MTBigUnion t) {
        //We better have encountered each quantified type explicitly
        for (String var : t.getQuantifiedVariables().keySet()) {
            Object lastReplace =
                    getInnermostBindingAnnotation(var, "LastReplace");

            if (lastReplace == null) {
                //TODO : Ideally this exception would be semantically connected
                //       to the location in the RESOLVE source code that caused
                //       the error so we could give nice error output
                throw new IllegalArgumentException("Universal type variable "
                        + "\"" + var + "\" is not concretely bound.");
            }
        }

        //We're gonna get rid of all intermediate big unions and just have one
        //big one at the top
        replaceWith(((MTBigUnion) getTransformedVersion()).getExpression());
    }

    /**
     * <p>This method returns the inner most binding object for the
     * given variable name and key.</p>
     *
     * @param name A variable name.
     * @param key The binding key.
     *
     * @return The object bound by the key for this variable.
     */
    @Override
    protected final Object getInnermostBindingAnnotation(String name, Object key) {
        Object result;

        try {
            result = super.getInnermostBindingAnnotation(name, key);
        }
        catch (NoSuchElementException e) {
            try {
                SymbolTableEntry entry =
                        myEnvironment
                                .queryForOne(new UnqualifiedNameQuery(name));

                Map<Object, Object> annotations =
                        myEnvironmentAnnotations.get(entry);
                if (annotations == null) {
                    result = null;
                }
                else {
                    result = annotations.get(key);
                }
            }
            catch (NoSuchSymbolException | DuplicateSymbolException nsse) {
                //Shouldn't be possible--we'd've noticed it before now
                throw new NoSuchElementException(name);
            }
        }

        return result;
    }

    /**
     * <p>This method adds additional logic to mutate
     * {@code t} before we visit it.</p>
     *
     * @param t A math type.
     */
    @Override
    protected final void mutateBeginMTType(MTType t) {
        if (mySpentFlag) {
            throw new IllegalStateException("Cannot reuse a " + this.getClass()
                    + ".  Make a new one.");
        }

        if (myRoot == null) {
            myRoot = t;
        }
    }

    /**
     * <p>This method adds additional logic to mutate
     * a {@code t} after we visit it.</p>
     *
     * @param t A math type.
     */
    @Override
    protected final void mutateEndMTType(MTType t) {
        if (atRoot()) {
            //Note at this point that myFinalExpression has no big unions and
            //unique quantified variable names

            Map<String, MTType> quantifiedVariables = new HashMap<>();

            for (int i = 1; i < myQuantifiedVariableCount; i++) {
                quantifiedVariables.put("c" + i, myTypeGraph.CLS);
            }

            if (quantifiedVariables.isEmpty()) {
                quantifiedVariables.put("", myTypeGraph.CLS);
            }

            myFinalExpression =
                    new MTBigUnion(myTypeGraph, quantifiedVariables,
                            myFinalExpression);

            myRoot = null;

            mySpentFlag = true;
        }
    }

}
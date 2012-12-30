/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author hamptos
 */
public class BindingsIterator implements Iterator<Map<PExp, PExp>> {

    //The basic strategy here is that each binding iterator really only takes
    //care of binding the first pattern, then defers to another BindingsIterator
    //to inductively handle all the remaining patterns

    //These variables are for setting up sub-iterators
    private final Iterable<PExp> myFacts;
    private final List<PExp> myPatterns;
    private final int myPatternsSize;
    private Iterator<Map<PExp, PExp>> myCurrentSubIterator;

    //These variables represent the work I'm doing at this level
    private Iterator<PExp> myFactsIterator;
    private PExp myPattern;
    private Map<PExp, PExp> myLocalBindings;
    private Map<PExp, PExp> myExistingBindings;

    private Map<PExp, PExp> myNextReturn;

    private int myCurrentPatternIndex;

    /**
     * @param knownFacts
     * @param patterns Only needs to implement .get(), which should support 
     *              constant-time access, and size().
     * @param startingPattern
     * @param existingBindings 
     */
    public BindingsIterator(Iterable<PExp> knownFacts, List<PExp> patterns,
            int startingPattern, Map<PExp, PExp> existingBindings) {

        myFacts = knownFacts;

        myPatterns = patterns;
        myPatternsSize = myPatterns.size();

        if (myPatternsSize == 0) {
            myNextReturn = existingBindings;
        }
        else {
            myCurrentPatternIndex = startingPattern;

            myFactsIterator = myFacts.iterator();
            myPattern = patterns.get(myCurrentPatternIndex);

            myExistingBindings = existingBindings;

            setUpNext();
        }
    }

    public BindingsIterator(Iterable<PExp> knownFacts, List<PExp> patterns) {
        this(knownFacts, patterns, 0, new HashMap<PExp, PExp>());
    }

    private void setUpNext() {
        myNextReturn = null;

        Map<PExp, PExp> bindings = null;

        //Prime myCurrentSubIterator as necessary
        if (myCurrentSubIterator == null || !myCurrentSubIterator.hasNext()) {
            PExp fact;
            while (myFactsIterator.hasNext()
                    && (myCurrentSubIterator == null || !myCurrentSubIterator
                            .hasNext())) {

                fact = myFactsIterator.next();

                try {
                    myLocalBindings =
                            myPattern.substitute(myExistingBindings).bindTo(
                                    fact);

                    bindings = new HashMap<PExp, PExp>(myLocalBindings);
                    bindings.putAll(myExistingBindings);

                    if (myCurrentPatternIndex < myPatternsSize - 1) {
                        myCurrentSubIterator =
                                new BindingsIterator(myFacts, myPatterns,
                                        myCurrentPatternIndex + 1, bindings);
                    }
                }
                catch (BindingException be) {
                    //This pattern doesn't bind to this fact, we'll move on to
                    //the next one.
                }
            }

            if (bindings != null
                    && (myCurrentSubIterator == null || myCurrentSubIterator
                            .hasNext())) {

                if (myCurrentSubIterator == null) {
                    myNextReturn = new HashMap<PExp, PExp>(myExistingBindings);
                    myNextReturn.putAll(myLocalBindings);
                }
                else {
                    myNextReturn = myCurrentSubIterator.next();
                }
            }
            else {
                myNextReturn = null;
            }
        }
        else {
            myNextReturn = myCurrentSubIterator.next();
        }
    }

    @Override
    public boolean hasNext() {
        return (myNextReturn != null);
    }

    @Override
    public Map<PExp, PExp> next() {
        if (myNextReturn == null) {
            throw new NoSuchElementException();
        }

        Map<PExp, PExp> result = myNextReturn;

        if (myPatternsSize == 0) {
            myNextReturn = null;
        }
        else {
            setUpNext();
        }

        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}

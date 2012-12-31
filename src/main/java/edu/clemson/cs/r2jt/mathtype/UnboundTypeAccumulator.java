/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.mathtype;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class UnboundTypeAccumulator extends BoundVariableVisitor {

    private final Set<String> myUnboundTypeNames = new HashSet<String>();
    private final Scope myEnvironment;

    public UnboundTypeAccumulator(Scope environment) {
        myEnvironment = environment;
    }

    public Set<String> getFinalUnboundNamedTypes() {
        return new HashSet<String>(myUnboundTypeNames);
    }

    @Override
    public void beginMTNamed(MTNamed namedType) {

        boolean universal;
        try {
            getInnermostBinding(namedType.name);
            universal = true;
        }
        catch (NoSuchElementException e) {

            try {
                //We cast rather than call toMathSymbolEntry() because this 
                //would represent an error in the compiler code rather than the
                //RESOLVE source: we're looking at math things here only
                MathSymbolEntry entry =
                        (MathSymbolEntry) myEnvironment
                                .queryForOne(new UnqualifiedNameQuery(
                                        namedType.name));
                universal =
                        entry.getQuantification().equals(
                                MathSymbolEntry.Quantification.UNIVERSAL);
            }
            catch (NoSuchSymbolException nsse) {
                //Shouldn't be possible--we'd have dealt with it by now
                throw new RuntimeException(nsse);
            }
            catch (DuplicateSymbolException dse) {
                //Shouldn't be possible--we'd have dealt with it by now
                throw new RuntimeException(dse);
            }
        }

        if (universal) {
            myUnboundTypeNames.add(namedType.name);
        }
    }
}

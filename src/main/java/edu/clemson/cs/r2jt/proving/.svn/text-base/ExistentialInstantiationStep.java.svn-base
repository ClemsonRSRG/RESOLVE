package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.Map;

import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>An <code>ExistentialInstantiationStep</code> attempts to bind known facts
 * against existentially quantified variables in the conjunct of provided VCs.
 * </p>
 */
public class ExistentialInstantiationStep implements VCTransformer {

    private static final Iterator<VC> DUMMY_ITERATOR =
            DummyIterator.getInstance((Iterator<VC>) null);
    private final Iterable<PExp> myGlobalFacts;

    public ExistentialInstantiationStep(Iterable<PExp> globalFact) {

        myGlobalFacts = globalFact;
    }

    @Override
    public Iterator<VC> transform(VC original) {
        Iterator<VC> soFar = DummyIterator.getInstance(DUMMY_ITERATOR);

        Antecedent originalAntecedent = original.getAntecedent();
        Consequent originalConsequent = original.getConsequent();

        int consequentIndex = 0;
        for (PExp e : original.getConsequent()) {

            if (e.containsExistential()) {
                soFar = new ChainingIterator<VC>(soFar,
                        new StaticAntecedentIterator(original.getSourceName(),
                            originalAntecedent,
                            new SingleExistentialInstantiator(e,
                                originalAntecedent,
                                originalConsequent.removed(
                                    consequentIndex))));
            }

            consequentIndex++;
        }

        return soFar;
    }

    @Override
    public Antecedent getPattern() {
        throw new UnsupportedOperationException("Not applicable.");
    }

    @Override
    public Consequent getReplacementTemplate() {
        throw new UnsupportedOperationException("Not applicable.");
    }

    private class SingleExistentialInstantiator
            implements Iterator<Consequent> {

        private final PExp myExistential;
        private final Consequent myOriginal;
        private final Iterator<PExp> myFactIterator;
        private Consequent myNextConsequent;

        public SingleExistentialInstantiator(PExp existentialExpression,
                Antecedent vcAntecedent, Consequent remainingConsequent) {
            myExistential = existentialExpression;
            myOriginal = remainingConsequent;
            myFactIterator = new ChainingIterator<PExp>(vcAntecedent.iterator(),
                    myGlobalFacts.iterator());

            setUpNext();
        }

        private void setUpNext() {

            PExp curFact;
            Map<PExp, PExp> binding = null;
            while (myFactIterator.hasNext() && binding == null) {
                curFact = myFactIterator.next();

                try {
                    binding = myExistential.bindTo(curFact);
                } catch (BindingException e) {
                }
            }

            if (binding != null) {
                myNextConsequent = myOriginal.substitute(binding);
            }
            else {
                myNextConsequent = null;
            }
        }

        @Override
        public boolean hasNext() {
            return myNextConsequent != null;
        }

        @Override
        public Consequent next() {
            Consequent retval = myNextConsequent;

            setUpNext();

            return retval;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        return "Bind Existential";
    }

	@Override
	public boolean introducesQuantifiedVariables() {
		return false;
	}
}

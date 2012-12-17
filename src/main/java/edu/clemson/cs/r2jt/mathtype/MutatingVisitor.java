package edu.clemson.cs.r2jt.mathtype;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * TODO : MutatingVisitor tends to crash if you use it for multiple sequential
 * traversals.  Always creating a new instance of the visitor is a workaround
 * for now.
 */
public class MutatingVisitor extends BoundVariableVisitor {

    private LinkedList<Integer> myIndices = new LinkedList<Integer>();
    private MTType myRoot;
    private LinkedList<Map<Integer, MTType>> myChangesAtLevel =
            new LinkedList<Map<Integer, MTType>>();

    private MTType myClosingType;

    protected MTType myFinalExpression;

    public MTType getFinalExpression() {
        return myFinalExpression;
    }

    @Override
    public final void beginMTType(MTType t) {

        if (myRoot == null) {
            myRoot = t;
            myFinalExpression = myRoot;
        }

        myIndices.push(0); //We start at the zeroth child
        myChangesAtLevel.push(new HashMap<Integer, MTType>());

        mutateBeginMTType(t);
    }

    protected boolean atRoot() {
        return (myIndices.size() == 1);
    }

    public void mutateBeginMTType(MTType t) {}

    public void mutateEndMTType(MTType t) {}

    public void replaceWith(MTType replacement) {
        if (myIndices.size() == 1) {
            //We're the root
            myFinalExpression = replacement;
        }
        else {
            myChangesAtLevel.get(1).put(myIndices.get(1), replacement);
        }
    }

    protected final MTType getTransformedVersion() {
        return myClosingType;
    }

    @Override
    public final void endChildren(MTType t) {
        myClosingType = t;

        Map<Integer, MTType> changes = myChangesAtLevel.peek();
        if (!changes.isEmpty()) {
            myClosingType = t.withComponentsReplaced(changes);
            replaceWith(myClosingType);
        }

        mutateEndChildren(t);
    }

    public void mutateEndChildren(MTType t) {}

    @Override
    public final void endMTType(MTType t) {
        mutateEndMTType(t);

        //We're not visiting any more children at this level (because the
        //level just ended!)
        myIndices.pop();
        myChangesAtLevel.pop();

        //If I'm the root, there's no chance I have any siblings
        if (t != myRoot) {
            //Increment to the next potential child index
            int i = myIndices.pop();

            myIndices.push(i + 1);
        }
    }
}

package edu.clemson.cs.r2jt.mathtype;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.IterativeExp;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>A constructed type consisting of the union over one or more quantified
 * types.  For example U{t, r : MType}{t intersect r} is the type of all 
 * intersections.</p>
 */
public class MTBigUnion extends MTAbstract<MTBigUnion> {

    private static final int BASE_HASH = "MTBigUnion".hashCode();

    private final TreeMap<String, MTType> myQuantifiedVariables;
    private final MTType myExpression;

    private final Map<Integer, String> myComponentIndecis =
            new HashMap<Integer, String>();
    private final List<MTType> myComponents;

    public MTBigUnion(TypeGraph g, Map<String, MTType> quantifiedVariables,
            MTType expression) {
        super(g);

        myQuantifiedVariables =
                new TreeMap<String, MTType>(quantifiedVariables);
        myExpression = expression;

        List<MTType> components = new LinkedList<MTType>();
        for (Map.Entry<String, MTType> entry : myQuantifiedVariables.entrySet()) {

            myComponentIndecis.put(components.size(), entry.getKey());
            components.add(entry.getValue());
        }
        components.add(expression);
        myComponents = Collections.unmodifiableList(components);
    }

    public MTType getExpression() {
        return myExpression;
    }

    public Map<String, MTType> getQuantifiedVariables() {
        return myQuantifiedVariables;
    }

    @Override
    public void accept(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTBigUnion(this);

        v.beginChildren(this);

        for (MTType t : myQuantifiedVariables.values()) {
            t.accept(v);
        }

        myExpression.accept(v);

        v.endChildren(this);

        v.endMTBigUnion(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    @Override
    public List<MTType> getComponentTypes() {
        return myComponents;
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        Map<String, MTType> newQuantifiedVariables;
        MTType newExpression;

        if (index < myQuantifiedVariables.size()) {
            newQuantifiedVariables =
                    new HashMap<String, MTType>(myQuantifiedVariables);

            newQuantifiedVariables.put(myComponentIndecis.get(index), newType);

            newExpression = myExpression;
        }
        else if (index == myQuantifiedVariables.size()) {
            newQuantifiedVariables = myQuantifiedVariables;

            newExpression = newType;
        }
        else {
            throw new IndexOutOfBoundsException();
        }

        return new MTBigUnion(getTypeGraph(), newQuantifiedVariables,
                newExpression);
    }

    @Override
    public int getHashCode() {
        int result = BASE_HASH;

        //Note that order of these MTTypes doesn't matter
        for (MTType t : myQuantifiedVariables.values()) {
            result += t.hashCode();
        }

        result *= 57;
        result += myExpression.hashCode();

        return result;
    }

    @Override
    public String toString() {
        return "BigUnion" + myQuantifiedVariables + "{" + myExpression + "}";
    }
}

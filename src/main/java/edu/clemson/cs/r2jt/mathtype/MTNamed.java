package edu.clemson.cs.r2jt.mathtype;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>Represents a type that is simply a named reference to some bound variable.
 * For example, in BigUnion{t : MType}{t}, the second "t" is a named type.</p>
 */
public class MTNamed extends MTType {

    private final static int BASE_HASH = "MTNamed".hashCode();

    public final String name;

    public MTNamed(TypeGraph g, String name) {
        super(g);

        this.name = name;
    }

    @Override
    public void accept(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTNamed(this);

        v.beginChildren(this);
        v.endChildren(this);

        v.endMTNamed(this);
        v.endMTType(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MTType> getComponentTypes() {
        return (List<MTType>) Collections.EMPTY_LIST;
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getHashCode() {
        return BASE_HASH;
    }

    @Override
    public String toString() {
        return "'" + name + "'";
    }

    public static Map<MTNamed, MTType> toMTNamedMap(TypeGraph source,
            Map<String, MTType> original) {

        Map<MTNamed, MTType> result = new HashMap<MTNamed, MTType>();

        for (Map.Entry<String, MTType> e : original.entrySet()) {
            result.put(new MTNamed(source, e.getKey()), e.getValue());
        }

        return result;
    }
}

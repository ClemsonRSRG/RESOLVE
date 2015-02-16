package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MTType {

    protected final TypeGraph myTypeGraph;

    private final Set<Object> myKnownAlphaEquivalencies = new HashSet<Object>();
    private final Map<MTType, Map<String, MTType>> myKnownSyntacticSubtypeBindings =
            new HashMap<MTType, Map<String, MTType>>();

    /**
     * <p>Allows us to detect if we're getting into an equals-loop.</p>
     */
    private int myEqualsDepth = 0;

    public MTType(TypeGraph typeGraph) {
        myTypeGraph = typeGraph;
    }

    public TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    /**
     * <p>Indicates that this type is known to contain only elements <em>that
     * are themselves</em> types.  Practically, this answers the question, "can
     * an instance of this type itself be used as a type?"</p>
     */
    public boolean isKnownToContainOnlyMTypes() {
        return false;
    }

    /**
     * <p>Indicates that every instance of this type is itself known to contain
     * only elements that are types.  Practically, this answers the question,
     * "if a function returns an instance of this type, can that instance itself
     * be said to contain only types?"</p>
     */
    public boolean membersKnownToContainOnlyMTypes() {
        return false;
    }
}

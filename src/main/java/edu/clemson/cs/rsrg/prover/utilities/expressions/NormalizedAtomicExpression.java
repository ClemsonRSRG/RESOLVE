package edu.clemson.cs.rsrg.prover.utilities.expressions;

import edu.clemson.cs.rsrg.prover.utilities.Registry;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * <p>This class represents an normalized atomic expression.</p>
 *
 * @author Mike Khabbani
 * @version 2.0
 */
public class NormalizedAtomicExpression {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final int[] m_expression;
    private int m_classConstant;
    private int arity; // number of arguments
    private final Registry m_registry;
    private Map<String, Integer> m_opMmap;
    private Set<Integer> m_opIdSet;
    private Map<String, Integer> m_argMmap;

    // ===========================================================
    // Constructors
    // ===========================================================

    public NormalizedAtomicExpression(Registry registry, int[] intArray) {
        m_registry = registry;
        arity = intArray.length - 1;
        if (!m_registry.isCommutative(intArray[0])) {
            m_expression = intArray;
        }
        else {
            int[] ord = new int[arity];
            System.arraycopy(intArray, 1, ord, 0, intArray.length - 1);

            Arrays.sort(ord);

            int[] ne = new int[intArray.length];
            ne[0] = intArray[0];
            System.arraycopy(ord, 0, ne, 1, ord.length);

            m_expression = ne;
        }

        m_classConstant = -1;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

}
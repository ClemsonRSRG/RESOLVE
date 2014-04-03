package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PExpSubexpressionIterator;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

import java.util.*;

/**
 * Created by mike on 4/3/2014.
 */
public class ConjuctionOfNormalizedAtomicExpressions {
    private final byte m_maxPositions = 6;
    private Registry m_registry;
    private List<TreeMap<Integer, Integer>> m_equalityMatrix; //[atom][operator] = position
    private AtomCompare m_Atom_Compare = new AtomCompare();
    private AtomCompareLHS m_Atom_Compare_LHs = new AtomCompareLHS();

    /**
     * @param registry the Registry symbols contained in the conjunction will reference.  This class will add entries to
     *                 the registry if needed.
     */
    public ConjuctionOfNormalizedAtomicExpressions(Registry registry) {
        m_registry = registry;
        m_equalityMatrix = new LinkedList<TreeMap<Integer, Integer>>();
    }

    private int getOperatorByPosition(TreeMap<Integer, Integer> eqPred,
                                      int position) {
        Iterator<Map.Entry<Integer, Integer>> it = eqPred.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> curr = it.next();
            if ((curr.getValue() & (1 << position)) > 0)
                return curr.getKey();
        }
        // if return -1, position is not used.
        return -1;
    }

    private class AtomCompare implements Comparator<TreeMap<Integer, Integer>> {

        @Override
        public int compare(TreeMap<Integer, Integer> integerIntegerTreeMap,
                           TreeMap<Integer, Integer> integerIntegerTreeMap2) {

            for (int i = 0; i < m_maxPositions; ++i) {
                int cmp =
                        getOperatorByPosition(integerIntegerTreeMap, i)
                                - getOperatorByPosition(integerIntegerTreeMap2,
                                i);
                if (cmp != 0)
                    return cmp;
            }
            return 0;
        }
    }

    private class AtomCompareLHS
            implements
            Comparator<TreeMap<Integer, Integer>> {

        @Override
        public int compare(TreeMap<Integer, Integer> integerIntegerTreeMap,
                           TreeMap<Integer, Integer> integerIntegerTreeMap2) {

            for (int i = 0; i < m_maxPositions - 1; ++i) {
                int cmp =
                        getOperatorByPosition(integerIntegerTreeMap, i)
                                - getOperatorByPosition(integerIntegerTreeMap2,
                                i);
                if (cmp != 0)
                    return cmp;
            }
            return 0;
        }
    }


    /**
     *
     * @param formula a formula that should not contain =. Predicate symbols are treated as any other function symbol here.
     * @return current index in list of expressions.
     */
    protected int addExpression(PExp formula) {
        String name = formula.getTopLevelOperation();
        MTType type = formula.getType();
        int intRepOfOp = m_registry.addSymbol(name,type);
        // base case
        if (formula.isVariable())
             return intRepOfOp;

        TreeMap<Integer, Integer> map = new TreeMap<Integer, Integer>();
        map.put(intRepOfOp,1);
        int pos = 1;
        PExpSubexpressionIterator it = formula.getSubExpressionIterator();
        while (it.hasNext()) {
            PExp p = it.next();
            pos <<= 1;
            int root = addExpression(p);
            Integer r = map.get(root);
            if (r == null)
                map.put(root, pos);
            else
                map.put(root, pos | r);
        }
        return addAtomicFormula(map);
    }

    private int addAtomicFormula(TreeMap<Integer, Integer> atomicFormula) {
        int posIfFound =
                Collections.binarySearch(m_equalityMatrix, atomicFormula,
                        m_Atom_Compare_LHs);
        if (posIfFound >= 0) {
            return getOperatorByPosition(m_equalityMatrix.get(posIfFound),
                    m_maxPositions - 1);
        }
        int indexToInsert = -(posIfFound + 1);
        MTType typeOfFormula = m_registry.getTypeByIndex(getOperatorByPosition(atomicFormula,1));
        int rhs = m_registry.makeSymbol(typeOfFormula);
        atomicFormula.put(rhs, 1 << m_maxPositions - 1);
        m_equalityMatrix.add(indexToInsert, atomicFormula);
        return rhs;
    }
    // Return list of modified predicates by their position. Only these can cause new merges.
    protected List<Integer> mergeOperators(int a, int b) {
        if (a == b)
            return null;
        if (a > b) {
            int temp = a;
            a = b;
            b = temp;
        }
        Iterator<TreeMap<Integer, Integer>> it = m_equalityMatrix.iterator();
        Stack<TreeMap<Integer, Integer>> modifiedEntries =
                new Stack<TreeMap<Integer, Integer>>();
        while (it.hasNext()) {
            TreeMap<Integer, Integer> curr = it.next();
            int valB = curr.get(b) == null ? 0 : curr.get(b);
            if (valB > 0) {
                curr.put(a, (curr.get(a) == null ? 0 : curr.get(a)) | valB);
                curr.remove(b);
                modifiedEntries.push(curr);
                it.remove();
            }
        }
        while (!modifiedEntries.empty()) {
            int indexToInsert =
                    Collections.binarySearch(m_equalityMatrix, modifiedEntries
                            .peek(), m_Atom_Compare);
            // If the modified one is already there, don't put it back
            if (indexToInsert < 0) {
                indexToInsert = -(indexToInsert + 1);
                m_equalityMatrix.add(indexToInsert, modifiedEntries.pop());
            } else {
                modifiedEntries.pop();
            }
        }
        m_registry.substitute(a, b); // This is only so that things can be added that use remapped vars
        return null;
    }
}

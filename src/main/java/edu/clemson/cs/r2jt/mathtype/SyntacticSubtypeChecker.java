package edu.clemson.cs.r2jt.mathtype;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.NoSuchElementException;

/**
 * <p>A <em>syntactic subtype</em> refers to a type that can be demonstrated
 * as a (non-strict) subtype of some other type using only syntactic 
 * information.  Specifically, without recourse to type theorems.  The syntactic
 * subtype relationship thus completely captures all hard-coded type 
 * relationship information.</p>
 * 
 * <p>This class implements a check for a syntactic subtype relationship as a
 * symmetric visitor.  To check if <code>t1</code> is a syntactic subtype of 
 * <code>t2</code>, use code like this:</p>
 * 
 * <pre>
 * SyntacticSubtypeChecker checker = new SyntacticSubtypeChecker(typeGraph);
 * try {
 *     checker.visit(t1, t2);
 *     //Stuff to do if t1 is a syntactic subtype of t2
 * }
 * catch (IllegalArgumentException e) {
 *     TypeMismatchException mismatch = (TypeMismatchException) e.getCause();
 *     //Stuff to do if t1 is NOT a syntactic subtype of t2
 * }
 * </pre>
 * 
 * <p>As shown, the <code>visit()</code> method of a 
 * <code>SyntacticSubtypeChecker</code> will exit normally if two types have a
 * syntactic subtype relationship, or throw an 
 * <code>IllegalArgumentException</code> if they do not.  The 
 * <code>IllegalArgumentException</code> will wrap a 
 * <code>TypeMismatchException</code> describing the specific problem.</p>
 *
 * <p>As the checker descends the type trees in parallel, it keeps a record of
 * actual component types from <code>t1</code> that were matched with some
 * quantified type-variable from <code>t2</code>.  After a successful check,
 * this record can be accessed via <code>getBindings()</code>.</p>
 *
 * <p>At this time, the following syntactic relationships are recognized as 
 * forming a syntactic subtype relationship:</p>
 * 
 * <ul>
 * <li>Any type is a syntactic subtype of both <strong>MType</strong> and 
 *     <strong>Entity</strong> (which is a superset of 
 *     <strong>MType</strong>).</li>
 * <li><strong>Empty_Set</strong> is a subtype of all types, including itself.
 *     </li>
 * <li>Any type is a syntactic subtype of itself or a type that is <em>alpha
 *     equivalent</em> to itself.</li>
 * <li><code>t1</code> is a syntactic subtype of <code>t2</code> if 
 *     <code>BigUnion{unique_var_name_1 : MType, 
 *     ... unique_var_name_n : MType}{t1}</code> (for some <code>n > 0</code>, 
 *     where each <code>unique_var_name</code> does not appear in 
 *     <code>t1</code>) is a syntactic subtype of <code>t2</code>.</li>
 * <li><pre>
 * BigUnion{t1 : (T1 : Power(MType)), 
 *          t2 : (T2 : Power(MType)),
 *                ... 
 *          tn : (Tn : Power(MType))}
 *         {t_type_valued_expression}
 * </pre>
 * 
 * Is a syntactic subtype of:
 * 
 * <pre>
 * BigUnion{r1 : (R1 : Power(MType)), 
 *          r2 : (R2 : Power(MType)),
 *                ... 
 *          rk : (rk : Power(MType))}
 *         {r_type_valued_expression}
 * </pre>
 * 
 * If <code>n &lt; k</code> and there is some valuation of a (non-strict) subset
 * of the <code>r</code>s and some restriction (to syntactic subtypes) of the
 * <code>R</code>s not associated with <code>r</code>s in the valuation subset
 * such that <code>r_type_valued_expression</code> becomes alpha-equivalent to
 * </code>t_type_valued_expression</code>.
 * </li>
 * 
 * <p><strong>TODO:</strong> Currently we do not deal correctly with types
 * where the same quantified variable appears multiple times in the template,
 * e.g., "<code>BigUnion{t : MType}{t union t}</code>".  This is coded 
 * defensively and will throw a <code>RuntimeException</code> if it occurs.</p>
 * </ul>
 */
public class SyntacticSubtypeChecker extends SymmetricBoundVariableVisitor {

    private Map<String, MTType> myBindings = new HashMap<String, MTType>();

    private final TypeGraph myTypeGraph;

    public SyntacticSubtypeChecker(TypeGraph g) {
        myTypeGraph = g;
    }

    public SyntacticSubtypeChecker(TypeGraph g, FinalizedScope context1) {
        super(context1);
        myTypeGraph = g;
    }

    public SyntacticSubtypeChecker(TypeGraph g, Map<String, MTType> context1) {
        super(context1);
        myTypeGraph = g;
    }

    public Map<String, MTType> getBindings() {
        return myBindings;
    }

    /**
     * <p>Resets a checker so that it is prepared to check a new pair of types.
     * </p>
     */
    public void reset() {
        myBindings.clear();
    }

    @Override
    public boolean beginMTType(MTType t1, MTType t2) {
        //Alpha-equivalent types are definitely syntactic subtypes.  No need 
        //to descend
        return !t1.equals(t2);
    }

    @Override
    public boolean beginMTNamed(MTNamed t1, MTNamed t2) {

        if (!t1.name.equals(t2.name)) {

            if (getInnermostBinding2(((MTNamed) t2).name).equals(
                    myTypeGraph.MTYPE)) {
                bind(((MTNamed) t2).name, t1);
            }
            else {
                MTType t1DeclaredType = t1;
                MTType t2DeclaredType = t2;
                try {
                    t1DeclaredType = getInnermostBinding1(t1.name);
                }
                catch (NoSuchElementException nsee) {

                }

                try {
                    t2DeclaredType = getInnermostBinding2(t2.name);
                }
                catch (NoSuchElementException nsee) {

                }

                if (t1DeclaredType == t1 && t2DeclaredType == t2) {
                    //We have no information on these named types, but they don't
                    //share a name, so...
                    throw new IllegalArgumentException(
                            new TypeMismatchException(t1, t2));
                }

                if (!haveAxiomaticSubtypeRelationship(t1DeclaredType,
                        t2DeclaredType)) {
                    //This is fine if the declared type of t1 is a syntactic subtype
                    //of the declared type of t2
                    visit(t1DeclaredType, t2DeclaredType);
                }
            }
        }

        return true; //Keep searching siblings
    }

    private void bind(String name, MTType type) {
        if (myBindings.containsKey(name)) {
            throw new RuntimeException("Duplicate quantified variable name: "
                    + name);
        }

        myBindings.put(name, type);
    }

    @Override
    public boolean beginMTSetRestriction(MTSetRestriction t1,
            MTSetRestriction t2) {

        //TODO:
        //For the moment, there's no obvious way to do this.  We'll just say no
        //set restriction can be a syntactic subtype of any other.
        throw new IllegalArgumentException(new TypeMismatchException(t1, t2));
    }

    @Override
    public boolean beginMTProper(MTProper t1, MTProper t2) {
        if (!(t1 == t2 || haveAxiomaticSubtypeRelationship(t1, t2))) {
            throw new IllegalArgumentException(
                    new TypeMismatchException(t1, t2));
        }

        return true;
    }

    @Override
    public boolean mismatch(MTType t1, MTType t2) {

        //Note it's possible that t1 and t2 could both be MTBigUnion, even
        //though we're in mismatch() because they could have a different number 
        //of quantified subtypes.
        if (t2 instanceof MTBigUnion && !(t1 instanceof MTBigUnion)) {
            //This may be ok, since we can wrap any expression in a trivial
            //big union
            MTBigUnion t2AsMTBigUnion = (MTBigUnion) t2;
            int quantifiedVariableCount =
                    t2AsMTBigUnion.getQuantifiedVariables().size();

            //TODO : Find a more graceful solution to this
            //Note that since "*" cannot appear in a RESOLVE variable name, 
            //these names must be unique
            Map<String, MTType> uniqueVars = new HashMap<String, MTType>();
            for (int i = 0; i < quantifiedVariableCount; i++) {
                uniqueVars.put("*" + i, myTypeGraph.MTYPE);
            }

            t1 = new MTBigUnion(t1.getTypeGraph(), uniqueVars, t1);

            visit(t1, t2);
        }
        else if (t2 instanceof MTNamed
                && getInnermostBinding2(((MTNamed) t2).name).equals(
                        myTypeGraph.MTYPE)) {

            bind(((MTNamed) t2).name, t1);
        }
        else if (haveAxiomaticSubtypeRelationship(t1, t2)) {
            //We're a syntactic subtype, so we don't need to do anything
        }
        else {
            //Otherwise, there's no way to continue, so we bomb
            throw new IllegalArgumentException(
                    new TypeMismatchException(t1, t2));
        }

        return true; //Keep searching siblings
    }

    private boolean haveAxiomaticSubtypeRelationship(MTType subtype,
            MTType supertype) {

        //Respectively, here:  EMPTY_SET is a subtype of everything, everything
        //is a subtype of MTYPE, and everything is a subtype of ENTITY.

        return subtype.equals(myTypeGraph.EMPTY_SET)
                || supertype.equals(myTypeGraph.MTYPE)
                || supertype.equals(myTypeGraph.ENTITY);
    }
}
package edu.clemson.cs.r2jt.mathtype;

import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.type.ConcType;
import edu.clemson.cs.r2jt.type.FormalType;
import edu.clemson.cs.r2jt.type.NewType;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>The parent class of all mathematical types.</p>
 */
public abstract class MTType {

    protected final TypeGraph myTypeGraph;

    public MTType(TypeGraph typeGraph) {
        myTypeGraph = typeGraph;
    }

    public static MTType fromOldType(Type oldType, ScopeRepository repo) {
        MTType result;

        if (oldType instanceof NewType) {
            result = ((NewType) oldType).getWrappedType();
        }
        else if (oldType instanceof ConcType) {
            result = fromOldType(((ConcType) oldType).getType(), repo);
        }
        else if (oldType instanceof FormalType) {
            FormalType formal = (FormalType) oldType;
            result =
                    new MTNamed(repo.getTypeGraph(), formal.getName().getName());
        }
        else {
            throw new RuntimeException("Don't know how to convert type: "
                    + oldType + " (" + oldType.getClass() + ")");
        }

        return result;
    }

    public TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    public abstract void accept(TypeVisitor v);

    public abstract List<MTType> getComponentTypes();

    public abstract MTType withComponentReplaced(int index, MTType newType);

    public MTType withComponentsReplaced(Map<Integer, MTType> newTypes) {

        MTType target = this;
        for (Map.Entry<Integer, MTType> entry : newTypes.entrySet()) {
            target =
                    target.withComponentReplaced(entry.getKey(), entry
                            .getValue());
        }

        return target;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> <code>o</code> is an
     * </code>MTType</code> that is <em>alpha equivalent</em> to this type.
     * I.e., it must be exactly the same with the sole exception that 
     * quantified variables may have different names if they are otherwise
     * identical.  So, BigUnion{t : MType}{t} <code>equals</code>
     * BigUnion{r : MType}{r}.  However, BigUnion{t : MType}{t} <em>does 
     * not</em> <code>equals</code> BigUnion{r : Power(MType)}{r}.</p>
     * 
     * @param o The object to compare with this <code>MTType</code>.
     * 
     * @return <code>true</code> <strong>iff</strong> this <code>MTType</code>
     *		is alpha equivalent to <code>o</code>.
     */
    @Override
    public final boolean equals(Object o) {

        //All 'equals' logic should be put into AlphaEquivalencyChecker!  Don't
        //override equals!
        AlphaEquivalencyChecker alphaEq = new AlphaEquivalencyChecker();

        boolean result = (o instanceof MTType);

        if (result) {
            try {
                alphaEq.visit(this, (MTType) o);
            }
            catch (RuntimeException e) {
                Throwable cause = e.getCause();
                while (cause != null
                        && !(cause instanceof TypeMismatchException)) {
                    cause = cause.getCause();
                }

                if (cause == null) {
                    throw e;
                }

                result = false;
            }
        }

        return result;
    }

    public final Map<String, MTType> getSyntacticSubtypeBindings(MTType o)
            throws NoSolutionException {

        SyntacticSubtypeChecker checker =
                new SyntacticSubtypeChecker(myTypeGraph);

        try {
            checker.visit(this, o);
        }
        catch (RuntimeException e) {

            Throwable cause = e;
            while (cause != null && !(cause instanceof TypeMismatchException)) {
                cause = cause.getCause();
            }

            if (cause == null) {
                throw e;
            }

            throw new NoSolutionException();
        }

        return checker.getBindings();
    }

    public final boolean isSubtypeOf(MTType o) {
        return myTypeGraph.isSubtype(this, o);
    }

    public final boolean isSyntacticSubtypeOf(MTType o) {

        boolean result;

        try {
            getSyntacticSubtypeBindings(o);
            result = true;
        }
        catch (NoSolutionException e) {
            result = false;
        }

        return result;
    }

    public final boolean isBoolean() {
        return (myTypeGraph.BOOLEAN == this);
    }

    public final boolean alphaEquivalentTo(MTType t) {
        return this.equals(t);
    }

    public final MTType getCopyWithVariablesSubstituted(
            Map<String, MTType> substitutions) {
        VariableReplacingVisitor renamer =
                new VariableReplacingVisitor(substitutions);
        accept(renamer);
        return renamer.getFinalExpression();
    }

    public Map<String, MTType> bindTo(MTType o, FinalizedScope context)
            throws BindingException {

        BindingVisitor bind = new BindingVisitor(myTypeGraph, context);
        bind.visit(this, o);

        if (!bind.binds()) {
            throw new BindingException(this, o);
        }

        return bind.getBindings();
    }

    public Map<String, MTType> bindTo(MTType o, Map<String, MTType> context)
            throws BindingException {

        BindingVisitor bind = new BindingVisitor(myTypeGraph, context);
        bind.visit(this, o);

        if (!bind.binds()) {
            throw new BindingException(this, o);
        }

        return bind.getBindings();
    }

    public Map<String, MTType> bindTo(MTType template,
            Map<String, MTType> thisContext, Map<String, MTType> templateContext)
            throws BindingException {

        BindingVisitor bind =
                new BindingVisitor(myTypeGraph, thisContext, templateContext);
        bind.visit(this, template);

        if (!bind.binds()) {
            throw new BindingException(this, template);
        }

        return bind.getBindings();
    }

    public MTType getType() {
        //TODO : Each MTType should really contain it's declared type.  I.e.,
        //       if I say "Definition X : Set", I should store that X is
        //       of type Set someplace.  That's not currently available, so for
        //       the moment we say that all types are of type MType, the parent
        //       type of all types.
        return myTypeGraph.MTYPE;
    }

    /**
     * <p>Returns the object-reference hash.</p>
     */
    public final int objectReferenceHashCode() {
        return super.hashCode();
    }

    @Override
    public final int hashCode() {
        return getHashCode();
    }

    /**
     * <p>This is just a template method to <em>force</em> all concrete 
     * subclasses of <code>MTType</code> to implement <code>hashCode()</code>,
     * as the type resolution algorithm depends on it being implemented 
     * sensibly.</p>
     * 
     * @return A hashcode consistent with <code>equals()</code> and thus
     *     alpha-equivalency.
     */
    public abstract int getHashCode();

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

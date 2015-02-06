package edu.clemson.cs.r2jt.absynnew.expr;

import edu.clemson.cs.r2jt.absynnew.AbstractNodeBuilder;
import edu.clemson.cs.r2jt.absynnew.ResolveToken;
import edu.clemson.cs.r2jt.parsing.ResolveLexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>A <code>MathSymbolAST</code> represents a reference to a named element such
 * as a variable, constant, or function. More specifically, all three are
 * represented as function calls, with the former two represented as functions
 * with no arguments.</p>
 */
public class MathSymbolAST extends ExprAST {

    private final Token myName;

    private final List<ExprAST> myArguments = new ArrayList<ExprAST>();
    private final boolean myLiteralFlag, myIncomingFlag;

    private MathSymbolAST(MathSymbolExprBuilder builder) {
        super(builder.getStart(), builder.getStop());

        myName = builder.name;
        myArguments.addAll(builder.arguments);
        myLiteralFlag = builder.literal;
        myIncomingFlag = builder.incoming;

        //Todo: Add quantification.
    }

    public Token getName() {
        return myName;
    }

    public List<ExprAST> getArguments() {
        return myArguments;
    }

    public boolean isIncoming() {
        return myIncomingFlag;
    }

    public boolean isFunction() {
        return myArguments.size() > 0;
    }

    @Override
    public boolean isLiteral() {
        return myLiteralFlag;
    }

    /**
     * <p>A builder for {@link MathSymbolAST}s intended to ease construction of
     * math symbols needed on-the-fly in both
     * {@link edu.clemson.cs.r2jt.absynnew.ASTBuildingVisitor} and
     * {@link edu.clemson.cs.r2jt.typereasoning.TypeGraph}.</p>
     */
    public static class MathSymbolExprBuilder
            extends
            AbstractNodeBuilder<MathSymbolAST> {

        protected final Token name, lprint, rprint;

        protected boolean incoming = false;
        protected boolean literal = false;

        protected final List<ExprAST> arguments = new ArrayList<ExprAST>();

        public MathSymbolExprBuilder(String name) {
            this(null, null, new ResolveToken(ResolveLexer.Identifier, name),
                    null);
        }

        public MathSymbolExprBuilder(Token start, Token stop, Token lprint,
                Token rprint) {
            super(start, stop);

            if (rprint == null) {
                if (lprint == null) {
                    throw new IllegalStateException("null name; all math "
                            + "symbols must be named.");
                }
                rprint = lprint;
                this.name = lprint;
            }
            else {
                this.name =
                        new ResolveToken(ResolveLexer.Identifier,
                                lprint.getText() + "..." + rprint.getText());
            }
            this.lprint = lprint;
            this.rprint = rprint;
        }

        public MathSymbolExprBuilder(ParserRuleContext ctx, Token lprint,
                Token rprint) {
            this(ctx.getStart(), ctx.getStop(), lprint, rprint);
        }

        public MathSymbolExprBuilder literal(boolean e) {
            literal = e;
            return this;
        }

        public MathSymbolExprBuilder incoming(boolean e) {
            incoming = e;
            return this;
        }

        public MathSymbolExprBuilder arguments(ExprAST... e) {
            arguments(Arrays.asList(e));
            return this;
        }

        public MathSymbolExprBuilder arguments(Collection<ExprAST> args) {
            sanityCheckAdditions(args);
            arguments.addAll(args);
            return this;
        }

        @Override
        public MathSymbolAST build() {
            return new MathSymbolAST(this);
        }
    }
}
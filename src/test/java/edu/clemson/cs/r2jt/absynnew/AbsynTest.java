package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.decl.TypeModelAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST;
import edu.clemson.cs.r2jt.parsing.ResolveParser;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class AbsynTest {

    @Test
    public void testTypeModelAST() {
        ResolveParser parser =
                new ResolveParserFactory()
                        .createParser("Type Family Boolean is modeled by B; "
                                + "exemplar b;");

        TypeModelAST result =
                TreeUtil.createASTNodeFrom(parser.typeModelDecl());

        assertEquals(result.getModel().toString(), "B");
        assertEquals(result.getExemplar().getText(), "b");
        assertEquals(result.getName().getText(), "Boolean");
    }

    @Test
    public void testSimpleMathSymbol() {
        ResolveParser parser =
                new ResolveParserFactory().createParser("b = true");

        MathSymbolAST result = TreeUtil.createASTNodeFrom(parser.mathExp());

        Iterator<ExprAST> subexpressions = result.getArguments().iterator();
        ExprAST subexp = subexpressions.next();

        assertFalse(subexp.isLiteral());
        assertEquals(((MathSymbolAST) subexp).getName().getText(), "b");
        assertFalse(((MathSymbolAST) subexp).isIncoming());

        subexp = subexpressions.next();

        assertTrue(subexp.isLiteral());
        assertEquals(((MathSymbolAST) subexp).getName().getText(), "true");
        assertFalse(((MathSymbolAST) subexp).isIncoming());

        assertFalse(result.isIncoming());
        assertFalse(result.isLiteral());
        assertEquals(result.getName().getText(), "=");
    }

    @Test
    public void testComplicatedMathSymbol() {
        ResolveParser parser =
                new ResolveParserFactory()
                        .createParser("F(b) = <#a> o S and not (v or x)");

        MathSymbolAST result = TreeUtil.createASTNodeFrom(parser.mathExp());

        Iterator<ExprAST> subexpressions = result.getArguments().iterator();
        assertFalse(result.isLiteral());
        assertFalse(result.isIncoming());
        assertTrue(result.isFunction());
        assertEquals(result.getName().getText(), "and");
        assertEquals(result.getArguments().size(), 2);

        //F(b) = <#a> o S
        ExprAST subexp = subexpressions.next();
        assertEquals(((MathSymbolAST) subexp).getName().getText(), "=");
        assertTrue(((MathSymbolAST) subexp).isFunction());
        assertEquals(((MathSymbolAST) subexp).getArguments().size(), 2);

        //F(b)
        MathSymbolAST lhsSubExp1 =
                (MathSymbolAST) ((MathSymbolAST) subexp).getArguments().get(0);
        assertEquals(lhsSubExp1.getName().getText(), "F");
        assertEquals(lhsSubExp1.getArguments().size(), 1);

        //<#a> o S
        MathSymbolAST lhsSubExp2 =
                (MathSymbolAST) ((MathSymbolAST) subexp).getArguments().get(1);
        assertEquals(lhsSubExp2.getName().getText(), "o");
        assertEquals(lhsSubExp2.getArguments().size(), 2);

        //<#a>
        MathSymbolAST outfixsubexp =
                (MathSymbolAST) lhsSubExp2.getArguments().get(0);
        assertEquals(outfixsubexp.getName().getText(), "<...>");

        MathSymbolAST outfixArg =
                ((MathSymbolAST) outfixsubexp.getArguments().get(0));
        //#a
        assertEquals(outfixArg.getName().getText(), "a");
        assertEquals(outfixArg.isFunction(), false);
        assertEquals(outfixArg.isIncoming(), true);

        //not (v or x)
        subexp = subexpressions.next();
        assertEquals(((MathSymbolAST) subexp).getName().getText(), "not");
        assertEquals(((MathSymbolAST) subexp).getArguments().size(), 1);
    }
}
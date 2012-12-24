/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author hamptos
 */
public class BindingsIteratorTest {

    @Test
    public void testNoBindings() {
        TypeGraph g = new TypeGraph();
        List<PExp> facts = new LinkedList<PExp>();

        //foo(x, y), foo(z, y), foo(y, x), foo(y, y)
        facts.add(PExp.buildPExp("x Z y Z ( foo 2 p B", g));
        facts.add(PExp.buildPExp("z Z y Z ( foo 2 p B", g));
        facts.add(PExp.buildPExp("y Z x Z ( foo 2 p B", g));
        facts.add(PExp.buildPExp("y Z y Z ( foo 2 p B", g));

        //bar(x, y, x), bar(x, z, x), bar(x, x, y)
        //bar(z, z, x), bar(z, y, x), bar(z, z, y), bar(z, w, x)
        facts.add(PExp.buildPExp("x Z y Z x Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("x Z z Z x Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("x Z x Z y Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("z Z z Z x Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("z Z y Z x Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("z Z z Z y Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("z Z w Z x Z ( bar 3 p B", g));

        //fizz(v)
        facts.add(PExp.buildPExp("v Z ( fizz 1 p B", g));

        List<PExp> patterns = new LinkedList<PExp>();

        //for all a, b; foo(a, y) and bar(a, b, x) and fizz(b)
        patterns.add(PExp.buildPExp("forall a Z y Z ( foo 2 p B", g));
        patterns
                .add(PExp.buildPExp("forall a Z forall b Z x Z ( bar 3 p B", g));
        patterns.add(PExp.buildPExp("forall b Z ( fizz 1 p B", g));

        BindingsIterator i =
                new BindingsIterator(facts, patterns.toArray(new PExp[0]));

        //No bindings
        assertFalse(i.hasNext());
    }

    @Test
    public void testSingleBinding() {
        TypeGraph g = new TypeGraph();
        List<PExp> facts = new LinkedList<PExp>();

        //foo(x)
        facts.add(PExp.buildPExp("x Z ( foo 1 p B", g));

        //foo(y)
        facts.add(PExp.buildPExp("y Z ( foo 1 p B", g));

        //bar(x, y)
        facts.add(PExp.buildPExp("x Z y Z ( bar 2 p B", g));

        //bar(y, z)
        facts.add(PExp.buildPExp("y Z z Z ( bar 2 p B", g));

        List<PExp> patterns = new LinkedList<PExp>();

        //for all a, foo(a)
        patterns.add(PExp.buildPExp("forall a Z ( foo 1 p B", g));

        //for all a, bar(a, z)
        patterns.add(PExp.buildPExp("forall a Z z Z ( bar 2 p B", g));

        BindingsIterator i =
                new BindingsIterator(facts, patterns.toArray(new PExp[0]));

        assertTrue(i.hasNext());
        Map<PExp, PExp> result = i.next();

        assertEquals(result.size(), 1);
        assertEquals(result.get(PExp.buildPExp("forall a Z", g)), PExp
                .buildPExp("y Z", g));

        assertFalse(i.hasNext());
    }

    @Test
    public void testMultipleBinding() {
        TypeGraph g = new TypeGraph();
        List<PExp> facts = new LinkedList<PExp>();

        //foo(x, y), foo(z, y), foo(y, x), foo(y, y)
        facts.add(PExp.buildPExp("x Z y Z ( foo 2 p B", g));
        facts.add(PExp.buildPExp("z Z y Z ( foo 2 p B", g));
        facts.add(PExp.buildPExp("y Z x Z ( foo 2 p B", g));
        facts.add(PExp.buildPExp("y Z y Z ( foo 2 p B", g));

        //bar(x, y, x), bar(x, z, x), bar(x, x, y)
        //bar(z, z, x), bar(z, y, x), bar(z, z, y), bar(z, w, x)
        facts.add(PExp.buildPExp("x Z y Z x Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("x Z z Z x Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("x Z x Z y Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("z Z z Z x Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("z Z y Z x Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("z Z z Z y Z ( bar 3 p B", g));
        facts.add(PExp.buildPExp("z Z w Z x Z ( bar 3 p B", g));

        //fizz(z), fizz(w)
        facts.add(PExp.buildPExp("z Z ( fizz 1 p B", g));
        facts.add(PExp.buildPExp("w Z ( fizz 1 p B", g));

        List<PExp> patterns = new LinkedList<PExp>();

        //for all a, b; foo(a, y) and bar(a, b, x) and fizz(b)
        patterns.add(PExp.buildPExp("forall a Z y Z ( foo 2 p B", g));
        patterns
                .add(PExp.buildPExp("forall a Z forall b Z x Z ( bar 3 p B", g));
        patterns.add(PExp.buildPExp("forall b Z ( fizz 1 p B", g));

        BindingsIterator i =
                new BindingsIterator(facts, patterns.toArray(new PExp[0]));

        //First binding should be a ~> x, b ~> z
        assertTrue(i.hasNext());
        Map<PExp, PExp> result = i.next();

        assertEquals(result.size(), 2);
        assertEquals(result.get(PExp.buildPExp("forall a Z", g)), PExp
                .buildPExp("x Z", g));
        assertEquals(result.get(PExp.buildPExp("forall b Z", g)), PExp
                .buildPExp("z Z", g));

        //Second binding should be a ~> z, b ~> z
        assertTrue(i.hasNext());
        result = i.next();

        assertEquals(result.size(), 2);
        assertEquals(result.get(PExp.buildPExp("forall a Z", g)), PExp
                .buildPExp("z Z", g));
        assertEquals(result.get(PExp.buildPExp("forall b Z", g)), PExp
                .buildPExp("z Z", g));

        //Third binding should be a ~> z, b ~> w
        assertTrue(i.hasNext());
        result = i.next();

        assertEquals(result.size(), 2);
        assertEquals(result.get(PExp.buildPExp("forall a Z", g)), PExp
                .buildPExp("z Z", g));
        assertEquals(result.get(PExp.buildPExp("forall b Z", g)), PExp
                .buildPExp("w Z", g));

        //No further bindings
        assertFalse(i.hasNext());
    }
}

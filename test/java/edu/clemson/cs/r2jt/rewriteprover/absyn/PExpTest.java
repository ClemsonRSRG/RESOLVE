/*
 * PExpTest.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.absyn;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol.Quantification;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hamptos
 */
public class PExpTest {

    /**
     * Test of buildPExp method, of class PExp.
     */
    @Test
    public void testBuildPExp_String_TypeGraph() {
        /*
         * System.out.println("buildPExp(String, TypeGraph)");
         * 
         * TypeGraph g = new TypeGraph();
         * 
         * PExp result = PExp.buildPExp("0 Z", g);
         * 
         * assertEquals(result.toString(), "0"); assertEquals(result.getType(),
         * g.Z);
         * assertEquals(((PSymbol) result).quantification, Quantification.NONE);
         * 
         * result = PExp.buildPExp("forall x B", g);
         * 
         * assertEquals(result.toString(), "x"); assertEquals(result.getType(),
         * g.BOOLEAN);
         * assertEquals(((PSymbol) result).quantification,
         * Quantification.FOR_ALL);
         * 
         * result = PExp.buildPExp("0 Z forall x B ( foo 2 p SSet", g);
         * 
         * assertEquals(result.toString(), "foo(0, x)");
         * assertEquals(result.getType(), new
         * MTFunction(g, g.SET, g.Z, g.BOOLEAN)); assertEquals(((PSymbol)
         * result).quantification,
         * Quantification.NONE);
         * 
         * Iterator<PExp> subexpressions =
         * result.getSubExpressions().iterator();
         * 
         * PExp subexp = subexpressions.next(); assertEquals(subexp.getType(),
         * g.Z);
         * assertEquals(((PSymbol) subexp).quantification, Quantification.NONE);
         * 
         * subexp = subexpressions.next(); assertEquals(subexp.getType(),
         * g.BOOLEAN);
         * assertEquals(((PSymbol) subexp).quantification,
         * Quantification.FOR_ALL);
         */
    }

}

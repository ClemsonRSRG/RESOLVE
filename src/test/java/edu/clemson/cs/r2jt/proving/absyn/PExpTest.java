/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.proving.absyn;

import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol.Quantification;
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
        System.out.println("buildPExp(String, TypeGraph)");

        TypeGraph g = new TypeGraph();

        PExp result = PExp.buildPExp("0 Z", g);

        assertEquals(result.toString(), "0");
        assertEquals(result.getType(), g.Z);
        assertEquals(((PSymbol) result).quantification, Quantification.NONE);

        result = PExp.buildPExp("forall x B", g);

        assertEquals(result.toString(), "x");
        assertEquals(result.getType(), g.BOOLEAN);
        assertEquals(((PSymbol) result).quantification, Quantification.FOR_ALL);

        result = PExp.buildPExp("0 Z forall x B ( foo 2 p SSet", g);

        assertEquals(result.toString(), "foo(0, x)");
        assertEquals(result.getType(), new MTFunction(g, g.SET, g.Z, g.BOOLEAN));
        assertEquals(((PSymbol) result).quantification, Quantification.NONE);

        Iterator<PExp> subexpressions = result.getSubExpressions().iterator();

        PExp subexp = subexpressions.next();
        assertEquals(subexp.getType(), g.Z);
        assertEquals(((PSymbol) subexp).quantification, Quantification.NONE);

        subexp = subexpressions.next();
        assertEquals(subexp.getType(), g.BOOLEAN);
        assertEquals(((PSymbol) subexp).quantification, Quantification.FOR_ALL);
    }
}

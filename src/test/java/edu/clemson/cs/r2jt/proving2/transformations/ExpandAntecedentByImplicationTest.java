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
package edu.clemson.cs.r2jt.proving2.transformations;

/**
 * 
 * @author hamptos
 */
/*public class ExpandAntecedentByImplicationTest {

 @Test
 public void testMultipleBinding() {
 System.out
 .println("ExpandAntecedentByImplicationTest.testMultipleBinding");

 TypeGraph g = new TypeGraph();
 List<PExp> localTheorems = new LinkedList<PExp>();
 List<PExp> globalTheorems = new LinkedList<PExp>();

 //foo(x, y), foo(z, y), foo(y, x), foo(y, y)
 localTheorems.add(PExp.buildPExp("x Z y Z ( foo 2 p B", g));
 localTheorems.add(PExp.buildPExp("z Z y Z ( foo 2 p B", g));
 localTheorems.add(PExp.buildPExp("y Z x Z ( foo 2 p B", g));
 localTheorems.add(PExp.buildPExp("y Z y Z ( foo 2 p B", g));

 //bar(x, y, x), bar(x, x, y)
 //bar(z, z, x), bar(z, y, x), bar(z, z, y), bar(z, w, x)
 localTheorems.add(PExp.buildPExp("x Z y Z x Z ( bar 3 p B", g));
 localTheorems.add(PExp.buildPExp("x Z x Z y Z ( bar 3 p B", g));
 localTheorems.add(PExp.buildPExp("z Z z Z x Z ( bar 3 p B", g));
 localTheorems.add(PExp.buildPExp("z Z y Z x Z ( bar 3 p B", g));
 localTheorems.add(PExp.buildPExp("z Z z Z y Z ( bar 3 p B", g));
 localTheorems.add(PExp.buildPExp("z Z w Z x Z ( bar 3 p B", g));

 //fizz(w)
 localTheorems.add(PExp.buildPExp("w Z ( fizz 1 p B", g));

 //Now the global theorems--note that while there is a set of global
 //theorems that satisfies the patterns below without recourse to a local
 //theorem, that binding should not show up since all bindings must use
 //at least one local theorem
 //foo(h, y), bar(h, z, x), fizz(z)
 globalTheorems.add(PExp.buildPExp("h Z y Z ( foo 2 p B", g));
 globalTheorems.add(PExp.buildPExp("h Z z Z x Z ( bar 3 p B", g));
 globalTheorems.add(PExp.buildPExp("x Z z Z x Z ( bar 3 p B", g));
 globalTheorems.add(PExp.buildPExp("z Z ( fizz 1 p B", g));

 List<PExp> patterns = new LinkedList<PExp>();

 //for all a, b; foo(a, y) and bar(a, b, x) and fizz(b)
 patterns.add(PExp.buildPExp("forall a Z y Z ( foo 2 p B", g));
 patterns
 .add(PExp.buildPExp("forall a Z forall b Z x Z ( bar 3 p B", g));
 patterns.add(PExp.buildPExp("forall b Z ( fizz 1 p B", g));

 ExtendedAntecedentsIterator i =
 new ExtendedAntecedentsIterator(patterns, localTheorems,
 globalTheorems);

 //The order the bindings are returned is undefined, so we throw them all
 //in a set and then test for their presence.
 Set<Map<PExp, PExp>> bindings = new HashSet<Map<PExp, PExp>>();
 while (i.hasNext()) {
 bindings.add(i.next());
 }

 assertEquals(bindings.size(), 3);

 //There is a binding a ~> x, b ~> z
 Map<PExp, PExp> expectedBindings = new HashMap<PExp, PExp>();
 expectedBindings.put(PExp.buildPExp("forall a Z", g), PExp.buildPExp(
 "forall x Z", g));
 expectedBindings.put(PExp.buildPExp("forall b Z", g), PExp.buildPExp(
 "forall z Z", g));
 assertTrue(bindings.contains(expectedBindings));

 //There is a binding a ~> z, b ~> z
 expectedBindings.clear();
 expectedBindings.put(PExp.buildPExp("forall a Z", g), PExp.buildPExp(
 "forall z Z", g));
 expectedBindings.put(PExp.buildPExp("forall b Z", g), PExp.buildPExp(
 "forall z Z", g));
 assertTrue(bindings.contains(expectedBindings));

 //There is a binding a ~> z, b ~> w
 expectedBindings.clear();
 expectedBindings.put(PExp.buildPExp("forall a Z", g), PExp.buildPExp(
 "forall z Z", g));
 expectedBindings.put(PExp.buildPExp("forall b Z", g), PExp.buildPExp(
 "forall w Z", g));
 assertTrue(bindings.contains(expectedBindings));
 }
 }
 */
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
package edu.clemson.cs.r2jt.proving;

/**
 * <p>A <code>ReplacementApplicator</code> provides a mechanism for iterating 
 * over all possible single applications of a <code>MatchReplace</code> over a 
 * set of conjuncts.</p>
 * 
 * <p>This class is intended to succeed MatchApplicator.  The former should be
 * phased out and eventually wholly replaced with this one.</p>
 */
public interface ReplacementApplicator {

    /**
     * <p>Returns a deep copy of the conjuncts provided to the constructor with
     * a single possible replacement made (defined by the matcher provided to
     * the constructor).  Each call to this method will return a new deep copy,
     * each with a different single replacement from any previous call, until
     * no such replacement is possible, at which time it will return 
     * <code>null</code>.</p>
     *  
     * @return Either the next possible single replacement, or 
     *         <code>null</code> if there are no further non-redundant
     *         replacements.
     */
    public ImmutableConjuncts getNextApplication();
}

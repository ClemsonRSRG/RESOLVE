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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import java.util.Deque;

/**
 * <p>An <code>Automator</code> represents an in-process prover task.  While it
 * is at the top of the automator stack, it will receive <code>step()</code>
 * calls each time the automated prover is ready to take its next step (we will
 * call this <em>receiving the heartbeat</em>).  At that time, the automator 
 * must take 0 or 1 of the following steps:</p>
 * 
 * <ul>
 *      <li>Add any number of new <code>Automators</code> to the stack.</li>
 *      <li>Apply one 
 *          {@link edu.clemson.cs.r2jt.proving2.applications.Application
 *              Application} to the proof model.</li>
 *      <li>Undo any number of proof steps.</li>
 * </ul>
 * 
 * <p>Following taking its step, it may also, optionally:</p>
 * 
 * <ul>
 *      <li>Pop itself off the stack.</li>
 * </ul>
 * 
 * <p>While it follows that an automator may take no action, an automator
 * that takes no action is guaranteed to receive the next heartbeat, and thus
 * must itself guarantee that after a finite number of heartbeats, it will take
 * action (otherwise the automated prover will go into an infinite loop).</p>
 */
public interface Automator {

    /**
     * <p>Called when this automator receives the heartbeat.  <code>stack</code>
     * is the automator stack, which is guaranteed to have <code>this</code> at 
     * the top.  <code>model</code> is the prover model.  When 
     * <code>step()</code> is called, this automator must take action as 
     * detailed in the class comments.</p>
     * 
     * @param stack The automator stack, with <code>this</code> at the top.
     * @param model The prover model.
     */
    public void step(Deque<Automator> stack, PerVCProverModel model);
}
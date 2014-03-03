/**
 * Automator.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
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
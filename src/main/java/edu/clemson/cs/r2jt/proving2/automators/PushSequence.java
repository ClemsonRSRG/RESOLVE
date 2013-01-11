/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import java.util.Deque;
import java.util.Iterator;

/**
 * <p>An {@link Automator Automator} that, when given the heartbeat, simply 
 * pushes the next in a sequence of automators onto the stack.  When it runs out
 * of automators to push, it pops itself off the stack.</p>
 */
public class PushSequence implements Automator {

    private Iterator<Automator> mySequence;

    public PushSequence(Iterator<Automator> sequence) {
        mySequence = sequence;
    }

    public PushSequence(Iterable<Automator> sequence) {
        this(sequence.iterator());
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {

        if (mySequence.hasNext()) {
            stack.push(mySequence.next());
        }
        else {
            stack.pop();
        }
    }
}

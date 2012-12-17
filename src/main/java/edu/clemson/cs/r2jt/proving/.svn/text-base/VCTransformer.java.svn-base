package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>VCTransformer</code> maps a VC into one or more new VCs according
 * to some predefined rule.</p>
 */
public interface VCTransformer {

    /**
     * <p>Returns an <code>Iterator</code> over alternative applications of this
     * transformer to the given <code>VC</code>.</p>
     *
     * @param original The <code>VC</code> to transform.
     *
     * @return A non-<code>null</code> <code>Iterator</code> over alternative
     *         applications of this transformer to <code>original</code>.  Note
     *         that if there are no such applications, this method will return
     *         an <code>Iterator</code> over the empty set.
     */
    public Iterator<VC> transform(VC original);

    /**
     * <p>Most <code>VCTransformer</code>s represent the application of a
     * theorem that is looking for some pattern and transforming it according
     * to some template.</p>
     *
     * <p>If this is the case for this transformer, this method returns the
     * pattern it is looking for.  Otherwise, this method indicates that the
     * idea of a pattern is not applicable by throwing an
     * <code>UnsupportedOperationException</code>.</p>
     *
     * @return The pattern this transformer is matching against.
     *
     * @throws UnsupportedOperationException If the concept of a pattern is
     *      not applicable.
     */
    public Antecedent getPattern();

    /**
     * <p>Most <code>VCTransformer</code>s represent the application of a
     * theorem that is looking for some pattern and transforming it according
     * to some template.</p>
     *
     * <p>If this is the case for this transformer, this method returns the
     * template used for transforming.  Otherwise, this method indicates that 
     * the idea of a template is not applicable by throwing an
     * <code>UnsupportedOperationException</code>.</p>
     *
     * @return The replacement template this transformer is applying.
     *
     * @throws UnsupportedOperationException If the concept of a template is
     *      not applicable.
     */
    public Consequent getReplacementTemplate();
    
    /**
     * <p>Returns <code>false</code> <strong>iff</strong> no application of
     * this transformation to any VC could result in a new, unbound quantified
     * variable.</p>
     * 
     * @return <code>false</code> <strong>iff</strong> no application of
     * 		this transformation to any VC could result in a new, unbound 
     * 		quantified variable.
     */
    public boolean introducesQuantifiedVariables();
}

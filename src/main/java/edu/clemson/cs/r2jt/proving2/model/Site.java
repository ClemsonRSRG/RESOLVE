/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.model;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.EmptyImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>A <code>Site</code> identifies a particular <code>PExp</code> accessible
 * from a {@link PerVCProverModel PerVCProverModel}, which may be a top level
 * local or global theorem, or consequent, or may be a sub-expression embedded
 * in a theorem or consequent.</p>
 */
public class Site {
    
    public enum Section {
        ANTECEDENTS {
            @Override
            public PExp getPExp(PerVCProverModel m, int index) {
                return m.getLocalTheorem(index).getAssertion();
            }
        }, 
        CONSEQUENTS {
            @Override
            public PExp getPExp(PerVCProverModel m, int index) {
                return m.getConsequent(index);
            }
        }, 
        THEOREM_LIBRARY {
            @Override
            public PExp getPExp(PerVCProverModel m, int index) {
                return m.getTheoremLibrary().get(index).getAssertion();
            }
        };
    
        public abstract PExp getPExp(PerVCProverModel m, int index);
    };
    
    public final Section section;
    public final int index;
    public final ImmutableList path;
    public final PExp exp;
    public final Site root;

    private final int myHashCode;

    private final PerVCProverModel mySource;
    
    public Site(PerVCProverModel source, Section section, int index, 
            Iterable<Integer> path, PExp exp) {
        this(source, section, index, path, exp, new Site(
                source, section, index, section.getPExp(source, index)));
    }
    
    public Site(PerVCProverModel source, Section section, int index, PExp exp) {
        this(source, section, index, Collections.EMPTY_LIST, exp, null);
    }
    
    private Site(PerVCProverModel source, Section section, int index, 
            Iterable<Integer> path, PExp exp, Site root) {
        this.section = section;
        this.index = index;
        this.path = new ArrayBackedImmutableList(path);
        this.exp = exp;
        
        myHashCode = section.hashCode() + 
                (41 * (index + (57 * this.path.hashCode())));
        
        if (root == null) {
            //This looks weird but suppresses a "leaked this" warning
            Site r = this;
            this.root = r;
        }
        else {
            this.root = root;
        }
        
        mySource = source;
    }

    @Override
    public int hashCode() {
        return myHashCode;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = (o instanceof Site);

        if (result) {
            Site oAsSite = (Site) o;
            result = section.equals(oAsSite.section) && 
                    index == oAsSite.index && path.equals(oAsSite.path);
        }

        return result;
    }

    public PerVCProverModel getModel() {
        return mySource;
    }

    public Iterator<Integer> pathIterator() {
        return path.iterator();
    }

    public boolean inside(Site s) {
        boolean result = (getModel() == s.getModel()) && 
                (section.equals(s.section)) &&
                (index == s.index);

        if (result) {
            Iterator<Integer> myIter = pathIterator();
            Iterator<Integer> oIter = s.pathIterator();

            boolean goodSoFar = true;
            while (myIter.hasNext() && oIter.hasNext() && goodSoFar) {
                goodSoFar = (myIter.next().equals(oIter.next()));
            }

            result = (goodSoFar && !oIter.hasNext());
        }

        return result;
    }
    
    @Override
    public String toString() {
        return section + ":" + section.getPExp(mySource, index) + "(" + index + "):" + exp;
    }
}

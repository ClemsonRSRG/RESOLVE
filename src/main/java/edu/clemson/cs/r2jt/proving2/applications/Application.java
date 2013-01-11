/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.applications;

import edu.clemson.cs.r2jt.proving.absyn.NodeIdentifier;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import java.util.List;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public interface Application {

    public void apply(PerVCProverModel m);

    public Set<Site> involvedSubExpressions();

    public String description();
}

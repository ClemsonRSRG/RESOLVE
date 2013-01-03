/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.InductiveAntecedentBinder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.ModifyConsequent;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class SubstituteInPlaceInConsequent implements Transformation {

    private final BindResultToApplication BIND_RESULT_TO_APPLICATION =
            new BindResultToApplication();
    
    private PExp myMatchPattern;
    private PExp myTransformationTemplate;
    
    public SubstituteInPlaceInConsequent(PExp matchPattern, 
            PExp transformationTemplate) {
        
        myMatchPattern = matchPattern;
        myTransformationTemplate = transformationTemplate;
    }
    
    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Iterator<PerVCProverModel.BindResult> bindResults = m.bind(
                Collections.singleton(
                    (Binder) new InductiveAntecedentBinder(myMatchPattern)));

        return new LazyMappingIterator<BindResult, Application>(bindResults,
                BIND_RESULT_TO_APPLICATION);
    }
    
    private class BindResultToApplication 
            implements Mapping<BindResult, Application> {

        @Override
        public Application map(BindResult input) {
            return new SubstituteInPlaceInConsequentApplication(
                    input.bindSites.values().iterator().next(),
                    input.freeVariableBindings);
        }
        
    }
    
    private class SubstituteInPlaceInConsequentApplication 
            implements Application {
        
        private final Site myBindSite;
        private final Map<PExp, PExp> myBindings;

        public SubstituteInPlaceInConsequentApplication(Site bindSite,
                Map<PExp, PExp> bindings) {
            myBindSite = bindSite;
            myBindings = bindings;
        }

        @Override
        public void apply(PerVCProverModel m) {
            PExp transformed = myTransformationTemplate.substitute(myBindings);
            m.alterSite(myBindSite, transformed);
            
            m.addProofStep(new ModifyConsequent(myBindSite,
                    SubstituteInPlaceInConsequent.this));
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.singleton(myBindSite);
        }
    }
    
    @Override
    public String toString() {
        return "" + myMatchPattern + " = " + myTransformationTemplate;
    }
}

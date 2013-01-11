/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.PTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 *
 * @author hamptos
 */
public class ProgramTypeDefinitionEntry extends ProgramTypeEntry {
    
    private final String myExemplarName;
    private final PExp myInitialization;
    private final PExp myFinalization;
    
    public ProgramTypeDefinitionEntry(TypeGraph g, String name,
            ResolveConceptualElement definingElement, 
            ModuleIdentifier sourceModule, MTType modelType, 
            PTType programType, String exemplarName, PExp initialization, 
            PExp finalization) {
        super(g, name, definingElement, sourceModule, modelType, programType);
        
        myExemplarName = exemplarName;
        myInitialization = initialization;
        myFinalization = finalization;
    }
}

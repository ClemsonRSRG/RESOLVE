/**
 * FacilityAST.java
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
package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.EnhancementPairAST;
import edu.clemson.cs.r2jt.absynnew.ModuleArgumentAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class FacilityAST extends DeclAST {

    private final Token myConceptName, myBodyName;

    private final List<ModuleArgumentAST> myConceptArguments =
            new ArrayList<ModuleArgumentAST>();

    private final List<ModuleArgumentAST> myBodyArguments =
            new ArrayList<ModuleArgumentAST>();

    private final List<EnhancementPairAST> myEnhancements;

    public FacilityAST(Token start, Token stop, Token name, Token conceptName,
            Token bodyName) {
        this(start, stop, name, conceptName,
                new ArrayList<ModuleArgumentAST>(), bodyName,
                new ArrayList<ModuleArgumentAST>(),
                new ArrayList<EnhancementPairAST>());
    }

    public FacilityAST(Token start, Token stop, Token name, Token conceptName,
            Token bodyName, List<EnhancementPairAST> enhancements) {
        this(start, stop, name, conceptName,
                new ArrayList<ModuleArgumentAST>(), bodyName,
                new ArrayList<ModuleArgumentAST>(), enhancements);
    }

    public FacilityAST(Token start, Token stop, Token name, Token conceptName,
            List<ModuleArgumentAST> specArgs, Token bodyName,
            List<ModuleArgumentAST> bodyArgs,
            List<EnhancementPairAST> enhancements) {
        super(start, stop, name);
        myConceptName = conceptName;
        myBodyName = bodyName;
        myEnhancements = enhancements;

        myBodyArguments.addAll(bodyArgs);
        myConceptArguments.addAll(specArgs);
    }

    public Token getConceptName() {
        return myConceptName;
    }

    public Token getBodyName() {
        return myBodyName;
    }

    public List<ModuleArgumentAST> getConceptArguments() {
        return myConceptArguments;
    }

    public List<ModuleArgumentAST> getBodyArguments() {
        return myBodyArguments;
    }

    public List<EnhancementPairAST> getEnhancementPairs() {
        return myEnhancements;
    }
}
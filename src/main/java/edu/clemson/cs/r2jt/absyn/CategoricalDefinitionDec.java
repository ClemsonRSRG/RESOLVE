/*
 * CategoricalDefinitionDec.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class CategoricalDefinitionDec extends Dec {

    private List<DefinitionDec> definitions;
    private Exp relatedByExp;

    public CategoricalDefinitionDec() {};

    public CategoricalDefinitionDec(List<DefinitionDec> definitions,
            Exp relatedByExp) {
        this.definitions = definitions;
        this.relatedByExp = relatedByExp;
    }

    public List<DefinitionDec> getDefitions() {
        return definitions;
    }

    public Exp getRelatedByExpression() {
        return relatedByExp;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
        // TODO Auto-generated method stub

    }

    @Override
    public String asString(int indent, int increment) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PosSymbol getName() {
        // TODO Auto-generated method stub
        return null;
    }

}

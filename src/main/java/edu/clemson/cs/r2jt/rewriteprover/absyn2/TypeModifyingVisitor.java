/**
 * TypeModifyingVisitor.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.absyn2;

import edu.clemson.cs.r2jt.typeandpopulate2.MTNamed;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class TypeModifyingVisitor extends MutatingVisitor {

    private final Map<MTType, MTType> mySubstitutions =
            new HashMap<MTType, MTType>();

    public TypeModifyingVisitor(Map<MTType, MTType> substitutions) {
        mySubstitutions.putAll(substitutions);
    }

    public void mutateEndPExp(PExpr e) {

        PExpr finalValue = e;
        MTType eType = e.getType();
        MTType eTypeValue = e.getTypeValue();

        MTType typeReplacement = mySubstitutions.get(e.getType());
        if (eType instanceof MTNamed) {
            try {
                getInnermostBinding(((MTNamed) eType).name);
                //The type name is bound to some shadowing scope, so we don't
                //want to apply the replacement
                typeReplacement = null;
            }
            catch (NoSuchElementException nsee) {}
        }

        if (typeReplacement != null) {
            finalValue = finalValue.withTypeReplaced(typeReplacement);
        }

        MTType typeValueReplacement = mySubstitutions.get(e.getType());
        if (eTypeValue instanceof MTNamed) {
            try {
                getInnermostBinding(((MTNamed) eTypeValue).name);
                //The type name is bound to some shadowing scope, so we don't
                //want to apply the replacement
                typeValueReplacement = null;
            }
            catch (NoSuchElementException nsee) {}
        }

        if (typeValueReplacement != null) {
            finalValue = finalValue.withTypeValueReplaced(typeReplacement);
        }

        if (finalValue != e) {
            //We do this once at the end both for efficiency and fear that 
            //mutating visitor might break down if we do a double-replace
            replaceWith(e);
        }
    }
}

/*
 * AbstractParameterizedModuleDec.java
 * ---------------------------------
 * Copyright (c) 2019
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

public abstract class AbstractParameterizedModuleDec extends ModuleDec {

    /** The parameters member. */
    protected List<ModuleParameterDec> parameters;

    /** Returns the value of the parameters variable. */
    public List<ModuleParameterDec> getParameters() {
        return parameters;
    }

    /** Sets the parameters variable to the specified value. */
    public void setParameters(List<ModuleParameterDec> parameters) {
        this.parameters = parameters;
    }

}

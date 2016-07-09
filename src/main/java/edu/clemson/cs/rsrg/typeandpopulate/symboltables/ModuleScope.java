/**
 * ModuleScope.java
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
package edu.clemson.cs.rsrg.typeandpopulate.symboltables;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;

/**
 * TODO: Refactor this class
 */
public interface ModuleScope extends Scope {

    ModuleDec getDefiningElement();

}
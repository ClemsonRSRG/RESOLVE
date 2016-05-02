/**
 * OperationQuery.java
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
package edu.clemson.cs.r2jt.typeandpopulate2.query;

import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate2.PossiblyQualifiedPath;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.OperationSearcher;
import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * <p>An <code>OperationQuery</code> searched for a (possibly-qualified) 
 * operation.  If a qualifier is provided, the named facility or module is 
 * searched.  Otherwise, the operation is searched for in any directly imported
 * modules and in instantiated versions of any available facilities.</p>
 */
public class OperationQuery extends BaseSymbolQuery<OperationEntry> {

    public OperationQuery(Token qualifier, Token name,
            List<PTType> argumentTypes) {
        super(new PossiblyQualifiedPath(qualifier, ImportStrategy.IMPORT_NAMED,
                FacilityStrategy.FACILITY_INSTANTIATE, false),
                new OperationSearcher(name, argumentTypes));
    }
}

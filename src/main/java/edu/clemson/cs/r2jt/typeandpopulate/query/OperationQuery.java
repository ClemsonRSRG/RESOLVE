/*
 * OperationQuery.java
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
package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import java.util.List;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.OperationSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.PossiblyQualifiedPath;

/**
 * <p>
 * An <code>OperationQuery</code> searched for a (possibly-qualified) operation.
 * If a qualifier is
 * provided, the named facility or module is searched. Otherwise, the operation
 * is searched for in
 * any directly imported modules and in instantiated versions of any available
 * facilities.
 * </p>
 */
public class OperationQuery extends BaseSymbolQuery<OperationEntry> {

    public OperationQuery(PosSymbol qualifier, PosSymbol name,
            List<PTType> argumentTypes) {
        super(new PossiblyQualifiedPath(qualifier, ImportStrategy.IMPORT_NAMED,
                FacilityStrategy.FACILITY_INSTANTIATE, false),
                new OperationSearcher(name, argumentTypes));
    }
}

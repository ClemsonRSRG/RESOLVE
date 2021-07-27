/*
 * ReductionTreeExporter.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.sequents.reductiontree;

import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

/**
 * <p>
 * A common interface that allows the {@code Sequent Reduction Tree} to be
 * exported as a
 * {@link String}. The format of the output is implementation dependent.
 * </p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public interface ReductionTreeExporter {

    /**
     * <p>
     * This method returns the {@code reductionTree} as a string.
     * </p>
     *
     * @param reductionTree A {@link Graph} representing a reduction tree.
     *
     * @return A string.
     */
    String output(Graph<Sequent, DefaultEdge> reductionTree);

}

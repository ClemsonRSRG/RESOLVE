/*
 * ReductionTreeDotExporter.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.vcgeneration.sequents.reductiontree;

import edu.clemson.rsrg.vcgeneration.sequents.Sequent;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.nio.*;
import org.jgrapht.nio.dot.*;
import org.jgrapht.graph.DefaultEdge;

/**
 * <p>
 * This class outputs the {@code Sequent Reduction Tree} as a to be exported as a {@code DOT file}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class ReductionTreeDotExporter implements ReductionTreeExporter {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A {@link DOTExporter} object.
     * </p>
     */
    private final DOTExporter<Sequent, DefaultEdge> myDotExporter;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This construct an object that will handle all the necessary steps to output our {@code Sequent Reduction Tree}
     * using the {@link DOTExporter}.
     * </p>
     */
    public ReductionTreeDotExporter() {
        myDotExporter = new DOTExporter<>();
        myDotExporter.setVertexAttributeProvider(v -> {
            Map<String, Attribute> attributesMap = new LinkedHashMap<>();

            // Add the attributes for the sequent to the map
            attributesMap.put("label", DefaultAttribute.createAttribute(v.toString()));
            attributesMap.put("shape", DefaultAttribute.createAttribute("box"));
            if (v.consistOfAtomicFormulas()) {
                attributesMap.put("color", DefaultAttribute.createAttribute("red"));
            }

            return attributesMap;
        });
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the {@code reductionTree} as a string.
     * </p>
     *
     * @param reductionTree
     *            A {@link Graph} representing a reduction tree.
     *
     * @return A string.
     */
    @Override
    public final String output(Graph<Sequent, DefaultEdge> reductionTree) {
        // Output the reduction tree as a dot file to the step model
        StringWriter writer = new StringWriter();
        myDotExporter.exportGraph(reductionTree, writer);

        return writer.toString();
    }

}
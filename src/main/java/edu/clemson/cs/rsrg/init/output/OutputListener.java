/*
 * OutputListener.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.init.output;

import edu.clemson.cs.r2jt.rewriteprover.Metrics;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.astoutput.GenerateGraphvizModel;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import edu.clemson.cs.rsrg.vcgeneration.vcs.AssertiveCodeBlock;
import java.util.List;

/**
 * <p>A listener that contains methods for retrieving compilation
 * results from the compiler.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public interface OutputListener {

    /**
     * <p>This method outputs the provided {@code Graphviz} model generated
     * from the {@link GenerateGraphvizModel}.</p>
     *
     * @param dec The module declaration we are currently processing.
     * @param graphvizModel The inner {@code AST} represented in a {@code GraphViz}
     *                      file format.
     */
    void astGraphvizModelResult(ModuleDec dec, String graphvizModel);

    /**
     * <p>This method outputs the provided the java translation results
     * from the {@code JavaTranslator}.</p>
     *
     * @param dec The module declaration we are currently processing.
     * @param javaTranslation The translated {@code Java} source code.
     */
    void javaTranslationResult(ModuleDec dec, String javaTranslation);

    /**
     * <p>This method outputs the provided results
     * from the {@code CCProver}.</p>
     *
     * @param dec The module declaration we are currently processing.
     */
    void proverResult(ModuleDec dec);

    /**
     * <p>This method outputs the provided {@link AssertiveCodeBlock AssertiveCodeBlocks}
     * from the {@link VCGenerator}.</p>
     *
     * @param dec The module declaration we are currently processing.
     * @param blocks A list of final {@link AssertiveCodeBlock AssertiveCodeBlocks}.
     */
    void vcGeneratorResult(ModuleDec dec, List<AssertiveCodeBlock> blocks);

    /**
     * <p>This method outputs the prover results for a given {@code VC}.</p>
     *
     * @param proved {@code true} if the {@code VC} was proved,
     *               {@code false} otherwise.
     * @param finalModel The prover representation for a {@code VC}.
     * @param m The prover generated metrics.
     */
    void vcResult(boolean proved, PerVCProverModel finalModel, Metrics m);

}
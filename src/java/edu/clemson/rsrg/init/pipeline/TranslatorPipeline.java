/*
 * TranslatorPipeline.java
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
package edu.clemson.rsrg.init.pipeline;

import edu.clemson.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.init.ResolveCompiler;
import edu.clemson.rsrg.init.output.OutputListener;
import edu.clemson.rsrg.statushandling.StatusHandler;
import edu.clemson.rsrg.translation.AbstractTranslator;
import edu.clemson.rsrg.translation.targets.CTranslator;
import edu.clemson.rsrg.translation.targets.JavaTranslator;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;

/**
 * <p>
 * This is pipeline that uses the RESOLVE AST and symbol table and translates to the various supported target languages.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class TranslatorPipeline extends AbstractPipeline {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This generates a pipeline to translate a file to the various supported target languages.
     * </p>
     *
     * @param ce
     *            The current compilation environment.
     * @param symbolTable
     *            The symbol table.
     */
    public TranslatorPipeline(CompileEnvironment ce, MathSymbolTableBuilder symbolTable) {
        super(ce, symbolTable);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final void process(ModuleIdentifier currentTarget) {
        ModuleDec moduleDec = myCompileEnvironment.getModuleAST(currentTarget);
        StatusHandler statusHandler = myCompileEnvironment.getStatusHandler();

        // Check to see if the file is on the no translate list
        if (!AbstractTranslator.onNoTranslateList(currentTarget)) {
            // Check to see if we are translating to Java
            boolean isJavaTranslateFlagOn = myCompileEnvironment.flags.isFlagSet(JavaTranslator.JAVA_FLAG_TRANSLATE)
                    || myCompileEnvironment.flags.isFlagSet(JavaTranslator.JAVA_FLAG_TRANSLATE_CLEAN);
            if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                String messageString = "\n---------------Begin Translation---------------\n\n"
                        + targetLanguageMessage(moduleDec.getName().getName(), isJavaTranslateFlagOn);

                statusHandler.info(null, messageString);
            }

            // Create the appropriate translator
            AbstractTranslator translator;
            if (isJavaTranslateFlagOn) {
                translator = new JavaTranslator(mySymbolTable, myCompileEnvironment);
            } else {
                translator = new CTranslator(mySymbolTable, myCompileEnvironment);
            }

            // Walk the AST and translate into the appropriate target source file
            TreeWalker.visit(translator, moduleDec);

            // Output the contents to listener objects
            for (OutputListener listener : myCompileEnvironment.getOutputListeners()) {
                if (isJavaTranslateFlagOn) {
                    listener.javaTranslationResult(myCompileEnvironment.getFile(currentTarget).toString(),
                            moduleDec.getName().getName(), translator.getOutputCode());
                } else {
                    listener.cTranslationResult(myCompileEnvironment.getFile(currentTarget).toString(),
                            moduleDec.getName().getName(), translator.getOutputCode());
                }
            }

            if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                String messageString = "Done "
                        + targetLanguageMessage(moduleDec.getName().getName(), isJavaTranslateFlagOn)
                        + "\n---------------End Translation---------------\n";

                statusHandler.info(null, messageString);
            }
        } else {
            // Skip translating this module.
            if (myCompileEnvironment.flags.isFlagSet(ResolveCompiler.FLAG_DEBUG)) {
                String messageString = "Skipping Translating Module: " + moduleDec.getName().getName() + "\n";

                statusHandler.info(null, messageString);
            }
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method that outputs the appropriate string message that indicates the source module name and the target
     * language we are translating to.
     * </p>
     *
     * @param moduleName
     *            Name of the module we are translating.
     * @param isJavaTranslateFlagOn
     *            A flag that indicates whether or not we are translating to {@code Java}.
     *
     * @return A message string.
     */
    private String targetLanguageMessage(String moduleName, boolean isJavaTranslateFlagOn) {
        StringBuilder sb = new StringBuilder();

        sb.append("Translating: ");
        sb.append(moduleName);
        sb.append(" to ");
        if (isJavaTranslateFlagOn) {
            sb.append("Java");
        } else {
            sb.append("C");
        }

        return sb.toString();
    }

    // TODO : See if there is a simpler, less verbose way of writing
    // the next three methods. And also try to get them into the abstract
    // translator.
    /*
     * private boolean needToTranslate(File file) { boolean translate = false; String inFile = file.toString(); String[]
     * temp = inFile.split("\\."); String ext = temp[temp.length - 1]; if (!onNoCompileList(file)) { if
     * (ext.equals("co") || ext.equals("rb") || ext.equals("en") || ext.equals("fa")) { String javaName =
     * modifyString(inFile, "\\." + ext, ".java"); File javaFile = new File(javaName); if (!javaFile.exists() ||
     * sourceNewerThan(file, javaFile)) { translate = true; } else if (myInstanceEnvironment.flags
     * .isFlagSet(JAVA_FLAG_TRANSLATE_CLEAN)) { translate = true; } } } return translate; }
     *
     * private String modifyString(String src, String find, String replace) { Pattern pattern = Pattern.compile(find);
     * Matcher matcher = pattern.matcher(src); return matcher.replaceAll(replace); }
     *
     * private boolean sourceNewerThan(File a, File b) { if (a.lastModified() > b.lastModified()) { return true; }
     * return false; }
     */

}

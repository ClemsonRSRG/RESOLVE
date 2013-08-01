package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.translation.bookkeeping.*;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.absyn.*;

public class JavaTranslator extends AbstractTranslator {

    private static final String FLAG_SECTION_NAME = "Translation";
    private static final String FLAG_DESC_TRANSLATE =
            "Translate RESOLVE file to Java source file.";
    private static final String FLAG_DESC_TRANSLATE_CLEAN =
            "Regenerates Java code for all supporting RESOLVE files.";

    public static final Flag JAVA_FLAG_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "javaTranslate", FLAG_DESC_TRANSLATE);

    public static final Flag JAVA_FLAG_TRANSLATE_CLEAN =
            new Flag(FLAG_SECTION_NAME, "javaTranslateClean",
                    FLAG_DESC_TRANSLATE_CLEAN);

    public JavaTranslator(CompileEnvironment env, ModuleScope scope,
            ModuleDec dec, ErrorHandler err) {

        super(env, scope, dec, err);
        myQualSymbol = ".";
    }

    // -----------------------------------------------------------
    //   Visitor methods
    // -----------------------------------------------------------

    @Override
    public void preModuleDec(ModuleDec node) {
        JavaTranslator.emitDebug("-----------------------------\n"
                + "Translate module: " + node.getName().getName()
                + "\n-----------------------------");

        String moduleName = node.getName().toString();

        if (node instanceof FacilityModuleDec) {
            myBookkeeper = new JavaFacilityBookkeeper(moduleName, true);
        }
    }
}

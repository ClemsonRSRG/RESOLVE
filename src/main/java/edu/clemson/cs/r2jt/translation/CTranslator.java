package edu.clemson.cs.r2jt.translation;

import java.io.File;

import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.translation.bookkeeping.*;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.absyn.*;

/**
 *
 * @author Welch D and Mark T
 */
public class CTranslator extends AbstractTranslator {

    private static final String FLAG_SECTION_NAME = "C Translation";
    private static final String FLAG_DESC_TRANSLATE =
            "Translate RESOLVE file to C source file.";
    private static final String FLAG_DESC_TRANSLATE_CLEAN =
            "Regenerates C code for all supporting RESOLVE files.";

    public static final Flag C_FLAG_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "cTranslate", FLAG_DESC_TRANSLATE);

    public static final Flag C_FLAG_TRANSLATE_CLEAN =
            new Flag(FLAG_SECTION_NAME, "cTranslateClean",
                    FLAG_DESC_TRANSLATE_CLEAN);

    public CTranslator(CompileEnvironment env, ModuleScope scope,
            ModuleDec dec, ErrorHandler err) {
        super(env, scope, dec, err);
        qualifyingSymbol = "->";
        File srcFile = dec.getName().getFile();
    }

    /* Visitor Methods */

    public void preModuleDec(ModuleDec dec) {
        if (dec instanceof FacilityModuleDec) {
            String facName = dec.getName().toString();
            myBookkeeper = new CFacilityBookkeeper(facName, true);
        }
    }

    @Override
    public void preVarDec(VarDec dec) {
        String varName = dec.getName().getName();
        String varType = ((NameTy) dec.getTy()).toString(0);

        String facName = getTypeFacility((NameTy) dec.getTy());
        //String conceptName = getTypeConceptName(facName);
        myBookkeeper.fxnAddVarDecl("r_type_ptr " + varName + " = " + facName
                + qualifyingSymbol + varType + qualifyingSymbol + "init("
                + facName + qualifyingSymbol + varType + ");");
    }

    @Override
    public void preParameterVarDec(ParameterVarDec dec) {
        String varName = dec.getName().getName();

        String facName = getTypeFacility((NameTy) dec.getTy());
        myBookkeeper.fxnAddParam("r_type_ptr " + varName);
    }

    @Override
    public void postProcedureDecParameters(ProcedureDec dec) {
        int i = 0;
        ResolveConceptualElement a = this.getParent();
        ModuleDec myMod = (ModuleDec) a;
        String b;
        for (; i < this.getAncestorSize(); i++) {
            a = this.getAncestor(i);
            if (a instanceof ModuleDec) {
                break;
            }
        }
        myBookkeeper.fxnAddParam(", " + getConceptName(myMod) + " _thisFac");
    }

    @Override
    public void postFacilityOperationDecParameters(FacilityOperationDec dec) {
    //may need you later, shhhh
    }

    // Helper Functions
    protected String getConceptName(ModuleDec dec) {
        if (dec instanceof ConceptBodyModuleDec) {
            return ((ConceptBodyModuleDec) dec).getConceptName().getName();
        }
        else if (dec instanceof EnhancementBodyModuleDec) {
            return ((EnhancementBodyModuleDec) dec).getConceptName().getName();
        }
        else if (dec instanceof ConceptModuleDec) {
            return ((ConceptModuleDec) dec).getName().getName();
        }
        else if (dec instanceof EnhancementModuleDec) {
            return ((EnhancementModuleDec) dec).getConceptName().getName();
        }
        else {
            return "FIND PROPER ERROR TO THROW";
        }
    }
}
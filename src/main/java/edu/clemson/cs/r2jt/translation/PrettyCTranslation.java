/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.absyn.FacilityModuleDec;
import edu.clemson.cs.r2jt.absyn.FacilityOperationDec;
import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.NameTy;
import edu.clemson.cs.r2jt.absyn.ParameterVarDec;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.scope.SymbolTable;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.utilities.Flag;

/**
 *
 * @author Mark T
 */
public class PrettyCTranslation extends TreeWalkerStackVisitor {

    /*
     * Variable Declaration
     */

    PrettyCTranslationInfo cInfo;
    private final CompileEnvironment env;
    private ErrorHandler err;
    private String targetFileName;
    private SymbolTable table;

    //Flags
    private static final String FLAG_SECTION_NAME = "Pretty C Translation";

    private static final String FLAG_DESC_TRANSLATE =
            "Translates into "
                    + "a \"Pretty\" C version following the line numbers of the "
                    + "RESOLVE Facility.";
    public static final Flag FLAG_PRETTY_C_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "prettyctranslate", FLAG_DESC_TRANSLATE);

    //Global stmt buf
    StringBuffer stmtBuf;

    /*
     * End of Variable Declaration
     */

    public PrettyCTranslation(CompileEnvironment env, SymbolTable table,
            ModuleDec dec, ErrorHandler err) {
        this.err = err;
        this.env = env;
        this.table = table;
        targetFileName = dec.getName().getFile().getName();
        cInfo = new PrettyCTranslationInfo(dec.getName().getFile().getName());
        stmtBuf = new StringBuffer();
    }

    /*
     * Visitor Methods
     */

    @Override
    public void preModuleDec(ModuleDec dec) {
    //Stuff done before start of dec trees
    }

    @Override
    public void postModuleDec(ModuleDec dec) {
    //Stuff done after end of dec trees
    }

    @Override
    public void preFacilityModuleDec(FacilityModuleDec dec) {
        int a = 0;
    }

    @Override
    public void postFacilityModuleDec(FacilityModuleDec dec) {
        System.out.println("Debug");
    }

    @Override
    public void preFacilityOperationDec(FacilityOperationDec dec) {
        cInfo.addFunction(dec.getName());

    }

    @Override
    public void preParameterVarDec(ParameterVarDec dec) {
        StringBuilder parmSet = new StringBuilder();
        if (dec.getTy() instanceof NameTy) {
            parmSet.append(cInfo
                    .stringFromSym(((NameTy) dec.getTy()).getName()));
        }
        else {
            System.out.println("How did you reach here?");
        }
        parmSet.append(cInfo.stringFromSym(dec.getName()));
        cInfo.addParamToFunc(parmSet.toString());
    }

    /*
     * End of Visitor Methods
     */

    public static final void setUpFlags() {

    }

}

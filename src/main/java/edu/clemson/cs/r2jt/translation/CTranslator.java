package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.utilities.Flag;

import java.io.File;

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

    public CTranslator(CompileEnvironment environment, ModuleScope scope,
            ModuleDec dec) {
        super(environment, scope, dec);
        myQualifierSymbol = "->";
        File srcFile = dec.getName().getFile();
    }

    /* Visitor Methods */

    /* Visit the DECS!!!! */
    /*  public void preModuleDec(ModuleDec dec) {
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
                  + qualSymbol + varType + qualSymbol + "init(" + facName
                  + qualSymbol + varType + ");");
      }

      //Operation/Procedures
      @Override
      public void preParameterVarDec(ParameterVarDec dec) {
          String varName = dec.getName().getName();

          //String facName = getTypeFacility((NameTy) dec.getTy());
          //I don't need type information >,>
          myBookkeeper.fxnAddParam("r_type_ptr " + varName);
      }

      @Override
      public void postProcedureDecParameters(ProcedureDec dec) {
          int i = 0;
          ResolveConceptualElement a;
          String b;
          a = getAncestorMatchingClass(dec, ModuleDec.class);
          ModuleDec myMod = (ModuleDec) a;
          myBookkeeper.fxnAddParam(", " + getConceptName(myMod) + " _thisFac");
      }

      @Override
      public void postFacilityOperationDecParameters(FacilityOperationDec dec) {
      //may need you later, shhhh
      }

      //Facility Decs
      @Override
      public void preModuleArgumentItem(ModuleArgumentItem item) {
          String param = "";
          if (item.getName() == null) {
              if (item.getEvalExp() instanceof ProgramIntegerExp) {
                  param =
                          "Std_Integer_Fac->CreateIntFromConstant("
                                  + item.getEvalExp().toString() + ")";
              }
              else if (item.getEvalExp() instanceof ProgramCharExp) {
                  param =
                          "Std_Character_Fac->CreateCharFromConstant('"
                                  + item.getEvalExp().toString() + "')";
              }
              if (myBookkeeper.facEnhanceIsOpen()) {
                  myBookkeeper.facAddEnhanceParam(param);
              }
              else {
                  myBookkeeper.facAddParam(param);
              }
          }
          else {
              super.preModuleArgumentItem(item);
          }
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
              throw new RuntimeException("Module does not have a Concept: "
                      + dec.toString());
          }
      }

      protected ResolveConceptualElement getAncestorMatchingClass(
              ResolveConceptualElement element, Class ancClass) {
          ResolveConceptualElement a;
          for (int i = 0; i < this.getAncestorSize(); i++) {
              a = this.getAncestor(i);
              if (ancClass.isInstance(a)) {
                  return a;
              }
          }
          return null;
      }*/

    public static final void setUpFlags() {

    // TODO:         Check Prover to see the correct way to do this using
    //                         HwS's FlagDependencies system.
    }
}

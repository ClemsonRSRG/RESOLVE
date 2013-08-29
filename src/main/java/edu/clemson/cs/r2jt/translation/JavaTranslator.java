package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.translation.bookkeeping.*;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.NoSuchSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTVoid;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;

/**
 * 
 * @author Welchd
 */

public class JavaTranslator extends AbstractTranslator {

    private static final String FLAG_SECTION_NAME = "Translation";

    private static final String FLAG_DESC_TRANSLATE =
            "Translate a RESOLVE file to Java source file.";

    private static final String FLAG_DESC_TRANSLATE_CLEAN =
            "Regenerates Java code for all supporting RESOLVE files.";

    /**
     * <p>The Java Translator flag. Specifies that Java should be 
     * used as the compiler's target language.</p>
     */
    public static final Flag JAVA_FLAG_TRANSLATE =
            new Flag(FLAG_SECTION_NAME, "javaTranslate", FLAG_DESC_TRANSLATE);

    public static final Flag JAVA_FLAG_TRANSLATE_CLEAN =
            new Flag(FLAG_SECTION_NAME, "javaTranslateClean",
                    FLAG_DESC_TRANSLATE_CLEAN);

    public JavaTranslator(CompileEnvironment environment, ModuleScope scope,
            ModuleDec dec, ErrorHandler err) {

        super(environment, scope, dec, err);
        myQualifierSymbol = ".";
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
        else if (node instanceof ConceptModuleDec) {
            myBookkeeper = new JavaConceptBookkeeper(moduleName, false);
        }
    }

    @Override
    public void preModuleArgumentItem(ModuleArgumentItem node) {

        String parameter, qualifier;
        PTType type = node.getProgramTypeValue();

        // Argument is an operation - so build a wrapped parameter.
        if (type instanceof PTVoid) {
            parameter =
                    buildOperationParameter(node.getName().getName(), node
                            .getQualifier());
        }

        // Argument is an EvalExp - typically an integer in {0 ... n}.
        else if (node.getEvalExp() != null) {
            parameter =
                    getDefiningFacilityEntry(type).getName() + ".create"
                            + node.getProgramTypeValue().toString() + "("
                            + node.getEvalExp().toString() + ")";

            JavaTranslator.emitDebug("\t"
                    + node.getEvalExp().toString()
                    + " -> "
                    + myModuleFormalParameters
                            .get(node.getEvalExp().toString()).getName());
        }
        else {
            parameter =
                    getDefiningFacilityEntry(type).getName() + ".create"
                            + node.getProgramTypeValue() + "()";
            JavaTranslator.emitDebug("\t"
                    + node.getName().getName()
                    + " -> "
                    + myModuleFormalParameters.get(node.getName().getName())
                            .getName());
        }

        // Finally, add the built parameter to the appropriate book.
        if (myBookkeeper.facEnhancementIsOpen()) {
            myBookkeeper.facAddEnhancementParameter(parameter);
            JavaTranslator.emitDebug("Adding enhancement parameter");
        }
        else {
            myBookkeeper.facAddParameter(parameter);
            JavaTranslator.emitDebug("Adding facility parameter");
        }
    }

    // -----------------------------------------------------------
    //   Helper methods
    // -----------------------------------------------------------

    private String buildOperationParameter(String argument, PosSymbol qualifier) {
        StringBuilder parameter = new StringBuilder();

        try {
            // TODO	:	For some reason NameAndEntryTypeQueries are
            //			bombing. See: 		
            //			https://www.pivotaltracker.com/story/show/55770154
            //			Ideally here we would want to use a query that
            //			looks specifically at "OperationEntries", a name, 
            //			and nothing else. However, until I'm able to figure
            //			out exacty what's going wrong and fix it, regular 
            //			NameQueries will have to suffice. So expect unexpected
            //			behavior here until that point.

            // First find the OperationEntry corresponding to the actual op.
            OperationEntry actualOp =
                    myModuleScope.queryForOne(
                            new NameQuery(qualifier, argument,
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false)).toOperationEntry(null);

            // Now grab the op corresponding to the formal parameter.
            OperationEntry formalOp =
                    myModuleFormalParameters.get(argument).toOperationEntry(
                            null);

            parameter.append("new ").append(
                    formalOp.getSourceModuleIdentifier().toString())
                    .append(".").append(formalOp.getName()).append("() {")
                    .append("public void ").append(formalOp.getName()).append(
                            "(");

            // |formalOp.getParameters()| = |actualOp.getParameters()|.
            int parameterCount = 0;
            int incomingLength = parameter.length();

            for (ProgramParameterEntry p : formalOp.getParameters()) {

                // Keep comma from proceeding final parameter.
                if (parameter.length() != incomingLength) {
                    parameter.append(", ");
                }

                if (p.getDeclaredType() instanceof PTGeneric) {
                    parameter.append("RType ").append("p").append(
                            parameterCount);
                }
                else {
                    parameter.append(p.getDeclaredType().toString()).append(
                            " p");
                }

                parameter.append(") {").append(qualifier.getName()).append(".")
                        .append(argument).append("(");

                incomingLength = parameter.length();
                for (ProgramParameterEntry pe : actualOp.getParameters()) {

                    if (parameter.length() != incomingLength) {
                        parameter.append(", ");
                    }

                    // From "Std_Integer_Fac", we want: "Integer_Template".
                    String facilitySpec =
                            getDefiningFacilityEntry(pe.getDeclaredType())
                                    .getFacility().getSpecification()
                                    .getModuleIdentifier().toString();

                    parameter.append("(").append(facilitySpec).append(".")
                            .append(pe.getDeclaredType().toString()).append(
                                    ") ").append("p").append(parameterCount);
                }
                parameterCount++;
                parameter.append(");");
                parameter.append("}}");
            }

        }
        catch (NoSuchSymbolException nsse) {
            noSuchSymbol(qualifier, argument, qualifier.getLocation());
            throw new RuntimeException();
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
        return parameter.toString();
    }
}
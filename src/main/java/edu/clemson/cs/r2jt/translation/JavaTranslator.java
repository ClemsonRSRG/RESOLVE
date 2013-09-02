package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.typeandpopulate.ModuleScope;
import edu.clemson.cs.r2jt.translation.bookkeeping.*;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.EntryTypeQuery;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleParameterization;
import edu.clemson.cs.r2jt.typeandpopulate.NoSuchSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTVoid;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;
import java.util.LinkedList;
import java.util.List;

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
                + "Translate [Java]: " + node.getName().getName()
                + "\n-----------------------------");

        String moduleName = node.getName().toString();

        if (node instanceof FacilityModuleDec) {
            myBookkeeper = new JavaFacilityBookkeeper(moduleName, true);
        }
        else if (node instanceof ConceptModuleDec) {
            myBookkeeper = new JavaConceptBookkeeper(moduleName, false);
        }
    }

    /**
     * <p>This isn't in <code>AbstractTranslator</code> since Java 
     * translation requires that calls to operations derived from
     * facility enhancements be specially qualified. Since this
     * special case doesn't apply to C, the separation seems necessary.
     * 
     * Note <code>preCallStmt</code> in {@link CTranslator CTranslator} 
     * still needs to qualify calls so qualification finding methods
     * are still found in the <code>AbstractTranslator</code>.</p>
     */
    @Override
    public void preCallStmt(CallStmt node) {

        String qualifier;
        StringBuilder enhancementQualifier = new StringBuilder();
        try {
            // For instance, if we see a call to "Read" and our
            // facility is enhanced with "Reading_Capability", 
            // AND there is more than a single enhancement, we
            // need to qualify the call to Read specially.
            if (isCallFromEnhancement(node.getName().getName(),
                    enhancementQualifier)) {
                qualifier = enhancementQualifier.toString();
            }
            // Otherwise, we are dealing with a regular call to an 
            // operation defined in a concept. Yet it looks like the
            // user saw fit to not qualify it. So lets try to find it.
            else if (node.getQualifier() == null) {
                qualifier = getIntendedCallQualifier(node);
            }
            else {
                qualifier = node.getQualifier().getName();
            }
            myBookkeeper.fxnAppendTo(qualifier + myQualifierSymbol
                    + node.getName().getName() + "(");
        }
        catch (SourceErrorException see) {
            ambiguousCall(node.getName());
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

            JavaTranslator.emitDebug("\t"
                    + node.getName().getName()
                    + " -> "
                    + myModuleFormalParameters.get(node.getName().getName())
                            .getName());
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
                            + node.getProgramTypeValue().toString() + "()";
            JavaTranslator.emitDebug("\t"
                    + node.getName().getName()
                    + " -> "
                    + myModuleFormalParameters.get(node.getName().getName())
                            .getName());
        }

        // Finally, add the built parameter to the appropriate book.
        if (myBookkeeper.facEnhancementIsOpen()) {
            myBookkeeper.facAddEnhancementParameter(parameter);
        }
        else {
            myBookkeeper.facAddParameter(parameter);
        }
    }

    @Override
    public void postModuleDec(ModuleDec node) {
        JavaTranslator.emitDebug("-----------------------------\n"
                + "End translate: " + node.getName().getName()
                + "\n-----------------------------");
    }

    // -----------------------------------------------------------
    //   Helper methods
    // -----------------------------------------------------------

    private boolean isCallFromEnhancement(String callName,
            StringBuilder qualifier) {

        List<ModuleArgumentItem> allArgs = new LinkedList<ModuleArgumentItem>();

        List<FacilityEntry> facilities =
                myModuleScope.query(new EntryTypeQuery(FacilityEntry.class,
                        ImportStrategy.IMPORT_NAMED,
                        FacilityStrategy.FACILITY_IGNORE));

        for (FacilityEntry f : facilities) {

            for (ModuleParameterization p : f.getEnhancements()) {

                allArgs.addAll(p.getParameters());
                allArgs.addAll(f.getEnhancementRealization(p).getParameters());

                for (ModuleArgumentItem i : allArgs) {
                    if (i.getName().getName().equals(callName)) {
                        // If there is more than one enhancement, we
                        if (f.getEnhancements().size() > 1) {
                            qualifier.append("((").append(
                                    p.getModuleIdentifier().toString()).append(
                                    ")").append(f.getName()).append(")");
                        }
                        else {
                            // Else - only one enhancement. Just use the 
                            // facility's name.
                            qualifier.append(f.getName());
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String buildOperationParameter(String argument, PosSymbol qualifier) {
        StringBuilder parameter = new StringBuilder();

        /**
         * TODO	: For some reason NameAndEntryTypeQueries are
         * bombing. See: 
         * {@Link https://www.pivotaltracker.com/story/show/55770154}
         * Ideally here we would use a query that looks 
         * at both name and entry-type (i.e. NameAndEntryTypeQuery)
         * but unfortunately it's not working. Until I'm able to 
         * figure would what exactly is going wrong and fix it, 
         * regular NameQueries will have to suffice. So expect 
         * the unexpected here until then.
         */
        try {

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
        }
        catch (DuplicateSymbolException dse) {
            throw new RuntimeException(dse);
        }
        return parameter.toString();
    }
}
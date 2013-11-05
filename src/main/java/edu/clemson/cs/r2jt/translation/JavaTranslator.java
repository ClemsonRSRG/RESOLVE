package edu.clemson.cs.r2jt.translation;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.translation.bookkeeping.JavaConceptBookkeeper;
import edu.clemson.cs.r2jt.translation.bookkeeping.JavaFacilityBookkeeper;
import edu.clemson.cs.r2jt.typeandpopulate.*;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTGeneric;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTVoid;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.query.NameQuery;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.util.LinkedList;
import java.util.List;

public class JavaTranslator extends AbstractTranslator {

    private static final String FLAG_SECTION_NAME = "Java Translation";

    private static final String FLAG_DESC_TRANSLATE =
            "Translate RESOLVE code to Java.";

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
            ModuleDec dec) {

        super(environment, scope, dec);
        myQualifierSymbol = ".";
    }

    //-------------------------------------------------------------------
    //   Visitor methods
    //-------------------------------------------------------------------

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
     * <p>Any conceptual parameters must be transformed into operations
     * and placed in the interface extending <code>RESOLVE_INTERFACE</code>.
     * We do this here using "fxn" <code>Bookkeeper</code> methods. None
     * of the if-statements should get tripped if the module being looked
     * at is anything other than a concept module. If they do, then we
     * are going to get excess operation declarations in the translated file
     * and this will have to be re-thought..</p>
     */
    @Override
    public void preModuleParameterDec(ModuleParameterDec node) {

        String operation = "";
        if (node.getWrappedDec() instanceof ConstantParamDec) {
            ConstantParamDec p = (ConstantParamDec) node.getWrappedDec();
            addOperationLikeThingToBookkeeper("get" + node.getName().getName(),
                    p.getTy(), null);
        }
        else if (node.getWrappedDec() instanceof ConceptTypeParamDec) {
            ConceptTypeParamDec p = (ConceptTypeParamDec) node.getWrappedDec();
            addOperationLikeThingToBookkeeper("getType"
                    + node.getName().getName(), null, "public RTYPE");
        }
    }

    @Override
    public void preTypeDec(TypeDec data) {
        myBookkeeper.addConstructor(data.getName().getName());
    }

    /**
     * <p>This isn't in <code>AbstractTranslator</code> since Java
     * translation requires that calls to operations derived from
     * facility enhancements be specially qualified. Since this
     * special case doesn't apply to C, the separation seems necessary.
     *
     * Note <code>preCallStmt</code> in {@link CTranslator CTranslator}
     * will need to qualify calls so qualification finding methods
     * are still found in the <code>AbstractTranslator</code>.</p>
     */
    @Override
    public void preCallStmt(CallStmt node) {

        String qualifier;
        StringBuilder enhancementQualifier = new StringBuilder();
        try {
            // If we see a call to "Read" and our facility is enhanced
            // with "Reading_Capability", AND there is more than a single
            // enhancement, we need to qualify the call to Read specially.
            if (isCallFromEnhancement(node.getName().getName(),
                    enhancementQualifier)) {

                qualifier = enhancementQualifier.toString();
            }
            // Otherwise, we are dealing with a regular call to an
            // operation defined in a concept. Yet it looks like the
            // user saw fit to not qualify it. So lets lend them a hand
            // try to find it for them -- erroring if things get hairy.
            else if (node.getQualifier() == null) {
                qualifier = getIntendedCallQualifier(node);
            }
            // Else, the user chose to qualify the call.
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

    /**
     * <p>Aside: It seems like this visitor method is responsible for
     * too much. It Handles parameters for not only EVERY KIND of module...
     * but also parameters to facility/facility enhancement specifications..
     * So take care if you need to add anything here as it might affect many
     * things.</p>
     */
    @Override
    public void preModuleArgumentItem(ModuleArgumentItem node) {

        String parameter;
        PTType type = node.getProgramTypeValue();

        // Argument is an operation -- so build a wrapped parameter.
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

    /**
     * This method returns <code>true</code> <strong>iff</strong>
     * <code>callName</code> matches an argument to a facility
     * enhancement. Additionally, to avoid code duplication, if
     * <code>true</code> is returned, this method takes the liberty
     * of mutating the initially empty stringBuilder parameter,
     * <code>qualifier</code>, into one that is appropriate for
     * qualifying an enhancement defined call.
     *
     * "Appropriate" simply means wrapping the normal qualifier
     * with the specificational name of the current enhancement.
     * For instance, in the case of two or more enhancements:
     *
     * <pre>
     * Facility SF is Stack_Template(..) realized by Array_Realiz
     *                 enhanced with Reading_Capability realized by
     *                         Obvious_Reading_Realiz(Std_Int_Fac.Read)
     *                 enhanced with Writing_Capability realized by
     *                         Obvious_Writing_Capability(Std_Int_Fac.Write);
     * </pre>
     *
     * a call to <code>Read</code> should ideally look something like,
     * <code>((Reading_Capability)SF).Read</code>. However, in the
     * case where there is only a single enhancement, this method
     * goes ahead simply makes the qualifier the base facility's
     * name, I.e.: <code>SF.Read</code>.
     *
     * @param callName
     * @param qualifier
     * @return A boolean indicating whether or not
     *                    <code>callName</code> matches a facility enhancement
     *                    argument.
     */
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

                    // Does callName match an enhancement argument
                    if (i.getName().getName().equals(callName)) {

                        // It does. Ok, is there more than one enhancement?
                        if (f.getEnhancements().size() > 1) {

                            // Yep, so lets wrap the qualifier appropriately.
                            qualifier.append("((").append(
                                    p.getModuleIdentifier().toString()).append(
                                    ")").append(f.getName()).append(")");
                        }
                        else {
                            // Looks like there's only one enhancement.
                            // Just use the base-facility's name.
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
         * TODO :   For some reason NameAndEntryTypeQueries are bombing. See:
         *          {@Link https://www.pivotaltracker.com/story/show/55770154}
         *          Ideally here we would use a query that looks at both name
         *          and entry-type (i.e. NameAndEntryTypeQuery) but unfortunately
         *          it's not working. Until I'm able to figure would what exactly
         *          is going wrong and fix it, regular NameQueries will have to
         *          suffice. So expect the unexpected here until then.
         */
        try {

            OperationEntry actualOp =
                    myModuleScope.queryForOne(
                            new NameQuery(qualifier, argument,
                                    ImportStrategy.IMPORT_NAMED,
                                    FacilityStrategy.FACILITY_INSTANTIATE,
                                    false)).toOperationEntry(null);

            // Now grab the actual op parameter's formal counterpart.
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

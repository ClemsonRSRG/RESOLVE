package edu.clemson.rsrg.init.pipeline;

import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;

public class ParameterCheckPipeline extends AbstractPipeline{
    /**
     * <p>
     * An helper constructor that allow us to store the {@link CompileEnvironment} and {@link MathSymbolTableBuilder}
     * from a class that inherits from {@code AbstractPipeline}.
     * </p>
     *
     * @param ce          The current compilation environment.
     * @param symbolTable The symbol table.
     */
    protected ParameterCheckPipeline(CompileEnvironment ce, MathSymbolTableBuilder symbolTable) {
        super(ce, symbolTable);
    }

    @Override
    public void process(ModuleIdentifier currentTarget) {

    }
}

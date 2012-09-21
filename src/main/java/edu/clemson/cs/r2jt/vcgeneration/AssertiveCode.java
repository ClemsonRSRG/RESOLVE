package edu.clemson.cs.r2jt.vcgeneration;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;

/**
 * TODO: Write a description of this module
 */
public class AssertiveCode {

    // ===========================================================
    // Global Variables 
    // ===========================================================

    // Compile Environment
    private CompileEnvironment myInstanceEnvironment;

    // Error Handler
    private ErrorHandler myErr;

    // Final Confirm Statement
    private Exp myConfirm;

    // ===========================================================
    // Constructors
    // ===========================================================

    public AssertiveCode(CompileEnvironment env) {
        myInstanceEnvironment = env;
        myConfirm = Exp.getTrueVarExp();
        myErr = env.getErrorHandler();
    }
}

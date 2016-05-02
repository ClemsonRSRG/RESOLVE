/**
 * BindingExpression.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typereasoning2;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST;
import edu.clemson.cs.r2jt.absynnew.expr.MathTupleAST;
import edu.clemson.cs.r2jt.typeandpopulate2.BindingException;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.TypeMismatchException;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BindingExpression {

    private final TypeGraph myTypeGraph;
    private ExprAST myExpression;

    public BindingExpression(TypeGraph g, ExprAST expression) {
        myExpression = expression;
        myTypeGraph = g;
    }

    public MTType getType() {
        return myExpression.getMathType();
    }

    public MTType getTypeValue() {
        return myExpression.getMathTypeValue();
    }

    public Map<String, ExprAST> bindTo(ExprAST expr,
            Map<String, MTType> typeBindings)
            throws TypeMismatchException,
                BindingException {

        Map<String, ExprAST> result = new HashMap<String, ExprAST>();

        bindTo(myExpression, expr, typeBindings, result);

        return result;
    }

    private MTType getTypeUnderBinding(MTType original,
            Map<String, MTType> typeBindings) {

        return original.getCopyWithVariablesSubstituted(typeBindings);
    }

    private void bindTo(ExprAST expr1, ExprAST expr2,
            Map<String, MTType> typeBindings, Map<String, ExprAST> accumulator)
            throws TypeMismatchException,
                BindingException {

        //TODO : Ultimately, in theory, one of the arguments THEMSELVES could 
        //       involve a reference to a named type.  We don't deal with that 
        //       case (only the case where the TYPE of the argument involves a 
        //       named type.)

        //Either type might actually be a named type that's already been mapped,
        //so perform the substitution if necessary
        MTType expr1Type =
                getTypeUnderBinding(expr1.getMathType(), typeBindings);
        MTType expr2Type =
                getTypeUnderBinding(expr2.getMathType(), typeBindings);

        if (!myTypeGraph.isSubtype(expr2Type, expr1Type)) {
            throw TypeMismatchException.INSTANCE;
        }

        if (expr1 instanceof MathSymbolAST) {
            MathSymbolAST e1AsVarExp = (MathSymbolAST) expr1;
            String e1Name = e1AsVarExp.getName().getText();

            if (e1AsVarExp.getQuantification() == SymbolTableEntry.Quantification.UNIVERSAL) {
                if (accumulator.containsKey(e1Name)) {
                    bindTo(accumulator.get(e1Name), expr2, typeBindings,
                            accumulator);
                }
                else {
                    accumulator.put(e1Name, expr2);
                }
            }
            else {
                if (expr2 instanceof MathSymbolAST) {
                    MathSymbolAST e2AsVarExp = (MathSymbolAST) expr2;

                    if (!e1Name.equals(e2AsVarExp.getName().getText())) {
                        throw new BindingException(expr1, expr2);
                    }
                }
                else {
                    throw new BindingException(expr1, expr2);
                }
            }
        }
        else if (expr1 instanceof MathSymbolAST
                && expr2 instanceof MathSymbolAST) {

            MathSymbolAST funExpr1 = (MathSymbolAST) expr1;
            String fun1Name = funExpr1.getName().getText();

            MathSymbolAST funExpr2 = (MathSymbolAST) expr2;

            if (funExpr1.getQuantification() == SymbolTableEntry.Quantification.UNIVERSAL) {
                if (accumulator.containsKey(fun1Name)) {
                    fun1Name =
                            ((MathSymbolAST) accumulator.get(fun1Name))
                                    .getName().getText();

                    if (!fun1Name.equals(funExpr2.getName().getText())) {
                        throw new BindingException(expr1, expr2);
                    }
                }
                else {
                    accumulator.put(fun1Name, expr2);

                    /*if (myTypeGraph.isSubtype(expr2Type, expr1Type)) {
                    	accumulator.put(fun1Name, expr2);
                    }
                    else {
                    	throw new TypeMismatchException(expr1.getMathType(), 
                    			expr2.getMathType());
                    }*/
                }
            }
            else {
                if (!fun1Name.equals(funExpr2.getName().getText())) {
                    throw new BindingException(expr1, expr2);
                }
            }

            /*if (!myTypeGraph.isSubtype(expr2Type, expr1Type)) {
            	throw new TypeMismatchException(expr1.getMathType(), 
            			expr2.getMathType());
            }*/

            Iterator<ExprAST> fun1Args = funExpr1.getArguments().iterator();
            Iterator<ExprAST> fun2Args = funExpr2.getArguments().iterator();

            //There must be the same number of parameters, otherwise the 
            //original typecheck would have failed
            while (fun1Args.hasNext()) {
                bindTo(fun1Args.next(), fun2Args.next(), typeBindings,
                        accumulator);
            }
        }
        else if (expr1 instanceof MathTupleAST) {

            MathTupleAST expr1AsTupleExp = (MathTupleAST) expr1;

            //TODO : Do we need to somehow "descend" (into what is in all 
            //likelihood a DummyExp) and match universal fields to sub 
            //components of expr2?

            //We checked earlier that it's a subtype.  So, if it's universally
            //quantified--we're done here.
            if (!expr1AsTupleExp.isUniversallyQuantified()) {
                Iterator<ExprAST> tuple1Fields =
                        ((MathTupleAST) expr1).getFields().iterator();
                Iterator<ExprAST> tuple2Fields =
                        ((MathTupleAST) expr2).getFields().iterator();

                //There must be the same number of fields, otherwise the above
                //typecheck would have failed
                while (tuple1Fields.hasNext()) {
                    bindTo(tuple1Fields.next(), tuple2Fields.next(),
                            typeBindings, accumulator);
                }
            }
        }
        /*else if (expr1 instanceof LambdaExp && expr2 instanceof LambdaExp) {
            LambdaExp expr1AsLambdaExp = (LambdaExp) expr1;
            LambdaExp expr2AsLambdaExp = (LambdaExp) expr2;

            //Note that we don't have to worry about parameters counts or types:
            //the original type check would have kicked us out if those didn't
            //match

            bindTo(expr1AsLambdaExp.getBody(), expr2AsLambdaExp.getBody(),
                    typeBindings, accumulator);

        }*/
        else {
            throw new BindingException(expr1, expr2);
        }
    }

    @Override
    public String toString() {
        return myExpression.toString();
    }
}

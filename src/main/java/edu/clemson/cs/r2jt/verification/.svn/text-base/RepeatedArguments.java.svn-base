package edu.clemson.cs.r2jt.verification;

import edu.clemson.cs.r2jt.absyn.*;

import java.io.*;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.type.ConcType;
import edu.clemson.cs.r2jt.type.TypeConverter;

public class RepeatedArguments {
	public boolean checkRepeatedArguments(CallStmt stmt, AssertiveCode assertive, OperationDec opDec) {
		System.out.println("");
		System.out.println("");
		boolean toReturn = false;
		List<ProgramExp> arguments = stmt.getArguments();
		List<VariableExp> variableExpressions = new List<VariableExp>();
		List<ProgramParamExp> programParamExpressions = new List<ProgramParamExp>();
		List<ParameterVarDec>test = opDec.getParameters();
		
		//This for loop helps me to see where things are in the OpDec so that I can figure out how to get
			//names and types of variables, this code should be deleted. there is no purpose to it.
		System.out.println("OpDec Parameters");
		TypeConverter converter = new TypeConverter(null);
		for(int i = 0; i<test.size(); i++){
			System.out.println(test.get(i).getName());
			System.out.println(test.get(i).getTy().asString(0,0));
			ConcType A = (ConcType)(converter.getConceptualType(test.get(i).getTy(), test.get(i).getName()));
			System.out.println(" ");
		}
		//Check to see what types of arguments there are...
		for(int count = 0; count<arguments.size(); count++){
			if(arguments.get(count)instanceof VariableExp){
				//System.out.println("VariableExp");
				variableExpressions.add((VariableExp)(arguments.get(count)));
			}else{
				if(arguments.get(count) instanceof ProgramParamExp){
					//System.out.println("ProgramParamExp");
					programParamExpressions.add((ProgramParamExp)(arguments.get(count)));
				}else{
					System.out.println("other...do something about it!");
				}
			}
		}
		
		//Now take each list of same type of expressions to compare likeness
		if(variableExpressions.size()>1){
			
			// note to self, as of right now only worrying about case where only a,a. not worrying about case with a,a,a (etc.)
			if(this.exactDuplicateCheck1(variableExpressions, opDec)){
				
				/*
				 * from here, p,p add ?p and ??p
				 * 
				 */
				//System.out.println("variable expressions match");
			}
		}
		if(programParamExpressions.size()>1){
			if(this.exactDuplicateCheck2(programParamExpressions)){
				//System.out.println("array expressions could match");
			}
		}
		if(programParamExpressions.size()>0 && variableExpressions.size()>0){
			if(this.differentVariableTypeExactCheck(programParamExpressions,variableExpressions)){
				//System.out.println("could have A, A[i]");
			}
		}
		
		


		return toReturn;
	}
private boolean differentVariableTypeExactCheck(
			List<ProgramParamExp> programParamExpressions,
			List<VariableExp> variableExpressions) {
		for(int a = 0; a<variableExpressions.size();a++){
			for(int b = 0; b<programParamExpressions.size();b++){
				//System.out.println("." + variableExpressions.get(a).toString()+".");
				//System.out.println("." + programParamExpressions.get(b).getName().toString()+".");
				if(differentTypeCompare1(programParamExpressions.get(b), variableExpressions.get(a))){
					//System.out.println("it is possible that it is a,a(i)");
					return true;
				}
				
			}
		}
		return false;
	}
private boolean differentTypeCompare1(ProgramParamExp programParamExp,
		VariableExp variableExp) {
	String paramExp = programParamExp.getName().toString();
	String varName = variableExp.toString();
	
	if(paramExp.equals(varName)){
		return true;
	}
	//System.out.println("not equal");
	return false;
}
private boolean exactDuplicateCheck2(List<ProgramParamExp> variableExpressions) {
	for(int a = 0; a<variableExpressions.size()-1;a++){
		for(int b = 1; b<variableExpressions.size();b++){
			//System.out.println("." +variableExpressions.get(a).toString()+".");
			//System.out.println("."+variableExpressions.get(b).toString()+".");
			if(paramCompare(variableExpressions.get(a),variableExpressions.get(b))==true){
				//System.out.println("Yes, I am herererererer");
				return true;
			}
		}
	}
	
	return false;
	}


	private boolean exactDuplicateCheck1(List<VariableExp> variableExpressions, OperationDec opDec){
		for(int a = 0; a<variableExpressions.size()-1;a++){
			for(int b = 1; b<variableExpressions.size();b++){
				//System.out.println("." +variableExpressions.get(a).toString()+".");
				//System.out.println("."+variableExpressions.get(b).toString()+".");
				if(variableCompare(variableExpressions.get(a),variableExpressions.get(b))==true){
					//here will make the ?P, ?Q and do the swaps
					
					//call new function
					applySwaps(variableExpressions.get(a),a,variableExpressions.get(b),b,opDec);
					
					
					System.out.println("Yes, they are equal");
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void applySwaps(VariableExp variableExp, int a,
			VariableExp variableExp2, int b, OperationDec opDec) {
		Exp exp;
		ConcType emily;
		//ConcType A = (ConcType)(converter.getConceptualType(test.get(i).getTy(), test.get(i).getName()));
		//ConcType test = Verifier.NQV((Exp)(variableExp), emily );
		
	}
	private boolean paramCompare(ProgramParamExp p1, ProgramParamExp p2){
		List<Exp> subExp1 = p1.getSubExpressions();
    	List<Exp> subExp2 = p2.getSubExpressions();
    	String varName1 = p2.getName().toString();
    	String varName2 = p1.getName().toString();
    	
    	String semantic = p1.getSemanticExp().toString();
    	//System.out.println("Semantic Exp:" + semantic);
    	List args = p1.getArguments();
    	for(int i = 0; i<args.size(); i++){
    		//System.out.println("args:" + args.get(i).toString());
    	}
    	

    	//System.out.println(subExp1.toString());
    	//System.out.println(subExp2.toString());
		
		//System.out.println("..." + varName1);
		//System.out.println(varName2);
    	//System.out.println(p1.toString());
    	
    	if(varName1.equals(varName2)){
    		//System.out.println("they are equal");
    		return true;
    	}
    	  	

    	
    	
	

			return false;
		
	}
	
	private boolean variableCompare(VariableExp variableExp, VariableExp variableExp2){
		List<Exp> subExp1 = variableExp.getSubExpressions();
    	List<Exp> subExp2 = variableExp2.getSubExpressions();
    	String varName1 = variableExp2.toString();
    	String varName2 = variableExp.toString();
    	
    	if( variableExp instanceof VariableNameExp){
    		//System.out.println(((VariableNameExp)variableExp).getName().toString());
    	}
    	//System.out.println(subExp1.toString());
    	//System.out.println(subExp2.toString());
		
		//System.out.println(varName1);
		//System.out.println(varName2);
    	//System.out.println(variableExp.toString());
    	
    	if(varName1.equals(varName2)){
    		//System.out.println("they are equal");
    		return true;
    	}
    	  	

    	
    	return false;
	}

}


package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.absyn.InfixExp;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

/**
 * @author Chuck
 * This is the built-in class for is_in statements. This statement
 * return type will be BooleanType
 *
 */
public class IsInType extends Type {
	
	private Type retType = BooleanType.INSTANCE;

    private PosSymbol name;

    private List<Type> args = new List<Type>();
	
	public IsInType(PosSymbol name, List<Type> args){
		this.name = name;
		this.args = args;
	}
	
	public void setArgs(List<Type> args){
		this.args = args;
	}
	
	public Type getRetType(InfixExp exp)
		throws TypeResolutionException{
    	//if(exp.getBType() instanceof IsInType){
        	//Type t1 = getMathExpType(exp.getLeft());
        	//Type t2 = getMathExpType(exp.getRight());
		return retType;
	}

	@Override
	public String asString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeName getProgramName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRelativeName(Location loc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type instantiate(ScopeID sid, Binding binding) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type toMath() {
		// TODO Auto-generated method stub
		return null;
	}
}

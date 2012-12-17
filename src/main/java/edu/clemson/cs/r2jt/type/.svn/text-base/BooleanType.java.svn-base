package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.Environment;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;

public class BooleanType extends Type {

	public static final BooleanType INSTANCE = new BooleanType();
	
    // ===========================================================
    // Variables
    // ===========================================================

    //private ModuleID id = ModuleID.createTheoryID(Symbol.symbol("Boolean"));

    private PosSymbol myQualifier = new PosSymbol();

    private PosSymbol myName = new PosSymbol();
    
	
	private BooleanType(){
		myQualifier.setSymbol(Symbol.symbol("Boolean"));
		myName.setSymbol(Symbol.symbol("B"));
	}

    // ===========================================================
    // Accessors
    // ===========================================================

    public PosSymbol getName() { return myName; }
	
    // ===========================================================
    // Public Methods
    // ===========================================================
  
    public Type instantiate(ScopeID sid, Binding replBind) {
    	return new BooleanType();
    }

    public Type toMath() {
        return new BooleanType();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("*");
        if (myQualifier != null) {
            sb.append(myQualifier.toString() + ".");
        }
        sb.append(myName.toString());
        return sb.toString();
    }
    public String asString() {
        StringBuffer sb = new StringBuffer();
    //    sb.append("*");
        if (myQualifier != null) {
            sb.append(myQualifier.toString() + ".");
        }
        sb.append(myName.toString());
        return sb.toString();
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
}

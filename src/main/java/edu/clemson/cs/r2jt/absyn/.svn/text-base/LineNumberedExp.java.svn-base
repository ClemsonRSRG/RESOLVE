package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.type.Type;

public abstract class LineNumberedExp extends Exp {

	protected PosSymbol myLineNumber;
	
	public LineNumberedExp(PosSymbol lineNumber) {
		myLineNumber = lineNumber;
	}
	
	/** Returns the line number for this expression. */
    public PosSymbol getLineNum() { 
    	return myLineNumber; 
    }
	
    /** Sets the line number for this expression. */
    public void setLineNum(PosSymbol lineNumber) { 
    	myLineNumber = lineNumber; 
    }
}

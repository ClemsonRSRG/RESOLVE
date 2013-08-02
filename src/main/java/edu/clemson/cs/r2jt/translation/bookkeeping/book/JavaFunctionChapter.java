package edu.clemson.cs.r2jt.translation.bookkeeping.book;

class JavaFunctionChapter extends AbstractBookDecorator {

	public JavaFunctionChapter(Book workingBook) {
        super(workingBook);
    }

	@Override
	public String getDescription() {
        return super.getDescription() + ", written in Java.";
    }

    /**
     * <p>Returns an unformatted (no newlines or tabs) Java string 
     * representation of <code>JavaFunctionBook</code>.</p>
     */
    @Override
    public String getString() {
        throw new UnsupportedOperationException("Not supported yet.");
     /*   StringBuilder finalFunc = new StringBuilder();
        finalFunc.append("public ").append(super.getReturnType()).append(" ");
        finalFunc.append(super.getName()).append("(");

		for (String param : super.getParameters()) {
			
		}
        for (int i = 0; i < myParameterList.size(); i++) {
            finalFunc.append(myParameterList.get(i));
            if (i != myParameterList.size() - 1) {
                finalFunc.append(", ");
            }
        }
        if (hasBody) {
            finalFunc.append(") {");

            for (String s : myVariableInitializationList) {
                finalFunc.append(s);
            }
            if (myStmt != null) {
                finalFunc.append(myStmt);
            }
            //  if (!returnType.equals("void ")) {
            //      finalFunc.append("return ").append(functionName);
            //  }
            finalFunc.append("}");
        }
        else {
            finalFunc.append(");");
        }
        return finalFunc.toString();*/
    }
}

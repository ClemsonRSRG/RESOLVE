package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

import java.util.List;

public interface FunctionApplicationFactory {

    public MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                                           String calledAsName,
                                           List<MTType> arguments);
}


package edu.clemson.cs.r2jt.mathtype;

import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public class MTPowertypeApplication extends MTFunctionApplication {

    public MTPowertypeApplication(TypeGraph g, MTType argument) {
        super(g, g.POWERTYPE, "Powerset", argument);
    }

    @Override
    public boolean isKnownToContainOnlyMTypes() {
        //The powertype is, by definition, a container of containers
        return true;
    }

    @Override
    public boolean membersKnownToContainOnlyMTypes() {
        //I'm the container of all sub-containers of my argument.  My members
        //are containers of members from the original argument.
        return getArgument(0).isKnownToContainOnlyMTypes();
    }

    @Override
    public void accept(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTFunctionApplication(this);
        v.beginMTPowertypeApplication(this);

        v.beginChildren(this);

        getFunction().accept(v);

        for (MTType arg : getArguments()) {
            arg.accept(v);
        }

        v.endChildren(this);

        v.endMTPowertypeApplication(this);
        v.endMTFunctionApplication(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        MTType result;

        switch (index) {
        case 0:
            result =
                    new MTFunctionApplication(getTypeGraph(),
                            (MTFunction) newType, getArguments());
            break;
        case 1:
            result = new MTPowertypeApplication(getTypeGraph(), newType);
            break;
        default:
            throw new IndexOutOfBoundsException("" + index);
        }

        return result;
    }

    /*@Override
    public boolean bindsTo(MTType type, Exp bindingExpr) {
    	return this.getArgument(0).bindsToWithCoercion(type, bindingExpr);
    }*/
}

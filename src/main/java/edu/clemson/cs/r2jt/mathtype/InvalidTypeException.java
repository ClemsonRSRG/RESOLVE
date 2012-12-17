package edu.clemson.cs.r2jt.mathtype;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.errors.ErrorHandler;

public class InvalidTypeException extends Exception {

    private ResolveConceptualElement myErrorNode;
    private String myErrorMessage;

    public InvalidTypeException() {
        myErrorNode = null;
        myErrorMessage = "Unknown math type error occurred.";
    }

    public InvalidTypeException(ResolveConceptualElement node, String msg) {
        myErrorNode = node;
        myErrorMessage = msg;
    }

    public ResolveConceptualElement getErrorNode() {
        return myErrorNode;
    }

    public String getErrorMessage() {
        return myErrorMessage;
    }

    private static ArrayList<Field> getClassMembers(Class<?> elementClass) {
        ArrayList<Field> fields = new ArrayList<Field>();
        Class<?> curClass = elementClass;
        while (curClass != ResolveConceptualElement.class) {
            Field[] curFields = curClass.getDeclaredFields();
            for (int i = 0; i < curFields.length; ++i) {
                fields.add(curFields[i]);
            }
            curClass = curClass.getSuperclass();
        }
        return fields;
    }
}

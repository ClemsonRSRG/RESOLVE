package edu.clemson.cs.r2jt.absyn;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.AsStringCapability;
import edu.clemson.cs.r2jt.data.Location;
import java.lang.reflect.ParameterizedType;

/**
 * <p>A <code>ResolveConceptualElement</code> represents the various elements
 * composing Resolve's <em>abstract syntax tree</em> (AST) at the most general,
 * highest level.</p>
 */
public abstract class ResolveConceptualElement implements AsStringCapability {

    /**
     * <p>Allows a <code>ResolveConceptualElement</code> to accept a
     * <code>ResolveConceptualVisitor</code> for treewalking purposes.</p>
     *
     * @param v The visitor.
     */
    public abstract void accept(ResolveConceptualVisitor v);

    /**
     * <p>Creates and returns a string representation of this
     * <code>ResolveConceptualElement</code>.</p>
     *
     * @param indent The desired default indentation level.
     * @param increment The amount of additional indentation to add on top of
     *                  the default.
     * @return The string representing this
     * 		   <code>ResolveConceptualElement</code>.
     */
    public abstract String asString(int indent, int increment);

    /**
     * <p>Returns a <code>Location</code> indicating the position of this
     * <code>ResolveConceptualElement</code> within a Resolve source file.</p>
     *
     * @return The <code>Location</code>.
     */
    public abstract Location getLocation();

    /**
     * <p>Mutates a buffer, <code>spaces</code>, into one containing
     * <code>n</code> consecutive blank spaces.</p>
     *
     * @param n The number of consecutive blank spaces.
     * @param spaces The whitespace string buffer.
     */
    protected void printSpace(int n, StringBuffer spaces) {
        for (int i = 0; i < n; ++i) {
            spaces.append(" ");
        }
    }

    public java.util.List<ResolveConceptualElement> getChildren() {

        //We'd like to hit the fields in the order they appear in the class,
        //starting with the most general class and getting more specific.  So,
        //we build a stack of the class hierarchy of this instance
        Deque<Class<?>> hierarchy = new LinkedList<Class<?>>();
        Class<?> curClass = this.getClass();
        do {
            hierarchy.push(curClass);
            curClass = curClass.getSuperclass();
        } while (curClass != ResolveConceptualElement.class);

        List<ResolveConceptualElement> children =
                new List<ResolveConceptualElement>();
        // get a list of all the declared and inherited members of that class
        ArrayList<Field> fields = new ArrayList<Field>();
        while (!hierarchy.isEmpty()) {

            curClass = hierarchy.pop();

            Field[] curFields = curClass.getDeclaredFields();
            for (int i = 0; i < curFields.length; ++i) {
                fields.add(curFields[i]);
            }
            curClass = curClass.getSuperclass();
        }

        // loop through all the class members
        Iterator<Field> iterFields = fields.iterator();
        while (iterFields.hasNext()) {
            Field curField = iterFields.next();

            if (!Modifier.isStatic(curField.getModifiers())) {

                curField.setAccessible(true);
                Class<?> fieldType = curField.getType();

                try {
                    // is this member a ResolveConceptualElement?
                    // if so, add it as a child
                    if (ResolveConceptualElement.class
                            .isAssignableFrom(fieldType)) {
                        //System.out.println("Walking: " + curField.getName());
                        children.add(ResolveConceptualElement.class
                                .cast(curField.get(this)));
                    }
                    // is this member a list of ResolveConceptualElements?
                    // if so, add the elements to the list of children
                    else if (java.util.List.class.isAssignableFrom(fieldType)) {
                        Class<?> listOf =
                                (Class<?>) ((ParameterizedType) curField
                                        .getGenericType())
                                        .getActualTypeArguments()[0];
                        java.util.List<?> fieldList =
                                java.util.List.class.cast(curField.get(this));
                        if (fieldList != null
                                && fieldList.size() > 0
                                && ResolveConceptualElement.class
                                        .isAssignableFrom(listOf)) {
                            children
                                    .add(new VirtualListNode(
                                            this,
                                            curField.getName(),
                                            (java.util.List<ResolveConceptualElement>) fieldList,
                                            (Class<?>) ((ParameterizedType) curField
                                                    .getGenericType())
                                                    .getActualTypeArguments()[0]));
                        }
                    }
                }
                catch (Exception ex) {
                    if (ex instanceof RuntimeException) {
                        throw (RuntimeException) ex;
                    }
                    else {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        return children;
    }
}

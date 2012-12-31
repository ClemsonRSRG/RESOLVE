/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import java.util.StringTokenizer;

public class VirtualListNode extends ResolveConceptualElement {

    ResolveConceptualElement myParent;
    String myName;
    List<ResolveConceptualElement> myList;
    Class<?> myListType;

    public VirtualListNode(ResolveConceptualElement parent, String listName,
            List<ResolveConceptualElement> list, Class<?> listType) {
        this.myParent = parent;
        this.myName = parent.getClass().getSimpleName() + toCamelCase(listName);
        this.myList = list;
        this.myListType = listType;
    }

    public ResolveConceptualElement getParent() {
        return myParent;
    }

    public String getNodeName() {
        return myName;
    }

    public Class<?> getListType() {
        return myListType;
    }

    @Override
    public List<ResolveConceptualElement> getChildren() {
        List<ResolveConceptualElement> children =
                new List<ResolveConceptualElement>();
        Iterator<ResolveConceptualElement> iter = myList.iterator();
        while (iter.hasNext()) {
            children.add(ResolveConceptualElement.class.cast(iter.next()));
        }
        return children;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String asString(int indent, int increment) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private String toCamelCase(String s) {
        StringBuilder buffer = new StringBuilder();
        StringTokenizer tokens = new StringTokenizer(s, "_");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            buffer.append(Character.toUpperCase(token.charAt(0)));
            buffer.append(token.substring(1));
        }
        return buffer.toString();
    }

    @Override
    public Location getLocation() {
        throw new UnsupportedOperationException(this.getClass()
                + " has no location by definition.");
    }
}

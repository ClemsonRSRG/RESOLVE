package edu.clemson.cs.r2jt.mathtype;

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

public class BoundVariableVisitor extends TypeVisitor {

    private Deque<Map<String, BindingInfo>> myBoundVariables =
            new LinkedList<Map<String, BindingInfo>>();

    @Override
    public final void beginMTBigUnion(MTBigUnion u) {
        myBoundVariables.push(toBindingInfoMap(u.getQuantifiedVariables()));
        boundBeginMTBigUnion(u);
    }

    @Override
    public final void endMTBigUnion(MTBigUnion u) {
        boundEndMTBigUnion(u);
        myBoundVariables.pop();
    }

    public void boundBeginMTBigUnion(MTBigUnion u) {

    }

    public void boundEndMTBigUnion(MTBigUnion u) {

    }

    public MTType getInnermostBinding(String name) {
        return getInnermostBindingInfo(name).type;
    }

    public void annotateInnermostBinding(String name, Object key, Object value) {

        getInnermostBindingInfo(name).annotations.put(key, value);
    }

    public Object getInnermostBindingAnnotation(String name, Object key) {
        return getInnermostBindingInfo(name).annotations.get(key);
    }

    private BindingInfo getInnermostBindingInfo(String name) {
        BindingInfo binding = null;
        Iterator<Map<String, BindingInfo>> scopes = myBoundVariables.iterator();
        while (binding == null && scopes.hasNext()) {
            binding = scopes.next().get(name);
        }

        if (binding == null) {
            throw new NoSuchElementException();
        }

        return binding;
    }

    protected Map<String, BindingInfo> toBindingInfoMap(Map<String, MTType> vars) {

        Map<String, BindingInfo> result = new HashMap<String, BindingInfo>();

        for (Map.Entry<String, MTType> entry : vars.entrySet()) {
            result.put(entry.getKey(), new BindingInfo(entry.getValue()));
        }

        return result;
    }

    protected class BindingInfo {

        public final MTType type;
        public final Map<Object, Object> annotations;

        public BindingInfo(MTType type) {
            this.type = type;
            this.annotations = new HashMap<Object, Object>();
        }
    }
}
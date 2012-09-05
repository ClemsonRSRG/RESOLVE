package edu.clemson.cs.r2jt.mathtype;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class MTCartesian extends MTAbstract<MTCartesian> {

    private List<MTType> myElements = new LinkedList<MTType>();
    private Map<String, MTType> myTags = new HashMap<String, MTType>();

    public void addFactor(MTType f) {
        myElements.add(f);
    }

    public void addFactor(String tag, MTType f) {
        myElements.add(f);
        myTags.put(tag, f);
    }

    public int size() {
        return myElements.size();
    }

    public MTType getFactor(int index) {
        return myElements.get(index);
    }

    public MTType getFactor(String tag) {
        if (!myTags.containsKey(tag)) {
            throw new NoSuchElementException();
        }

        return myTags.get(tag);
    }

    @Override
    public boolean valueEqual(MTCartesian t) {

        boolean result = true;

        Iterator<MTType> myTypes = myElements.iterator();
        Iterator<MTType> tTypes = t.myElements.iterator();

        while (result && myTypes.hasNext() && tTypes.hasNext()) {
            result &= myTypes.next().equals(tTypes.next());
        }

        return result && !myTypes.hasNext() && !tTypes.hasNext();
    }

    @Override
    public int hashCode() {
        int result = 0;

        for (MTType t : myElements) {
            result *= 37;
            result += t.hashCode();
        }

        return result;
    }
}
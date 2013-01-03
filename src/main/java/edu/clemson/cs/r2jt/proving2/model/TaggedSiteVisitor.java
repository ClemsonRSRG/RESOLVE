package edu.clemson.cs.r2jt.proving2.model;

public interface TaggedSiteVisitor<T> {

    public void visitSite(Site s, T data);
}
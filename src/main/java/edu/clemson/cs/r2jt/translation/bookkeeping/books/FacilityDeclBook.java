/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.bookkeeping.books;

/**
 *
 * @author
 * Mark
 */
public interface FacilityDeclBook {

    interface FacilityDeclEnhancement {

    }

    public void addParameter(String parName);

    public void addEnhancement(String enhanceName, String enhanceRealiz);

    public void addEnhanceParameter(String parName);

}

package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;

/**
 * <p>A <code>FacilityDec</code> encapsulates all content responsible for the
 * instantiation of a abstract concept in a client's source. A typical
 * facility declaration looks like the following:</p>
 *
 * <pre>Std_Integer_Fac is Integer_Template
 *              realized by Std_Integer_Realiz;</pre>
 *
 * <p>Note that each <code>FacilityDec</code> pairs a (possibly)
 * parameterized specification with a (possibly) parameterized realization.
 * Additionally, any number of <code>EnhancementItem</code>s may follow.</p>
 */
public class FacilityDec extends Dec {

    /**
     * <p>A handle to the name of this <code>FacilityDec</code>. For instance,
     * in the example above, the name is: <code>Std_Integer_Fac</code>.</p>
     */
    private PosSymbol myName;

    /**
     * <p>This refers to the specification portion of the current
     * <code>FacilityDec</code>.</p>
     */
    private PosSymbol myConceptName;
    private List<ModuleArgumentItem> myConceptParameters;

    /**
     * <p>A <em>body</em> is simply another way of saying
     * <em>realization</em>.  This is a handle to the realization portion of
     * the <code>FacilityDec</code>.</p>
     */
    private PosSymbol myBodyName;
    private List<ModuleArgumentItem> myBodyParameters;

    /**
     * <p>This refers to an (optional) performance profile component for an
     * otherwise normal <code>FacilityDec</code>.</p>
     */
    private PosSymbol myProfileName;

    /**
     * <p>Any enhancements associated with this <code>FacilityDec</code>.</p>
     */
    private List<EnhancementItem> myEnhancements;
    private List<EnhancementBodyItem> myEnhancementBodies;

    /**
     * <p>A flag indicating whether or not this <code>FacilityDec</code>'s
     * body/realization has an implementation written in Resolve.</p>
     */
    private boolean myExternallyRealizedFlag;

    public FacilityDec(PosSymbol name, PosSymbol conceptName,
                       List<ModuleArgumentItem> conceptParams,
                       List<EnhancementItem> enhancements, PosSymbol bodyName,
                       PosSymbol profileName, List<ModuleArgumentItem> bodyParams,
                       List<EnhancementBodyItem> enhancementBodies,
                       boolean externallyRealized) {

        myName = name;
        myConceptName = conceptName;
        myConceptParameters = conceptParams;
        myEnhancements = enhancements;
        myBodyName = bodyName;
        myProfileName = profileName;
        myBodyParameters = bodyParams;
        myEnhancementBodies = enhancementBodies;
        myExternallyRealizedFlag = externallyRealized;
    }

    public PosSymbol getName() {
        return myName;
    }

    /**
     * <p>Returns the name of this <code>FacilityDec</code>'s
     * specification.</p>
     *
     * @return A <code>PosSymbol</code> containing the name and
     *                      <code>Location</code> of the specification.
     */
    public PosSymbol getConceptName() {
        return myConceptName;
    }

    /**
     * <p>Returns a list of any user-supplied parameters to the
     * this <code>FacilityDec</code>'s specification.</p>
     *
     * @return A list of <code>ModuleArgumentItem</code>s
     */
    public List<ModuleArgumentItem> getConceptParams() {
        return myConceptParameters;
    }

    /**
     * <p>Returns a list of enhancements (<code>EnhancementItem</code>s)
     * corresponding to this <code>FacilityDec</code>.</p>
     *
     * @return
     */
    public List<EnhancementItem> getEnhancements() {
        return myEnhancements;
    }

    /**
     * <p>Returns the name of this <code>FacilityDec</code>'s realization.</p>
     *
     * @return A <code>PosSymbol</code> containing the name and location of
     *         the realization.
     */
    public PosSymbol getBodyName() {
        return myBodyName;
    }

    /**
     * <p>Returns the name of the performance profile associated with this
     * <code>FacilityDec</code>.</p>
     *
     * @return The performance profile's name.
     */
    public PosSymbol getProfileName() {
        return myProfileName;
    }

    /**
     * <p>Returns a list of parameters </p>
     * @return
     */
    public List<ModuleArgumentItem> getBodyParams() {
        return myBodyParameters;
    }

    public List<EnhancementBodyItem> getEnhancementBodies() {
        return myEnhancementBodies;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> there currently
     * isn't a Resolve realization for the concept this
     * <code>FacilityDec</code> instantiates.</p>
     *
     * @return <code>true</code> if the body/spec lacks a Resolve
     *         implementation; <code>false</code> otherwise.
     */
    public boolean isExternallyRealized() {
        return myExternallyRealizedFlag;
    }

    public void setName(PosSymbol name) {
        myName = name;
    }

    public void setConceptName(PosSymbol conceptName) {
        myConceptName = conceptName;
    }

    public void setConceptParams(List<ModuleArgumentItem> conceptParams) {
        myConceptParameters = conceptParams;
    }

    public void setEnhancements(List<EnhancementItem> enhancements) {
        myEnhancements = enhancements;
    }

    public void setBodyName(PosSymbol bodyName) {
        myBodyName = bodyName;
    }

    public void setProfileName(PosSymbol name) {
        myProfileName = name;
    }

    public void setBodyParams(List<ModuleArgumentItem> bodyParams) {
        myBodyParameters = bodyParams;
    }

    public void setEnhancementBodies(List<EnhancementBodyItem> enhancementBodies) {
        myEnhancementBodies = enhancementBodies;
    }

    public void accept(ResolveConceptualVisitor v) {
        v.visitFacilityDec(this);
    }

    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("FacilityDec\n");

        if (myName != null) {
            sb.append(myName.asString(indent + increment, increment));
        }

        if (myConceptName != null) {
            sb.append(myConceptName.asString(indent + increment, increment));
        }

        if (myConceptParameters != null) {
            sb.append(myConceptParameters.asString(indent + increment,
                    increment));
        }

        if (myEnhancements != null) {
            sb.append(myEnhancements.asString(indent + increment, increment));
        }

        if (myBodyName != null) {
            sb.append(myBodyName.asString(indent + increment, increment));
        }

        if (myBodyParameters != null) {
            sb.append(myBodyParameters.asString(indent + increment, increment));
        }

        if (myEnhancementBodies != null) {
            sb.append(myEnhancementBodies.asString(indent + increment,
                    increment));
        }

        return sb.toString();
    }
}

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
 * Additionally, any number of <code>EnhancementItem</code>s are permitted
 * to follow each <code>FacilityDec</code>.</p>
 */
public class FacilityDec extends Dec {

    private PosSymbol myName;
    private PosSymbol myConceptName;
    private List<ModuleArgumentItem> myConceptParameters;
    private PosSymbol myBodyName;
    private List<ModuleArgumentItem> myBodyParameters;
    private List<EnhancementItem> myEnhancements;
    private List<EnhancementBodyItem> myEnhancementBodies;

    /**
     * <p>This refers to an (optional) performance profile component for an
     * otherwise normal <code>FacilityDec</code>.</p>
     */
    private PosSymbol myProfileName;

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
     * @return The name of the facility declaration.
     */
    public PosSymbol getConceptName() {
        return myConceptName;
    }

    /**
     * <p>Returns a list of any user-supplied parameters corresponding to this
     * <code>FacilityDec</code>'s specification.</p>
     *
     * @return The list of concept/specification parameters
     */
    public List<ModuleArgumentItem> getConceptParams() {
        return myConceptParameters;
    }

    /**
     * <p>Returns a list of enhancements (<code>EnhancementItem</code>s)
     * corresponding to this <code>FacilityDec</code>.</p>
     *
     * @return The list of enhancements.
     */
    public List<EnhancementItem> getEnhancements() {
        return myEnhancements;
    }

    /**
     * <p>Returns the name of this <code>FacilityDec</code>'s realization.</p>
     *
     * @return The realization's name.
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
     * <p>Returns a list of parameters (<code>ModuleArgumentItem</code>s)
     * for this <code>FacilityDec</code>s realization.</p>
     *
     * @return The list of body parameters.
     */
    public List<ModuleArgumentItem> getBodyParams() {
        return myBodyParameters;
    }

    public List<EnhancementBodyItem> getEnhancementBodies() {
        return myEnhancementBodies;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> there does not
     * exist a Resolve realization for the concept/specification this
     * <code>FacilityDec</code> instantiates.</p>
     *
     * @return <code>true</code> if the specification lacks a realization
     * 		   written in Resolve; <code>false</code> otherwise.
     */
    public boolean isExternallyRealized() {
        return myExternallyRealizedFlag;
    }

    /**
     * <p>Sets the name of this <code>FacilityDec</code>.</p>
     *
     * @param name The desired name.
     */
    public void setName(PosSymbol name) {
        myName = name;
    }

    /**
     * <p>Sets the name of this <code>FacilityDec</code>s specification.</p>
     *
     * @param conceptName The desired name.
     */
    public void setConceptName(PosSymbol conceptName) {
        myConceptName = conceptName;
    }

    /**
     * <p>Sets a list of conceptual (<code>ModuleArgumentItems</code>)
     * parameters for this <code>FacilityDec</code>'s specification.</p>
     *
     * @param conceptParameters The list of conceptual parameters.
     */
    public void setConceptParams(List<ModuleArgumentItem> conceptParameters) {
        myConceptParameters = conceptParameters;
    }

    /**
     * <p>Sets a list of conceptual (<code>ModuleArgumentItems</code>)
     * parameters for this <code>FacilityDec</code>'s specification.</p>
     *
     * @param enhancements The list of conceptual parameters.
     */
    public void setEnhancements(List<EnhancementItem> enhancements) {
        myEnhancements = enhancements;
    }

    /**
     * <p>Sets the name of this <code>FacilityDec</code>'s realization.</p>
     *
     * @param bodyName The name of a realization.
     */
    public void setBodyName(PosSymbol bodyName) {
        myBodyName = bodyName;
    }

    /**
     * <p>Sets the name of this <code>FacilityDec</code>'s performance
     * profile.</p>
     *
     * @param performanceProfileName The name of the performance proile.
     */
    public void setProfileName(PosSymbol performanceProfileName) {
        myProfileName = performanceProfileName;
    }

    /**
     * <p>Sets a list of <code>ModuleArgumentItems</code> parameters for this
     * <code>FacilityDec</code>'s realization (or, body).</p>
     *
     * @param bodyParams The list of realization parameters.
     */
    public void setBodyParams(List<ModuleArgumentItem> bodyParams) {
        myBodyParameters = bodyParams;
    }

    /**
     * <p>Sets a list of realizations corresponding to
     * <code>EnhancementItem</code>s listed by this
     * <code>FacilityDec</code>.</p>
     *
     * @param enhancementBodies The list of enhancement realizations.
     */
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

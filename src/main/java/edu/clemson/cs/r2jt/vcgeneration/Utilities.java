package edu.clemson.cs.r2jt.vcgeneration;

/*
 * Libraries
 */
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Stack;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;

/**
 * TODO: Write a description of this module
 */
public class Utilities {

    // ===========================================================
    // Global Variables 
    // ===========================================================

    // Compile Environment
    private CompileEnvironment myInstanceEnvironment;

    // ConceptModuleDec for the current file
    private ConceptModuleDec myConceptModuleDec;

    // EnhancementModuleDec for the current file
    private EnhancementModuleDec myEnhancementModuleDec;

    // Stack of ModuleDecs currently in use in this file
    private Stack<ModuleDec> myModuleDecStack;

    // Module Parameters String List
    private List<String> myTypeParms;
    private List<String> myConcParms;

    // Uses List
    private List<UsesItem> myUsesList;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Utilities(final CompileEnvironment env) {
        // Initialize global variables
        myInstanceEnvironment = env;
        myConceptModuleDec = null;
        myEnhancementModuleDec = null;
        myModuleDecStack = new Stack<ModuleDec>();
        myTypeParms = null;
        myConcParms = null;
        myUsesList = null;
    }

    // ===========================================================
    // Mutator/Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    // Returns the concept associated with this module.
    public ConceptModuleDec getConceptModuleDec() {
        return myConceptModuleDec;
    }

    // Returns the enhancement associated with this module.
    public EnhancementModuleDec getEnhancementModuleDec() {
        return myEnhancementModuleDec;
    }

    // Returns the list containing strings of type parameters with this module.
    public List<String> getTypeParmsList() {
        return myTypeParms;
    }

    // Returns the list containing strings of conceptual parameters with this
    // module.
    public List<String> getConcParmsList() {
        return myConcParms;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    // Sets the concept associated with this module.
    public void setConceptModuleDec(ConceptModuleDec dec) {
        myConceptModuleDec = dec;
    }

    // Sets the enhancement associated with this module.
    public void setEnhancementModuleDec(EnhancementModuleDec dec) {
        myEnhancementModuleDec = dec;
    }

    // Sets the list containing strings of type parameters to this module.
    public void setTypeParmsList(List<String> list) {
        this.myTypeParms = list;
    }

    // Sets the list containing strings of type parameters to this module.
    public void setConcParmsList(List<String> list) {
        this.myConcParms = list;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void addCurrentModuleDec(ModuleDec dec) {
        // Add the current ModuleDec to the stack
        myModuleDecStack.push(dec);
    }

    public void constructParamList(PosSymbol conceptName) {
        // Create a ModuleID and get the type and conceptual parameters
        ModuleID cid = ModuleID.createConceptID(conceptName);
        myTypeParms = getTypeParms(cid);
        myConcParms = getConcParms(cid);
    }

    public void clearParamList() {
        // Clear the global variables
        myTypeParms = null;
        myConcParms = null;
    }

    public void populateUsesList(List<UsesItem> list) {
        // Populate the uses list from my enhancements if it is not null
        if (myEnhancementModuleDec != null) {
            myUsesList = myEnhancementModuleDec.getUsesItems();
        }

        // Populate the uses list with the current dec's uses list
        myUsesList.addAllUnique(list);

        // Populate the uses list from my concept if it is not null
        if (myConceptModuleDec != null) {
            myUsesList.addAllUnique(myConceptModuleDec.getUsesItems());
        }
    }

    public ModuleDec removeCurrentModuleDec() {
        // Remove the top ModuleDec from the stack
        return myModuleDecStack.pop();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private List<String> getTypeParms(ModuleID cid) {
        // Variables
        List<String> typeParms = new List<String>();

        // Retrieve the ModuleDec for this Concept
        ConceptModuleDec cDec =
                (ConceptModuleDec) myInstanceEnvironment.getModuleDec(cid);

        // Iterate through and add all ConceptTypeParamDec
        Iterator<ModuleParameterDec> mpIt = cDec.getParameters().iterator();
        while (mpIt.hasNext()) {
            ModuleParameterDec md = mpIt.next();
            Dec mp = md.getWrappedDec();
            if (mp instanceof ConceptTypeParamDec) {
                // Add all unique instances
                typeParms.addUnique(((ConceptTypeParamDec) mp).getName()
                        .toString());
            }
        }

        return typeParms;
    }

    private List<String> getConcParms(ModuleID cid) {
        // Variables
        List<String> concParms = new List<String>();

        // Retrieve the ModuleDec for this Concept
        ConceptModuleDec cDec =
                (ConceptModuleDec) myInstanceEnvironment.getModuleDec(cid);

        // Iterate through and add all ConceptParamDec
        Iterator<ModuleParameterDec> mpIt = cDec.getParameters().iterator();
        while (mpIt.hasNext()) {
            ModuleParameterDec md = mpIt.next();
            Dec mp = md.getWrappedDec();
            if (mp instanceof ConstantParamDec) {
                // Add all unique instances
                concParms.addUnique(((ConstantParamDec) mp).getName()
                        .toString());
            }
        }

        return concParms;
    }
}
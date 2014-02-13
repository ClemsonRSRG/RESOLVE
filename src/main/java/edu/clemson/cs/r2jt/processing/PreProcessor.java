package edu.clemson.cs.r2jt.processing;

// Libraries
import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.treewalk.TreeWalkerStackVisitor;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

import java.util.Iterator;

/**
 * TODO: Write a description of this module
 */
public class PreProcessor extends TreeWalkerStackVisitor {

    // ===========================================================
    // Global Variables
    // ===========================================================

    /**
     * <p>A counter used to keep track the number of things
     * created by the PreProcessor.</p>
     */
    private int myCounter;

    /**
     * <p>Map of all the local array types encountered.</p>
     */
    private Map<String, NameTy> myArrayFacilityMap;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PreProcessor() {
        myCounter = 1;
        myArrayFacilityMap = new Map<String, NameTy>();
    }

    // ===========================================================
    // TreeWalker Methods
    // ===========================================================

    // -----------------------------------------------------------
    // ArrayTy
    // -----------------------------------------------------------

    @Override
    public void postArrayTy(ArrayTy ty) {
        // Variables
        Location location = ty.getLocation();
        NameTy oldTy = (NameTy) ty.getEntryType();
        ResolveConceptualElement parent = this.getAncestor(1);
        String arrayName = null;

        // Check if we have a FacilityTypeDec, RepresentationDec or VarDec
        if (parent instanceof FacilityTypeDec) {
            arrayName = ((FacilityTypeDec) parent).getName().getName();
        }
        else if (parent instanceof RepresentationDec) {
            arrayName = ((RepresentationDec) parent).getName().getName();
        }
        else if (parent instanceof VarDec) {
            arrayName = ((VarDec) parent).getName().getName();
        }

        // Check for not null
        if (arrayName != null) {
            // Create name in the format of "_(Name of Variable)_Array_Fac_(myCounter)"
            String newArrayName = "";
            newArrayName += ("_" + arrayName + "_Array_Fac_" + myCounter++);

            // Create newTy
            NameTy newTy =
                    new NameTy(new PosSymbol(location, Symbol
                            .symbol(newArrayName)), new PosSymbol(location,
                            Symbol.symbol("Static_Array")));

            //Check if we have a FacilityTypeDec, RepresentationDec or VarDec
            if (parent instanceof FacilityTypeDec) {
                // Set the Ty of the Parent
                ((FacilityTypeDec) parent).setRepresentation(newTy);
            }
            else if (parent instanceof RepresentationDec) {
                // Set the Ty of the Parent
                ((RepresentationDec) parent).setRepresentation(newTy);
            }
            else if (parent instanceof VarDec) {
                // Set the Ty of the Parent
                ((VarDec) parent).setTy(newTy);
            }

            // Create a list of arguments for the new FacilityDec
            List<ModuleArgumentItem> listItem = new List<ModuleArgumentItem>();
            String typeName = oldTy.getName().getName();

            // Add the type, Low and High for Arrays
            listItem.add(new ModuleArgumentItem(null, new PosSymbol(location,
                    Symbol.symbol(typeName)), null));
            listItem.add(new ModuleArgumentItem(null, null, ty.getLo()));
            listItem.add(new ModuleArgumentItem(null, null, ty.getHi()));

            // Call method to createFacilityDec
            FacilityDec arrayFacilityDec =
                    createFacilityDec(location, newArrayName,
                            "Static_Array_Template", "Std_Array_Realiz",
                            listItem, new List<ModuleArgumentItem>(),
                            new List<EnhancementItem>(),
                            new List<EnhancementBodyItem>());

            // Iterate through AST and add the arrayFacilityDec
            // to the list of Decs where it belongs
            Iterator<ResolveConceptualElement> it = this.getAncestorInterator();
            addFacilityDec(it, arrayFacilityDec);

            // Saving the Ty of this ArrayFacility for future use
            myArrayFacilityMap.put(newArrayName, oldTy);
        }
        else {
            notHandledArrayTyParent(ty.getLocation(), ty, parent);
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Error Handling
    // -----------------------------------------------------------

    public void notHandledArrayTyParent(Location location, ArrayTy ty,
            ResolveConceptualElement parent) {
        String message =
                "ArrayTy "
                        + ty.toString()
                        + "'s parent is "
                        + parent.toString()
                        + ". This type of parent is not handled in the PreProcessor.";
        throw new SourceErrorException(message, location);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Adds the newly created <code>FacilityDec</code> to the
     * right place in our AST.</p>
     *
     * @param it The ancestor iterator.
     * @param newDec The newly created <code>FacilityDec</code>.
     */
    private void addFacilityDec(Iterator<ResolveConceptualElement> it,
            FacilityDec newDec) {
        // Loop
        while (it.hasNext()) {
            // Obtain a temp from it
            ResolveConceptualElement temp = it.next();

            // Check to see if it is an instance of FacilityModuleDec,
            // FacilityOperationDec, ConceptBodyModuleDec, ProcedureDec
            // or EnhancementBodyModuleDec
            if (temp instanceof FacilityModuleDec) {
                // Obtain a list of Decs from FacilityModuleDec
                List<Dec> decList = ((FacilityModuleDec) temp).getDecs();

                // Add the FacilityDec created to decList
                decList.add(0, newDec);

                // Reinsert the modified list back into FacilityModuleDec
                ((FacilityModuleDec) temp).setDecs(decList);

                break;
            }
            else if (temp instanceof FacilityOperationDec) {
                // Obtain a list of Decs from FacilityOperationDec
                List<FacilityDec> decList =
                        ((FacilityOperationDec) temp).getFacilities();

                // Add the FacilityDec created to decList
                decList.add(0, newDec);

                // Reinsert the modified list back into FacilityOperationDec
                ((FacilityOperationDec) temp).setFacilities(decList);

                break;
            }
            else if (temp instanceof ConceptBodyModuleDec) {
                // Obtain a list of Decs from ConceptBodyModuleDec
                List<Dec> decList = ((ConceptBodyModuleDec) temp).getDecs();

                // Add the FacilityDec created to decList
                decList.add(0, newDec);

                // Reinsert the modified list back into ConceptBodyModuleDec
                ((ConceptBodyModuleDec) temp).setDecs(decList);

                break;
            }
            else if (temp instanceof ProcedureDec) {
                // Obtain a list of FacilityDecs from ProcedureDec
                List<FacilityDec> decList =
                        ((ProcedureDec) temp).getFacilities();

                // Add the FacilityDec created to decList
                decList.add(0, newDec);

                // Reinsert the modified list back into ProcedureDec
                ((ProcedureDec) temp).setFacilities(decList);

                break;
            }
            else if (temp instanceof EnhancementBodyModuleDec) {
                // Obtain a list of FacilityDecs from EnhancementBodyModuleDec
                List<Dec> decList = ((EnhancementBodyModuleDec) temp).getDecs();

                // Add the FacilityDec created to decList
                decList.add(0, newDec);

                // Reinsert the modified list back into EnhancementBodyModuleDec
                ((EnhancementBodyModuleDec) temp).setDecs(decList);

                break;
            }
        }
    }

    /**
     * <p>Creates a new <code>FacilityDec</code>.</p>
     *
     * @param location The location where the <code>FacilityDec</code> is created
     * @param name The name of the new <code>FacilityDec</code>.
     * @param conceptName The name of the Concept of this <code>FacilityDec</code>.
     * @param conceptRealizationName The name of the Concept Realization of this
     *                               <code>FacilityDec</code>.
     * @param conceptParam The list of parameters for the Concept.
     * @param conceptBodiesParam The list of parameters for the Concept
     *                           Realization.
     * @param enhancementParam The list of parameters for the Enhancement.
     * @param enhancementBodiesParam The list of parameters for the Enhancement
     *                               Realization.
     *
     * @return Newly created <code>FacilityDec</code>
     */
    private FacilityDec createFacilityDec(Location location, String name,
            String conceptName, String conceptRealizationName,
            List<ModuleArgumentItem> conceptParam,
            List<ModuleArgumentItem> conceptBodiesParam,
            List<EnhancementItem> enhancementParam,
            List<EnhancementBodyItem> enhancementBodiesParam) {
        // Create a FacilityDec
        FacilityDec newFacilityDec = new FacilityDec();

        // Check for null
        if (newFacilityDec != null) {
            // Set the name
            newFacilityDec
                    .setName(new PosSymbol(location, Symbol.symbol(name)));

            // Set the Concept to "Static_Array_Template
            newFacilityDec.setConceptName(new PosSymbol(location, Symbol
                    .symbol(conceptName)));
            newFacilityDec.setConceptParams(conceptParam);

            // Set the Concept Realization to "Std_Array_Realiz */
            newFacilityDec.setBodyName(new PosSymbol(location, Symbol
                    .symbol(conceptRealizationName)));
            newFacilityDec.setBodyParams(conceptBodiesParam);

            // Set the Enhancement to empty
            newFacilityDec.setEnhancements(enhancementParam);
            newFacilityDec.setEnhancementBodies(enhancementBodiesParam);
        }

        return newFacilityDec;
    }
}
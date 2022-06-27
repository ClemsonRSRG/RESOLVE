package edu.clemson.rsrg.nProver.registry;

/**
 * <p></p>
 * @author Nicodemus Msafiri J. M.
 * @version v1.0
 * @since 2021
 */
public class CClassLabel {
    //private Integer treeNodeLabel;
    private int indexToCClass;
    private int nextVarietalClass;
    private int prevVarietalClass;
    private int plantationDesignator; //not a pair we will need a struture to keep these

    public CClassLabel(int indexToCClass, int nextVarietalClass, int prevVarietalClass, int plantationDesignator){
        //this.treeNodeLabel = treeNodeLabel; // it might be redundant, we know this before we got here
        this.indexToCClass = indexToCClass;
        this.nextVarietalClass = nextVarietalClass;
        this.prevVarietalClass = prevVarietalClass;
        this.plantationDesignator = plantationDesignator;
    }

    public int getIndexToCClass(){return indexToCClass;}
    public void setIndexToCClass(int newClassDesignator){indexToCClass = newClassDesignator;}
    public int getNextVarietalClass(){return nextVarietalClass;}
    public void setNextVarietalClass(int newIndex){nextVarietalClass = newIndex;}
    public int getPrevVarietalClass(){return prevVarietalClass;}
    public void setPrevVarietalClass(int newIndex){prevVarietalClass = newIndex;}
    public int getPlantationDesignator(){return plantationDesignator;}
    public void setPlantationDesignator(int newPlantationDesignator){plantationDesignator = newPlantationDesignator;}

}

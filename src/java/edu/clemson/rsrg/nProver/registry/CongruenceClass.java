package edu.clemson.rsrg.nProver.registry;

import edu.clemson.rsrg.nProver.tags.TreeCategoryTag;

import java.util.*;

/**
 * <p>This class is for every congruence class that is created and stored in the registry</p>
 *
 * @author Nicodemus Msafiri J. M.
 * @version v1.0
 */
public class CongruenceClass {

    private int firstPlantation;
    private int classTag;
    private BitSet classAttribute;
    private int [] argStringOccPos;//it is going to be an array of levels the class happen to be in the argument string structure
    private int lastArgStringPosition; //should be the last level it the class shows up in the cluster argument array structure
    private int dominantCClass;

    public CongruenceClass(int firstPlantation, int classTag, int lastArgStringPosition, int dominantCClass ){
        this.firstPlantation = firstPlantation; //introduced after review
        this.classTag = classTag; // to a structure that keeps the class tags
        this.classAttribute = new BitSet(); //initially every bit is false;
        this.argStringOccPos = new int[10];
        Arrays.fill(argStringOccPos,0);//initialize them all to 0
        this.lastArgStringPosition = lastArgStringPosition;
        this.dominantCClass = dominantCClass;
    }


    public int getFirstPlantation(){return firstPlantation;}
    public void setFirstPlantation(int firstPlantation) {this.firstPlantation = firstPlantation;}

    public int getClassTag(){
        return classTag;
    }

    public BitSet getAttribute(){return classAttribute;}
    public void setClassAttribute(BitSet classAttributeProvided){classAttribute = classAttributeProvided;}

    //add the index from the cluster argument array to the level of the argument string occurrence position (ASOP) array
    public void addToArgStringOccPos(int indexInClusterArgArray, int level){ argStringOccPos[level] = indexInClusterArgArray;}
    //get the index to the cluster argument array from the ASOP given a level.
    public int getIndexInClusterArgArrayFromASOP (int level){ return argStringOccPos[level];}

    //public int getArgStringOccPos(){return argStringOccPos;}
    public int getLastArgStringPosition(){return lastArgStringPosition;}
    public void setLastArgStringPosition(int indexToArgString){lastArgStringPosition = indexToArgString;}
    public int getDominantCClass(){return dominantCClass;}
    public void setDominantCClass(int dominantCClass){this.dominantCClass = dominantCClass;}
}

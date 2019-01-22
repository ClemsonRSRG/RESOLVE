/*
 * Location.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.data;

import java.io.*;

import edu.clemson.cs.r2jt.absyn.EqualsExp;
import edu.clemson.cs.r2jt.absyn.Exp;

public class Location {

    // ===========================================================
    // Variables
    // ===========================================================

    private File file;

    private Pos pos;

    private String details;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Location(File file, Pos pos) {
        this.file = file;
        this.pos = pos;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    public File getFile() {
        return file;
    }

    public String getFilename() {
        return file.getName();
    }

    public Pos getPos() {
        return pos;
    }

    public String getDetails() {
        return details;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    public void setDetails(String details) {
        this.details = details;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public String printErrorLine() {
        StringBuffer sb = new StringBuffer();
        try {
            FileReader fileReader = new FileReader(file);
            LineNumberReader reader = new LineNumberReader(fileReader);
            reader.setLineNumber(pos.getLine());
            sb.append(reader.readLine() + "\n");
            sb.append(printSpace(pos.getColumn()));
            sb.append("^\n");
        }
        catch (FileNotFoundException nfex) {
            System.err.println(nfex);
        }
        catch (IOException ioex) {
            System.err.println(ioex);
        }
        return sb.toString();
    }

    public boolean equals(Location loc) {
        return (file.equals(loc.file) && pos.equals(loc.pos));
    }

    //      public boolean equals(Location loc) {
    //          return (  this.file.equals(loc.getFile()) &&
    //                    this.pos.equals(loc.getPos()));
    //      }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        //Environment env = Environment.getInstance();
        //          if (env.getModuleID(file).hasConcept()) {
        //              sb.append("[");
        //              sb.append(env.getModuleID(file).getConceptName().toString());
        //              sb.append("]");
        //          }
        File par = file.getParentFile();
        String path = file.toString();
        String mask = par.toString();
        assert path.startsWith(mask) : "Path does not start with " + mask;
        sb.append(path.substring(mask.length() + 1));
        sb.append("(");
        sb.append(pos.getLine());
        sb.append(")");
        return sb.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private String printSpace(int n) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public Object clone() {
        Location clone = new Location(this.getFile(), this.getPos());
        clone.setDetails(this.getDetails());
        return clone;
    }
}

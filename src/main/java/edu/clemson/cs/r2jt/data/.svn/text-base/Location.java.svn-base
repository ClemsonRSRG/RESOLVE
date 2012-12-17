/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */
/*
 * Location.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.data;

import java.io.*;

import edu.clemson.cs.r2jt.absyn.EqualsExp;
import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.init.Environment;

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

    public File getFile() { return file; }

    public String getFilename() { return file.getName(); }

    public Pos getPos() { return pos; }
    
    public String getDetails(){ return details; }
    
    
    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    public void setDetails(String details){
    	this.details=details;
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
        } catch (FileNotFoundException nfex) {
            System.err.println(nfex);
        } catch (IOException ioex) {
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
    
    
    public Object clone(){
   	 	Location clone = new Location(this.getFile(), this.getPos());
   	 	clone.setDetails(this.getDetails());
   	 	return clone;
   }    
}

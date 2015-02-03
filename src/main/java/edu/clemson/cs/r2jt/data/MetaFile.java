/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.data;

import java.io.File;

import edu.clemson.cs.r2jt.init.CompileEnvironment;

/**
 * @author Chuck
 * This class is used in conjunction with the web interface as a way
 * to store all the information for user created components that is
 * needed by the compiler (since they will not be written to disk) 
 */

public class MetaFile {

    private String myFileName;
    private String myAssocConcept;
    private String myPkg;
    private String myFileSource;
    private ModuleKind myKind;
    private boolean myCustom;
    private String myCustomPath;
    private String jarTempDir;

    public MetaFile(String fileName, String assocConcept, String pkg,
            String fileSource, ModuleKind kind) {
        myFileName = fileName;
        myAssocConcept = assocConcept;
        myPkg = pkg;
        myFileSource = fileSource;
        myKind = kind;
        myCustom = false;
    }

    public void setMyFileName(String myFileName) {
        this.myFileName = myFileName;
    }

    public void setMyAssocConcept(String myAssocConcept) {
        this.myAssocConcept = myAssocConcept;
    }

    public void setMyPkg(String pkg) {
        this.myPkg = pkg;
    }

    public void setMyFileSource(String myFileSource) {
        this.myFileSource = myFileSource;
    }

    public void setIsCustomLoc() {
        myCustom = true;
    }

    public void setMyCustomPath(String customPath) {
        myCustomPath = customPath;
    }

    /*public void setMyKind(ModuleKind myKind) {
    	this.myKind = myKind;
    }*/

    public String getMyFileName() {
        return myFileName;
    }

    public String getMyAssocConcept() {
        return myAssocConcept;
    }

    public String getMyPkg() {
        return myPkg;
    }

    public String getMyFileSource() {
        return myFileSource;
    }

    public ModuleKind getMyKind() {
        return myKind;
    }

    public boolean getIsCustomLoc() {
        return myCustom;
    }

    public void setJarTempDir(String dir) {
        jarTempDir = dir;
    }

    public String getJarTempDir() {
        return jarTempDir;
    }

    public File getMyCustomFile() {
        String filePath = myCustomPath;
        if (myKind.equals(ModuleKind.FACILITY)) {
            filePath += "Facilities" + File.separator;
        }
        else if (myKind.equals(ModuleKind.THEORY)) {
            filePath += File.separator + "Theories" + File.separator;
        }
        else {
            filePath += File.separator + "Concepts" + File.separator;
        }
        filePath += myPkg + File.separator + myFileName + myKind.getExtension();
        return new File(filePath);
    }

    public File getMyFile(File mainDir) {
        if (false) {
            String filePath = myCustomPath;
            if (myKind.equals(ModuleKind.FACILITY)) {
                filePath += File.separator + "Facilities" + File.separator;
            }
            else if (myKind.equals(ModuleKind.THEORY)) {
                filePath += File.separator + "Theories" + File.separator;
            }
            else {
                filePath += File.separator + "Concepts" + File.separator;
            }
            filePath +=
                    myPkg + File.separator + myFileName + myKind.getExtension();
            return new File(filePath);
        }
        else {
            String filePath = mainDir.getAbsolutePath();
            if (myKind.equals(ModuleKind.FACILITY)) {
                filePath += File.separator + "Facilities" + File.separator;
            }
            else if (myKind.equals(ModuleKind.THEORY)) {
                filePath += File.separator + "Theories" + File.separator;
            }
            else {
                filePath += File.separator + "Concepts" + File.separator;
            }
            if (myPkg.equals("Static_Array_Template")) {
                myPkg = "Standard" + File.separator + "Static_Array_Template";
            }
            else if (myPkg.equals("Location_Linking_Template_1")) {
                myPkg =
                        "Standard" + File.separator
                                + "Location_Linking_Template_1";
            }
            filePath +=
                    myPkg + File.separator + myFileName + myKind.getExtension();
            return new File(filePath);
        }
    }
}

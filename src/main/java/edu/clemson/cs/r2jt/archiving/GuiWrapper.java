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
package edu.clemson.cs.r2jt.archiving;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

import edu.clemson.cs.r2jt.compilereport.CompileReport;
import edu.clemson.cs.r2jt.init.CompileEnvironment;

public class GuiWrapper {

    private GuiSetup gui;
    private String type;
    private String guiName;
    private String facilityName;
    private String programFacilityName;
    private String javaLocation;
    private String entryClass;
    private String packageName;
    private StringBuffer sb = new StringBuffer();

    public GuiWrapper(String type, String targetFile, String workspaceDir,
            CompileEnvironment instanceEnvironment) {
        gui = new GuiSetup();
        this.type = type;
        String facilityFileName =
                analyzeTargetFile(targetFile, workspaceDir) + ".fa";
        //String facilityFileName = analyzeManifest(manifestContents) + ".fa";

        javaLocation =
                targetFile.replaceAll(facilityFileName, guiName + ".java");
        CompileReport myReport = instanceEnvironment.getCompileReport();
        if (!myReport.getFacilityName().equals("")) {
            programFacilityName = myReport.getFacilityName();
        }
        else {
            programFacilityName = facilityName;
        }
        //System.out.println(facilityName);
    }

    public void setJavaLocation(String java, String name) {
        javaLocation = java.replaceAll(name, guiName + ".java");
    }

    public boolean generateCode() {
        if (type.equals("gui")) {
            generateSwing();
        }
        else if (type.equals("applet")) {
            generateApplet();
        }
        else {
            return false;
        }
        return true;
    }

    private void generateSwing() {
        SwingGui sg =
                new SwingGui(guiName, facilityName, programFacilityName,
                        packageName, gui);
        sb = sg.generateCode();
    }

    private void generateApplet() {

    }

    public String getJavaPath() {
        return javaLocation;
    }

    public String getEntryClass() {
        return entryClass;
    }

    public String getGuiName() {
        return guiName;
    }

    public boolean createJavaFile() {
        boolean created = false;
        try {
            BufferedWriter out =
                    new BufferedWriter(new FileWriter(javaLocation));
            out.write(sb.toString());
            out.close();
            created = true;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return created;
    }

    private String analyzeTargetFile(String targetFile, String workspaceDir) {
        int lastSlash = targetFile.lastIndexOf(File.separator);
        int dot = targetFile.lastIndexOf(".");
        String path = targetFile.substring(0, lastSlash);
        String packagePath =
                path.substring(workspaceDir.length(), path.length());
        packageName =
                packagePath.replaceAll(Pattern.quote(File.separator), ".");
        facilityName = targetFile.substring(lastSlash + 1, dot);
        guiName = facilityName + "_Gui";
        entryClass = packageName + "." + guiName;
        return facilityName;
    }

}
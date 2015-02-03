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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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
package edu.clemson.cs.r2jt.compilereport;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.collections.List;

// import webui.utils.WebSocketWriter;

public class CompileReport {

    //private static CompileReport INSTANCE = new CompileReport();
    private boolean vc = false;
    private boolean prove = false;
    private boolean translate = false;
    private boolean jar = false;
    private boolean error = false;
    private String facilityName = "";
    private String proveVCs = null;
    private boolean myErrors = false;
    private StringBuilder myErrorBuffer = new StringBuilder();
    private boolean myBugReports = false;
    private StringBuilder myBugReportBuffer = new StringBuilder();
    private String myOutput = "";

    //public  WebSocketWriter myWsWriter = null;
    //private List<String> proveList = null;

    public CompileReport() {
    //proveList = new List<String>();
    }

    /*public static CompileReport getInstance(){
    	return INSTANCE;
    }*/

    public void resetReport() {
        vc = false;
        prove = false;
        translate = false;
        jar = false;
        error = false;
        facilityName = "";
        proveVCs = null;
    }

    public void setVcSuccess() {
        vc = true;
    }

    public void setProveSuccess() {
        prove = true;
    }

    public void setProveVCs(String s) {
        proveVCs = s;
    }

    public void setTranslateSuccess() {
        translate = true;
    }

    public void setJarSuccess() {
        jar = true;
    }

    public void setError() {
        error = true;
    }

    public void setFacilityName(String facName) {
        facilityName = facName;
    }

    /*public void setWsWriter(WebSocketWriter writer){
        myWsWriter = writer;
    }*/

    public boolean vcSuccess() {
        return vc;
    }

    public boolean proveSuccess() {
        return prove;
    }

    public String getProveVCs() {
        return proveVCs;
    }

    public boolean translateSuccess() {
        return translate;
    }

    public boolean jarSuccess() {
        return jar;
    }

    public boolean hasError() {
        return error;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public boolean hasErrors() {
        return myErrors;
    }

    public void addError(String error) {
        if (myErrors) {
            myErrorBuffer.append(",");
        }
        myErrorBuffer.append("{\"error\":");
        myErrorBuffer.append(error);
        myErrorBuffer.append("}");
        myErrors = true;
        this.error = true;
    }

    public String getErrors() {
        String ret = "\"errors\":[";
        ret += myErrorBuffer.toString();
        ret += "]";
        return ret;
    }

    public boolean hasBugReports() {
        return myBugReports;
    }

    public void addBugReport(String bug) {
        if (myBugReports) {
            myBugReportBuffer.append(",");
        }
        myBugReportBuffer.append("{\"bug\":\"");
        myBugReportBuffer.append(ResolveCompiler.webEncode(bug));
        myBugReportBuffer.append("\"}");
        myBugReports = true;
    }

    public String getBugReports() {
        String ret = "\"bugs\":[";
        ret += myBugReportBuffer.toString();
        ret += "]";
        return ret;
    }

    public String getOutput() {
        return myOutput;
    }

    public void setOutput(String op) {
        myOutput = op;
    }
}

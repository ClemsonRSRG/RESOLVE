package edu.clemson.cs.r2jt.compilereport;

import edu.clemson.cs.r2jt.ResolveCompiler;
import edu.clemson.cs.r2jt.collections.List;

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

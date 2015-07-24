/**
 * CompileReport.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.init;

/**
 * <p>This is a compilation report used by the WebIDE/WebAPI.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class CompileReport {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private StringBuilder myBugReportBuffer;
    private boolean myBugReports;
    private boolean myBuildJarSuccess;
    private boolean myErrors;
    private StringBuilder myErrorBuffer;
    private String myFacilityName;
    private String myOutput;
    private boolean myTranslateSuccess;
    private boolean myVCSuccess;

    // ===========================================================
    // Constructors
    // ===========================================================

    public CompileReport() {
        myBugReportBuffer = new StringBuilder();
        myBugReports = false;
        myBuildJarSuccess = false;
        myErrorBuffer = new StringBuilder();
        myErrors = false;
        myFacilityName = "";
        myOutput = "";
        myTranslateSuccess = false;
        myVCSuccess = false;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void setError() {
        myErrors = true;
    }

    public void setJarSuccess() {
        myBuildJarSuccess = true;
    }

    public void setTranslateSuccess() {
        myTranslateSuccess = true;
    }

    public void setVCSuccess() {
        myVCSuccess = true;
    }

}
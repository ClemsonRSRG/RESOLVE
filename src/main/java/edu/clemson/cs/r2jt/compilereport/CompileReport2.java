/**
 * CompileReport2.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.compilereport;

/**
 * TODO: Description for this class
 */
public class CompileReport2 {

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

    public CompileReport2() {
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

    public void resetReport() {
        myVCSuccess = false;
        myTranslateSuccess = false;
        myBuildJarSuccess = false;
        myErrors = false;
        myFacilityName = "";
    }

    public void setTranslateSuccess() {
        myTranslateSuccess = true;
    }

    public void setVCSuccess() {
        myVCSuccess = true;
    }

}
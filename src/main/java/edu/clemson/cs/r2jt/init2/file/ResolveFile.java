/**
 * ResolveFile.java
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
package edu.clemson.cs.r2jt.init2.file;

import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;

/**
 * TODO: Description for this class
 */
public class ResolveFile {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final ANTLRInputStream myInputStream;
    private final String myCreatedJarTempPath;
    private final String myModuleFileName;
    private final ModuleType myModuleFileType;
    private final List<String> myPkgList;
    private final String myWorkspaceAbsolutePath;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ResolveFile(String name, ModuleType moduleType,
            ANTLRInputStream input, String workspacePath,
            List<String> packageList, String jarTempPath) {
        myInputStream = input;
        myCreatedJarTempPath = jarTempPath;
        myModuleFileName = name;
        myModuleFileType = moduleType;
        myPkgList = packageList;
        myWorkspaceAbsolutePath = workspacePath;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public ANTLRInputStream getInputStream() {
        return myInputStream;
    }

    public String getCreatedJarTempPath() {
        return myCreatedJarTempPath;
    }

    public String getName() {
        return myModuleFileName;
    }

    public ModuleType getModuleType() {
        return myModuleFileType;
    }

    public List<String> getPkgList() {
        return myPkgList;
    }

    public String getWorkspaceAbsolutePath() {
        return myWorkspaceAbsolutePath;
    }

}
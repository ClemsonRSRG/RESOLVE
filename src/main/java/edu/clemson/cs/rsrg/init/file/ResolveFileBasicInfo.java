/*
 * ResolveFileBasicInfo.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.init.file;

/**
 * <p>This class contains all the basic information for describing
 * a {@link ResolveFile}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ResolveFileBasicInfo {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>File's name.</p> */
    private final String myModuleFileName;

    /** <p>File's extension type.</p> */
    private final ModuleType myModuleFileType;

    /** <p>File's "parent" directory name.</p> */
    private final String myModuleParentDirName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructor takes all the information relevant
     * from the original source object and stores the basic information
     * about the "file".</p>
     *
     * @param name Filename.
     * @param moduleType File extension type.
     * @param parentDirName The parent directory name if it is known.
     *                      Otherwise, this can be {@code ""}.
     */
    public ResolveFileBasicInfo(String name, ModuleType moduleType,
            String parentDirName) {
        myModuleFileName = name;
        myModuleFileType = moduleType;
        myModuleParentDirName = parentDirName;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default equals method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ResolveFileBasicInfo that = (ResolveFileBasicInfo) o;

        if (!myModuleFileName.equals(that.myModuleFileName))
            return false;
        if (!myModuleFileType.equals(that.myModuleFileType))
            return false;
        return myModuleParentDirName.equals(that.myModuleParentDirName);
    }

    /**
     * <p>This is the actual name of the file.</p>
     *
     * @return Filename
     */
    public final String getName() {
        return myModuleFileName;
    }

    /**
     * <p>This retrieves the internal representation of
     * the extension.</p>
     *
     * @return File's extension.
     */
    public final ModuleType getModuleType() {
        return myModuleFileType;
    }

    /**
     * <p>This name of the parent directory.</p>
     *
     * @return File's parent directory name.
     */
    public final String getParentDirName() {
        return myModuleParentDirName;
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myModuleFileName.hashCode();
        result = 31 * result + myModuleFileType.hashCode();
        result = 31 * result + myModuleParentDirName.hashCode();
        return result;
    }

    /**
     * <p>Returns the name of the file in string format.</p>
     *
     * @return File as a string.
     */
    @Override
    public final String toString() {
        return myModuleFileName + "." + myModuleFileType.getExtension();
    }

}
/*
 * ResolveFile.java
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
package edu.clemson.cs.rsrg.init.file;

import java.nio.file.Path;
import java.util.List;
import org.antlr.v4.runtime.CharStream;

/**
 * <p>This class is the standard "file" format for the RESOLVE compiler.
 * Since the compiler can be invoked from the web interface, we need to
 * be able to support both "real" and "meta" files. Rather than having
 * two compilation paths that look almost identical, we convert all
 * inputs and create an instance of this class.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ResolveFile {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>This contains all the basic information about this "file".</p> */
    private final ResolveFileBasicInfo myFileBasicInfo;

    /** <p>Path where is this file is located in our workspace.</p> */
    private final String myFilePath;

    /** <p>Input stream that will contain all the RESOLVE source code.</p> */
    private final CharStream myInputStream;

    /** <p>File's extension type.</p> */
    private final ModuleType myModuleFileType;

    /** <p>Path of the Parent File. (May be {@code null}).</p> */
    private final Path myParentPath;

    /** <p>RESOLVE's package structure (Translator/Archiver).</p> */
    private final List<String> myPkgList;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructor takes all the information relevant
     * from the original source object and creates a "file" object
     * that the compiler will operate on.</p>
     *
     * @param fileBasicInfo Basic information about the file.
     * @param moduleType File extension type.
     * @param input The source code input stream.
     * @param parentPath The parent path if it is known. Otherwise,
     *                   this can be {@code null}.
     * @param packageList The package where this source file belong.
     * @param filePath The path where this file was found.
     */
    public ResolveFile(ResolveFileBasicInfo fileBasicInfo,
            ModuleType moduleType, CharStream input, Path parentPath,
            List<String> packageList, String filePath) {
        myInputStream = input;
        myFileBasicInfo = fileBasicInfo;
        myFilePath = filePath;
        myModuleFileType = moduleType;
        myParentPath = parentPath;
        myPkgList = packageList;
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

        ResolveFile that = (ResolveFile) o;

        if (!myFileBasicInfo.equals(that.myFileBasicInfo))
            return false;
        if (!myFilePath.equals(that.myFilePath))
            return false;
        if (!myInputStream.equals(that.myInputStream))
            return false;
        if (!myModuleFileType.equals(that.myModuleFileType))
            return false;
        if (myParentPath != null ? !myParentPath.equals(that.myParentPath)
                : that.myParentPath != null)
            return false;
        return myPkgList.equals(that.myPkgList);
    }

    /**
     * <p>This returns a path where this {@link ResolveFile} would
     * be located in our workspace.</p>
     *
     * @return A path in our current system.
     */
    public final String getFilePath() {
        return myFilePath;
    }

    /**
     * <p>Obtains the input stream that contains the source code.</p>
     *
     * @return An input stream for ANTLR4.
     */
    public final CharStream getInputStream() {
        return myInputStream;
    }

    /**
     * <p>This is the actual name of the file.</p>
     *
     * @return Filename
     */
    public final String getName() {
        return myFileBasicInfo.getName();
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
        return myFileBasicInfo.getParentDirName();
    }

    /**
     * <p>This returns the parent path associated with this
     * {@link ResolveFile}.</p>
     *
     * @return The parent file's path.
     */
    public final Path getParentPath() {
        return myParentPath;
    }

    /**
     * <p>If the option to translate to Java or create an executable
     * jar is given, this is the RESOLVE folder structure for the
     * given file.</p>
     *
     * @return List of directory names that form the package.
     */
    public final List<String> getPkgList() {
        return myPkgList;
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myFileBasicInfo.hashCode();
        result = 31 * result + myFilePath.hashCode();
        result = 31 * result + myInputStream.hashCode();
        result = 31 * result + myModuleFileType.hashCode();
        result =
                31 * result
                        + (myParentPath != null ? myParentPath.hashCode() : 0);
        result = 31 * result + myPkgList.hashCode();
        return result;
    }

    /**
     * <p>Returns the name of the file in string format.</p>
     *
     * @return File as a string.
     */
    @Override
    public final String toString() {
        return myFileBasicInfo.toString() + "."
                + myModuleFileType.getExtension();
    }

}
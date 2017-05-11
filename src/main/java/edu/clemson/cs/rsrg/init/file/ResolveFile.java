/*
 * ResolveFile.java
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

    /** <p>Location for the created jar (Archiver).</p> */
    private final String myCreatedJarPath;

    /** <p>Input stream that will contain all the RESOLVE source code.</p> */
    private final CharStream myInputStream;

    /** <p>File's name.</p> */
    private final String myModuleFileName;

    /** <p>File's extension type.</p> */
    private final ModuleType myModuleFileType;

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
     * @param name Filename.
     * @param moduleType File extension type.
     * @param input The source code input stream.
     * @param packageList The package where this source file belong.
     * @param jarPath The path where we want the jar to be generated.
     */
    public ResolveFile(String name, ModuleType moduleType, CharStream input,
            List<String> packageList, String jarPath) {
        myInputStream = input;
        myCreatedJarPath = jarPath;
        myModuleFileName = name;
        myModuleFileType = moduleType;
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

        if (!myCreatedJarPath.equals(that.myCreatedJarPath))
            return false;
        if (!myInputStream.equals(that.myInputStream))
            return false;
        if (!myModuleFileName.equals(that.myModuleFileName))
            return false;
        if (!myModuleFileType.equals(that.myModuleFileType))
            return false;

        return myPkgList.equals(that.myPkgList);
    }

    /**
     * <p>If the option to create an executable jar is given,
     * this is the place where the file will be created.</p>
     *
     * @return A path in our current system.
     */
    public final String getCreatedJarPath() {
        return myCreatedJarPath;
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
        int result = myCreatedJarPath.hashCode();
        result = 31 * result + myInputStream.hashCode();
        result = 31 * result + myModuleFileName.hashCode();
        result = 31 * result + myModuleFileType.hashCode();
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
        return myModuleFileName + "." + myModuleFileType.getExtension();
    }

}
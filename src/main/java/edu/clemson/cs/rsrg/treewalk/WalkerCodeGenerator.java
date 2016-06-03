/**
 * WalkerCodeGenerator.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.treewalk;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import org.reflections.Reflections;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import java.io.*;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class generates a Java abstract class containing dummy implementations
 * for each node in the RESOLVE AST hierarchy.</p>
 *
 * @author Blair Durkee
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 2.0
 */
public class WalkerCodeGenerator {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>String template for generating the tree.</p> */
    private static final STGroup GROUP =
            new STGroupFile("templates/walker.stg");

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Generates a tree walker.</p>
     *
     * <p>Two optional arguments in the array:
     * <ul>
     * <li>The desired name of the walker (default: {@code TreeWalkerVisitor})</li>
     * <li>The input package directory (default: {@code edu/clemson/cs/rsrg/absyn}</li>
     * <li>The output package directory (default: {@code edu/clemson/cs/rsrg/treewalk})</li>
     * </ul>
     * </p>
     *
     * @param args Arguments required to perform the tree walker
     *             generation process.
     */
    public static void main(String[] args) {
        String walkerName;
        String inputPackageDir;
        String outputPackageDir;
        switch (args.length) {
        case 0:
            walkerName = "TreeWalkerVisitor";
            inputPackageDir = "edu/clemson/cs/rsrg/absyn";
            outputPackageDir = "edu/clemson/cs/rsrg/treewalk";
            break;
        case 3:
            walkerName = args[0];
            inputPackageDir = args[1];
            outputPackageDir = args[2];
            break;
        default:
            throw new IllegalArgumentException(
                    "usage: Use default args or supply the following args [walker name, input directory, output directory]");
        }

        ST c =
                createClassTemplate(walkerName, inputPackageDir,
                        outputPackageDir);
        try {
            // Generate the output directory string
            String javaSrcDir = "/src/main/java/";
            String targetDir =
                    Paths.get(".").toAbsolutePath().normalize().toString()
                            + javaSrcDir + outputPackageDir + "/";
            targetDir = targetDir.replace(File.separator, "/");

            // Write the contents to file
            Writer writer =
                    new BufferedWriter(new FileWriter(targetDir + walkerName
                            + ".java", false));
            writer.write(c.render());
            writer.close();

            System.out.println("Successfully created: " + walkerName + ".java");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private static ST createClassTemplate(String className, String inputDirectory, String outputDirectory) {
        // Replace with the correct file separator.
        String[] inputDirectoryArr = inputDirectory.split("/");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inputDirectoryArr.length; i++) {
            sb.append(inputDirectoryArr[i]);

            if (i < inputDirectoryArr.length-1) {
                sb.append(".");
            }
        }
        String inputPkg = sb.toString();

        String[] outputDirectoryArr = outputDirectory.split("/");
        StringBuffer sb2 = new StringBuffer();
        for (int i = 0; i < outputDirectoryArr.length; i++) {
            sb2.append(outputDirectoryArr[i]);

            if (i < outputDirectoryArr.length-1) {
                sb2.append(".");
            }
        }
        String outputPkg = sb2.toString();

        // Use reflection to retrieve all the classes.
        Reflections reflections = new Reflections(inputPkg);
        Set<Class<? extends ResolveConceptualElement>> absynClasses =
                reflections.getSubTypesOf(ResolveConceptualElement.class);

        // Create the walker class with the general information
        ST walkerClass =
                GROUP.getInstanceOf("walkerImplementation").add("pkgName",
                        outputPkg).add("filename", className);

        // Add in methods for each of the classes.
        // Also add in new imports if needed.
        Set<String> pkgNames = new HashSet<>();
        for (Class<?> e : absynClasses) {
            // Search for new imports to add.
            String pkgName = e.getPackage().getName();
            if (!pkgNames.contains(pkgName)) {
                // Add to walker and to our set.
                pkgNames.add(pkgName);

                ST imports = GROUP.getInstanceOf("walkerImports").add("importItem", pkgName);
                walkerClass.add("imports", imports);
            }

            // Add the default implementations for each class and
            // add to walker.
            ST defaultImplementations =
                    GROUP.getInstanceOf("walkerMethods").add("name",
                            e.getSimpleName()).add("qualName",
                            e.getCanonicalName()).add("isMember",
                            e.isMemberClass());
            walkerClass.add("methods", defaultImplementations);
        }

        return walkerClass;
    }
}
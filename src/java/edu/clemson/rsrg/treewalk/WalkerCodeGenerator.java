/*
 * WalkerCodeGenerator.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.treewalk;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * <p>
 * This class generates a Java abstract class containing dummy implementations for each node in the RESOLVE AST
 * hierarchy.
 * </p>
 *
 * @author Blair Durkee
 * @author Yu-Shan Sun
 * @author Daniel Welch
 *
 * @version 2.0
 */
public class WalkerCodeGenerator {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * String template for generating the tree.
     * </p>
     */
    private static final STGroup GROUP = new STGroupFile("templates/Treewalker.stg");

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Generates a tree walker.
     * </p>
     * <p>
     * Two optional arguments in the array:
     * </p>
     * <ul>
     * <li>The desired name of the walker (default: {@code TreeWalkerVisitor})</li>
     * <li>The desired name of the stack walker (default: {@code TreeWalkerStackVisitor})</li>
     * <li>The input package directory (default: {@code edu/clemson/cs/rsrg/absyn}</li>
     * <li>The output package directory (default: {@code edu/clemson/cs/rsrg/treewalk})</li>
     * </ul>
     *
     * @param args
     *            Arguments required to perform the tree walker generation process.
     */
    public static void main(String[] args) {
        String walkerName;
        String stackWalkerName;
        String inputPackageDir;
        String outputPackageDir;
        switch (args.length) {
            case 0:
                walkerName = "TreeWalkerVisitor";
                stackWalkerName = "TreeWalkerStackVisitor";
                inputPackageDir = "edu/clemson/cs/rsrg/absyn";
                outputPackageDir = "edu/clemson/cs/rsrg/treewalk";
                break;
            case 4:
                walkerName = args[0];
                stackWalkerName = args[1];
                inputPackageDir = args[2];
                outputPackageDir = args[3];
                break;
            default:
                throw new IllegalArgumentException("usage:\n"
                        + "\tSupply the following args: [walker name, stack walker name, input directory, output directory]\n"
                        + "\tor use the built-in default args.\n");
        }

        ST visitor = createDefaultVisitor(walkerName, inputPackageDir, outputPackageDir);
        ST stackVisitor = createStackVisitor(stackWalkerName, walkerName, inputPackageDir, outputPackageDir);
        try {
            // Generate the output directory string
            String javaSrcDir = "/src/main/java/";
            String targetDir = Paths.get(".").toAbsolutePath().normalize().toString() + javaSrcDir + outputPackageDir
                    + "/";
            targetDir = targetDir.replace(File.separator, "/");

            // Path and Charset
            Path walkerFilePath = Paths.get(targetDir, walkerName + ".java");
            Path stackWalkerFilePath = Paths.get(targetDir, stackWalkerName + ".java");
            Charset charset = Charset.forName("UTF-8");

            // Write the visitor contents to file
            Writer writer = Files.newBufferedWriter(walkerFilePath, charset);
            writer.write(visitor.render());
            writer.close();

            System.out.println("Successfully created: " + walkerName + ".java");

            // Write the stack visitor contents to file
            writer = Files.newBufferedWriter(stackWalkerFilePath, charset);
            writer.write(stackVisitor.render());
            writer.close();

            System.out.println("Successfully created: " + stackWalkerName + ".java");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This method uses {@code StringTemplate} to generate the default tree walker.
     * </p>
     *
     * @param className
     *            Name for the new default tree walker.
     * @param inputDirectory
     *            The source files directory.
     * @param outputDirectory
     *            The tree walker output directory.
     *
     * @return A completed {@code StringTemplate}.
     */
    private static ST createDefaultVisitor(String className, String inputDirectory, String outputDirectory) {
        // Replace with the correct file separator.
        String inputPkg = convertDirToPkg(inputDirectory);
        String outputPkg = convertDirToPkg(outputDirectory);

        // Use reflection to retrieve all the classes.
        Reflections reflections = new Reflections(inputPkg);
        Set<Class<? extends ResolveConceptualElement>> absynClasses = reflections
                .getSubTypesOf(ResolveConceptualElement.class);

        // Create the walker class with the general information
        ST walkerClass = GROUP.getInstanceOf("walkerImplementation").add("pkgName", outputPkg).add("className",
                className);

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
            ST defaultImplementations = GROUP.getInstanceOf("walkerMethods").add("name", e.getSimpleName())
                    .add("qualName", e.getCanonicalName()).add("isMember", e.isMemberClass());
            walkerClass.add("methods", defaultImplementations);

            // Generate methods for before visiting a list of items.
            // This way we can add some logic before visiting elements inside the list.
            Set<Field> fields = ReflectionUtils.getAllFields(e);
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    Class<?> fieldType = field.getType();

                    try {
                        // is this member a list of ResolveConceptualElements?
                        // if so, create a visit method for it
                        if (java.util.List.class.isAssignableFrom(fieldType)) {
                            Class<?> listOf = (Class<?>) ((ParameterizedType) field.getGenericType())
                                    .getActualTypeArguments()[0];
                            if (ResolveConceptualElement.class.isAssignableFrom(listOf)) {
                                ST listDefaultImplementations = GROUP.getInstanceOf("walkerVirtualListMethods")
                                        .add("name", e.getSimpleName() + toCamelCase(field.getName()))
                                        .add("qualClassName", e.getCanonicalName()).add("className", e.getSimpleName())
                                        .add("isMember", e.isMemberClass());
                                walkerClass.add("methods", listDefaultImplementations);
                            }
                        }
                    } catch (RuntimeException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        return walkerClass;
    }

    /**
     * <p>
     * This method uses {@code StringTemplate} to generate a stack-based tree walker that inherits from the default
     * walker.
     * </p>
     *
     * @param className
     *            Name for the new stack tree walker.
     * @param parentWalkerName
     *            Name for the default walker.
     * @param inputDirectory
     *            The source files directory.
     * @param outputDirectory
     *            The tree walker output directory.
     *
     * @return A completed {@code StringTemplate}.
     */
    private static ST createStackVisitor(String className, String parentWalkerName, String inputDirectory,
            String outputDirectory) {
        // Replace with the correct file separator.
        String inputPkg = convertDirToPkg(inputDirectory);
        String outputPkg = convertDirToPkg(outputDirectory);

        // Create the stack walker class with the general information
        ST walkerClass = GROUP.getInstanceOf("walkerStackImplementation").add("pkgName", outputPkg)
                .add("className", className).add("parentClassName", parentWalkerName);

        // Add in the import for ResolveConceptualElement
        ST imports = GROUP.getInstanceOf("walkerImports").add("importItem", inputPkg);
        walkerClass.add("imports", imports);

        return walkerClass;
    }

    /**
     * <p>
     * This method converts the directory path to package names.
     * </p>
     *
     * @param dir
     *            Directory path.
     *
     * @return String containing the converted package name.
     */
    private static String convertDirToPkg(String dir) {
        // Replace with the correct file separator.
        String[] dirArray = dir.split("/");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < dirArray.length; i++) {
            sb.append(dirArray[i]);

            if (i < dirArray.length - 1) {
                sb.append(".");
            }
        }

        return sb.toString();
    }

    /**
     * <p>
     * Applies proper camel casing to the string passed in.
     * </p>
     *
     * @param s
     *            Original string.
     *
     * @return Modified string.
     */
    private static String toCamelCase(String s) {
        StringBuilder buffer = new StringBuilder();
        StringTokenizer tokens = new StringTokenizer(s, "_");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            buffer.append(Character.toUpperCase(token.charAt(0)));
            buffer.append(token.substring(1));
        }

        return buffer.toString();
    }

}

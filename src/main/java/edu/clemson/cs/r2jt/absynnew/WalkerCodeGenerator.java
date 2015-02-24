/**
 * WalkerCodeGenerator.java
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
package edu.clemson.cs.r2jt.absynnew;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class generates a <tt>Java</tt> treewalking abstract class
 * containing dummy implementations for each node in <tt>RESOLVE</tt>s AST
 * hierarchy.</p>
 */
public class WalkerCodeGenerator {

    private static final STGroup GROUP =
            new STGroupFile("templates/walker.stg");
    private static final String OUT_DIR = "edu/clemson/cs/r2jt/absynnew";

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                throw new IllegalArgumentException("usage: [walker name]");
            }

            ST c = createClassTemplate(args[0]);

            FileWriter fileWriter =
                    new FileWriter(OUT_DIR + "/"
                            + c.getAttribute("name").toString() + ".java");

            BufferedWriter result = new BufferedWriter(fileWriter);
            result.write(c.render());
            result.close();

            System.out.println("successfully created "
                    + c.getAttribute("name").toString() + ".java");

        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static ST createClassTemplate(String className) {
        /*Reflections reflections =
                  new Reflections("edu.clemson.cs.r2jt.absynnew");

          Set<Class<? extends ResolveAST>> absynClasses =
                  reflections.getSubTypesOf(ResolveAST.class);*/
        Set<Class<? extends ResolveAST>> absynClasses =
                new HashSet<Class<? extends ResolveAST>>();
        ST walkerClass =
                GROUP.getInstanceOf("WalkerImplementation").add("name",
                        className);

        for (Class<?> e : absynClasses) {

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
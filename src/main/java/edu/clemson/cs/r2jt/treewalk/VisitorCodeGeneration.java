package edu.clemson.cs.r2jt.treewalk;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.absyn.VirtualListNode;
import edu.clemson.cs.r2jt.collections.List;
import java.lang.reflect.*;
import java.util.StringTokenizer;

public class VisitorCodeGeneration {

    private static String myPackagePath = "edu.clemson.cs.r2jt.";

    /**
     * Generates a treewalker. Two optional argument sin the array:
     * 1: the desired name of the walker (default: TreeWalker)
     * 2: the output package directory (default: treewalk)
     * @param String array
     */
    public static void main(String[] args) {
        String walkerName = "TreeWalkerVisitor";
        String outputPackage = "treewalk";
        if (args.length > 0) {
            walkerName = args[0];
        }
        if (args.length == 2) {
            outputPackage = args[1];
        }
        String packageName = myPackagePath + outputPackage;
        StringBuilder buffer = generateVisitorClass(walkerName);
        ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(path);
            ArrayList<File> dirs = new ArrayList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                URI resourceURI = resource.toURI();
                dirs.add(new File(resourceURI.getPath()));
            }
            for (File directory : dirs) {
                String targetDir =
                        directory.getAbsolutePath().replace("bin", "src");
                String outputFile =
                        targetDir + File.separator + walkerName + ".java";
                FileWriter fstream = new FileWriter(outputFile);
                BufferedWriter out = new BufferedWriter(fstream);
                out.append("package " + packageName + ";\n\n");
                out.append(buffer);
                System.out.println("Successfully created " + outputFile);
                out.close();
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static StringBuilder generateVisitorClass(String walkerName) {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("import ");
        buffer.append(myPackagePath);
        buffer.append("absyn.*;\n");
        buffer.append("import edu.clemson.cs.r2jt.data.PosSymbol;\n\n");
        buffer.append("public abstract class ");
        buffer.append(walkerName);
        buffer.append(" {\n");
        buffer
                .append("\tpublic void preAny(ResolveConceptualElement data) { }\n");
        buffer
                .append("\tpublic void postAny(ResolveConceptualElement data) { }\n\n");
        try {
            Class<?>[] absynClasses = getClasses(myPackagePath + "absyn");
            for (Class<?> absynClass : absynClasses) {
                if (ResolveConceptualElement.class.isAssignableFrom(absynClass)) {
                    if (VirtualListNode.class.isAssignableFrom(absynClass)) {
                        continue;
                    }

                    String className = absynClass.getSimpleName();

                    addMethods(buffer, className, className,
                            "ResolveConceptualElement");

                    Field[] curFields = absynClass.getDeclaredFields();
                    for (Field curField : curFields) {
                        if (List.class.isAssignableFrom(curField.getType())) {
                            String typeParam =
                                    ((Class<?>) ((ParameterizedType) curField
                                            .getGenericType())
                                            .getActualTypeArguments()[0])
                                            .getSimpleName();
                            String listName = toCamelCase(curField.getName());
                            buffer.append("\n");
                            addMethods(buffer, className + listName, className,
                                    typeParam);
                        }
                    }
                    buffer.append("\n");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        buffer.append("}\n");
        return buffer;
    }

    public static String extractSimpleName(String qualifiedName) {
        StringTokenizer tokens = new StringTokenizer(qualifiedName, " .");
        String token = "";
        while (tokens.hasMoreTokens()) {
            token = tokens.nextToken();
        }
        return token;
    }

    public static void addMethods(StringBuilder buffer, String methodName,
            String className, String midTypeParameter) {
        // Class name comment
        buffer.append("// ");
        buffer.append(methodName);
        buffer.append("\n");

        // Pre method
        buffer.append("\tpublic void pre");
        buffer.append(methodName);
        buffer.append("(");
        buffer.append(className);
        buffer.append(" data) { }\n");

        // Mid method
        buffer.append("\tpublic void mid");
        buffer.append(methodName);
        buffer.append("(");
        buffer.append(className);
        buffer.append(" node, ");
        buffer.append(midTypeParameter);
        buffer.append(" previous, ");
        buffer.append(midTypeParameter);
        buffer.append(" next) { }\n");

        // Post method
        buffer.append("\tpublic void post");
        buffer.append(methodName);
        buffer.append("(");
        buffer.append(className);
        buffer.append(" data) { }\n");
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws URISyntaxException 
     */
    private static Class<?>[] getClasses(String packageName)
            throws ClassNotFoundException,
                IOException,
                URISyntaxException {
        ClassLoader classLoader =
                Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        ArrayList<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            URI resourceURI = resource.toURI();
            dirs.add(new File(resourceURI.getPath()));
        }
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static ArrayList<Class<?>> findClasses(File directory,
            String packageName) throws ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "."
                        + file.getName()));
            }
            else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName
                        + '.'
                        + file.getName().substring(0,
                                file.getName().length() - 6)));
            }
        }
        return classes;
    }

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

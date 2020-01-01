/*
 * FileLocator.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.init;

import java.io.File;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.errors.*;

public class FileLocator {

    // ===========================================================
    // Constructors
    // ===========================================================

    public FileLocator() {
        ;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * Recursively searches the specified directory tree for a file with the
     * specified name. If
     * exactly one file is found, it returns that file. If no file is found a
     * file-not-found exception
     * is thrown, if more than one file is found, a multi-files-found exception
     * is thrown.
     */
    public File locateFileInTree(String name, File dir)
            throws FileLocatorException {
        List<File> files = recursivelyLocateFiles(name, dir);
        if (files.size() == 0) {
            String msg = noFileMessage(name, dir.getName());
            throw new FileLocatorException(msg);
        }
        else if (files.size() == 1) {
            return files.get(0);
        }
        else { // files.size() > 1
            String msg =
                    multiFilesMessage(name, dir.getName(), files.toString());
            throw new FileLocatorException(msg);
        }
    }

    /**
     * Recursively searches the specified directory tree for a file with any of
     * the specified names.
     * If exactly one file is found, it returns that file. If no file is found a
     * file-not-found
     * exception is thrown, if more than one file is found, a multi-files-found
     * exception is thrown.
     */
    public File locateFileInTree(String name1, String name2, String name3,
            String name4, File dir)
            throws FileLocatorException {
        List<File> files = new List<File>();
        files.addAll(recursivelyLocateFiles(name1, dir));
        files.addAll(recursivelyLocateFiles(name2, dir));
        files.addAll(recursivelyLocateFiles(name3, dir));
        files.addAll(recursivelyLocateFiles(name4, dir));
        if (files.size() == 0) {
            String msg =
                    noFileMessage4(name1, name2, name3, name4, dir.getName());
            throw new FileLocatorException(msg);
        }
        else if (files.size() == 1) {
            return files.get(0);
        }
        else { // files.size() > 1
            String msg = multiFilesMessage4(name1, name2, name3, name4,
                    dir.getName(), files.toString());
            throw new FileLocatorException(msg);
        }
    }

    /**
     * Searches the specified directory for a file with the specified name and
     * returns that file. If
     * no file is found a file-not-found exception is thrown.
     */
    public File locateFileInDir(String name, File dir)
            throws FileLocatorException {
        // System.out.println("locating: "+name+" : "+dir.getAbsolutePath());
        File resultFile = null;
        File[] fileArray = dir.listFiles();
        List<File> files = new List<File>();
        // JMH avoid problems with 1.5 generics files.addAll(fileArray);
        for (int i = 0; i < fileArray.length; i++) {
            files.add(fileArray[i]);
        }

        Iterator<File> i = files.iterator();
        while (i.hasNext()) {
            File file = i.next();
            if (file.getName().equals(name)) {
                resultFile = file;
                break;
            }
        }
        if (resultFile == null) {
            String msg = noFileInDirMessage(name, dir.getName());
            throw new FileLocatorException(msg);
        }
        else {
            return resultFile;
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private List<File> recursivelyLocateFiles(String name, File dir) {
        List<File> resultFiles = new List<File>();
        File[] fileArray = dir.listFiles();
        List<File> files = new List<File>();
        // JMH avoid problems with 1.5 generics files.addAll(fileArray);
        for (int i = 0; i < fileArray.length; i++) {
            files.add(fileArray[i]);
        }
        Iterator<File> i = files.iterator();
        while (i.hasNext()) {
            File file = i.next();
            if (file.isDirectory()) {
                resultFiles.addAll(recursivelyLocateFiles(name, file));
            }
            else if (file.getName().equals(name)) {
                resultFiles.add(file);
            }
        }
        return resultFiles;
    }

    // -----------------------------------------------------------
    // Error Related Methods
    // -----------------------------------------------------------

    private String noFileMessage(String name, String dir) {
        String msg = "Could not find a file with name " + name
                + " in the directory " + dir + " or any of its subdirectories.";
        return msg;
    }

    private String multiFilesMessage(String name, String dir, String files) {
        String msg =
                "Found multiple files with name " + name + " in the directory "
                        + dir + " or its subdirectories: " + files;
        return msg;
    }

    private String noFileInDirMessage(String name, String dir) {
        String msg = "Could not find a file with name " + name
                + " in the directory " + dir + ".";
        return msg;
    }

    private String noFileMessage4(String name1, String name2, String name3,
            String name4, String dir) {
        String msg = "Could not find a file with name " + name1 + " or " + name2
                + " or " + name3 + " or " + name4 + " in the directory " + dir
                + " or any of its subdirectories.";
        return msg;
    }

    private String multiFilesMessage4(String name1, String name2, String name3,
            String name4, String dir, String files) {
        String msg = "Found multiple files with name " + name1 + " or " + name2
                + " or " + name3 + " or " + name4 + " in the directory " + dir
                + " or its subdirectories: " + files;
        return msg;
    }

}

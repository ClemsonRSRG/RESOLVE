/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */

/*
 * ModuleLocator.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
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
    
    public FileLocator() { ; }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * Recursively searches the specified directory tree for a file
     * with the specified name. If exactly one file is found, it
     * returns that file. If no file is found a file-not-found
     * exception is thrown, if more than one file is found, a
     * multi-files-found exception is thrown.
     */
    public File locateFileInTree(String name, File dir)
        throws FileLocatorException
    {
        List<File> files = recursivelyLocateFiles(name, dir);
        if (files.size() == 0) {
            String msg = noFileMessage(name, dir.getName());
            throw new FileLocatorException(msg);
        } else if (files.size() == 1) {
            return files.get(0);
        } else { // files.size() > 1
            String msg = multiFilesMessage(name, dir.getName(),
                                           files.toString());
            throw new FileLocatorException(msg);
        }
    }

    /**
     * Recursively searches the specified directory tree for a file
     * with any of the specified names. If exactly one file is
     * found, it returns that file. If no file is found a
     * file-not-found exception is thrown, if more than one file is
     * found, a multi-files-found exception is thrown.
     */
    public File locateFileInTree(String name1, String name2, String name3,
                                 File dir)
        throws FileLocatorException
    {
        List<File> files = new List<File>();
        files.addAll(recursivelyLocateFiles(name1, dir));
        files.addAll(recursivelyLocateFiles(name2, dir));
        files.addAll(recursivelyLocateFiles(name3, dir));
        if (files.size() == 0) {
            String msg = noFileMessage3(name1, name2, name3, dir.getName());
            throw new FileLocatorException(msg);
        } else if (files.size() == 1) {
            return files.get(0);
        } else { // files.size() > 1
            String msg = multiFilesMessage3(name1, name2, name3, dir.getName(),
                                            files.toString());
            throw new FileLocatorException(msg);
        }
    }

    /**
     * Searches the specified directory for a file with the specified
     * name and returns that file. If no file is found a
     * file-not-found exception is thrown.
     */
    public File locateFileInDir(String name, File dir)
        throws FileLocatorException
    {
        //System.out.println("locating: "+name+" : "+dir.getAbsolutePath());
        File resultFile = null;
        File[] fileArray = dir.listFiles();
        List<File> files = new List<File>();
        //JMH avoid problems with 1.5 generics files.addAll(fileArray);
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
        } else {
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
        //JMH avoid problems with 1.5 generics files.addAll(fileArray);
        for (int i = 0; i < fileArray.length; i++) {
            files.add(fileArray[i]);
        }
        Iterator<File> i = files.iterator();
        while (i.hasNext()) {
            File file = i.next();
            if (file.isDirectory()) {
                resultFiles.addAll(recursivelyLocateFiles(name, file));
            } else if (file.getName().equals(name)) {
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
        String msg = "Found multiple files with name " + name
            + " in the directory " + dir + " or its subdirectories: "
            + files;
        return msg;
    }

    private String noFileInDirMessage(String name, String dir) {
        String msg = "Could not find a file with name " + name
            + " in the directory " + dir + ".";
        return msg;
    }

    private String noFileMessage3(String name1, String name2,
                                  String name3, String dir) {
        String msg = "Could not find a file with name " + name1
            + " or " + name2 + " or " + name3 + " in the directory " + dir
            + " or any of its subdirectories.";
        return msg;
    }

    private String multiFilesMessage3(String name1, String name2, String name3,
                                      String dir, String files) {
        String msg = "Found multiple files with name " + name1
            + " or " + name2 + " or " + name3 + " in the directory " + dir
            + " or its subdirectories: " + files;
        return msg;
    }

}

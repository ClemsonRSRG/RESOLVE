/**
 * FileLocator.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.init.file;

import edu.clemson.cs.rsrg.misc.Utilities;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Traverses a tree of directories. Each file encountered is reported via the
 * {@link #visitFile(Path, BasicFileAttributes)} method and each directory via
 * optional {@link #preVisitDirectory} or {@link #postVisitDirectory} methods.
 * Override others as needed.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 1.0
 */
public class FileLocator extends SimpleFileVisitor<Path> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The search path matcher currently used.</p> */
    private final PathMatcher myPathMatcher;

    /** <p>The name of the file.</p> */
    private String myPattern = null;

    /** <p>The list of resulting matches.</p> */
    private List<File> myMatches = new ArrayList<>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * Constructs a new {@code FileLocator} that will match based on the
     * {@code (pattern, extensions)} pair provided.
     *
     * @param pattern An extensionless pattern.
     * @param extensions An list of valid extensions to choose from after a
     *                   myPattern is matched (e.g. {@code ["java", "cpp", "groovy"]}).
     */
    public FileLocator(String pattern, List<String> extensions) {
        myPattern = pattern;
        myPathMatcher =
                FileSystems.getDefault().getPathMatcher(
                        "glob:" + pattern + "." + parseExtensions(extensions));
    }

    /**
     * Constructs a new {@code FileLocator} that will match based on the
     * {@code extensions} provided.
     *
     * @param extension An list of valid extensions to choose from after a
     *                  myPattern is matched (e.g. {@code ["java", "cpp", "groovy"]}).
     */
    public FileLocator(String extension) {
        myPathMatcher =
                FileSystems.getDefault().getPathMatcher(
                        "glob:*.{" + extension + "}");
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Returns a single file matching the supplied pattern.</p>
     *
     * @throws NoSuchFileException If a file matching pattern could
     *         not be found.
     *
     * @return The matching file.
     */
    public File getFile() throws IOException {
        if (myMatches.size() == 0) {
            throw new NoSuchFileException("File matching name '" + myPattern
                    + "' could not be found");
        }
        return myMatches.get(0);
    }

    /**
     * <p>Returns a single file matching the supplied pattern.</p>
     *
     * @throws NoSuchFileException If a file matching pattern could
     *         not be found.
     *
     * @return The matching file.
     */
    public List<File> getFiles() {
        return myMatches;
    }

    /**
     * <p>Using a path and the basic file attributes, attempt to find
     * the file that matches.</p>
     *
     * @param file The current visiting path.
     * @param attr The file attributes.
     *
     * @return Always continue searching until done.
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        Path name = file.getFileName();
        if (name != null && myPathMatcher.matches(name)) {
            myMatches.add(file.toFile());
        }

        return FileVisitResult.CONTINUE;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Converts the module types into a search string for
     * extensions.</p>
     *
     * @param extensions The list of searching extensions.
     *
     * @return The search string for extensions.
     */
    private String parseExtensions(List<String> extensions) {
        return Utilities.join(extensions, ",", "{", "}");
    }

}
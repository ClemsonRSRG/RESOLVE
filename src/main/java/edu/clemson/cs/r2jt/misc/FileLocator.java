package edu.clemson.cs.r2jt.misc;

import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Traverses a tree of directories. Each file encountered is reported via the
 * <code>visitFile</code> method and each directory via an optional
 * <code>visitDirectory</code> method (override others as needed).</p>
 */
public class FileLocator extends SimpleFileVisitor<Path> {

    private final PathMatcher myMatcher;
    private String myPattern = null;

    private List<File> myMatches = new ArrayList<File>();

    /**
     * <p>Constructs a new <code>FileLocator</code> that will match based on the
     * <code>pattern</code> (or, name) and <code>extension</code> pair
     * provided.</p>
     *
     * @param pattern An <em>extensionless</em> pattern.
     * @param extensions An list of valid extensions to choose from after a
     *        pattern is matched (i.e. ["java", "cpp", "groovy"]).
     */
    public FileLocator(String pattern, List<String> extensions) {
        myPattern = pattern;

        myMatcher =
                FileSystems.getDefault().getPathMatcher(
                        "glob:" + pattern + parseExtensions(extensions));
    }

    public FileLocator(String extension) {
        myMatcher =
                FileSystems.getDefault().getPathMatcher(
                        "glob:*.{" + extension + "}");
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        Path name = file.getFileName();
        if (name != null && myMatcher.matches(name)) {
            myMatches.add(file.toFile());
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * <p>Returns a single file matching <code>myPattern</code>.</p>
     * @throws NoSuchFileException If a file matching <code>myPattern</code>
     *         could not be found.
     *
     * @return The matching file.
     */
    public File getFile() throws IOException {
        if (myMatches.size() == 0) {
            throw new NoSuchFileException("File matching name '" + myPattern
                    + "' could not be found.");
        }
        return myMatches.get(0);
    }

    public List<File> getFiles() {
        return myMatches;
    }

    private String parseExtensions(List<String> extensions) {
        ST result = new ST("*.{<exts; separator={,}>}");
        return result.add("exts", extensions).render();
    }
}
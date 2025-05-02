/*
 * VCGenerationTest.java
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
package edu.clemson.rsrg.vcgeneration.sequents;

import edu.clemson.rsrg.Utilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.fail;

public class VCGenerationTest {

    @BeforeAll
    public static void setup() {
        String jarFileName = Utilities.checkforJarFile();
        Utilities.initializeSubrepos();

        String repoPath = System.getProperty("user.dir");
        String executable = jarFileName;
        String returnPath = repoPath + "/RESOLVE-Workspace/Main";

        Utilities.executeCommand("rm -r *.asrt", returnPath);

        try {
            Files.walk(Paths.get(returnPath)).filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".rb")).forEach(path -> {
                        String file = path.toString();
                        String dir = path.getParent().toString();
                        String baseFile = path.getFileName().toString().replace(".rb", "");

                        System.out.println("Directory: " + dir);
                        System.out.println("File: " + file);

                        try {
                            Utilities.executeCommand("java -jar " + executable + " -VCs " + file, dir);

                            List<String> foundFiles = Files.walk(Paths.get(dir)).filter(Files::isRegularFile)
                                    .filter(p -> p.getFileName().toString().equals(baseFile + ".asrt"))
                                    .map(p -> p.toString()).toList();

                            for (String foundFile : foundFiles) {
                                String[] parts;
                                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                                    parts = foundFile.split("\\\\");
                                } else {
                                    parts = foundFile.split("/");
                                }
                                String lastDir = parts[parts.length - 2];

                                // Handle the case where the last directory is "Static_Array_Template"
                                // Skipping to prevent duplicate Do_Nothing_Realiz file
                                // This test is just a general test for VC generation and should not be run on the
                                // Static_Array_Template
                                // Is easier to skip this folder as enough testing has been done on VC generation.
                                if (lastDir.equals("Static_Array_Template")) {
                                    continue;
                                }

                                // Get last directory of the file
                                if (Files.exists(Paths.get(returnPath, baseFile + ".asrt"))) {
                                    if (parts.length < 2) {
                                        System.out.println("Error: Unable to parse path for file: " + foundFile);
                                        return;
                                    }

                                    Utilities.executeCommand(
                                            "mv " + foundFile + " " + lastDir + "_" + baseFile + ".asrt ", dir);
                                    Utilities.executeCommand("mv " + lastDir + "_" + baseFile + ".asrt " + returnPath,
                                            dir);
                                } else {
                                    Utilities.executeCommand("mv " + foundFile + " " + returnPath, dir);
                                }

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @TestFactory
    Collection<DynamicTest> testVCGeneration() {
        String relativeResolveWorkspacePath = "/RESOLVE-Workspace/Main/";
        File projectDir = new File(System.getProperty("user.dir"));
        // File parentDirFile = projectDir.getParentFile();

        File fullResolveWorkspacePath = new File(projectDir, relativeResolveWorkspacePath);
        System.out.println(fullResolveWorkspacePath.getAbsolutePath());

        ArrayList<String> testFiles = new ArrayList<>();
        ArrayList<String> controlFiles = new ArrayList<>();
        ArrayList<String> fileNames = new ArrayList<>();

        if (fullResolveWorkspacePath.isDirectory()) {

            FilenameFilter asrtFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".asrt");
                }
            };

            String[] asrtFiles = fullResolveWorkspacePath.list(asrtFilter);

            if (asrtFiles != null && asrtFiles.length > 0) {
                System.out.println(".asrt files found:");
                String controlFilesDir = "/test/validVCs/";
                File vcDir = new File(projectDir.getAbsolutePath(), controlFilesDir);
                for (String fileName : asrtFiles) {
                    File controlFile = new File(vcDir.getAbsolutePath(), fileName);
                    File testFile = new File(fullResolveWorkspacePath.getAbsolutePath(), fileName);

                    controlFiles.add(controlFile.getAbsolutePath());
                    testFiles.add(testFile.getAbsolutePath());
                    fileNames.add(fileName);

                }
            } else {
                Assertions.fail("No .asrt files found in the directory.");

            }
        } else {
            Assertions.fail("The specified path is not a directory.");
        }

        return IntStream.range(0, fileNames.size()).mapToObj(i -> DynamicTest.dynamicTest(fileNames.get(i), () -> {
            String testFile = testFiles.get(i);
            String controlFile = controlFiles.get(i);
            File test = new File(testFile);
            File control = new File(controlFile);

            if (!control.exists()) {
                fail("Control file does not exist: " + controlFile);
            }
            if (!test.exists()) {
                fail("Test file does not exist: " + testFile);
            }

            try {
                boolean filesAreEqual = filesAreEqualIgnoreLineEndingsAndEncoding(control, test);
                if (!filesAreEqual) {
                    fail("Files are not equal: " + controlFile + " and " + testFile);
                }
            } catch (IOException e) {
                fail("Error comparing files: " + e.getMessage());
            }
        })).toList();
    }

    public static boolean filesAreEqualIgnoreLineEndingsAndEncoding(File file1, File file2) throws IOException {
        List<String> linesFile1 = Files.readAllLines(file1.toPath(), StandardCharsets.UTF_8);
        List<String> linesFile2 = Files.readAllLines(file2.toPath(), StandardCharsets.UTF_8);

        if (linesFile1.size() != linesFile2.size()) {
            return false;
        }

        // starting at index 1 to ignore the first line which is the file header and contains timestamp
        for (int i = 1; i < linesFile1.size(); i++) {
            String line1 = linesFile1.get(i).trim();
            String line2 = linesFile2.get(i).trim();
            if (!line1.equals(line2)) {
                return false;
            }
        }
        return true;
    }
}

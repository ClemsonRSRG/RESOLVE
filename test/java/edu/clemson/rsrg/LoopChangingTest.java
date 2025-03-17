/*
 * LoopChangingTest.java
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
package edu.clemson.rsrg;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.fail;

public class LoopChangingTest {

    private static String JAR_FILE_NAME;

    public static void executeCommand(String command, String directory) {
        try {
            // Detect the operating system
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder;

            // Set the command based on the operating system
            if (os.contains("win")) {
                // Windows
                processBuilder = new ProcessBuilder("powershell.exe", "-Command", command);
            } else {
                // Linux or MacOS
                processBuilder = new ProcessBuilder("bash", "-c", command);
            }

            // Set the working directory if provided
            if (directory != null && !directory.isEmpty()) {
                processBuilder.directory(new File(directory));
            } else {
                processBuilder.directory(new File(System.getProperty("user.dir")));
            }

            File stdoutFile = new File("stdout.txt");
            File stderrFile = new File("stderr.txt");
            processBuilder.redirectOutput(stdoutFile);
            processBuilder.redirectError(stderrFile);

            // Start the process
            Process process = processBuilder.start();

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode + " for command: " + command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeAll
    public static void checkforJarFile() {
        // issue a maven clean install command
        // mvn clean install
        // check if the jar file exists in the target folder

        // Get the project directory

        File projectDir = new File(System.getProperty("user.dir"));
        File jarFileFolder = new File(projectDir, "target/");

        // Get list of .jar files in target folder
        FilenameFilter jarFilter = (dir, name) -> name.toLowerCase().endsWith(".jar");

        // get list of files in target folder as string array
        String[] files = jarFileFolder.list(jarFilter);
        if (files == null || files.length == 0) {
            System.out.println("No files in target folder");

            // issue a maven clean package command
            executeCommand("mvn package -DskipTests", "");
        }

        Pattern pattern = Pattern.compile("RESOLVE-.*-jar-with-dependencies.jar", Pattern.CASE_INSENSITIVE);
        files = jarFileFolder.list(jarFilter);
        // Check if the jar file exists
        boolean jarFileExists = false;

        for (String file : files) {
            Matcher matcher = pattern.matcher(file);
            if (matcher.find()) {
                System.out.println("Jar file found: " + file);
                File jarFile = new File(jarFileFolder, file);
                JAR_FILE_NAME = jarFile.getAbsolutePath();
                jarFileExists = true;
                break;
            }
        }

        if (!jarFileExists) {
            System.out.println("Jar file not found. Exiting...");
            System.exit(1);
        }

    }

    @Test
    public void loopChangingTest_no_modifications_expectSuccess() {
        // Run the RESOLVE compiler
        executeCommand("java -jar " + JAR_FILE_NAME + " -VCs Inject_Front_Realiz.rb",
                "RESOLVE-Workspace/Main/Concepts/Queue_Template/");

        // Check if the output file exists
        assert new File("RESOLVE-Workspace/Main/Concepts/Queue_Template/Inject_Front_Realiz.asrt").exists();

        executeCommand("rm Inject_Front_Realiz.asrt", "RESOLVE-Workspace/Main/Concepts/Queue_Template/");
    }

    @Test
    public void loopChangingTest_illegal_CallStmt_expectCompilerException() {

        try {
            // Run the RESOLVE compiler
            executeCommand(
                    "cp test/resources/Inject_Front_Realiz_CallStmt_Illegally_Changing.rb RESOLVE-Workspace/Main/Concepts/Queue_Template/Inject_Front_Realiz_CallStmt_Illegally_Changing.rb",
                    "");

            executeCommand("java -jar " + JAR_FILE_NAME + " -VCs Inject_Front_Realiz_CallStmt_Illegally_Changing.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");

            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(new FileInputStream("stderr.txt")));
            String errorString = "";
            String stderrLine;
            boolean foundError = false;
            while ((stderrLine = stderrReader.readLine()) != null) {
                errorString += stderrLine + "\n";
            }
            stderrReader.close();

            Pattern pattern = Pattern.compile(".*Fault: (.*)\\n.*\\nError: (.*)", Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(errorString);

            if (matcher.find()) {
                Assertions.assertEquals("Compiler Exception", matcher.group(1),
                        "Expected 'Compiler Exception' but found: " + matcher.group(1));
                Assertions.assertEquals("variable T appears in a call statement but not the changing clause",
                        matcher.group(2), "Expected specific error message but found: " + matcher.group(2));
            } else {
                Assertions.fail("Compiler Exception not found in stderr.txt");
            }

            // Check if the output file exists
        } catch (FileNotFoundException e) {
            Assertions.fail("FileNotFoundException occurred: " + e.getMessage());
        } catch (IOException e) {
            Assertions.fail("IOException occurred: " + e.getMessage());
        } finally {
            executeCommand("rm Inject_Front_Realiz_CallStmt_Illegally_Changing.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");
        }
    }

    @Test
    public void loopChangingTest_illegal_Left_SwapStmnt_expectCompilerException() {
        try {
            // Run the RESOLVE compiler
            executeCommand(
                    "cp test/resources/Inject_Front_Realiz_Left_SwapStmnt_Illegally_Changing.rb RESOLVE-Workspace/Main/Concepts/Queue_Template/Inject_Front_Realiz_Left_SwapStmnt_Illegally_Changing.rb",
                    "");

            executeCommand(
                    "java -jar " + JAR_FILE_NAME + " -VCs Inject_Front_Realiz_Left_SwapStmnt_Illegally_Changing.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");

            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(new FileInputStream("stderr.txt")));
            String errorString = "";
            String stderrLine;
            boolean foundError = false;
            while ((stderrLine = stderrReader.readLine()) != null) {
                errorString += stderrLine + "\n";
            }
            stderrReader.close();

            Pattern pattern = Pattern.compile(".*Fault: (.*)\\n.*\\nError: (.*)", Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(errorString);

            if (matcher.find()) {
                Assertions.assertEquals("Compiler Exception", matcher.group(1),
                        "Expected 'Compiler Exception' but found: " + matcher.group(1));
                Assertions.assertEquals("left variable T appears in a swap statement but not the changing clause",
                        matcher.group(2), "Expected specific error message but found: " + matcher.group(2));
            } else {
                Assertions.fail("Compiler Exception not found in stderr.txt");
            }

            // Check if the output file exists
        } catch (FileNotFoundException e) {
            Assertions.fail("FileNotFoundException occurred: " + e.getMessage());
        } catch (IOException e) {
            Assertions.fail("IOException occurred: " + e.getMessage());
        } finally {
            executeCommand("rm Inject_Front_Realiz_Left_SwapStmnt_Illegally_Changing.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");
        }
    }

    @Test
    public void loopChangingTest_illegal_Right_SwapStmnt_expectCompilerException() {
        try {
            // Run the RESOLVE compiler
            executeCommand(
                    "cp test/resources/Inject_Front_Realiz_Right_SwapStmnt_Illegally_Changing.rb RESOLVE-Workspace/Main/Concepts/Queue_Template/Inject_Front_Realiz_Right_SwapStmnt_Illegally_Changing.rb",
                    "");

            executeCommand(
                    "java -jar " + JAR_FILE_NAME + " -VCs Inject_Front_Realiz_Right_SwapStmnt_Illegally_Changing.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");

            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(new FileInputStream("stderr.txt")));
            String errorString = "";
            String stderrLine;
            boolean foundError = false;
            while ((stderrLine = stderrReader.readLine()) != null) {
                errorString += stderrLine + "\n";
            }
            stderrReader.close();

            Pattern pattern = Pattern.compile(".*Fault: (.*)\\n.*\\nError: (.*)", Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(errorString);

            if (matcher.find()) {
                Assertions.assertEquals("Compiler Exception", matcher.group(1),
                        "Expected 'Compiler Exception' but found: " + matcher.group(1));
                Assertions.assertEquals("right variable T appears in a swap statement but not the changing clause",
                        matcher.group(2), "Expected specific error message but found: " + matcher.group(2));
            } else {
                Assertions.fail("Compiler Exception not found in stderr.txt");
            }

            // Check if the output file exists
        } catch (FileNotFoundException e) {
            Assertions.fail("FileNotFoundException occurred: " + e.getMessage());
        } catch (IOException e) {
            Assertions.fail("IOException occurred: " + e.getMessage());
        } finally {
            executeCommand("rm Inject_Front_Realiz_Right_SwapStmnt_Illegally_Changing.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");
        }
    }

    @Test
    public void loopChangingTest_illegal_FuncAssignStmt_expectCompilerException() {
        try {
            // Run the RESOLVE compiler
            executeCommand(
                    "cp test/resources/Inject_Front_Realiz_FuncAssignStmt_Causing_Illegal_Var_Change.rb RESOLVE-Workspace/Main/Concepts/Queue_Template/Inject_Front_Realiz_FuncAssignStmt_Causing_Illegal_Var_Change.rb",
                    "");

            executeCommand(
                    "java -jar " + JAR_FILE_NAME
                            + " -VCs Inject_Front_Realiz_FuncAssignStmt_Causing_Illegal_Var_Change.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");

            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(new FileInputStream("stderr.txt")));
            String errorString = "";
            String stderrLine;
            boolean foundError = false;
            while ((stderrLine = stderrReader.readLine()) != null) {
                errorString += stderrLine + "\n";
            }
            stderrReader.close();

            Pattern pattern = Pattern.compile(".*Fault: (.*)\\n.*\\nError: (.*)", Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(errorString);

            if (matcher.find()) {
                Assertions.assertEquals("Compiler Exception", matcher.group(1),
                        "Expected 'Compiler Exception' but found: " + matcher.group(1));
                Assertions.assertEquals("variable Y appears in an assign statement but not the changing clause",
                        matcher.group(2), "Expected specific error message but found: " + matcher.group(2));
            } else {
                Assertions.fail("Compiler Exception not found in stderr.txt");
            }

            // Check if the output file exists
        } catch (FileNotFoundException e) {
            Assertions.fail("FileNotFoundException occurred: " + e.getMessage());
        } catch (IOException e) {
            Assertions.fail("IOException occurred: " + e.getMessage());
        } finally {
            executeCommand("rm Inject_Front_Realiz_FuncAssignStmt_Causing_Illegal_Var_Change.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");
        }
    }

}

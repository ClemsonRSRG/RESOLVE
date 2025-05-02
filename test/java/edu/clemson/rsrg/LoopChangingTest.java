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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoopChangingTest {

    private static String JAR_FILE_NAME;

    @BeforeAll
    public static void setup() {
        JAR_FILE_NAME = Utilities.checkforJarFile();
        Utilities.initializeSubrepos();
    }

    @Test
    public void loopChangingTest_no_modifications_expectSuccess() {
        // Run the RESOLVE compiler
        Utilities.executeCommand("java -jar " + JAR_FILE_NAME + " -VCs Inject_Front_Realiz.rb",
                "RESOLVE-Workspace/Main/Concepts/Queue_Template/");

        // Check if the output file exists
        assert new File("RESOLVE-Workspace/Main/Concepts/Queue_Template/Inject_Front_Realiz.asrt").exists();

        Utilities.executeCommand("rm Inject_Front_Realiz.asrt", "RESOLVE-Workspace/Main/Concepts/Queue_Template/");
    }

    @Test
    public void loopChangingTest_illegal_CallStmt_expectCompilerException() {

        try {
            // Run the RESOLVE compiler
            Utilities.executeCommand(
                    "cp test/resources/Inject_Front_Realiz_CallStmt_Illegally_Changing.rb RESOLVE-Workspace/Main/Concepts/Queue_Template/Inject_Front_Realiz_CallStmt_Illegally_Changing.rb",
                    "");

            Utilities.executeCommand(
                    "java -jar " + JAR_FILE_NAME + " -VCs Inject_Front_Realiz_CallStmt_Illegally_Changing.rb",
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
            Utilities.executeCommand("rm Inject_Front_Realiz_CallStmt_Illegally_Changing.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");
        }
    }

    @Test
    public void loopChangingTest_illegal_Left_SwapStmnt_expectCompilerException() {
        try {
            // Run the RESOLVE compiler
            Utilities.executeCommand(
                    "cp test/resources/Inject_Front_Realiz_Left_SwapStmnt_Illegally_Changing.rb RESOLVE-Workspace/Main/Concepts/Queue_Template/Inject_Front_Realiz_Left_SwapStmnt_Illegally_Changing.rb",
                    "");

            Utilities.executeCommand(
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
            Utilities.executeCommand("rm Inject_Front_Realiz_Left_SwapStmnt_Illegally_Changing.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");
        }
    }

    @Test
    public void loopChangingTest_illegal_Right_SwapStmnt_expectCompilerException() {
        try {
            // Run the RESOLVE compiler
            Utilities.executeCommand(
                    "cp test/resources/Inject_Front_Realiz_Right_SwapStmnt_Illegally_Changing.rb RESOLVE-Workspace/Main/Concepts/Queue_Template/Inject_Front_Realiz_Right_SwapStmnt_Illegally_Changing.rb",
                    "");

            Utilities.executeCommand(
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
            Utilities.executeCommand("rm Inject_Front_Realiz_Right_SwapStmnt_Illegally_Changing.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");
        }
    }

    @Test
    public void loopChangingTest_illegal_FuncAssignStmt_expectCompilerException() {
        try {
            // Run the RESOLVE compiler
            Utilities.executeCommand(
                    "cp test/resources/Inject_Front_Realiz_FuncAssignStmt_Causing_Illegal_Var_Change.rb RESOLVE-Workspace/Main/Concepts/Queue_Template/Inject_Front_Realiz_FuncAssignStmt_Causing_Illegal_Var_Change.rb",
                    "");

            Utilities.executeCommand(
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
            Utilities.executeCommand("rm Inject_Front_Realiz_FuncAssignStmt_Causing_Illegal_Var_Change.rb",
                    "RESOLVE-Workspace/Main/Concepts/Queue_Template/");
        }
    }

}

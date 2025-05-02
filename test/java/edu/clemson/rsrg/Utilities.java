/*
 * Utilities.java
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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    public static String checkforJarFile() {
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
            Utilities.executeCommand("mvn package -DskipTests", "");
        }

        Pattern pattern = Pattern.compile("RESOLVE-.*-jar-with-dependencies.jar", Pattern.CASE_INSENSITIVE);
        files = jarFileFolder.list(jarFilter);
        // Check if the jar file exists
        boolean jarFileExists = false;
        String jarFileName = null;

        for (String file : files) {
            Matcher matcher = pattern.matcher(file);
            if (matcher.find()) {
                System.out.println("Jar file found: " + file);
                File jarFile = new File(jarFileFolder, file);
                jarFileName = jarFile.getAbsolutePath();
                jarFileExists = true;
                break;
            }
        }

        if (!jarFileExists) {
            System.out.println("Jar file not found. Exiting...");
            System.exit(1);
        }

        return jarFileName;

    }

    public static void initializeSubrepos() {
        Utilities.executeCommand("ls RESOLVE-Workspace/", "");
        File stdoutFile = new File("stdout.txt");
        if (stdoutFile.exists()) {
            try {
                List<String> lines = Files.readAllLines(stdoutFile.toPath(), StandardCharsets.UTF_8);
                if (lines.isEmpty()) {
                    Utilities.executeCommand("git submodule update --init --recursive", "");
                } else {
                    System.out.println("stdout.txt is not empty.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
}

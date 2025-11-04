/*
 * RegistryCI.java
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
package edu.clemson.rsrg.nProver.registryCI;

import edu.clemson.rsrg.nProver.registry.CongruenceClassRegistry;

import java.util.*;

public class RegistryCI {
    private CongruenceClassRegistry registry;
    private Map<String, Integer> symbolToMapping;
    private List<String> mappingToSymbol;
    private Scanner scan;
    private int currentMapping;

    private static String prompt = "> ";

    public RegistryCI() {
        registry = new CongruenceClassRegistry<>(100, 100, 100, 100);
        symbolToMapping = new HashMap<String, Integer>();
        mappingToSymbol = new ArrayList<String>();
        scan = new Scanner(System.in);
        currentMapping = 0;
    }

    public static void sendStartupMessage() {
        System.out.println(
                "R - registerCluster\n? - isRegistered\nA - appendToClusterArgList\nM - makeCongruent\nD - display\nQ - quit");
    }

    public void runCommandLoop() {
        sendStartupMessage();
        while (true) {
            System.out.print(prompt);
            String input = scan.nextLine();
            if (input.equals("Q")) {
                break;
            }
            if (input.equals("?")) {
                sendStartupMessage();
            } else if (input.equals("D")) {
                for (int i = 1; registry.isClassDesignator(i); i++)
                    registry.displayCongruence(mappingToSymbol, i);
            } else {
                processCommand(input);
            }
        }
    }

    public void processCommand(String command) {
        String parsedCommand[] = command.split(" ");
        if (parsedCommand.length < 2) {
            System.out.println("Invalid input \"" + command + "\". Command must have an argument.");
            return;
        }
        switch (parsedCommand[0]) {
        case "R":
            int mapping = 0;
            if (symbolToMapping.containsKey(parsedCommand[1])) {
                mapping = symbolToMapping.get(parsedCommand[1]);
            } else {
                symbolToMapping.put(parsedCommand[1], currentMapping);
                mappingToSymbol.add(parsedCommand[1]);
                mapping = currentMapping;
                currentMapping++;
            }
            int designator;
            if (!registry.isRegistryLabel(mapping)) {
                designator = registry.registerCluster(mapping);
            } else {
                designator = registry.getAccessorFor(mapping);
            }
            System.out.println("Designator: " + designator);
            break;
        case "A":
            try {
                int num = Integer.parseInt(parsedCommand[1]);
                if (num < 0) {
                    System.out.println("Argument must be non-negative.");
                    break;
                }
                // should check if it's a valid designator first but not sure how - only checks appear to be for labels
                // registry.
                if (!registry.isClassDesignator(num)) {
                    System.out.println("Not a valid congruence class.");
                    break;
                }
                registry.appendToClusterArgList(num);
                System.out.println("Added to argument list");
            } catch (NumberFormatException e) {
                System.out.println("Argument must be a number.");
            }
            break;
        case "?":
            if (symbolToMapping.containsKey(parsedCommand[1])) {
                int label = symbolToMapping.get(parsedCommand[1]);
                if (registry.isRegistryLabel(label)) {
                    System.out.println("Registered");
                    break;
                }
            }
            System.out.println("Not registered");
            break;
        case "M":
            if (parsedCommand.length < 3) {
                System.out.println("Invalid input \"" + command + "\". Command must have two arguments.");
                return;
            }
            try {
                int designator1 = Integer.parseInt(parsedCommand[1]);
                int designator2 = Integer.parseInt(parsedCommand[2]);
                registry.makeCongruent(designator1, designator2);
                System.out.println("Made congruent.");
            } catch (NumberFormatException e) {
                System.out.println("Arguments must be numbers.");
            }
            break;
        default:
            System.out.println("Unspecified command: " + command);
        }
    }

    public static void main(String[] args) {
        RegistryCI registryCI = new RegistryCI();
        registryCI.runCommandLoop();
    }
}
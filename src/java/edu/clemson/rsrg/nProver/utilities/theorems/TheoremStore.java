/*
 * TheoremStore.java
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
package edu.clemson.rsrg.nProver.utilities.theorems;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.typeandpopulate.entry.TheoremEntry;
import edu.clemson.rsrg.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;

import java.util.*;
import java.util.stream.Collectors;

public final class TheoremStore {

    private final List<TheoremEntry> allTheorems;
    private final Map<TheoremEntry, Set<String>> theoremToOps;
    private final Map<String, Set<TheoremEntry>> opToTheorems;
    private final Set<TheoremEntry> zeroOpTheorems;

    public TheoremStore(ModuleScope scope) {
        Objects.requireNonNull(scope, "scope");
        // Query all theorems once
        List<TheoremEntry> programTheorems = scope.query(new EntryTypeQuery<>(TheoremEntry.class,
                MathSymbolTable.ImportStrategy.IMPORT_NAMED, MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));

        this.allTheorems = Collections.unmodifiableList(new ArrayList<>(programTheorems));
        this.theoremToOps = new LinkedHashMap<>(programTheorems.size());
        this.opToTheorems = new LinkedHashMap<>();
        this.zeroOpTheorems = new LinkedHashSet<>();

        // Build indices
        for (TheoremEntry te : programTheorems) {
            Set<String> opStrings = toOperatorStrings(te.getOperators());
            theoremToOps.put(te, opStrings);
            if (opStrings.isEmpty()) {
                zeroOpTheorems.add(te);
            }
            for (String op : opStrings) {
                opToTheorems.computeIfAbsent(op, k -> new LinkedHashSet<>()).add(te);
            }
        }
    }

    /** Convert a set of Exp operators to their canonical string form used by the prover's labeling. */
    private static Set<String> toOperatorStrings(Set<Exp> ops) {
        if (ops == null || ops.isEmpty()) {
            return Collections.emptySet();
        }
        // Use LinkedHashSet to preserve deterministic iteration order
        Set<String> res = new LinkedHashSet<>(ops.size());
        for (Exp e : ops) {
            if (e != null) {
                res.add(e.toString());
            }
        }
        return Collections.unmodifiableSet(res);
    }

    /**
     * Find all theorems whose operator set is a subset of the provided sequent operators.
     *
     * @param sequentOperators
     *            A set of operator strings extracted from the target sequent.
     *
     * @return A list of relevant {@link TheoremEntry} objects.
     */
    public List<TheoremEntry> findRelevantTheorems(Set<String> sequentOperators) {
        if (allTheorems.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> ops = (sequentOperators == null) ? Collections.emptySet() : sequentOperators;

        // Fast path: if sequent has no operators, only theorems with no operators can match (rare)
        if (ops.isEmpty()) {
            return allTheorems.stream().filter(te -> theoremToOps.getOrDefault(te, Collections.emptySet()).isEmpty())
                    .collect(Collectors.toList());
        }

        // Candidate reduction: union of theorems that contain at least one sequent operator
        // This avoids scanning all theorems for typical cases
        Set<TheoremEntry> candidates = new LinkedHashSet<>(zeroOpTheorems);
        for (String op : ops) {
            Set<TheoremEntry> bucket = opToTheorems.get(op);
            if (bucket != null) {
                candidates.addAll(bucket);
            }
        }
        // If union is empty (no overlap), there are no relevant theorems
        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        // Final filter: must be subset-of
        List<TheoremEntry> relevant = new ArrayList<>();
        for (TheoremEntry te : candidates) {
            Set<String> theoOps = theoremToOps.getOrDefault(te, Collections.emptySet());
            if (ops.containsAll(theoOps)) {
                relevant.add(te);
            }
        }

        return relevant;
    }

    /** Number of preloaded theorems. */
    public int size() {
        return allTheorems.size();
    }

    /** Expose all theorems if needed for diagnostics. */
    public List<TheoremEntry> getAllTheorems() {
        return allTheorems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (TheoremEntry theoremEntry : allTheorems) {
            sb.append(theoremEntry.toString());
        }
        return sb.toString();
    }
}

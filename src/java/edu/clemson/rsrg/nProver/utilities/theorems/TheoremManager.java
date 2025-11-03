package edu.clemson.rsrg.nProver.utilities.theorems;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.typeandpopulate.entry.TheoremEntry;

import java.util.Set;

public interface TheoremManager {
    Set<TheoremEntry> getRelevantTheorems(Set<Exp> expressions);
}

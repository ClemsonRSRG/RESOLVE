package edu.clemson.cs.r2jt.congruenceclassprover;


import edu.clemson.cs.r2jt.proving.Prover;
import edu.clemson.cs.r2jt.proving2.VC;
import edu.clemson.cs.r2jt.utilities.Flag;
import edu.clemson.cs.r2jt.utilities.FlagDependencies;

import java.util.List;

/**
 * Created by mike on 4/4/2014.
 */
public class CongruenceClassProver {
    public static final Flag FLAG_PROVE =
            new Flag(Prover.FLAG_SECTION_NAME, "ccprove", "congruence closure based prover");
    private List<VC> m_VCs;

    public static void setUpFlags() {
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_PROVE);
        FlagDependencies.addExcludes(FLAG_PROVE, Prover.FLAG_LEGACY_PROVE);

        FlagDependencies.addImplies(FLAG_PROVE, Prover.FLAG_SOME_PROVER);
    }

    public CongruenceClassProver(List<VC> vcs){
       System.out.println("in ccprover");
        m_VCs = vcs;

    }

    public void start(){
        VerificationConditionCongruenceClosureImpl vc0 = new VerificationConditionCongruenceClosureImpl(m_VCs.get(0));
        System.out.println(vc0);
    }
}

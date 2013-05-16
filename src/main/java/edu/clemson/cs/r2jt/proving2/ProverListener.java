package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;

public interface ProverListener {

    public void progressUpdate(double progess);

    public void vcResult(boolean proved, PerVCProverModel finalModel, Metrics m);
}

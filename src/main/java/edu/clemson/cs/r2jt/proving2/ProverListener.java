/**
 * ProverListener.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.proving2;

import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;

public interface ProverListener {

    public void progressUpdate(double progess);

    public void vcResult(boolean proved, PerVCProverModel finalModel, Metrics m);
}

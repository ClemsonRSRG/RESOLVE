package edu.clemson.cs.r2jt.proving;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>A <code>VCProver</code> that attempts multiple strategies in sequence.
 * Each time one strategy fails, the next is attempted.  When one succeeds,
 * returns success.  Only returns failure if all strategies fail.</p>
 * 
 * <p>This class attempts to estimate overall progress by querying each 
 * sub-prover's <code>getProofCountOrder</code> and weighting that prover's
 * individual progress accordingly.  Since some provers may be unable to 
 * estimate their proof count order (such as provers that query the user for the
 * next rule to apply), any prover that indicates via 
 * <code>getProofCountOrder</code> that it is unable to stimate its proof count
 * order is assumed to have average weight.</p> 
 * 
 * @author H. Smith, June 9th 2009
 */
public class MultiStrategyProver implements VCProver {
	
	/**
	 * <p>A constant <code>BigInteger</code> with a value of -1.</p>
	 */
	private final BigInteger NEGATIVE_ONE = BigInteger.valueOf(-1);
	
	/**
	 * <p>A list of <code>VCProver</code>s to be applied in order until one 
	 * works when attempting a proof.</p>
	 */
	private final List<VCProver> myStrategies = new LinkedList<VCProver>();
	
	/**
	 * <p>The number of strategies in <code>myStrategies</code>.</p>
	 * 
	 * <p>INVARIANT: <code>myStrategyCount = myStrategies.size()</code></p>
	 */
	private int myStrategyCount;
	
	/**
	 * <p>The number of strategies in <code>myStrategies</code> that are unable
	 * to estimate their proof order.</p>
	 */
	private int myNoOrderEstimateCount;
	
	/**
	 * <p>The weights of each strategy.<p>
	 * 
	 * <p>INVARIANT: <code>myStrategyProgressWeights.length = myStrategyCount &&
	 * sum(myStrategyProgressWeights) = 1.0 +/- epsilon</code>.</p>
	 */
	private double[] myStrategyProgressWeights;
	
	/**
	 * <p>The total proof count order of all estimatable sub-provers.</p>
	 */
	private BigInteger myProofCountOrder;
	
	/**
	 * <p>Creates a new <code>MultiStrategyProver</code> with no starting
	 * sub-strategies.</p>
	 */
	public MultiStrategyProver() {
		myStrategyCount = 0;
		myNoOrderEstimateCount = 0;
		myProofCountOrder = BigInteger.ZERO;
	}
	
	/**
	 * <p>Adds a new strategy to this prover, to be tried if all strategies 
	 * added before it fail.</p>
	 * 
	 * @param strategy The new strategy.
	 */
	public void addStrategy(VCProver strategy) {
		myStrategies.add(strategy);
		myStrategyCount++;
		
		BigInteger newCountOrder = strategy.getProofCountOrder();
		
		if (newCountOrder.equals(NEGATIVE_ONE)) {
			myNoOrderEstimateCount++;
		}
		else {
			myProofCountOrder = 
				myProofCountOrder.add(strategy.getProofCountOrder());	
		}
		
		updateProgressWeights();
	}
	
	/**
	 * <p>Updates the <code>myStrategyProgressWeights</code> private table such
	 * that the double value at index <code>i</code> represents the weight of
	 * <code>myStrategies.get(i)</code> in the overall progress of this 
	 * strategy.</p>
	 * 
	 * <p>When this method terminates, 
	 * <code>myStrategyProgressWeights.length</code> should be equal to
	 * <code>myStrategyCount</code> and the sum of its elements should be 
	 * roughly 1.0.</p>
	 */
	private void updateProgressWeights() {
		myStrategyProgressWeights = new double[myStrategyCount];
		
		//Since provers with un-estimatable orders are given "average" order,
		//we need to calculate what percentage of the number of strategies
		//represent estimatable orders so we can scale down their weights to
		//leave room for the "average" weights
		final double averageWeight = (1 / (double) myStrategyCount);
		final double estimatablePercent = 
			1 - (myNoOrderEstimateCount / (double) myStrategyCount);
		
		double sanityCheckTotal = 0;
		BigInteger curOrder;
		for (int curStrategyIndex = 0; curStrategyIndex < myStrategyCount;
				curStrategyIndex++) {
			
			curOrder = myStrategies.get(curStrategyIndex).getProofCountOrder();
			
			if (curOrder.equals(NEGATIVE_ONE)) {
				myStrategyProgressWeights[curStrategyIndex] = averageWeight;
			}
			else {
				myStrategyProgressWeights[curStrategyIndex] =
					divide(curOrder, myProofCountOrder, 5) * estimatablePercent;
			}
			
			sanityCheckTotal += myStrategyProgressWeights[curStrategyIndex];
		}
		
		if (Math.abs(1 - sanityCheckTotal) > 0.0001) {
			System.out.println("MultiStrategyProver.updateProgressWeights " +
					"reports weights do not sum to 100%.  Total is: " +
					(sanityCheckTotal * 100) + "%.");
		}
	}
	
	/**
	 * <p>Helper method to divide two <code>BigInteger</code>s and return a
	 * double value with an arbitrary number of decimal place's worth of
	 * precision.</p>
	 * 
	 * @param numerator The number to be divided.
	 * @param denominator The number to divide by.
	 * @param precision The number of decimal places in the solution.
	 * 
	 * @return The result approximated to the given number of decimal places.
	 */
	private static double divide(final BigInteger numerator, 
			final BigInteger denominator, final int precision) {
		
		int factor = (int) Math.pow(10, precision);
		
		return numerator.multiply(BigInteger.valueOf(factor))
				.divide(denominator).doubleValue() / factor;
	}
	
	public void prove(final VerificationCondition vC, 
			ProverListener progressListener, 
			ActionCanceller actionCanceller, long timeoutAt) 
			throws VCInconsistentException, VCProvedException, 
			       UnableToProveException {
		
		Metrics accumulatedMetrics = new Metrics();
		
		if (actionCanceller == null) {
			actionCanceller = new ActionCanceller();
		}
		
		ProgressAccumulator progressAccumulator;
		progressAccumulator = new ProgressAccumulator(progressListener);

		for (VCProver p : myStrategies) {
			try {
				p.prove(vC, progressAccumulator, actionCanceller, timeoutAt);
			}
			catch (UnableToProveException e) {
				if (!actionCanceller.running) {
					throw e;
				}
				accumulatedMetrics.accumulate(e.getMetrics());
			}
			catch (VCProvedException e) {
				accumulatedMetrics.accumulate(e.getMetrics());
				
				e.setMetrics(accumulatedMetrics);
				
				throw e;
			}
			
			progressAccumulator.startingNextStrategy();
		}
		
		throw new UnableToProveException(accumulatedMetrics);
	}

	public BigInteger getProofCountOrder() {
		BigInteger averageOrder = myProofCountOrder.divide(BigInteger.valueOf( 
				(myStrategyCount - myNoOrderEstimateCount)));
		
		BigInteger totalOrder = myProofCountOrder.add(
				averageOrder.multiply(
						BigInteger.valueOf(myNoOrderEstimateCount)));
		
		return totalOrder;
	}
	
	/**
	 * <p>A helper class which translates from the individual progress of each
	 * of the multiple local strategies to an overall progress to report to this
	 * strategy's own listeners.</p>
	 * 
	 * @author H. Smith, June 9th, 2009
	 */
	private class ProgressAccumulator implements ProverListener {

		private final ProverListener myParent;
		private int myCurrentStrategyIndex;
		private double myAccumulatedProgress;
		
		/**
		 * <p>Creates a new <code>ProgerssAccumulator</code> that will report
		 * overall progress to <code>parent</code> based on the strategy
		 * weights in this prover's <code>myStrategyProgressWeights</code>.</p>
		 * 
		 * @param parent The listener to alert of overall progress, or 
		 *               <code>null</code> if no one needs to be notified.
		 */
		public ProgressAccumulator(ProverListener parent) {
			myParent = parent;
			myCurrentStrategyIndex = 0;
		}
		
		/**
		 * <p>Indicates to this <code>ProgressAccumulator</code> that we are
		 * beginning on the next strategy, and future calls to 
		 * <code>progressUpdate()</code> represent progress from that 
		 * strategy.<p>
		 *
		 */
		public void startingNextStrategy() {
			myAccumulatedProgress += 
				myStrategyProgressWeights[myCurrentStrategyIndex];
			myCurrentStrategyIndex++;
		}
		
		public void progressUpdate(double progress) {
			if (myParent != null) {
				myParent.progressUpdate(myAccumulatedProgress + 
					(myStrategyProgressWeights[myCurrentStrategyIndex] * 
							progress));
			}
		}
	}
}

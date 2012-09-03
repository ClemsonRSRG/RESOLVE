package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.analysis.MathExpTypeResolver;

public class GuidedTransformationChooser extends AbstractTransformationChooser {
	
	public GuidedTransformationChooser(Iterable<VCTransformer> library, 
			MathExpTypeResolver r) {
		super(library, r);
	}

	@Override
	public Iterator<ProofPathSuggestion> doSuggestTransformations(VC vc, 
			int curLength, Metrics metrics, ProofData d, 
			Iterable<VCTransformer> localTheorems) {
		
		return new GuidedListSelectIterator<ProofPathSuggestion>(
				"Choose rule", vc.toString(), 
				new LazyMappingIterator<VCTransformer, ProofPathSuggestion>(
						getTransformerLibrary().iterator(),
						new StaticProofDataSuggestionMapper(d)));
	}
}

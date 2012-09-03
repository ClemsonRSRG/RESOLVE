package edu.clemson.cs.r2jt.proving;

public class StaticProofDataSuggestionMapper
		implements Mapper<VCTransformer, ProofPathSuggestion> {

	private final ProofData myData;

	public StaticProofDataSuggestionMapper(ProofData data) {
		myData = data;
	}

	@Override
	public ProofPathSuggestion map(VCTransformer i) {
		return new ProofPathSuggestion(i, myData);
	}
}

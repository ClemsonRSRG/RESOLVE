Realization Obvious_Reading_Realiz (Operation Read_Entry(replaces E: Entry);)
		for Reading_Capability of Stack_Template;
	uses Integer_Ext_Theory;

	Procedure Read_upto(replaces S: Stack; preserves Count: Integer);
		Var Next_Entry: Entry;

		Clear(S);
		While ( Depth(S) < Count )
			changing S, Next_Entry;
			maintaining |S| <= Count;
			decreasing (Count - |S|);
		do
			Read_Entry(Next_Entry);
			Push(Next_Entry, S);
		end;
	end Read_upto;

	Procedure Read(replaces S: Stack);
		Read_upto(S, Max_Depth);
	end Read;
	
end Obvious_Reading_Realiz;
Realization Obvious_CC_Realiz
(
	Operation Copy_Entry(replaces Copy: Entry; restores Orig: Entry);
		ensures Copy = Orig;
)
for Copying_Capability of Stack_Template;

	Procedure Copy_Stack(replaces S_Copy: Stack; restores S_Orig: Stack);
		Var Next_Entry, Entry_Copy: Entry;
		Var S_Reversed: Stack;

		While ( 1 <= Depth(S_Orig) )
			changing Next_Entry, S_Orig, S_Reversed;
			maintaining #S_Orig = Reverse(S_Reversed) o S_Orig;
			decreasing |S_Orig|;
		do
			Pop(Next_Entry, S_Orig);
			Push(Next_Entry, S_Reversed);
		end;
		
		Clear(S_Copy);
		
		While ( 1 <= Depth(S_Reversed) )
			changing Entry_Copy, Next_Entry, S_Copy, S_Orig, S_Reversed;
			maintaining S_Copy = S_Orig and
						#S_Orig = Reverse(S_Reversed) o S_Orig;
			decreasing |S_Reversed|;
		do
			Pop(Next_Entry, S_Reversed);
			Copy_Entry(Entry_Copy, Next_Entry);
			Push(Next_Entry, S_Orig);
			Push(Entry_Copy, S_Copy);
		end;
    end Copy_Stack;

end Obvious_CC_Realiz;
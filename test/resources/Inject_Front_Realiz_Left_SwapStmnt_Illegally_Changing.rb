Realization Inject_Front_Realiz_Left_SwapStmnt_Illegally_Changing for Inject_Front_Capability of Queue_Template;

	Procedure Inject_at_Front(alters E: Entry; updates Q: Queue);
		Var T: Queue;
		
		Enqueue (E, T);
		While ( 1 <= Length(Q) )
			changing Q, E;
			-- fill in a suitable invariant
			maintaining true;
			decreasing |Q|;
		do
			Dequeue(E,Q);
			-- T is not supposed to change as specified in the changing clause, but its value is being swapped with Q
			T :=: Q;
		end;
	end Inject_at_Front;
end Inject_Front_Realiz_Left_SwapStmnt_Illegally_Changing;

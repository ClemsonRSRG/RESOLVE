Realization Inject_Front_Realiz_FuncAssignStmt_Causing_Illegal_Var_Change for Inject_Front_Capability of Queue_Template;

	Procedure Inject_at_Front(alters E: Entry; updates Q: Queue);
		Var T: Queue;
		Var Y : Integer;
        Y := 1;
		
		Enqueue (E, T);
		While ( 1 <= Length(Q) )
			changing Q, T, E;
			-- fill in a suitable invariant
			maintaining true;
			decreasing |Q|;
		do
            -- Y is not supposed to change as it is not specified in the changing clause, but its value is being changed
            Y := Y + 1;
			Dequeue(E,Q);
			Enqueue(E,T);
		end;
		Q :=: T;
	end Inject_at_Front;
end Inject_Front_Realiz_FuncAssignStmt_Causing_Illegal_Var_Change;

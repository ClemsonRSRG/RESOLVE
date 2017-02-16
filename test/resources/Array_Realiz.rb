Realization Array_Realiz for Stack_Template;
	uses Integer_To_String_Function_Theory;

    Type Stack is represented by Record
            Contents: Array 1..Max_Depth of Entry;
            Top: Integer;
        end;
        convention
            0 <= S.Top <= Max_Depth;
        correspondence
            Conc.S = Reverse(Iterated_Concatenation(1, S.Top, 
                    Stringify_Z_Entity(S.Contents))); -- Stringed_Z_Entity
	end;

    Procedure Push(alters E: Entry; updates S: Stack);
        S.Top := S.Top + 1;
        E :=: S.Contents[S.Top];
    end;

    Procedure Pop(replaces R: Entry; updates S: Stack); 
        R :=: S.Contents[S.Top];
        S.Top := S.Top - 1;
    end;

    Procedure Depth(restores S: Stack): Integer;
        Depth := S.Top;
    end;

    Procedure Rem_Capacity(restores S: Stack): Integer;
        Rem_Capacity := Max_Depth - S.Top;
    end;

    Procedure Clear(clears S: Stack);
        S.Top := 0;
    end;
	
end;
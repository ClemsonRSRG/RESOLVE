Precis Integer_Theory;
	uses Monogenerator_Theory, Natural_Number_Theory, Basic_Properties;

Categorical Definition introduces
	Z : SSet,
	z0 : Z,
	NB : Z -> Z
related by
	Is_Monogeneric_for(Z, z0, NB);

Type Theorem N_subset_Of_Z:
	For all n:N,
		n:Z;
		
Theorem zero_N:
	z0 = 0;
		
Inductive Definition Is_Neg(n : Z) : B is
	(i.) Is_Neg(0) = false;
	(ii.) Is_Neg(NB(n)) = not(Is_Neg(n));
	
Inductive Definition -(n : Z) : Z is
	(i.) -0 = 0;
	(ii.) -NB(n) = conditional(Is_Neg(n), n, NB(NB(n)));

Theorem I0_a:
	-0 = 0;

Theorem I1:
	For all n : Z,
		Is_Neg(n) implies NB(n) = -n;

Theorem I2:
	For all n : Z,
		-(-n) = n;

Inductive Definition suc(n : Z) : Z is
	(i.) suc(0) = NB(NB(0));
	(ii.) suc(NB(n)) = conditional(Is_Neg(n), NB(NB(NB(n))), -n);

Definition z1 : Z = suc(0);

Theorem one_N:
	z1 = 1;

Theorem I3:
	For all n : Z,
		not(Is_Neg(n)) implies
			(suc(n) = NB(NB(n)));

Corollary I3_1:
	For all n : Z,
		NB(n) = conditional(Is_Neg(n), -n, -suc(n));

Theorem I4:
	For all n : Z,
		suc(-suc(n)) = -n;

Corollary I4_1:
	Is_Bijective(suc);

Inductive Definition (m : Z) + (n : Z) : Z is
	(i.) m + 0 = m;
	(ii.) m + NB(n) = conditional(Is_Neg(n), -(-m + n), -(suc(-m + n)));

Corollary Plus_ID_1:
	For all m:Z,
		m + 0 = m;
	
Corollary Plus_1:
	Is_Right_Identity_for(op +, 0);

Corollary Plus_1_Def:
	For all m,n:Z,
		0 + m = m;

Corollary Plus_2:
	For all m, n : Z,
		m + NB(n) = conditional(Is_Neg(n), -(-m + n), -(suc(-m + n)));

Corollary Plus_3:
	For all m, n : Z,
		m + NB(NB(n)) = conditional(Is_Neg(n), -suc(-(m + n)), suc(m + n));

Theorem I5:
	Is_Homomorphism_for(op +, op -);

Theorem I6:
	For all m, n : Z,
		suc(m + n) = m + suc(n);

Theorem I7:
	Is_Associative(op +);

Theorem I_7_Def:
	For all i,j,k:Z,
		(i + j) + k = i + (j + k);

Theorem I8:
	Is_Left_Identity_for(op +, 0);

Theorem I8_Def:
	For all m,n:Z,
		0 + n = n;

Corollary I8_1:
	Is_Identity_for(op +, 0);

Theorem I9:
	For all m, n : Z,
		suc(m + n) = suc(m) + n and suc(m + -n) = suc(m) + -n;

Theorem I10:
	Is_Commutative(op +);

Theorem I10_Def:
	For all m,n:Z,
		m + n = n + m;

Corollary I10_1:
	Is_Inverse_for(op +, op -);

Corollary I10_1_def:
	For all i:Z,
		i + -(i) = 0;

Corollary I10_2:
	Is_Abelian_Group(Z, z0, op +, op -);
		
Definition (m : Z) <= (n : Z) : B;
Definition (i: Z) >= (j: Z) : B;
Definition (i: Z) < (j: Z) : B;
Definition (i: Z) > (j: Z) : B;

Theorem One_Expanded_Def:
	1 = suc(0);

Corollary One_1:
	For all m,n:Z,
		suc(m) = n implies n = m + 1;

Corollary One_2_a:
	For all n:Z,
		0 <= n implies NB(n) = -(n + 1);

Corollary One_2_b:
	For all n:Z,
		Is_Neg(n) implies NB(n) = -n;

Corollary One_3:
	1 /= 0;

-- Corollaries 4 to 7 omitted
Corollary LTE_1:
	Is_Transitive(op <=);
	
Corollary LTE_1a: -- Is_Transitive(op<=);
	For all i,j,k:Z,
		i <= j and j <= k implies i <= k;

Corollary LTE_2: -- Is_Antisymmetric(<=)
	For all m,n:Z,
		(m <= n and n <= m) = (m = n);

Corollary LTE_3: -- Is_Total(<=)
	For all m,n:Z,
		m <= n or n <= m;

Corollary LTE_3a: --Is_Reflexive(<=) -- implied by Is_Total
	For all n:Z,
		n <= n;

Corollary LTE_4:
	Is_Total_Ordering(op <=); -- total_pre(trans, total) and antisymm

Corollary LTE_6:
	Is_Preserved_by(op +,op <=);
	
Corollary LTE_6_def: 
	For all l,m,n:Z,
		(l + n <= m + n) = (l <= m);
		
Corollary LTE_6_b:
	For all i,j,k,l:Z,
		i + j <= k and l <= j implies i + l <= k;

Corollary LTE_8:
	For all m,n:Z,
	For all p:B,
		(-n <= -m) = p implies p = (m <= n);

Corollary LTE_9:
	For all n:Z,
	For all p:B,
		Is_Neg(n) = p implies p = not(0 <= n);

Corollary LTE_10:
	not(1 <= 0);

Definition |(n: Z)| : Z;

Theorem Abs_Val_Expanded_Def_1:
	For all n,m:Z,
		|n| = m and not(Is_Neg(n)) implies m = n;

Theorem Abs_Val_Expanded_Def_2:
	For all n,m:Z,
		|n| = m and Is_Neg(n) implies m = -n;

Corollary Abs_Val_1:
	For all n:Z,
		|(|n|)| = |n|;

Corollary Abs_Val_2:
	For all n:Z,
		|(|-n|)| = |n|;

--Omitted Is_Alg_Int_Like section

Inductive Definition (m:Z) * (n:Z):Z is
	(i.) m * 0 = 0;
	(ii.) Is_Neg(n) implies m * NB(n) = -(m * n);

Theorem Mult_Ind_Def_Expansion_iii:
	For all k,m,n:Z,
		not(Is_Neg(n)) and m * NB(n) = k implies -(m * n + m) = k;

Corollary Mult_Def_1:
	For all n:Z,
		n * 0 = 0;

Corollary Mult_Def_2:
	For all n: Z,
		n * 1 = n;

Theorem I14: --Is_Right_Distributive_Over(op+,op*);
	For all l,m,n:Z,
		(l + m) * n = (l * n) + (m * n);

Theorem I15:
	For all m,n:Z,
		-(m*n) = (-m)*n;

Theorem I16:
	For all m,n:Z,
		m * (-n) = -(m*n);

Theorem I17:
	For all m,n:Z,
		m * suc(n) = (m * n) + m;

Theorem I18: --Is_Left_Distributive_Over(op+,op*);
	For all l,m,n:Z,
		l * (m + n) = (l * m) + (l * n);

Theorem I19: --Is_Associative(op*);
	For all l,m,n:Z,
		l * (m * n) = (l * m) * n;

Theorem I20: --Is_Left_Zero_for(*,0)
	For all n:Z,
		0 * n = 0;

Theorem I21: --Is_Left_Identity_for(op*,1)
	For all n:Z,
		1 * n = n;

Theorem I22: --Is_Commutative(op*)
	For all m,n:Z,
		m * n = n * m;

Corollary I25_1:
	For all l,m,n:Z,
		l /= 0 and m * l = n * l implies m = n;

Corollary I25_2:
	For all l,m,n:Z,
		l /= 0 and l * m = l * n implies m = n;

---------------------------------------------------------------
-- Potential Addons                                
---------------------------------------------------------------

Theorem Distribution_Unary_Minus_Over_Addition:
	For all i,j,k:Z,
		-(i + j) = (-i) + (-j);
				
Theorem Addition_Over_Equality:
	For all i,j,k:Z,
		(i + (-j) = k) = (i = k + j);
	
Theorem LTE_Z_N:
	For all i:Z,
	For all n:N,
		i <= i + n;
		
Theorem Addition_Over_LTEa:
	For all i,j,k:Z,
		(i + j <= k) = (i <= k + (-j));	
							
Theorem Addition_Over_LTEb:
	For all i,j,k:Z, 
		(i <= j + k) = (i + (-j) <= k);	
		
Theorem LTE_Sum:
	For all w,x,y,z: Z,
		w <= x + z and x <= y implies w <= y + z;
							
Theorem Zero_LTE_One:
	0 <= 1;

Theorem Neg_One_LTE_Zero:
	(-1) <= 0;
					
Theorem Not_LTE:
	For all i,j:Z,
	For all p:B,
		not(i <= j) = (j + 1 <= i);

Theorem Not_LT:
	For all i,j:Z,
		not(i + 1 <= j) = (j <= i);

Theorem Not_Eq_And_LTE:
	For all i,j:Z,
		((i <= j) and not(j = i)) = (i + 1 <= j);
		
Theorem Add_NonZero_Not_Eq:
	For all i,j,k:Z,
		(j /= 0) and (i + j = k) implies  i /= k;

Theorem Not_Equal_Primary_a: -- this is a way to express /= using + and <=
	For all i,j: Z,
		(not(i = j)) = ((i <= j) = (i + 1 <= j));

Definition (i: Z) ** (j: Z) : Z;
Definition (i: Z) / (j: Z) : Z;
Definition (i: Z) mod (j: Z) : Z;

Corollary Mod_1:
	For all i,j:N,
		i mod j <= j;

-- add mod theorems 
      		
end Integer_Theory;
RESOLVE
=======
Output from first working version.
-ccprove Concepts\Stack_Template\Obvious_Flipping_Realiz.rb

Complete record: Stack_Template.Flipping_Capability
Complete record: Stack_Template.Obvious_Flipping_Realiz
vc before: 0_1
<=(min_int,0)=true
<=(min_int,Max_Depth)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<(0,max_int)=true
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
|_|(S)=¢006
o(¢008,S)=¢009
Reverse(Empty_String)=¢008
----------------------------------
S=¢009

(Reverse(Empty_String) = Empty_String)
	inserting: (Reverse(Empty_String) = Empty_String)
((Empty_String o S) = S)
	inserting: ((Empty_String o S) = S)

2 iterations. proved vc: 0_1
<=(min_int,0)=true
<=(min_int,Max_Depth)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<(0,max_int)=true
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
|_|(S)=¢006
o(Empty_String,S)=S
Reverse(Empty_String)=Empty_String
----------------------------------
S=S

vc before: 0_2
<=(min_int,0)=true
<=(min_int,Max_Depth)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<(0,max_int)=true
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
|_|(S)=¢006
|_|(??S)=¢010
o(¢008,??S)=S
Reverse(?Temp)=¢008
/=(¢010,0)=true
----------------------------------
true=true


1 iterations. proved vc: 0_2
<=(min_int,0)=true
<=(min_int,Max_Depth)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<(0,max_int)=true
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
|_|(S)=¢006
|_|(??S)=¢010
o(¢008,??S)=S
Reverse(?Temp)=¢008
/=(¢010,0)=true
----------------------------------
true=true

vc before: 0_3
<=(min_int,0)=true
<=(min_int,Max_Depth)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<(0,max_int)=true
<(¢014,Max_Depth)=¢015
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
|_|(S)=¢006
|_|(?Temp)=¢014
|_|(??S)=¢010
o(¢008,??S)=S
o(¢012,?S)=??S
Reverse(?Temp)=¢008
/=(¢010,0)=true
<_>(?Next_Entry)=¢012
----------------------------------
¢015=true

(Reverse(Empty_String) = Empty_String)
	inserting: (Reverse(Empty_String) = Empty_String)
((S = T) implies (|S| = |T|))
	inserting: (|Empty_String| = |Empty_String|)inserting: (|¢009| = |¢009|)inserting: (|¢008| = |¢008|)inserting: (|¢013| = |¢013|)inserting: (|¢016| = |¢016|)inserting: (|?S| = |?S|)inserting: (|S| = |S|)inserting: (|??S| = |??S|)inserting: (|?Temp| = |?Temp|)inserting: (|¢012| = |¢012|)
((|(S o T)| <= i) implies ((|S| <= i) and (|T| <= i)))
	inserting: ((|¢008| <= Max_Depth) and (|??S| <= Max_Depth))
((|(<E> o S)| <= i) implies (|S| < i))
	inserting: (|?S| < Max_Depth)
((|(U o (<E> o V))| <= i) implies ((|U| < i) and (|V| < i)))
	inserting: ((|¢008| < Max_Depth) and (|?S| < Max_Depth))
(|Reverse(S)| = |S|)
	inserting: (|Reverse(Empty_String)| = |Empty_String|)inserting: (|Reverse(?Temp)| = |?Temp|)

2 iterations. proved vc: 0_3
<=(min_int,0)=true
<=(min_int,Max_Depth)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<=(¢010,Max_Depth)=true
<=(¢014,Max_Depth)=true
<(0,max_int)=true
<(¢014,Max_Depth)=true
<(¢019,Max_Depth)=true
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
|_|(S)=¢006
|_|(?Temp)=¢014
|_|(¢008)=¢014
|_|(??S)=¢010
|_|(¢012)=¢020
|_|(?S)=¢019
|_|(Empty_String)=¢017
o(¢008,??S)=S
o(¢012,?S)=??S
Reverse(?Temp)=¢008
Reverse(Empty_String)=Empty_String
/=(¢010,0)=true
<_>(?Next_Entry)=¢012
----------------------------------
true=true

vc before: 0_4
<=(min_int,0)=true
<=(min_int,Max_Depth)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<(0,max_int)=true
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
|_|(S)=¢006
|_|(??S)=¢010
o(¢008,??S)=S
o(¢012,?Temp)=¢014
o(¢012,?S)=??S
o(¢015,?S)=¢016
Reverse(?Temp)=¢008
Reverse(¢014)=¢015
/=(¢010,0)=true
<_>(?Next_Entry)=¢012
----------------------------------
S=¢016

(Reverse(Empty_String) = Empty_String)
	inserting: (Reverse(Empty_String) = Empty_String)
((S = T) implies (|S| = |T|))
	inserting: (|¢017| = |¢017|)inserting: (|Empty_String| = |Empty_String|)inserting: (|¢009| = |¢009|)inserting: (|¢008| = |¢008|)inserting: (|¢014| = |¢014|)inserting: (|¢013| = |¢013|)inserting: (|¢016| = |¢016|)inserting: (|¢015| = |¢015|)inserting: (|?S| = |?S|)inserting: (|S| = |S|)inserting: (|??S| = |??S|)inserting: (|?Temp| = |?Temp|)inserting: (|¢012| = |¢012|)
((|(S o T)| <= i) implies ((|S| <= i) and (|T| <= i)))
	inserting: ((|¢008| <= Max_Depth) and (|??S| <= Max_Depth))
((|(<E> o S)| <= i) implies (|S| < i))
	inserting: (|?S| < Max_Depth)
((|(U o (<E> o V))| <= i) implies ((|U| < i) and (|V| < i)))
	inserting: ((|¢008| < Max_Depth) and (|?S| < Max_Depth))
(|Reverse(S)| = |S|)
	inserting: (|Reverse(Empty_String)| = |Empty_String|)inserting: (|Reverse(¢014)| = |¢014|)inserting: (|Reverse(?Temp)| = |?Temp|)
(|(U o V)| = (|U| + |V|))
	inserting: (|(¢015 o ?S)| = (|¢015| + |?S|))inserting: (|(¢012 o ?Temp)| = (|¢012| + |?Temp|))inserting: (|(¢012 o ?S)| = (|¢012| + |?S|))inserting: (|(¢008 o ??S)| = (|¢008| + |??S|))
((|(<e> o S)| = |T|) implies (|S| < |T|))
	inserting: (|?Temp| < |¢015|)inserting: (|?Temp| < |¢014|)inserting: (|?S| < |??S|)
((((U o V) = W) and (|V| /= 0)) implies (|U| < |W|))
	inserting: (|¢008| < |S|)
(Reverse((U o V)) = (Reverse(V) o Reverse(U)))
	inserting: (Reverse((¢012 o ?Temp)) = (Reverse(?Temp) o Reverse(¢012)))
(Reverse(Reverse(S)) = S)
	inserting: (Reverse(Reverse(Empty_String)) = Empty_String)
(((U o V) o W) = (U o (V o W)))
	inserting: (((¢008 o ¢037) o ?S) = (¢008 o (¢037 o ?S)))
(0 < 1)
	inserting: (0 < 1)
(1 > 0)
	inserting: (1 > 0)
((((n + m) <= i) and (m /= 0)) implies (n < i))
	inserting: (¢019 < Max_Depth)
((i > 0) implies (i /= 0))
	inserting: (Last_Char_Num /= 0)
(((i <= j) and (k > j)) implies (i < k))
	inserting: (min_int < Last_Char_Num)
((i > j) = (j < i))
	inserting: ((Last_Char_Num > 0) = (0 < Last_Char_Num))
((i > j) implies (i >= j))
	inserting: (Last_Char_Num >= 0)
((i < j) implies (i <= j))
	inserting: (min_int <= Last_Char_Num)
((i <= j) = (i < (j + 1)))
	inserting: ((min_int <= 0) = (min_int < (0 + 1)))
((i < j) = ((i + 1) <= j))
	inserting: ((min_int < Last_Char_Num) = ((min_int + 1) <= Last_Char_Num))
((i < j) = (i <= (j - 1)))
	inserting: ((min_int < Last_Char_Num) = (min_int <= (Last_Char_Num - 1)))
((i <= j) = ((i - 1) <= (j - 1)))
	inserting: ((min_int <= 0) = ((min_int - 1) <= (0 - 1)))
((i <= j) implies ((i - 1) <= j))
	inserting: ((min_int - 1) <= 0)
(((i <= j) and (k >= 0)) implies ((i - k) <= j))
	inserting: ((¢054 - Last_Char_Num) <= ¢055)inserting: ((¢054 - Last_Char_Num) <= 0)inserting: ((¢050 - Last_Char_Num) <= Last_Char_Num)inserting: ((¢019 - Last_Char_Num) <= Max_Depth)inserting: ((¢010 - Last_Char_Num) <= Max_Depth)inserting: ((¢006 - Last_Char_Num) <= Max_Depth)inserting: ((Max_Depth - Last_Char_Num) <= max_int)inserting: ((min_int - Last_Char_Num) <= ¢052)inserting: ((min_int - Last_Char_Num) <= Max_Depth)inserting: ((min_int - Last_Char_Num) <= Last_Char_Num)inserting: ((min_int - Last_Char_Num) <= 0)
(((k <= j) and (i <= 0)) implies (i <= (j - k)))
	inserting: (min_int <= (¢052 - ¢071))inserting: (min_int <= (Max_Depth - ¢071))inserting: (min_int <= (Last_Char_Num - ¢071))inserting: (min_int <= (0 - ¢071))inserting: (min_int <= (max_int - ¢069))inserting: (min_int <= (Max_Depth - ¢067))inserting: (min_int <= (Max_Depth - ¢065))inserting: (min_int <= (Max_Depth - ¢063))inserting: (min_int <= (Last_Char_Num - ¢061))inserting: (min_int <= (¢055 - ¢058))inserting: (min_int <= (0 - ¢058))inserting: (min_int <= (¢055 - ¢054))inserting: (min_int <= (0 - ¢054))inserting: (min_int <= (Last_Char_Num - ¢050))inserting: (min_int <= (Max_Depth - ¢019))inserting: (min_int <= (Max_Depth - ¢010))inserting: (min_int <= (Max_Depth - ¢006))inserting: (min_int <= (max_int - Max_Depth))inserting: (min_int <= (¢052 - min_int))inserting: (min_int <= (Max_Depth - min_int))inserting: (min_int <= (Last_Char_Num - min_int))inserting: (min_int <= (0 - min_int))
(((i = (j - k)) and (k > 0)) implies (i < j))
	inserting: (¢058 < ¢054)inserting: (¢061 < ¢050)inserting: (¢063 < ¢019)inserting: (¢065 < ¢010)inserting: (¢067 < ¢006)inserting: (¢069 < Max_Depth)inserting: (¢052 < Last_Char_Num)inserting: (¢110 < max_int)inserting: (¢055 < 0)inserting: (¢054 < min_int)inserting: (¢071 < min_int)
((0 + i) = i)
	inserting: ((0 + 1) = 1)
((i + j) = (j + i))
	inserting: ((min_int + 1) = (1 + min_int))
(((i + j) + k) = (i + (j + k)))
	inserting: (((¢025 + ¢019) + ¢023) = (¢025 + (¢019 + ¢023)))inserting: (((0 + 1) + min_int) = (0 + (1 + min_int)))
((i <= j) implies (0 <= (j - i)))
	inserting: (0 <= (0 - min_int))
(Reverse(Empty_String) = Empty_String)
	inserting: (Reverse(Empty_String) = Empty_String)
(|<e>| = 1)
	inserting: (|<?Next_Entry>| = 1)
((S = T) implies (|S| = |T|))
	inserting: (|¢039| = |¢039|)inserting: (|¢017| = |¢017|)inserting: (|Empty_String| = |Empty_String|)inserting: (|¢009| = |¢009|)inserting: (|¢008| = |¢008|)inserting: (|¢014| = |¢014|)inserting: (|¢013| = |¢013|)inserting: (|¢038| = |¢038|)inserting: (|¢016| = |¢016|)inserting: (|¢037| = |¢037|)inserting: (|¢015| = |¢015|)inserting: (|?S| = |?S|)inserting: (|S| = |S|)inserting: (|??S| = |??S|)inserting: (|?Temp| = |?Temp|)inserting: (|¢012| = |¢012|)inserting: (|¢040| = |¢040|)
((|(S o T)| <= i) implies ((|S| <= i) and (|T| <= i)))
	inserting: ((|¢012| <= Max_Depth) and (|?S| <= Max_Depth))inserting: ((|¢008| <= Max_Depth) and (|??S| <= Max_Depth))
((|(<E> o S)| <= i) implies (|S| < i))
	inserting: (|?S| < Max_Depth)
((|(U o (<E> o V))| <= i) implies ((|U| < i) and (|V| < i)))
	inserting: ((|¢008| < Max_Depth) and (|?S| < Max_Depth))
(|Reverse(S)| = |S|)
	inserting: (|Reverse(¢012)| = |¢012|)inserting: (|Reverse(Empty_String)| = |Empty_String|)inserting: (|Reverse(¢014)| = |¢014|)inserting: (|Reverse(?Temp)| = |?Temp|)
(|S| < |(<E> o S)|)
	inserting: (|?S| < |(<?Next_Entry> o ?S)|)inserting: (|?Temp| < |(<?Next_Entry> o ?Temp)|)
(|(U o V)| = (|U| + |V|))
	inserting: (|(¢037 o ?S)| = (|¢037| + |?S|))inserting: (|(¢008 o ¢039)| = (|¢008| + |¢039|))inserting: (|(¢008 o ¢037)| = (|¢008| + |¢037|))inserting: (|(¢012 o ?Temp)| = (|¢012| + |?Temp|))inserting: (|(¢012 o ?S)| = (|¢012| + |?S|))inserting: (|(¢008 o ??S)| = (|¢008| + |??S|))
((|(<e> o S)| = |T|) implies (|S| < |T|))
	inserting: (|?Temp| < |¢015|)inserting: (|?Temp| < |¢014|)inserting: (|?S| < |¢039|)inserting: (|?S| < |??S|)
((((U o V) = W) and (|V| /= 0)) implies (|U| < |W|))
	inserting: (|¢008| < |¢016|)inserting: (|¢008| < |S|)
(Reverse(<E>) = <E>)
	inserting: (Reverse(<?Next_Entry>) = <?Next_Entry>)

3 iterations. proved vc: 0_4
<=(min_int,0)=true
<=(min_int,Last_Char_Num)=true
<=(min_int,Max_Depth)=true
<=(min_int,¢052)=true
<=(min_int,¢076)=true
<=(min_int,¢078)=true
<=(min_int,¢080)=true
<=(min_int,¢082)=true
<=(min_int,¢084)=true
<=(min_int,¢086)=true
<=(min_int,¢088)=true
<=(min_int,¢090)=true
<=(min_int,¢092)=true
<=(min_int,¢094)=true
<=(min_int,¢096)=true
<=(min_int,¢098)=true
<=(min_int,¢100)=true
<=(min_int,¢102)=true
<=(min_int,¢104)=true
<=(min_int,¢106)=true
<=(min_int,¢108)=true
<=(min_int,¢110)=true
<=(min_int,¢112)=true
<=(min_int,¢114)=true
<=(min_int,¢116)=true
<=(min_int,¢118)=true
<=(0,¢118)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<=(¢010,Max_Depth)=true
<=(¢019,Max_Depth)=true
<=(¢023,Max_Depth)=true
<=(1,Max_Depth)=true
<=(¢050,Last_Char_Num)=true
<=(¢054,0)=true
<=(¢054,¢055)=true
<=(¢058,0)=true
<=(¢058,¢055)=true
<=(¢061,Last_Char_Num)=true
<=(¢063,Max_Depth)=true
<=(¢065,Max_Depth)=true
<=(¢067,Max_Depth)=true
<=(¢069,max_int)=true
<=(¢071,0)=true
<=(¢071,Last_Char_Num)=true
<=(¢071,Max_Depth)=true
<=(¢071,¢052)=true
<(min_int,Last_Char_Num)=true
<(min_int,1)=true
<(0,max_int)=true
<(0,Last_Char_Num)=true
<(0,1)=true
<(¢019,Max_Depth)=true
<(¢019,¢006)=true
<(¢019,¢020)=true
<(¢023,Max_Depth)=true
<(¢023,¢010)=true
<(¢052,Last_Char_Num)=true
<(¢054,min_int)=true
<(¢055,0)=true
<(¢058,¢054)=true
<(¢061,¢050)=true
<(¢063,¢019)=true
<(¢065,¢010)=true
<(¢067,¢006)=true
<(¢069,Max_Depth)=true
<(¢071,min_int)=true
<(¢110,max_int)=true
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
>(1,0)=true
|_|(S)=¢006
|_|(?Temp)=¢019
|_|(¢008)=¢019
|_|(??S)=¢010
|_|(¢012)=1
|_|(?S)=¢023
|_|(¢014)=¢020
|_|(¢015)=¢020
|_|(Empty_String)=¢018
o(¢008,??S)=S
o(¢008,¢012)=¢015
o(¢012,?Temp)=¢014
o(¢012,?S)=??S
o(¢015,?S)=S
Reverse(?Temp)=¢008
Reverse(¢012)=¢012
Reverse(¢014)=¢015
Reverse(Empty_String)=Empty_String
/=(Last_Char_Num,0)=true
/=(¢010,0)=true
<_>(?Next_Entry)=¢012
+(min_int,1)=¢050
+(0,1)=1
+(0,¢050)=¢050
+(¢019,¢010)=¢006
+(¢019,¢023)=¢132
+(¢019,1)=¢020
+(¢020,¢023)=¢006
+(1,min_int)=¢050
+(1,¢019)=¢020
+(1,¢023)=¢010
+(1,¢132)=¢006
>=(Last_Char_Num,0)=true
-(min_int,Last_Char_Num)=¢071
-(min_int,1)=¢054
-(0,min_int)=¢118
-(0,1)=¢055
-(0,¢054)=¢100
-(0,¢058)=¢096
-(0,¢071)=¢082
-(max_int,Max_Depth)=¢110
-(max_int,¢069)=¢084
-(Last_Char_Num,min_int)=¢116
-(Last_Char_Num,1)=¢052
-(Last_Char_Num,¢050)=¢102
-(Last_Char_Num,¢061)=¢092
-(Last_Char_Num,¢071)=¢080
-(Max_Depth,min_int)=¢114
-(Max_Depth,Last_Char_Num)=¢069
-(Max_Depth,¢006)=¢108
-(Max_Depth,¢010)=¢106
-(Max_Depth,¢019)=¢104
-(Max_Depth,¢063)=¢090
-(Max_Depth,¢065)=¢088
-(Max_Depth,¢067)=¢086
-(Max_Depth,¢071)=¢078
-(¢006,Last_Char_Num)=¢067
-(¢010,Last_Char_Num)=¢065
-(¢019,Last_Char_Num)=¢063
-(¢050,Last_Char_Num)=¢061
-(¢052,min_int)=¢112
-(¢052,¢071)=¢076
-(¢054,Last_Char_Num)=¢058
-(¢055,¢054)=¢098
-(¢055,¢058)=¢094
----------------------------------
S=S

vc before: 0_5
<=(min_int,0)=true
<=(min_int,Max_Depth)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<(0,max_int)=true
<(¢014,¢010)=¢015
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
|_|(S)=¢006
|_|(??S)=¢010
|_|(?S)=¢014
o(¢008,??S)=S
o(¢012,?S)=??S
Reverse(?Temp)=¢008
/=(¢010,0)=true
<_>(?Next_Entry)=¢012
----------------------------------
¢015=true

(Reverse(Empty_String) = Empty_String)
	inserting: (Reverse(Empty_String) = Empty_String)
((S = T) implies (|S| = |T|))
	inserting: (|Empty_String| = |Empty_String|)inserting: (|¢009| = |¢009|)inserting: (|¢008| = |¢008|)inserting: (|¢013| = |¢013|)inserting: (|¢016| = |¢016|)inserting: (|?S| = |?S|)inserting: (|S| = |S|)inserting: (|??S| = |??S|)inserting: (|?Temp| = |?Temp|)inserting: (|¢012| = |¢012|)
((|(S o T)| <= i) implies ((|S| <= i) and (|T| <= i)))
	inserting: ((|¢008| <= Max_Depth) and (|??S| <= Max_Depth))
((|(<E> o S)| <= i) implies (|S| < i))
	inserting: (|?S| < Max_Depth)
((|(U o (<E> o V))| <= i) implies ((|U| < i) and (|V| < i)))
	inserting: ((|¢008| < Max_Depth) and (|?S| < Max_Depth))
(|Reverse(S)| = |S|)
	inserting: (|Reverse(Empty_String)| = |Empty_String|)inserting: (|Reverse(?Temp)| = |?Temp|)
(|(U o V)| = (|U| + |V|))
	inserting: (|(¢012 o ?S)| = (|¢012| + |?S|))inserting: (|(¢008 o ??S)| = (|¢008| + |??S|))
((|(<e> o S)| = |T|) implies (|S| < |T|))
	inserting: (|?S| < |??S|)

2 iterations. proved vc: 0_5
<=(min_int,0)=true
<=(min_int,Max_Depth)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<=(¢010,Max_Depth)=true
<=(¢018,Max_Depth)=true
<(0,max_int)=true
<(¢014,Max_Depth)=true
<(¢014,¢010)=true
<(¢018,Max_Depth)=true
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
|_|(S)=¢006
|_|(?Temp)=¢018
|_|(¢008)=¢018
|_|(??S)=¢010
|_|(¢012)=¢020
|_|(?S)=¢014
|_|(Empty_String)=¢017
o(¢008,??S)=S
o(¢012,?S)=??S
Reverse(?Temp)=¢008
Reverse(Empty_String)=Empty_String
/=(¢010,0)=true
<_>(?Next_Entry)=¢012
+(¢018,¢010)=¢006
+(¢020,¢014)=¢010
----------------------------------
true=true

vc before: 1_1
<=(min_int,0)=true
<=(min_int,Max_Depth)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<(0,max_int)=true
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
|_|(S)=¢006
|_|(?S)=0
o(¢008,?S)=S
Reverse(S)=¢011
Reverse(?Temp)=¢008
----------------------------------
?Temp=¢011

(Reverse(Empty_String) = Empty_String)
	inserting: (Reverse(Empty_String) = Empty_String)
((S = T) implies (|S| = |T|))
	inserting: (|Empty_String| = |Empty_String|)inserting: (|¢009| = |¢009|)inserting: (|?Temp| = |?Temp|)inserting: (|¢008| = |¢008|)inserting: (|¢012| = |¢012|)inserting: (|?S| = |?S|)inserting: (|¢011| = |¢011|)inserting: (|S| = |S|)
((|(S o T)| <= i) implies ((|S| <= i) and (|T| <= i)))
	inserting: ((|¢008| <= Max_Depth) and (|?S| <= Max_Depth))
(|Reverse(S)| = |S|)
	inserting: (|Reverse(Empty_String)| = |Empty_String|)inserting: (|Reverse(S)| = |S|)inserting: (|Reverse(?Temp)| = |?Temp|)
((|S| = 0) = (S = Empty_String))
	inserting: ((|?S| = 0) = (?S = Empty_String))
(|(U o V)| = (|U| + |V|))
	inserting: (|(¢008 o Empty_String)| = (|¢008| + |Empty_String|))
(Reverse((U o V)) = (Reverse(V) o Reverse(U)))
	inserting: (Reverse((¢008 o Empty_String)) = (Reverse(Empty_String) o Reverse(¢008)))
(Reverse(Reverse(S)) = S)
	inserting: (Reverse(Reverse(Empty_String)) = Empty_String)inserting: (Reverse(Reverse(?Temp)) = ?Temp)
(0 < 1)
	inserting: (0 < 1)
(1 > 0)
	inserting: (1 > 0)
((i > 0) implies (i /= 0))
	inserting: (Last_Char_Num /= 0)
(((i <= j) and (k > j)) implies (i < k))
	inserting: (min_int < Last_Char_Num)
((i > j) = (j < i))
	inserting: ((Last_Char_Num > 0) = (0 < Last_Char_Num))
((i > j) implies (i >= j))
	inserting: (Last_Char_Num >= 0)
((i < j) implies (i <= j))
	inserting: (min_int <= Last_Char_Num)
((i <= j) = (i < (j + 1)))
	inserting: ((min_int <= 0) = (min_int < (0 + 1)))
((i < j) = ((i + 1) <= j))
	inserting: ((min_int < Last_Char_Num) = ((min_int + 1) <= Last_Char_Num))
((i < j) = (i <= (j - 1)))
	inserting: ((min_int < Last_Char_Num) = (min_int <= (Last_Char_Num - 1)))
((i <= j) = ((i - 1) <= (j - 1)))
	inserting: ((min_int <= 0) = ((min_int - 1) <= (0 - 1)))
((i <= j) implies ((i - 1) <= j))
	inserting: ((min_int - 1) <= 0)
(((i <= j) and (k >= 0)) implies ((i - k) <= j))
	inserting: ((¢036 - Last_Char_Num) <= ¢037)inserting: ((¢036 - Last_Char_Num) <= 0)inserting: ((¢032 - Last_Char_Num) <= Last_Char_Num)inserting: ((¢014 - Last_Char_Num) <= Max_Depth)inserting: ((¢006 - Last_Char_Num) <= Max_Depth)inserting: ((Max_Depth - Last_Char_Num) <= max_int)inserting: ((0 - Last_Char_Num) <= Max_Depth)inserting: ((min_int - Last_Char_Num) <= ¢034)inserting: ((min_int - Last_Char_Num) <= Max_Depth)inserting: ((min_int - Last_Char_Num) <= Last_Char_Num)inserting: ((min_int - Last_Char_Num) <= 0)
(((k <= j) and (i <= 0)) implies (i <= (j - k)))
	inserting: (min_int <= (¢034 - ¢053))inserting: (min_int <= (Max_Depth - ¢053))inserting: (min_int <= (Last_Char_Num - ¢053))inserting: (min_int <= (0 - ¢053))inserting: (min_int <= (Max_Depth - ¢051))inserting: (min_int <= (max_int - ¢049))inserting: (min_int <= (Max_Depth - ¢047))inserting: (min_int <= (Max_Depth - ¢045))inserting: (min_int <= (Last_Char_Num - ¢043))inserting: (min_int <= (¢037 - ¢040))inserting: (min_int <= (0 - ¢040))inserting: (min_int <= (¢037 - ¢036))inserting: (min_int <= (0 - ¢036))inserting: (min_int <= (Last_Char_Num - ¢032))inserting: (min_int <= (Max_Depth - ¢014))inserting: (min_int <= (Max_Depth - ¢006))inserting: (min_int <= (max_int - Max_Depth))inserting: (min_int <= (Max_Depth - 0))inserting: (min_int <= (¢034 - min_int))inserting: (min_int <= (Max_Depth - min_int))inserting: (min_int <= (Last_Char_Num - min_int))inserting: (min_int <= (0 - min_int))
(((i = (j - k)) and (k > 0)) implies (i < j))
	inserting: (¢040 < ¢036)inserting: (¢043 < ¢032)inserting: (¢045 < ¢014)inserting: (¢047 < ¢006)inserting: (¢049 < Max_Depth)inserting: (¢034 < Last_Char_Num)inserting: (¢090 < max_int)inserting: (¢037 < 0)inserting: (¢051 < 0)inserting: (¢036 < min_int)inserting: (¢053 < min_int)
((i + 0) = i)
	inserting: ((¢014 + 0) = ¢014)
((0 + i) = i)
	inserting: ((0 + 1) = 1)
((i - 0) = i)
	inserting: ((Max_Depth - 0) = Max_Depth)
((i + j) = (j + i))
	inserting: ((min_int + 1) = (1 + min_int))
(((i + j) + k) = (i + (j + k)))
	inserting: (((¢006 + 0) + 0) = (¢006 + (0 + 0)))inserting: (((0 + 1) + min_int) = (0 + (1 + min_int)))
((i <= j) implies (0 <= (j - i)))
	inserting: (0 <= (0 - min_int))
(Reverse(Empty_String) = Empty_String)
	inserting: (Reverse(Empty_String) = Empty_String)
((S o Empty_String) = S)
	inserting: ((¢008 o Empty_String) = ¢008)

3 iterations. proved vc: 1_1
=?(Empty_String,Empty_String)=true
<=(min_int,0)=true
<=(min_int,Last_Char_Num)=true
<=(min_int,Max_Depth)=true
<=(min_int,¢034)=true
<=(min_int,¢058)=true
<=(min_int,¢060)=true
<=(min_int,¢062)=true
<=(min_int,¢064)=true
<=(min_int,¢066)=true
<=(min_int,¢068)=true
<=(min_int,¢070)=true
<=(min_int,¢074)=true
<=(min_int,¢076)=true
<=(min_int,¢078)=true
<=(min_int,¢080)=true
<=(min_int,¢082)=true
<=(min_int,¢084)=true
<=(min_int,¢086)=true
<=(min_int,¢090)=true
<=(min_int,¢094)=true
<=(min_int,¢096)=true
<=(min_int,¢098)=true
<=(min_int,¢100)=true
<=(0,Max_Depth)=true
<=(0,¢100)=true
<=(Max_Depth,max_int)=true
<=(¢006,Max_Depth)=true
<=(¢032,Last_Char_Num)=true
<=(¢036,0)=true
<=(¢036,¢037)=true
<=(¢040,0)=true
<=(¢040,¢037)=true
<=(¢043,Last_Char_Num)=true
<=(¢045,Max_Depth)=true
<=(¢049,max_int)=true
<=(¢051,Max_Depth)=true
<=(¢053,0)=true
<=(¢053,Last_Char_Num)=true
<=(¢053,Max_Depth)=true
<=(¢053,¢034)=true
<(min_int,Last_Char_Num)=true
<(min_int,1)=true
<(0,max_int)=true
<(0,Last_Char_Num)=true
<(0,1)=true
<(¢034,Last_Char_Num)=true
<(¢036,min_int)=true
<(¢037,0)=true
<(¢040,¢036)=true
<(¢043,¢032)=true
<(¢045,¢006)=true
<(¢049,Max_Depth)=true
<(¢051,0)=true
<(¢053,min_int)=true
<(¢090,max_int)=true
>(Last_Char_Num,0)=true
>(Max_Depth,0)=true
>(1,0)=true
|_|(S)=¢006
|_|(?Temp)=¢006
|_|(Empty_String)=0
o(S,Empty_String)=S
o(Empty_String,?Temp)=?Temp
Reverse(S)=?Temp
Reverse(?Temp)=S
Reverse(Empty_String)=Empty_String
+(min_int,1)=¢032
+(0,0)=¢114
+(0,1)=1
+(0,¢032)=¢032
+(¢006,0)=¢006
+(¢006,¢114)=¢006
+(1,min_int)=¢032
/=(Last_Char_Num,0)=true
>=(Last_Char_Num,0)=true
-(min_int,Last_Char_Num)=¢053
-(min_int,1)=¢036
-(0,min_int)=¢100
-(0,Last_Char_Num)=¢051
-(0,1)=¢037
-(0,¢036)=¢082
-(0,¢040)=¢078
-(0,¢053)=¢064
-(max_int,Max_Depth)=¢090
-(max_int,¢049)=¢068
-(Last_Char_Num,min_int)=¢098
-(Last_Char_Num,1)=¢034
-(Last_Char_Num,¢032)=¢084
-(Last_Char_Num,¢043)=¢074
-(Last_Char_Num,¢053)=¢062
-(Max_Depth,min_int)=¢096
-(Max_Depth,0)=Max_Depth
-(Max_Depth,Last_Char_Num)=¢049
-(Max_Depth,¢006)=¢086
-(Max_Depth,¢045)=¢070
-(Max_Depth,¢051)=¢066
-(Max_Depth,¢053)=¢060
-(¢006,Last_Char_Num)=¢045
-(¢032,Last_Char_Num)=¢043
-(¢034,min_int)=¢094
-(¢034,¢053)=¢058
-(¢036,Last_Char_Num)=¢040
-(¢037,¢036)=¢080
-(¢037,¢040)=¢076
----------------------------------
?Temp=?Temp

Proved 0_1time: 3 ms
Proved 0_2time: 0 ms
Proved 0_3time: 21 ms
Proved 0_4time: 116 ms
Proved 0_5time: 18 ms
Proved 1_1time: 40 ms

------------------------------------------------------------------------
BUILD SUCCESS
------------------------------------------------------------------------
Total time: 4.588s
Finished at: Sun Apr 13 21:56:25 EDT 2014
Final Memory: 5M/156M
------------------------------------------------------------------------


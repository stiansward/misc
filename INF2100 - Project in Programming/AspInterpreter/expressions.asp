"Testing integer expressions:"
42
-1017
-2 + 5 * 7
100 % (-2 % (+2 * 5) + 18)
1234567890 // 1000000

"Testing float expressions:"
42.0
-1017.1
-2 + 5.0 * 7
100 % (-2 % (+2 * 5.0) + 18)
1234567890.0 // 1000000.0

"Testing string expressions"
"Abc"
"x" * 10
"α" + "-"*5 + "ω"

"Testing boolean expressions"
not False
"To be" or "not to be"
"Yes" and 3.14
False or True or 144 or "?"

"Testing comparisons"
1 <= 2 <= 3
"a" != "b" > "b" != 99
3.14 > 2.718281828459045 > 0

"Testing lists"
[]
[22, "w", [-1,+1], 3.14159265]
[101,102,103][1]
[-1,0,1]*(2)
"Abcdef"[0]

"Testing dictionaries"
{"A": "a", "B": 1+2}
{"Ja": 1, "Nei": 0}["Ja"]

# JFilmak
Personnel project  
Under CC-BY-NC 4.0  
For more information check: https://creativecommons.org/licenses/by-nc/4.0/legalcode  
### Used Fonts:  
&ensp;&ensp;&ensp;&ensp;Smiley Sans : https://atelier-anchor.com/typefaces/smiley-sans/ (SIL Open Font License 1.1)  
&ensp;&ensp;&ensp;&ensp;TsangerYuMo : http://tsanger.cn/ (Public Domain)

### Calculator Syntax

#### Arithmetic operations:
**Keywords: +, -, \*, /, ^, (, )**

1 -2 \* 3 will be parsed to 1, 2, 3, \*, -.  
1/3 +0.1 will be parsed to 1, 3, /, 0.1 , +.  
4\*3^0.9/6 will be parsed to 4, 3, 0.9, ^, \*, 6, /.  

**Important:** when using "-" as negative sign, it adds parentheses which enclose all operations following and having higher precedence then negative sign. The negative sign can not be stacked, meaning that inputting expressions as "1---3" will lead to syntax problem.  
4+-1 will be transformed to 4+(0-1) then parsed to 4,0,1,-,+.  
5\*-6/2^7 will be transformed to 5\*(0-6/2^7) then parsed to 5,0,6,2,7,^,/,-,\*.  
4\*1.36^-3\*2.4-7 will be transformed to 4\*1.36^(0-3\*2.4)-7 then parsed to 4,1.36,0,3,2.4,\*,-,^,\*,7,-.  
Important: Adding parenthesis but not closing them will not lead to any syntax problem. However, the inverse is not permitted.  
3-(6/9\*(3+2 will be transformed to 3-(6/9\*(3+2)) then parsed to 3,6,9,/,3,2,+,\*,-.  
8-2/5) will not be successfully parsed.  

### Trigonometry functions:
**Keywords： sin, cos, tan, arcsin, arccos, arctan**  
**sinh, cosh, tanh, arsinh, arcosh, artanh**

Theses functions have the same precedence to addition and subtraction.  
si nx y^3 + 9 will be parsed to x,y,3,^,\*,sin,9,+.  
t an(x -6)^ 3-9 /4 will be parsed to x,6,-,3,^,tan,9,4,/,-.  
ar cs in ta n9 /4 will be parsed to 9,4,/,tan,arcsin.  

**Important:** expressions like sin(23)\*6 will be treated as sin((23)\*6) not 6sin(23), so it is recommanded to write the expressions in the following way : (sin(23))\*6

### Logarithm:
**Keywords: ln, log(base, power)**

ln1.9^0.39\*3 will be parsed to 1.9,0.39,^,3,\*,ln.  
log(3,13\*0.39)^3.1 will be parsed to 3,13,0.39,\*,log,3.1,^.  
log(3) will not be successfully parsed.  

### Constants:
**Keywords: \i, \e, \pi, \g, \G**

Constants as e or π are calculated during calculation.

(5+1.3\i)/(4-\i) will be parsed as 5,1.3,\i,\*,+,4,\i,-,/  
\i is the imaginary unit.  
\e is the Euler's number. e = 2.718281....  
\pi is π. π = 3.141592....  
\g is the standard acceleration due to gravity of earth. g = 9.80665(m/s²).  
\G is the gravitational constant. G = 6.67430E-11(m³/(kg\*s²)).  

### Summation and product operator:
**Keywords:**  
**sum(variable, start integer, end integer, expression),**  
**pro(variable, start integer, end integer, expression)**  

sum(x, 4, 13-3, x^3) will be parsed to x, 4, \[13,3,-\], \[x,3,^\], sum.  
pro(i, 1, 3, 5i+y)\*9.3 will be parsed to i, 1, 3, \[5,i,\*,y,+\], pro, 9.3, \*.  

### Variable:

5xy-4sin-x+5\i will be parsed as 5, x, \*, y, \*, 4, 0, x, -, sin, \*, -, 5, \i, \*, +  

It is possible to use multi-letter variables by doing : \[variable name\]. For example, one can write 10\[alpha\] which will be parsed as 10, \[alpha\], *. 

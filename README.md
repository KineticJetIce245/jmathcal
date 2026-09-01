# JMathcal

JMathcal is a calculator program that supports arithmetic operations, trigonometric functions, logarithms, constants, summation and product operators, and variables. It is distributed under the CC‑BY‑NC 4.0 license.

## Download and Installation

1. Download `jmathcal-1.0-RELEASE-shaded.jar` from the `target` folder.
2. Double‑click the JAR file to run the program.
3. The program requires a Java runtime environment. Download Java from [Oracle](https://www.oracle.com/ca-en/java/technologies/downloads/).

## Fonts Used

- **Smiley Sans** – [atelier-anchor.com](https://atelier-anchor.com/typefaces/smiley-sans/) (SIL Open Font License 1.1)
- **TsangerYuMo** – [tsanger.cn](http://tsanger.cn/) (Public Domain)

## License

This project is licensed under **CC‑BY‑NC 4.0**.  
For more details, see the [legal code](https://creativecommons.org/licenses/by-nc/4.0/legalcode).

---

## Calculator Syntax

### Arithmetic Operations

**Keywords:** `+`, `-`, `*`, `/`, `^`, `(`, `)`

Parsing follows standard precedence and left‑to‑right associativity. Examples:

- `1 - 2 * 3` → parsed as `1, 2, 3, *, -`
- `1/3 + 0.1` → `1, 3, /, 0.1, +`
- `4*3^0.9/6` → `4, 3, 0.9, ^, *, 6, /`

#### Negative Sign Handling

- The unary minus is treated by wrapping the following expression in `(0 - ...)`.
- Negative signs cannot be stacked (e.g., `1---3` causes a syntax error).
- Examples:
  - `4+-1` → `4+(0-1)` → parsed as `4,0,1,-,+`
  - `5*-6/2^7` → `5*(0-6/2^7)` → parsed as `5,0,6,2,7,^,/,-,*`
  - `4*1.36^-3*2.4-7` → `4*1.36^(0-3*2.4)-7` → `4,1.36,0,3,2.4,*, -, ^, *, 7, -`

#### Parentheses

- Unclosed opening parentheses are automatically closed at the end of the expression (e.g., `3-(6/9*(3+2` becomes `3-(6/9*(3+2))`).
- Closing parentheses without a matching opening one cause a parsing error (e.g., `8-2/5)` fails).

---

### Trigonometry Functions

**Keywords:** `sin`, `cos`, `tan`, `arcsin`, `arccos`, `arctan`, `sinh`, `cosh`, `tanh`, `arsinh`, `arcosh`, `artanh`

These functions have the same precedence as addition and subtraction.

Examples:
- `sin x y^3 + 9` → `x, y, 3, ^, *, sin, 9, +`
- `tan(x - 6)^3 - 9/4` → `x, 6, -, 3, ^, tan, 9, 4, /, -`
- `arcsin(tan 9 / 4)` → `9, 4, /, tan, arcsin`

**Important:**  
`sin(23)*6` is treated as `sin((23)*6)`. To multiply the result of a function, write `(sin(23))*6`.

---

### Logarithms

**Keywords:** `ln` (natural logarithm), `log(base, power)`

- `ln 1.9^0.39 * 3` → `1.9, 0.39, ^, 3, *, ln`
- `log(3, 13*0.39)^3.1` → `3, 13, 0.39, *, log, 3.1, ^`
- `log(3)` **will not parse successfully** – two arguments are required.

---

### Other Functions

**Keywords:** `sqrt`, `abs`, `sgn`, `todeg`, `deg`, `tograd`, `grad`, `PolR`, `PolT`

- `sqrt` – square root. It has the same precedence as addition and subtraction.  
  ⚠️ `sqrt(3)*3` is treated as `sqrt(3*3)`.
- `abs` – absolute value.
- `sgn` – sign of a number. For a complex number *z*, returns `z / abs(z)`.
- `deg` – converts a degree value to radians.
- `grad` – converts a grad value to radians.
- `todeg` – converts a radian value to degrees.
- `tograd` – converts a radian value to grads.
- `PolR(h, k)` – returns the norm (magnitude) of vector `<h, k>`.
- `PolT(h, k)` – returns the angle (in radians) of vector `<h, k>`.

Except for `sqrt`, all these functions have the **highest** precedence.

---

### Constants

**Keywords:** `\i`, `\e`, `\pi`, `\g`, `\G`, `\ran`

Constants are evaluated during calculation.

- `\i` – imaginary unit  
- `\e` – Euler’s number (2.718281…)  
- `\pi` – π (3.141592…)  
- `\g` – standard gravity (9.80665 m/s²)  
- `\G` – gravitational constant (6.67430×10⁻¹¹ m³/(kg·s²))  
- `\ran` – random real number in [0, 1)

Example:  
`(5+1.3\i)/(4-\i)` → parsed as `5, 1.3, \i, *, +, 4, \i, -, /`

---

### Summation and Product Operators

**Keywords:**  
`sum(variable, start, end, expression)`  
`pro(variable, start, end, expression)`

- `sum(x, 4, 13-3, x^3)` → `x, 4, [13,3,-], [x,3,^], sum`
- `pro(i, 1, 3, 5i+y) * 9.3` → `i, 1, 3, [5,i,*,y,+], pro, 9.3, *`

---

### Variables

Single‑letter variables are recognised (e.g., `5xy` means `5*x*y`).  
Multi‑letter variables can be enclosed in square brackets: `[variable name]`.

Examples:
- `5xy - 4sin - x + 5\i` → `5, x, *, y, *, 4, 0, x, -, sin, *, -, 5, \i, *, +`
- `10[alpha]` → `10, [alpha], *`

---

## Parsing Notes

- All expressions are parsed in a postfix (RPN) style.
- Implicit multiplication is supported (e.g., `5xy` is `5*x*y`).
- Use parentheses to explicitly control evaluation order, especially when mixing functions and multiplication.
- The parser automatically closes unclosed opening parentheses, but extra closing parentheses cause errors.

For any further questions, refer to the source code or contact the project maintainers.

package kim.jeonghyeon.recursive

fun main() {
    eval(
        Sum(Sum(Num(1), Sum(Num(1), Num(2))), Num(1))
    )

}

/**
 * for calculation. there is two type
 * 1. number
 * 2. operator
 */
open class Expr

class Num(val value: Int) : Expr()

class Sum(val left: Expr, val right: Expr) : Expr()

fun eval(expr: Expr): Int {
    return when (expr) {
        is Num -> expr.value
        is Sum -> eval(expr.left) + eval(expr.right)
        else -> error("Unknown Expression")
    }
}

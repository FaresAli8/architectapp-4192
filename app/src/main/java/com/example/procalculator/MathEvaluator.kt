package com.example.procalculator

import java.util.Stack
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A robust Math evaluator that handles basic operations, parentheses,
 * roots, exponents and order of operations using Shunting Yard Algorithm.
 * Zero external dependencies.
 */
object MathEvaluator {

    fun evaluate(expression: String): Double {
        val sanitized = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace(" ", "")

        val tokens = tokenize(sanitized)
        val rpn = shuntingYard(tokens)
        return evaluateRPN(rpn)
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                    continue
                }
                c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '%' || c == '(' || c == ')' -> {
                    // Check for unary minus
                    if (c == '-' && (tokens.isEmpty() || tokens.last() == "(" || isOperator(tokens.last()))) {
                        // It's a negative number start or negation
                        // For simplicity in this parser, we treat it as 0 - x or multiply by -1
                        // A simple hack: push "0" then "-"
                        // Better approach for simple tokenizer: read next number and negate
                        tokens.add("neg") // special token
                    } else {
                        tokens.add(c.toString())
                    }
                }
                c == '√' -> tokens.add("sqrt")
                else -> { /* ignore unknown */ }
            }
            i++
        }
        return tokens
    }

    private fun isOperator(token: String): Boolean {
        return token in listOf("+", "-", "*", "/", "^", "%", "sqrt", "neg")
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/", "%" -> 2
            "^", "sqrt", "neg" -> 3
            else -> 0
        }
    }

    private fun shuntingYard(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token == "(" -> stack.push(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(") {
                        output.add(stack.pop())
                    }
                    if (stack.isNotEmpty()) stack.pop() // pop '('
                }
                isOperator(token) -> {
                    while (stack.isNotEmpty() && stack.peek() != "(" && precedence(stack.peek()) >= precedence(token)) {
                        output.add(stack.pop())
                    }
                    stack.push(token)
                }
            }
        }
        while (stack.isNotEmpty()) {
            output.add(stack.pop())
        }
        return output
    }

    private fun evaluateRPN(rpn: List<String>): Double {
        val stack = Stack<Double>()

        for (token in rpn) {
            if (token.toDoubleOrNull() != null) {
                stack.push(token.toDouble())
            } else {
                if (token == "neg") {
                     val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                     stack.push(-a)
                     continue
                }
                if (token == "sqrt") {
                    val a = if (stack.isNotEmpty()) stack.pop() else 0.0
                    stack.push(sqrt(a))
                    continue
                }

                val b = if (stack.isNotEmpty()) stack.pop() else 0.0
                val a = if (stack.isNotEmpty()) stack.pop() else 0.0

                when (token) {
                    "+" -> stack.push(a + b)
                    "-" -> stack.push(a - b)
                    "*" -> stack.push(a * b)
                    "/" -> stack.push(a / b)
                    "^" -> stack.push(a.pow(b))
                    "%" -> stack.push(a * (b / 100.0)) // simple percent logic
                }
            }
        }
        return if (stack.isNotEmpty()) stack.pop() else 0.0
    }
}
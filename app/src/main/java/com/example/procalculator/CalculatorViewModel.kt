package com.example.procalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DecimalFormat

data class CalculatorState(
    val expression: String = "",
    val result: String = "",
    val history: List<String> = emptyList(),
    val isHistoryVisible: Boolean = false
)

class CalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CalculatorState())
    val uiState: StateFlow<CalculatorState> = _uiState.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> append(action.number)
            is CalculatorAction.Operator -> append(action.operator)
            is CalculatorAction.Clear -> {
                _uiState.value = _uiState.value.copy(expression = "", result = "")
            }
            is CalculatorAction.Delete -> {
                val expr = _uiState.value.expression
                if (expr.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(expression = expr.dropLast(1))
                }
            }
            is CalculatorAction.Calculate -> calculate()
            is CalculatorAction.ToggleHistory -> {
                _uiState.value = _uiState.value.copy(isHistoryVisible = !_uiState.value.isHistoryVisible)
            }
            is CalculatorAction.ClearHistory -> {
                _uiState.value = _uiState.value.copy(history = emptyList())
            }
        }
    }

    private fun append(char: String) {
        _uiState.value = _uiState.value.copy(
            expression = _uiState.value.expression + char
        )
    }

    private fun calculate() {
        val expr = _uiState.value.expression
        if (expr.isBlank()) return

        try {
            val res = MathEvaluator.evaluate(expr)
            val df = DecimalFormat("#.########") // Remove trailing zeros
            val formattedResult = df.format(res)
            
            val newHistory = _uiState.value.history.toMutableList()
            newHistory.add(0, "$expr = $formattedResult")

            _uiState.value = _uiState.value.copy(
                result = formattedResult,
                history = newHistory
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(result = "Error")
        }
    }
}

sealed class CalculatorAction {
    data class Number(val number: String) : CalculatorAction()
    data class Operator(val operator: String) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Calculate : CalculatorAction()
    object ToggleHistory : CalculatorAction()
    object ClearHistory : CalculatorAction()
}
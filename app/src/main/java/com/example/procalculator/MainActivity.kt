package com.example.procalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFFBB86FC),
                    secondary = Color(0xFF03DAC6),
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E),
                    onPrimary = Color.Black,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Bar Area
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.onAction(CalculatorAction.ToggleHistory) }) {
                Icon(Icons.Default.History, contentDescription = "History", tint = MaterialTheme.colorScheme.primary)
            }
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
            Spacer(modifier = Modifier.width(48.dp)) // Balance
        }

        // History View (Collapsible)
        AnimatedVisibility(
            visible = state.isHistoryVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.history_title), fontWeight = FontWeight.Bold)
                        TextButton(onClick = { viewModel.onAction(CalculatorAction.ClearHistory) }) {
                            Text(stringResource(R.string.clear_history), color = Color.Red)
                        }
                    }
                    if (state.history.isEmpty()) {
                        Text(
                            stringResource(R.string.no_history),
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                            items(state.history) { item ->
                                Text(
                                    text = item,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Divider(color = Color.Gray.copy(alpha = 0.2f))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Display Area
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = state.expression,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.End,
                lineHeight = 40.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (state.result.isNotEmpty()) "= ${state.result}" else "",
                fontSize = 48.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                lineHeight = 56.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Keypad
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Advanced Row
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalcButton(text = "C", color = Color(0xFFCF6679), onClick = { viewModel.onAction(CalculatorAction.Clear) })
                CalcButton(text = "(", color = MaterialTheme.colorScheme.secondary, onClick = { viewModel.onAction(CalculatorAction.Operator("(")) })
                CalcButton(text = ")", color = MaterialTheme.colorScheme.secondary, onClick = { viewModel.onAction(CalculatorAction.Operator(")")) })
                CalcButton(text = "÷", color = MaterialTheme.colorScheme.primary, onClick = { viewModel.onAction(CalculatorAction.Operator("/")) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalcButton(text = "7", onClick = { viewModel.onAction(CalculatorAction.Number("7")) })
                CalcButton(text = "8", onClick = { viewModel.onAction(CalculatorAction.Number("8")) })
                CalcButton(text = "9", onClick = { viewModel.onAction(CalculatorAction.Number("9")) })
                CalcButton(text = "×", color = MaterialTheme.colorScheme.primary, onClick = { viewModel.onAction(CalculatorAction.Operator("*")) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalcButton(text = "4", onClick = { viewModel.onAction(CalculatorAction.Number("4")) })
                CalcButton(text = "5", onClick = { viewModel.onAction(CalculatorAction.Number("5")) })
                CalcButton(text = "6", onClick = { viewModel.onAction(CalculatorAction.Number("6")) })
                CalcButton(text = "−", color = MaterialTheme.colorScheme.primary, onClick = { viewModel.onAction(CalculatorAction.Operator("-")) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalcButton(text = "1", onClick = { viewModel.onAction(CalculatorAction.Number("1")) })
                CalcButton(text = "2", onClick = { viewModel.onAction(CalculatorAction.Number("2")) })
                CalcButton(text = "3", onClick = { viewModel.onAction(CalculatorAction.Number("3")) })
                CalcButton(text = "+", color = MaterialTheme.colorScheme.primary, onClick = { viewModel.onAction(CalculatorAction.Operator("+")) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CalcButton(text = ".", onClick = { viewModel.onAction(CalculatorAction.Number(".")) })
                CalcButton(text = "0", onClick = { viewModel.onAction(CalculatorAction.Number("0")) })
                CalcButton(icon = Icons.Default.Backspace, color = Color(0xFFCF6679), onClick = { viewModel.onAction(CalculatorAction.Delete) })
                CalcButton(text = "=", color = Color(0xFF03DAC6), textColor = Color.Black, onClick = { viewModel.onAction(CalculatorAction.Calculate) })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                 CalcButton(text = "√", color = MaterialTheme.colorScheme.secondary, onClick = { viewModel.onAction(CalculatorAction.Operator("√")) })
                 CalcButton(text = "^", color = MaterialTheme.colorScheme.secondary, onClick = { viewModel.onAction(CalculatorAction.Operator("^")) })
                 CalcButton(text = "%", color = MaterialTheme.colorScheme.secondary, onClick = { viewModel.onAction(CalculatorAction.Operator("%")) })
            }
        }
    }
}

@Composable
fun RowScope.CalcButton(
    text: String? = null,
    icon: ImageVector? = null,
    color: Color = Color(0xFF2D2D2D),
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1.3f) // slightly wider than tall
            .clip(RoundedCornerShape(24.dp))
            .background(color)
            .clickable { onClick() }
    ) {
        if (text != null) {
            Text(text = text, fontSize = 24.sp, color = textColor, fontWeight = FontWeight.Bold)
        } else if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = textColor)
        }
    }
}
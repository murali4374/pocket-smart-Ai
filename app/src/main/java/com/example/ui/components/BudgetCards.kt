package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BudgetDangerRed
import com.example.ui.theme.BudgetSafeGreen
import com.example.ui.theme.BudgetWarningAmber
import java.text.NumberFormat
import java.util.Locale

fun formatInr(amount: Double): String {
    return try {
        val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
        format.maximumFractionDigits = 0
        format.format(amount)
    } catch (e: Exception) {
        "₹${String.format(Locale.getDefault(), "%,.0f", amount)}"
    }
}

@Composable
fun BudgetSummaryPanel(
    budget: Double,
    estimatedTotal: Double,
    remainingBudget: Double,
    modifier: Modifier = Modifier
) {
    val progress = if (budget > 0) (estimatedTotal / budget).toFloat().coerceIn(0f, 1.5f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceAtMost(1f), label = "budgetProgress")

    val statusColor = when {
        estimatedTotal > budget -> BudgetDangerRed
        remainingBudget < budget * 0.05 -> BudgetWarningAmber
        else -> BudgetSafeGreen
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("budget_summary_panel"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BUDGET ALLOCATION SUMMARY",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = null
                ) {
                    Text(
                        text = if (estimatedTotal <= budget) "✓ WITHIN BUDGET" else "OVER BUDGET",
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Column Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BudgetMetricItem(
                    label = "TOTAL BUDGET",
                    value = formatInr(budget),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                BudgetMetricItem(
                    label = "ESTIMATED SPEND",
                    value = formatInr(estimatedTotal),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1.1f)
                )
                BudgetMetricItem(
                    label = "BUFFER / REMAINING",
                    value = formatInr(remainingBudget),
                    color = statusColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Budget Utilization: ${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(remainingBudget / (if (budget > 0) budget else 1.0) * 100).toInt()}% buffer remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (progress > 1f) BudgetDangerRed else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
fun BudgetMetricItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            ),
            color = color,
            maxLines = 1
        )
    }
}

package com.hugodev.horasconamor.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hugodev.horasconamor.R
import com.hugodev.horasconamor.domain.OvertimeCalendar
import com.hugodev.horasconamor.ui.DailyOvertime
import com.hugodev.horasconamor.ui.OvertimeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: OvertimeViewModel) {
    val weekStart by viewModel.historyStartDate.collectAsState()
    val days by viewModel.historyDays.collectAsState()
    val incrementMinutes by viewModel.incrementMinutes.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val todayWeekStart = OvertimeCalendar.weekStart(LocalDate.now())
    val spanishLocale = Locale.forLanguageTag("es-ES")
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", spanishLocale)
    val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM", spanishLocale)
    val totalMinutes = days.sumOf(DailyOvertime::minutes)
    val weekRange = "${weekStart.format(dateFormatter)} – ${weekStart.plusDays(6).format(DateTimeFormatter.ofPattern("d MMM yyyy", spanishLocale))}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.history_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = { viewModel.moveHistoryWeek(-1) }) {
                        Icon(
                            Icons.Filled.ChevronLeft,
                            contentDescription = stringResource(R.string.previous_week),
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.week_of_label),
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Text(
                            text = weekRange,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    IconButton(
                        onClick = { viewModel.moveHistoryWeek(1) },
                        enabled = weekStart < todayWeekStart,
                    ) {
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = stringResource(R.string.next_week),
                        )
                    }
                }
                Text(
                    text = OvertimeCalendar.formatDuration(totalMinutes),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(days, key = { it.date.toString() }) { day ->
                HistoryDayRow(
                    day = day,
                    label = day.date.format(fullDateFormatter).replaceFirstChar { it.titlecase(spanishLocale) },
                    incrementMinutes = incrementMinutes,
                    canEdit = !day.date.isAfter(LocalDate.now()),
                    onAdjust = { delta ->
                        coroutineScope.launch {
                            viewModel.adjustMinutes(day.date, delta)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun HistoryDayRow(
    day: DailyOvertime,
    label: String,
    incrementMinutes: Int,
    canEdit: Boolean,
    onAdjust: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(
                    OvertimeCalendar.formatDuration(day.minutes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(
                onClick = { onAdjust(-incrementMinutes) },
                enabled = canEdit && day.minutes > 0,
            ) {
                Icon(Icons.Filled.Remove, contentDescription = stringResource(R.string.remove_time_accessibility))
            }
            IconButton(
                onClick = { onAdjust(incrementMinutes) },
                enabled = canEdit,
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_time_accessibility))
            }
        }
    }
}

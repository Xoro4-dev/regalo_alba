package com.hugodev.horasconamor.ui.counter

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hugodev.horasconamor.R
import com.hugodev.horasconamor.domain.OvertimeCalendar
import com.hugodev.horasconamor.ui.OvertimeViewModel
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CounterScreen(viewModel: OvertimeViewModel) {
    val summary by viewModel.todaySummary.collectAsState()
    val incrementMinutes by viewModel.incrementMinutes.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val reactionMessages = stringArrayResource(R.array.reaction_messages)
    val removedMessage = stringResource(R.string.time_removed_message)
    val undoLabel = stringResource(R.string.undo_action)
    val weekendMessage = stringResource(R.string.weekend_counter_message)
    val spanishLocale = remember { Locale.forLanguageTag("es-ES") }
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", spanishLocale)
    }
    val isWorkday = OvertimeCalendar.isWorkday(summary.date)
    val burstProgress = remember { Animatable(1f) }
    var reactionTrigger by remember { mutableIntStateOf(0) }
    val counterShape = RoundedCornerShape(32.dp)
    val counterColor = lerp(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        1f - burstProgress.value,
    )
    val counterTextColor = lerp(
        MaterialTheme.colorScheme.onPrimaryContainer,
        MaterialTheme.colorScheme.tertiary,
        (1f - burstProgress.value) * 0.9f,
    )

    LaunchedEffect(reactionTrigger) {
        if (reactionTrigger == 0) return@LaunchedEffect
        burstProgress.snapTo(0f)
        burstProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.home_greeting),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = summary.date.format(dateFormatter).replaceFirstChar { it.titlecase(spanishLocale) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.tertiary.copy(
                                alpha = ((1f - burstProgress.value) * 0.85f).coerceIn(0f, 1f),
                            ),
                        ),
                        counterShape,
                    ),
                shape = counterShape,
                colors = CardDefaults.cardColors(containerColor = counterColor),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.counter_status_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = counterTextColor.copy(alpha = 0.82f),
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(112.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawHourBurst(burstProgress.value)
                        }
                        Text(
                            text = OvertimeCalendar.formatDuration(summary.todayMinutes),
                            modifier = Modifier.offset(y = ((1f - burstProgress.value) * -5).dp),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = counterTextColor,
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        OutlinedButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            enabled = isWorkday && summary.todayMinutes > 0,
                            onClick = {
                                coroutineScope.launch {
                                    val change = viewModel.adjustMinutes(summary.date, -incrementMinutes)
                                    if (change != null) {
                                        val result = snackbarHostState.showSnackbar(
                                            message = removedMessage,
                                            actionLabel = undoLabel,
                                        )
                                        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                                            viewModel.undo(change)
                                        }
                                    }
                                }
                            },
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = null)
                            Text(
                                text = stringResource(R.string.decrement_label, incrementMinutes),
                                modifier = Modifier.padding(start = 6.dp),
                            )
                        }
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            enabled = isWorkday,
                            onClick = {
                                coroutineScope.launch {
                                    val change = viewModel.adjustMinutes(summary.date, incrementMinutes)
                                    if (change != null) {
                                        reactionTrigger += 1
                                        val result = snackbarHostState.showSnackbar(
                                            message = reactionMessages.random(),
                                            actionLabel = undoLabel,
                                        )
                                        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                                            viewModel.undo(change)
                                        }
                                    }

                                }
                            },
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Text(
                                text = stringResource(R.string.increment_label, incrementMinutes),
                                modifier = Modifier.padding(start = 6.dp),
                            )
                        }
                    }
                }
            }

            if (!isWorkday) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ) {
                    Text(
                        text = weekendMessage,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = stringResource(R.string.week_total_label),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = stringResource(R.string.week_total_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = OvertimeCalendar.formatDuration(summary.weekMinutes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Text(
                text = stringResource(R.string.counter_reassurance),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

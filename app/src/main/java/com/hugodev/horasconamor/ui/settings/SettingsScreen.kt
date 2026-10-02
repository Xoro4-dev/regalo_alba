package com.hugodev.horasconamor.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hugodev.horasconamor.R
import com.hugodev.horasconamor.ui.OvertimeViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(viewModel: OvertimeViewModel) {
    val incrementMinutes by viewModel.incrementMinutes.collectAsState()
    val angerLevel by viewModel.angerLevel.collectAsState()
    val beerChance by viewModel.beerChance.collectAsState()
    val angerDescription = when {
        angerLevel < 25 -> stringResource(R.string.anger_calm)
        angerLevel < 50 -> stringResource(R.string.anger_mild)
        angerLevel < 75 -> stringResource(R.string.anger_warning)
        else -> stringResource(R.string.anger_critical)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.settings_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        SettingsCard(title = stringResource(R.string.increment_setting_title)) {
            Text(
                text = stringResource(R.string.increment_setting_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OvertimeViewModel.INCREMENT_OPTIONS.forEach { minutes ->
                    FilterChip(
                        modifier = Modifier.weight(1f),
                        selected = incrementMinutes == minutes,
                        onClick = { viewModel.setIncrementMinutes(minutes) },
                        label = {
                            Text(
                                text = stringResource(R.string.increment_label, minutes),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        },
                    )
                }
            }
        }

        SettingsCard(
            title = stringResource(R.string.anger_meter_title),
            subtitle = stringResource(R.string.slider_range_hint),
        ) {
            Slider(
                value = angerLevel.toFloat(),
                onValueChange = { viewModel.setAngerLevel(it.roundToInt()) },
                valueRange = 0f..100f,
                steps = 19,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = angerDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    text = stringResource(R.string.percentage_label, angerLevel),
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }

        SettingsCard(
            title = stringResource(R.string.love_meter_title),
            subtitle = stringResource(R.string.love_meter_caption),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LinearProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                )
                Text(
                    text = stringResource(R.string.percentage_label, 100),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }

        SettingsCard(
            title = stringResource(R.string.beer_meter_title),
            subtitle = stringResource(R.string.beer_meter_caption),
        ) {
            Slider(
                value = beerChance.toFloat(),
                onValueChange = { viewModel.setBeerChance(it.roundToInt()) },
                valueRange = 0f..100f,
                steps = 9,
            )
            Text(
                text = stringResource(R.string.percentage_label, beerChance),
                modifier = Modifier.align(Alignment.End),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text = stringResource(R.string.local_storage_notice),
            modifier = Modifier.padding(bottom = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    subtitle: String? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                content()
            },
        )
    }
}

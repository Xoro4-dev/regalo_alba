package com.hugodev.horasconamor.ui.settings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
    var loveValue by remember { mutableFloatStateOf(100f) }
    var loveResetTrigger by remember { mutableIntStateOf(0) }
    val loveBounce = remember { Animatable(0f) }
    val angerDescription = when {
        angerLevel < 25 -> stringResource(R.string.anger_calm)
        angerLevel < 50 -> stringResource(R.string.anger_mild)
        angerLevel < 75 -> stringResource(R.string.anger_warning)
        else -> stringResource(R.string.anger_critical)
    }

    LaunchedEffect(loveResetTrigger) {
        if (loveResetTrigger == 0) return@LaunchedEffect
        val progress = Animatable((loveValue / 100f).coerceIn(0f, 1f))
        progress.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.42f, stiffness = 180f),
        ) {
            loveValue = value * 100f
        }
        loveValue = 100f
        loveBounce.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 420
                0f at 0
                1f at 110
                0f at 230
                0.45f at 310
                0f at 420
            },
        )
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
            Slider(
                value = (loveValue / 100f).coerceIn(0f, 1f),
                onValueChange = { loveValue = it * 100f },
                onValueChangeFinished = { loveResetTrigger += 1 },
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.secondary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.22f),
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.love_meter_reset_hint),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.percentage_label, loveValue.roundToInt()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .graphicsLayer {
                            translationY = -loveBounce.value * 14.dp.toPx()
                            scaleX = 1f + loveBounce.value * 0.22f
                            scaleY = 1f + loveBounce.value * 0.22f
                        },
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

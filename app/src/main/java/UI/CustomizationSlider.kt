package com.example.dishcustomizerdemo.ui.components

import DataClasses.CustomizationCategory
import DataClasses.CustomizationType
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.round

@Composable
fun CustomizationSlider(
    customizationType: CustomizationType,
    currentValue: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Enhanced animated values with different easing curves
    val animatedSliderValue by animateFloatAsState(
        targetValue = currentValue,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "slider_value_animation"
    )

    val animatedDisplayValue by animateFloatAsState(
        targetValue = currentValue,
        animationSpec = tween(
            durationMillis = 300,
            easing = LinearOutSlowInEasing
        ),
        label = "display_value_animation"
    )

    // Dynamic glow effect based on value intensity
    val glowIntensity by animateFloatAsState(
        targetValue = currentValue / customizationType.maxValue,
        animationSpec = tween(durationMillis = 400),
        label = "glow_animation"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Enhanced top label row with dynamic styling
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${customizationType.type.emoji} ${customizationType.type.displayName}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2D2D2D)
            )

            // Enhanced value display with pulsing effect
            val infiniteTransition = rememberInfiniteTransition(label = "value_pulse")
            val valueScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = if (glowIntensity > 0.7f) 1.1f else 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "value_scale"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        getSliderColor(customizationType.type).copy(alpha = 0.1f + glowIntensity * 0.2f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .graphicsLayer {
                        scaleX = valueScale
                        scaleY = valueScale
                    }
            ) {
                Text(
                    text = "${round(animatedDisplayValue).toInt()}/${customizationType.maxValue.toInt()}",
                    fontSize = 14.sp,
                    color = getSliderColor(customizationType.type),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Enhanced label feedback with gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            getSliderColor(customizationType.type).copy(alpha = 0.1f),
                            getSliderColor(customizationType.type).copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            AnimatedLabelText(
                category = customizationType.type,
                value = animatedDisplayValue,
                maxValue = customizationType.maxValue
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Enhanced visual feedback with more dynamic effects
        EnhancedVisualFeedbackRow(
            category = customizationType.type,
            value = animatedDisplayValue,
            maxValue = customizationType.maxValue,
            glowIntensity = glowIntensity
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Enhanced slider with custom track styling
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Custom gradient track background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFE0E0E0),
                                getSliderColor(customizationType.type).copy(alpha = 0.3f),
                                getSliderColor(customizationType.type).copy(alpha = 0.6f)
                            )
                        )
                    )
                    .align(Alignment.Center)
            )

            Slider(
                value = currentValue,
                onValueChange = onValueChange,
                valueRange = customizationType.minValue..customizationType.maxValue,
                steps = 0,
                colors = SliderDefaults.colors(
                    thumbColor = getSliderColor(customizationType.type),
                    activeTrackColor = getSliderColor(customizationType.type).copy(alpha = 0.8f),
                    inactiveTrackColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AnimatedLabelText(
    category: CustomizationCategory,
    value: Float,
    maxValue: Float
) {
    val label = getCustomizationLabel(category, value, maxValue)

    // Enhanced color animation with intensity-based effects
    val labelColor by animateColorAsState(
        targetValue = when {
            value > maxValue * 0.8f -> getSliderColor(category).copy(alpha = 1f)
            value > maxValue * 0.5f -> getSliderColor(category).copy(alpha = 0.8f)
            else -> getSliderColor(category).copy(alpha = 0.6f)
        },
        animationSpec = tween(durationMillis = 300),
        label = "label_color_animation"
    )

    // Add text emphasis for extreme values
    val textWeight by animateIntAsState(
        targetValue = if (value > maxValue * 0.8f) 700 else 500,
        animationSpec = tween(durationMillis = 200),
        label = "text_weight"
    )

    Text(
        text = label,
        fontSize = if (value > maxValue * 0.8f) 15.sp else 14.sp,
        color = labelColor,
        fontWeight = FontWeight(textWeight),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun EnhancedVisualFeedbackRow(
    category: CustomizationCategory,
    value: Float,
    maxValue: Float,
    glowIntensity: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        when (category) {
            CustomizationCategory.SPICE_LEVEL,
            CustomizationCategory.SALTINESS -> {
                val targetCount = round(value).toInt()
                val progress = value - round(value).toInt() + 1f

                // Enhanced emojis with glow effects and bounce animations
                for (i in 1..maxValue.toInt()) {
                    val isActive = i <= targetCount
                    val isPartial = i == targetCount + 1 && progress > 0f

                    val alpha by animateFloatAsState(
                        targetValue = when {
                            isActive -> 1f
                            isPartial -> progress.coerceIn(0f, 1f)
                            else -> 0.2f
                        },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "emoji_alpha_$i"
                    )

                    val scale by animateFloatAsState(
                        targetValue = if (isActive && glowIntensity > 0.7f) 1.2f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "emoji_scale_$i"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .shadow(
                                elevation = if (isActive) (4 + glowIntensity * 4).dp else 0.dp,
                                shape = CircleShape,
                                ambientColor = getSliderColor(category),
                                spotColor = getSliderColor(category)
                            )
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                    ) {
                        Text(
                            text = if (category == CustomizationCategory.SPICE_LEVEL) "🌶️" else "🧂",
                            fontSize = 20.sp,
                            color = Color.Black.copy(alpha = alpha.coerceIn(0f, 1f)),
                        )
                    }
                }
            }

            CustomizationCategory.PORTION_SIZE -> {
                val scale by animateFloatAsState(
                    targetValue = 0.8f + (value / maxValue) * 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "portion_animation"
                )

                val rotation by animateFloatAsState(
                    targetValue = if (glowIntensity > 0.8f) 5f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "portion_rotation"
                )

                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = (glowIntensity * 8).dp,
                            shape = CircleShape,
                            ambientColor = getSliderColor(category),
                            spotColor = getSliderColor(category)
                        )
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            rotationZ = rotation
                        }
                ) {
                    Text(
                        text = "🍽️",
                        fontSize = (16 + (scale * 16)).sp,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }

            CustomizationCategory.SWEETNESS -> {
                val targetCount = round(value).toInt()

                for (i in 1..maxValue.toInt()) {
                    val isActive = i <= targetCount

                    val alpha by animateFloatAsState(
                        targetValue = if (isActive) 1f else 0.2f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "sweetness_alpha_$i"
                    )

                    val bounce by animateFloatAsState(
                        targetValue = if (isActive && glowIntensity > 0.6f) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "sweetness_bounce_$i"
                    )

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .shadow(
                                elevation = if (isActive) (2 + glowIntensity * 3).dp else 0.dp,
                                shape = CircleShape,
                                ambientColor = getSliderColor(category),
                                spotColor = getSliderColor(category)
                            )
                            .graphicsLayer {
                                scaleX = bounce
                                scaleY = bounce
                            }
                    ) {
                        Text(
                            text = "🍯",
                            fontSize = 20.sp,
                            color = Color.Black.copy(alpha = alpha.coerceIn(0f, 1f))
                        )
                    }
                }
            }
        }
    }
}

private fun getCustomizationLabel(
    category: CustomizationCategory,
    value: Float,
    maxValue: Float
): String {
    val roundedValue = round(value).toInt()

    return when (category) {
        CustomizationCategory.SPICE_LEVEL -> {
            when (roundedValue) {
                0 -> "No Spice 😌"
                1 -> "Mild 🙂"
                2 -> "Less Spicy 😊"
                3 -> "Medium Spicy 🌶️"
                4 -> "Hot & Spicy 🔥"
                5 -> "Extra Hot 🌋"
                else -> "Extreme Heat 💀"
            }
        }

        CustomizationCategory.PORTION_SIZE -> {
            val percentage = (value / maxValue * 100).toInt()
            when {
                percentage <= 20 -> "Enough for 1 people 🤏"
                percentage <= 40 -> "Enough for 2 people 🥄"
                percentage <= 60 -> "Enough for 2 people 🍽️"
                percentage <= 80 -> "Enough for 3 people 🍛"
                percentage <= 95 -> "Enough for 4 people 📏"
                else -> "Mega Portion 🏔️"
            }
        }

        CustomizationCategory.SWEETNESS -> {
            when (roundedValue) {
                0 -> "No Sweetness 😐"
                1 -> "Lightly Sweet 🙂"
                2 -> "Mildly Sweet 😊"
                3 -> "Sweet 🍯"
                4 -> "Very Sweet 🍰"
                5 -> "Extra Sweet 🧁"
                else -> "Sugar Rush 🍭"
            }
        }

        CustomizationCategory.SALTINESS -> {
            when (roundedValue) {
                0 -> "No Salt 😔"
                1 -> "Low Salt 🙂"
                2 -> "Light Salt 😊"
                3 -> "Perfect Salt 🧂"
                4 -> "Salty 🌊"
                5 -> "Extra Salty 🧂🧂"
                else -> "Salt Overload 💀"
            }
        }
    }
}

private fun getSliderColor(category: CustomizationCategory): Color {
    return when (category) {
        CustomizationCategory.SPICE_LEVEL -> Color(0xFFFF5722) // Red-Orange
        CustomizationCategory.PORTION_SIZE -> Color(0xFF00A651) // Green
        CustomizationCategory.SWEETNESS -> Color(0xFFFC8019) // Orange
        CustomizationCategory.SALTINESS -> Color(0xFF607D8B) // Blue Grey
    }
}
package com.example.dishcustomizerdemo.ui.screens

import DataClasses.CustomizationCategory
import DataClasses.Dish
import ViewModels.CustomizationViewModel
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dishcustomizerdemo.R
import com.example.dishcustomizerdemo.ui.components.CustomizationSlider
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationScreen(
    dish: Dish,
    onBackClick: () -> Unit,
    viewModel: CustomizationViewModel = viewModel()
) {
    val customization by viewModel.customization.collectAsState()
    val isAddedToCart by viewModel.isAddedToCart.collectAsState()

    LaunchedEffect(dish) {
        viewModel.setDish(dish)
    }

    // Calculate dynamic background colors based on customization values
    val dynamicBackgroundGradient by animateGradientAsState(
        targetColors = calculateDynamicColors(customization, dish),
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Customize ${dish.name}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF00A651)
                )
            )
        },
        containerColor = Color.Transparent // Make transparent to show gradient background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(dynamicBackgroundGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                DishHeader(dish = dish, backgroundGradient = dynamicBackgroundGradient)

                // Customization Sliders with glass morphism effect
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            text = "🎛️ Customize Your Dish",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D2D2D)
                        )

                        dish.customizations.forEach { customizationType ->
                            when (customizationType.type) {
                                CustomizationCategory.SPICE_LEVEL -> {
                                    CustomizationSlider(
                                        customizationType = customizationType,
                                        currentValue = customization.spiceLevel,
                                        onValueChange = viewModel::updateSpiceLevel
                                    )
                                }
                                CustomizationCategory.PORTION_SIZE -> {
                                    CustomizationSlider(
                                        customizationType = customizationType,
                                        currentValue = customization.portionSize,
                                        onValueChange = viewModel::updatePortionSize
                                    )
                                }
                                CustomizationCategory.SWEETNESS -> {
                                    CustomizationSlider(
                                        customizationType = customizationType,
                                        currentValue = customization.sweetness,
                                        onValueChange = viewModel::updateSweetness
                                    )
                                }
                                CustomizationCategory.SALTINESS -> {
                                    CustomizationSlider(
                                        customizationType = customizationType,
                                        currentValue = customization.saltiness,
                                        onValueChange = viewModel::updateSaltiness
                                    )
                                }
                            }
                            // Enhanced divider with dynamic color
                            if (customizationType != dish.customizations.last()) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = getDominantColor(dynamicBackgroundGradient).copy(alpha = 0.3f),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }

                // Enhanced Add to Cart Button with pulsing effect
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = !isAddedToCart,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "button_pulse")
                        val buttonScale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.02f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "button_scale"
                        )

                        Button(
                            onClick = { viewModel.addToCart() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .graphicsLayer {
                                    scaleX = buttonScale
                                    scaleY = buttonScale
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00A651)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 12.dp
                            )
                        ) {
                            Text(
                                text = "Add to Cart - ₹${dish.price.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = isAddedToCart,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00A651)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Added to Cart!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Bottom spacing for better scrolling
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DishHeader(dish: Dish, backgroundGradient: Brush) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Enhanced dish image with glow effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            getImageColor(dish.imageUrl).copy(alpha = 0.8f),
                            getImageColor(dish.imageUrl).copy(alpha = 0.4f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = getImageResource(dish.imageUrl)),
                contentScale = ContentScale.Crop,
                contentDescription = dish.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enhanced text with shadow effects
        Text(
            text = dish.name,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 1.0f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )

        Text(
            text = dish.description,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = "₹${dish.price.toInt()}",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 1.0f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            )
        )
    }
}

@Composable
private fun animateGradientAsState(
    targetColors: List<Color>,
    animationSpec: AnimationSpec<Color> = tween()
): State<Brush> {
    val animatedColors = targetColors.map { targetColor ->
        val animatedColor = animateColorAsState(
            targetValue = targetColor,
            animationSpec = animationSpec,
            label = "gradient_color"
        ).value

        // Clamp alpha to valid range
        animatedColor.copy(alpha = animatedColor.alpha.coerceIn(0f, 1f))
    }

    return remember(animatedColors) {
        derivedStateOf {
            Brush.verticalGradient(
                colors = animatedColors,
                startY = 0f,
                endY = Float.POSITIVE_INFINITY
            )
        }
    }
}
// Calculate dynamic colors based on customization values
private fun calculateDynamicColors(customization: DataClasses.DishCustomization, dish: Dish): List<Color> {
    val baseColor = getImageColor(dish.imageUrl)

    // Calculate intensity multipliers based on customization values
    val spiceIntensity = customization.spiceLevel / 5f
    val sweetnessIntensity = customization.sweetness / 5f
    val saltinessIntensity = customization.saltiness / 5f
    val portionIntensity = customization.portionSize / 10f

    // Create color variations based on dominant customization
    val dominantCustomization = maxOf(spiceIntensity, sweetnessIntensity, saltinessIntensity)

    val primaryColor = when {
        spiceIntensity == dominantCustomization && spiceIntensity > 0.3f -> {
            baseColor.lerp(Color(0xFFFF4444), spiceIntensity * 0.6f)
        }
        sweetnessIntensity == dominantCustomization && sweetnessIntensity > 0.3f -> {
            baseColor.lerp(Color(0xFFFFB347), sweetnessIntensity * 0.6f)
        }
        saltinessIntensity == dominantCustomization && saltinessIntensity > 0.3f -> {
            baseColor.lerp(Color(0xFF607D8B), saltinessIntensity * 0.6f)
        }
        else -> baseColor
    }
    val secondaryColor = primaryColor.copy(alpha = 0.7f)
    val tertiaryColor = primaryColor.lerp(Color.White, 0.8f)
    // Adjust saturation based on portion size for more dramatic effect
    val saturationMultiplier = 0.5f + (portionIntensity * 0.5f)

    return listOf(
        primaryColor.copy(alpha = saturationMultiplier),
        secondaryColor.copy(alpha = saturationMultiplier * 0.8f),
        tertiaryColor.copy(alpha = saturationMultiplier * 0.6f)
    )
}

// Helper function to get dominant color from brush (simplified)
private fun getDominantColor(brush: Brush): Color {
    // Since we can't extract from Brush directly, return a default color
    // In a real app, you'd track this separately
    return Color(0xFF00A651)
}

// Helper extension for Color lerp
private fun Color.lerp(other: Color, fraction: Float): Color {
    return Color(
        red = this.red + fraction * (other.red - this.red),
        green = this.green + fraction * (other.green - this.green),
        blue = this.blue + fraction * (other.blue - this.blue),
        alpha = this.alpha + fraction * (other.alpha - this.alpha)
    )
}

private fun getImageColor(imageUrl: String): Color {
    return when (imageUrl) {
        "biryani" -> Color(0xFFFFE0B2)
        "cake" -> Color(0xFFE1BEE7)
        "dosa" -> Color(0xFFFFF9C4)
        "pasta" -> Color(0xFFFFCDD2)
        "pizza" -> Color(0xFFFFE0B2)
        "burger" -> Color(0xFFFFCDD2)
        "masala" -> Color(0xFFFFE0B2)
        "curry" -> Color(0xFFFFF9C4)
        else -> Color(0xFFE0E0E0)
    }
}

// Add this function to map imageUrl to drawable resources
private fun getImageResource(imageUrl: String): Int {
    return when (imageUrl) {
        "biryani" -> R.drawable.biryani
        "cake" -> R.drawable.cake
        "dosa" -> R.drawable.dosa
        "pasta" -> R.drawable.pasta
        "pizza" -> R.drawable.pizza
        "burger" -> R.drawable.burger
        "curry" -> R.drawable.curry
        else -> R.drawable.curry
    }
}
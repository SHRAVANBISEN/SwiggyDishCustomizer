package ViewModels

import DataClasses.Dish
import DataClasses.DishCustomization
import DataClasses.DishRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DishListViewModel : ViewModel() {
    private val _dishes = MutableStateFlow<List<Dish>>(emptyList())
    val dishes: StateFlow<List<Dish>> = _dishes.asStateFlow()

    init {
        loadDishes()
    }

    private fun loadDishes() {
        viewModelScope.launch {
            _dishes.value = DishRepository.getSampleDishes()
        }
    }
}

class CustomizationViewModel : ViewModel() {
    private val _selectedDish = MutableStateFlow<Dish?>(null)
    val selectedDish: StateFlow<Dish?> = _selectedDish.asStateFlow()

    private val _customization = MutableStateFlow(DishCustomization(0))
    val customization: StateFlow<DishCustomization> = _customization.asStateFlow()

    private val _isAddedToCart = MutableStateFlow(false)
    val isAddedToCart: StateFlow<Boolean> = _isAddedToCart.asStateFlow()

    // Enhanced: Track background transition intensity for more dynamic effects
    private val _backgroundIntensity = MutableStateFlow(0f)
    val backgroundIntensity: StateFlow<Float> = _backgroundIntensity.asStateFlow()

    // Enhanced: Track dominant customization type for themed effects
    private val _dominantCustomization = MutableStateFlow<String?>(null)
    val dominantCustomization: StateFlow<String?> = _dominantCustomization.asStateFlow()

    fun setDish(dish: Dish) {
        _selectedDish.value = dish
        _customization.value = DishCustomization(
            dishId = dish.id,
            spiceLevel = dish.customizations.find { it.type.name == "SPICE_LEVEL" }?.defaultValue ?: 3f,
            portionSize = dish.customizations.find { it.type.name == "PORTION_SIZE" }?.defaultValue ?: 5f,
            sweetness = dish.customizations.find { it.type.name == "SWEETNESS" }?.defaultValue ?: 3f,
            saltiness = dish.customizations.find { it.type.name == "SALTINESS" }?.defaultValue ?: 3f
        )
        updateDynamicEffects()
    }

    fun updateSpiceLevel(value: Float) {
        _customization.value = _customization.value.copy(spiceLevel = value)
        updateDynamicEffects()
    }

    fun updatePortionSize(value: Float) {
        _customization.value = _customization.value.copy(portionSize = value)
        updateDynamicEffects()
    }

    fun updateSweetness(value: Float) {
        _customization.value = _customization.value.copy(sweetness = value)
        updateDynamicEffects()
    }

    fun updateSaltiness(value: Float) {
        _customization.value = _customization.value.copy(saltiness = value)
        updateDynamicEffects()
    }

    fun addToCart() {
        _isAddedToCart.value = true
        viewModelScope.launch {
            // Add haptic feedback trigger here if needed
            kotlinx.coroutines.delay(2000)
            _isAddedToCart.value = false
        }
    }

    // Enhanced: Calculate dynamic effects based on all customization values
    private fun updateDynamicEffects() {
        val currentCustomization = _customization.value

        // Calculate normalized intensities (0f to 1f)
        val spiceIntensity = currentCustomization.spiceLevel / 5f
        val sweetnessIntensity = currentCustomization.sweetness / 5f
        val saltinessIntensity = currentCustomization.saltiness / 5f
        val portionIntensity = currentCustomization.portionSize / 10f

        // Calculate overall intensity for background effects
        val overallIntensity = (spiceIntensity + sweetnessIntensity + saltinessIntensity + portionIntensity) / 4f
        _backgroundIntensity.value = overallIntensity

        // Determine dominant customization for themed effects
        val maxIntensity = maxOf(spiceIntensity, sweetnessIntensity, saltinessIntensity)
        _dominantCustomization.value = when {
            spiceIntensity == maxIntensity && spiceIntensity > 0.3f -> "SPICE"
            sweetnessIntensity == maxIntensity && sweetnessIntensity > 0.3f -> "SWEET"
            saltinessIntensity == maxIntensity && saltinessIntensity > 0.3f -> "SALTY"
            portionIntensity > 0.7f -> "LARGE_PORTION"
            else -> null
        }
    }

    // Enhanced: Get current customization theme for UI adaptations
    fun getCurrentTheme(): CustomizationTheme {
        val customization = _customization.value
        val dominant = _dominantCustomization.value
        val intensity = _backgroundIntensity.value

        return CustomizationTheme(
            primaryColor = when (dominant) {
                "SPICE" -> android.graphics.Color.parseColor("#FF5722")
                "SWEET" -> android.graphics.Color.parseColor("#FC8019")
                "SALTY" -> android.graphics.Color.parseColor("#607D8B")
                "LARGE_PORTION" -> android.graphics.Color.parseColor("#00A651")
                else -> android.graphics.Color.parseColor("#00A651")
            },
            intensity = intensity,
            dominantType = dominant,
            shouldPulse = intensity > 0.8f,
            shouldGlow = intensity > 0.6f
        )
    }

    // Enhanced: Reset to defaults with smooth animation
    fun resetCustomization() {
        _selectedDish.value?.let { dish ->
            viewModelScope.launch {
                // Animate reset over time for smooth transition
                val originalCustomization = _customization.value
                val targetCustomization = DishCustomization(
                    dishId = dish.id,
                    spiceLevel = dish.customizations.find { it.type.name == "SPICE_LEVEL" }?.defaultValue ?: 3f,
                    portionSize = dish.customizations.find { it.type.name == "PORTION_SIZE" }?.defaultValue ?: 5f,
                    sweetness = dish.customizations.find { it.type.name == "SWEETNESS" }?.defaultValue ?: 3f,
                    saltiness = dish.customizations.find { it.type.name == "SALTINESS" }?.defaultValue ?: 3f
                )

                // Smooth transition to defaults
                for (i in 0..20) {
                    val progress = i / 20f
                    _customization.value = DishCustomization(
                        dishId = dish.id,
                        spiceLevel = lerp(originalCustomization.spiceLevel, targetCustomization.spiceLevel, progress),
                        portionSize = lerp(originalCustomization.portionSize, targetCustomization.portionSize, progress),
                        sweetness = lerp(originalCustomization.sweetness, targetCustomization.sweetness, progress),
                        saltiness = lerp(originalCustomization.saltiness, targetCustomization.saltiness, progress)
                    )
                    updateDynamicEffects()
                    kotlinx.coroutines.delay(50) // 1 second total animation
                }
            }
        }
    }

    // Helper function for smooth interpolation
    private fun lerp(start: Float, end: Float, fraction: Float): Float {
        return start + fraction * (end - start)
    }
}

// Enhanced: Data class for theming information
data class CustomizationTheme(
    val primaryColor: Int,
    val intensity: Float,
    val dominantType: String?,
    val shouldPulse: Boolean,
    val shouldGlow: Boolean
)
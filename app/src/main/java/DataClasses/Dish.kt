package DataClasses

data class Dish(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val customizations: List<CustomizationType>
)

data class CustomizationType(
    val type: CustomizationCategory,
    val minValue: Float,
    val maxValue: Float,
    val defaultValue: Float,
    val unit: String = ""
)

enum class CustomizationCategory(val displayName: String, val emoji: String) {
    SPICE_LEVEL("Spice Level", "🌶️"),
    PORTION_SIZE("Portion Size", "🍽️"),
    SWEETNESS("Sweetness", "🍯"),
    SALTINESS("Saltiness", "🧂")
}

data class DishCustomization(
    val dishId: Int,
    val spiceLevel: Float = 3f,
    val portionSize: Float = 5f,
    val sweetness: Float = 3f,
    val saltiness: Float = 3f
)

object DishRepository {
    fun getSampleDishes(): List<Dish> = listOf(
        Dish(
            id = 1,
            name = "Chicken Biryani",
            description = "Aromatic basmati rice with tender chicken",
            price = 299.0,
            imageUrl = "biryani",
            customizations = listOf(
                CustomizationType(CustomizationCategory.SPICE_LEVEL, 1f, 5f, 3f),
                CustomizationType(CustomizationCategory.PORTION_SIZE, 1f, 10f, 5f),
                CustomizationType(CustomizationCategory.SALTINESS, 1f, 5f, 3f)
            )
        ),
        Dish(
            id = 2,
            name = "Chocolate Cake",
            description = "Rich chocolate cake with cream frosting",
            price = 199.0,
            imageUrl = "cake",
            customizations = listOf(
                CustomizationType(CustomizationCategory.SWEETNESS, 1f, 5f, 3f),
                CustomizationType(CustomizationCategory.PORTION_SIZE, 1f, 10f, 5f)
            )
        ),
        Dish(
            id = 3,
            name = "Masala Dosa",
            description = "Crispy dosa with spiced potato filling",
            price = 89.0,
            imageUrl = "dosa",
            customizations = listOf(
                CustomizationType(CustomizationCategory.SPICE_LEVEL, 1f, 5f, 2f),
                CustomizationType(CustomizationCategory.PORTION_SIZE, 1f, 10f, 5f)
            )
        ),
        Dish(
            id = 4,
            name = "Pasta Arrabiata",
            description = "Spicy tomato pasta with herbs",
            price = 249.0,
            imageUrl = "pasta",
            customizations = listOf(
                CustomizationType(CustomizationCategory.SPICE_LEVEL, 1f, 5f, 3f),
                CustomizationType(CustomizationCategory.PORTION_SIZE, 1f, 10f, 5f),
                CustomizationType(CustomizationCategory.SALTINESS, 1f, 5f, 3f)
            )
        )
    )
}
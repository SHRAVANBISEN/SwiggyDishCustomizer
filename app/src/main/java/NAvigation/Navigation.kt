package NAvigation

import DataClasses.Dish
import UI.DishListScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dishcustomizerdemo.ui.screens.CustomizationScreen

@Composable
fun DishCustomizerApp() {
    val navController = rememberNavController()
    var selectedDish by remember { mutableStateOf<Dish?>(null) }

    NavHost(
        navController = navController,
        startDestination = "dish_list"
    ) {
        composable("dish_list") {
            DishListScreen(
                onDishClick = { dish ->
                    selectedDish = dish
                    navController.navigate("customization")
                }
            )
        }

        composable("customization") {
            selectedDish?.let { dish ->
                CustomizationScreen(
                    dish = dish,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
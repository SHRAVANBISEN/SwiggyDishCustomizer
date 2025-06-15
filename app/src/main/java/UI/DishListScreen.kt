package UI

import DataClasses.Dish
import ViewModels.DishListViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dishcustomizerdemo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishListScreen(
    onDishClick: (Dish) -> Unit,
    viewModel: DishListViewModel = viewModel()
) {
    val dishes by viewModel.dishes.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.White),
    )
    {
        Column(
            modifier = Modifier
                .fillMaxSize()

        ) {
            // Header section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Restaurant",

                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                    Text(
                        text = "(${dishes.size})",
                        fontSize = 16.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(dishes) { dish ->
                    DishCard(
                        dish = dish,
                        onClick = { onDishClick(dish) }
                    )
                }
            }
        }
    }

}

@Composable
private fun DishCard(
    dish: Dish,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dish image
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    ,
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getImageResource(dish.imageUrl)),
                    contentDescription = dish.name,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).width(200.dp).height(130.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Dish details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Veg/Non-veg indicator and bestseller
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Veg indicator (green square)
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    Color.White,
                                    RoundedCornerShape(2.dp)
                                )
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        Color(0xFF00A651),
                                        RoundedCornerShape(1.dp)
                                    )
                            )
                        }

                        // Bestseller tag
                        if (dish.name.contains("Biryani") || dish.name.contains("Cake")) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFC8019),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Bestseller",
                                    fontSize = 10.sp,
                                    color = Color(0xFFFC8019),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Dish name
                    Text(
                        text = dish.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D2D2D),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Price
                    Text(
                        text = "₹${dish.price.toInt()}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                }

                // Rating and description
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF00A651),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "4.${(3..5).random()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF00A651)
                        )
                    }

                    Text(
                        text = dish.description,
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // ADD button
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(100.dp)
            ) {
                // Customization indicator
                if (dish.customizations.isNotEmpty()) {
                    Text(
                        text = "Customizable",
                        fontSize = 10.sp,
                        color = Color(0xFFFC8019),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .background(
                                Color(0xFFFFF3E0),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ADD Button
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFFFC8019)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (dish.customizations.isNotEmpty()) "CUSTOMIZE" else "ADD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun getImageColor(imageUrl: String): Color {
    return when (imageUrl) {
        "biryani" -> Color(0xFFFFE0B2)
        "cake" -> Color(0xFFE1BEE7)
        "dosa" -> Color(0xFFFFF9C4)
        "pasta" -> Color(0xFFFFCDD2)
        "pizza" -> Color(0xFFFFCDD2)
        "burger" -> Color(0xFFFFF3E0)
        else -> Color(0xFFE0E0E0)
    }
}

// Add this function to map imageUrl to drawable resources
// Make sure to add corresponding drawable resources in res/drawable folder
private fun getImageResource(imageUrl: String): Int {
    return when (imageUrl) {
        "biryani" -> R.drawable.biryani  // Add biryani.png or biryani.xml in drawable
        "cake" -> R.drawable.cake        // Add cake.png or cake.xml in drawable
        "dosa" -> R.drawable.dosa        // Add dosa.png or dosa.xml in drawable
        "pasta" -> R.drawable.pasta      // Add pasta.png or pasta.xml in drawable
        "pizza" -> R.drawable.pizza      // Add pizza.png or pizza.xml in drawable
        "burger" -> R.drawable.burger    // Add burger.png or burger.xml in drawable
        "curry" -> R.drawable.curry      // Add curry.png or curry.xml in drawable
        else -> R.drawable.curry  // Add default dish image
    }
}
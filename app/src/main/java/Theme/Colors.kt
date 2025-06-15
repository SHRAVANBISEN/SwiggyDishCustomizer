package Theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Swiggy-inspired color scheme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00A651), // Swiggy Green
    secondary = Color(0xFFFC8019), // Swiggy Orange
    tertiary = Color(0xFF00A651),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    primaryContainer = Color(0xFF1B2A1B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onSurface = Color.White,
    onBackground = Color.White,
    outline = Color(0xFF3D3D3D)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00A651), // Swiggy Green
    secondary = Color(0xFFFC8019), // Swiggy Orange
    tertiary = Color(0xFF00A651),
    background = Color(0xFFFAFAFA), // Light gray background like Swiggy
    surface = Color.White,
    primaryContainer = Color(0xFFE8F5E8), // Light green container
    secondaryContainer = Color(0xFFFFF3E0), // Light orange container
    onPrimary = Color.White,
    onSecondary = Color.White,
    onSurface = Color(0xFF2D2D2D),
    onBackground = Color(0xFF2D2D2D),
    outline = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFFF5F5F5)
)

@Composable
fun SwiggyStyleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to maintain Swiggy colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.White.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
package com.tta.todolistainew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.tta.todolistainew.navigation.AppNavGraph
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tta.todolistainew.ui.theme.ToDoListAINewTheme

/**
 * Main Activity - single Activity architecture with Compose navigation.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Get the AppContainer from the Application class
        val appContainer = (application as ToDoListAIApplication).appContainer
        
        setContent {
            val settingsRepo = appContainer.settingsRepository
            val isDarkTheme by settingsRepo.isDarkTheme.collectAsStateWithLifecycle(initialValue = isSystemInDarkTheme())
            val languageCode by settingsRepo.languageCode.collectAsStateWithLifecycle(initialValue = "en")
            
            // Side effect to update locale on language change
            // WARNING: This is a simplified approach. Ideally, activity recreation or ContextWrapper is better.
            // But for Compose, this can often work for string resources if observing the config.
            // Note: We are not handling dynamic locale updates fully here without recreation, 
            // but we'll set the default locale so subsequent resource lookups use it.
            val context = androidx.compose.ui.platform.LocalContext.current
            androidx.compose.runtime.LaunchedEffect(languageCode) {
                 val locale = java.util.Locale(languageCode)
                 java.util.Locale.setDefault(locale)
                 val config = context.resources.configuration
                 config.setLocale(locale)
                 context.resources.updateConfiguration(config, context.resources.displayMetrics)
            }
            
            ToDoListAINewTheme(darkTheme = isDarkTheme) {
                AppNavGraph(
                    appContainer = appContainer,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
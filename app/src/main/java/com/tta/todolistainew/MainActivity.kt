package com.tta.todolistainew

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.tta.todolistainew.navigation.AppNavGraph
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
            ToDoListAINewTheme {
                AppNavGraph(
                    appContainer = appContainer,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
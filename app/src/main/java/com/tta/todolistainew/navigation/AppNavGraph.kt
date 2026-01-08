package com.tta.todolistainew.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tta.todolistainew.core.di.AppContainer
import com.tta.todolistainew.feature.auth.ui.LoginScreen
import com.tta.todolistainew.feature.auth.ui.LoginViewModel
import com.tta.todolistainew.feature.goal.ui.GoalDetailScreen
import com.tta.todolistainew.feature.goal.ui.GoalDetailViewModel
import com.tta.todolistainew.feature.home.ui.HomeScreen
import com.tta.todolistainew.feature.home.ui.HomeViewModel
import com.tta.todolistainew.feature.task.ui.TaskListScreen
import com.tta.todolistainew.feature.task.ui.TaskListViewModel

/**
 * Main navigation graph for the application.
 */
@Composable
fun AppNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Route = Route.Login
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Login Screen
        composable<Route.Login> {
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.Factory()
            )
            
            LoginScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                }
            )
        }
        
        // Home Screen
        composable<Route.Home> {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(
                    getTasksByTypeUseCase = appContainer.getTasksByTypeUseCase,
                    getGoalsUseCase = appContainer.getGoalsUseCase,
                    taskRepository = appContainer.taskRepository,
                    goalRepository = appContainer.goalRepository // Added dependency
                )
            )
            
            HomeScreen(
                viewModel = viewModel,
                onNavigateToGoalDetail = { goalId ->
                    navController.navigate(Route.GoalDetail(goalId))
                }
            )
        }
        
        // ... (Other routes remain same for brevity, will regenerate full file if needed but just updated HomeVM factory)
        
        composable<Route.TaskList> {
            val viewModel: TaskListViewModel = viewModel(
                factory = TaskListViewModel.Factory(
                    getTasksUseCase = appContainer.getTasksUseCase,
                    taskRepository = appContainer.taskRepository
                )
            )
            TaskListScreen(viewModel, { navController.navigate(Route.TaskDetail(it)) })
        }
        
        composable<Route.TaskDetail> { backStackEntry ->
            val route: Route.TaskDetail = backStackEntry.toRoute()
            TaskDetailPlaceholder(route.taskId, { navController.popBackStack() })
        }
        
        composable<Route.GoalDetail> { backStackEntry ->
            val route: Route.GoalDetail = backStackEntry.toRoute()
            val viewModel: GoalDetailViewModel = viewModel(
                factory = GoalDetailViewModel.Factory(
                    goalId = route.goalId,
                    goalRepository = appContainer.goalRepository,
                    taskRepository = appContainer.taskRepository
                )
            )
            GoalDetailScreen(viewModel, { navController.popBackStack() }, { navController.navigate(Route.TaskDetail(it)) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDetailPlaceholder(taskId: Long, onNavigateBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Task Detail") }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }) }) { 
        Box(Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) { Text("Task Detail for ID: $taskId") } 
    }
}

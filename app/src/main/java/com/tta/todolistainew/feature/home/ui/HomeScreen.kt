package com.tta.todolistainew.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.IconButton
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tta.todolistainew.core.common.UiEvent
import com.tta.todolistainew.feature.task.data.local.TaskType
import com.tta.todolistainew.navigation.Route
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Home Screen displaying task categories and goals.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    settingsViewModel: com.tta.todolistainew.feature.settings.ui.SettingsViewModel,
    onNavigateTo: (Route) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Bottom Sheet State
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Drawer State
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    
    // Handle UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    // Forward all navigation events to the main callback
                    if (event.route is Route) {
                        onNavigateTo(event.route)
                    }
                }
                else -> {}
            }
        }
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                settingsViewModel = settingsViewModel,
                onNavigateTo = onNavigateTo,
                onNavigateToExternal = { url ->
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    context.startActivity(intent)
                },
                onLogout = onLogout,
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        },
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Column {
                            Text(
                                text = "Hello, User!", 
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Your goals are waiting", 
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showBottomSheet = true }, // Show bottom sheet
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    
                    // Tasks Overview Section
                    item {
                        Text(
                            text = "Tasks Overview",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Daily Tasks Card
                            TaskSummaryCard(
                                title = "Daily Tasks",
                                completed = uiState.dailyTasksCompleted,
                                total = uiState.dailyTasksTotal,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                onClick = { viewModel.onDailyTasksClick() },
                                modifier = Modifier.weight(1f)
                            )
                            
                            // Quick Tasks Card
                            TaskSummaryCard(
                                title = "Quick Tasks",
                                completed = uiState.quickTasksCompleted,
                                total = uiState.quickTasksTotal,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                onClick = { viewModel.onQuickTasksClick() },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    
                    // Goals Section
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "My Goals",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    if (uiState.goals.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No goals yet. Create one!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(uiState.goals) { goalWithProgress ->
                            GoalCard(
                                goalWithProgress = goalWithProgress,
                                onClick = { viewModel.onGoalClick(goalWithProgress.goal.id) }
                            )
                        }
                    }
                }
            }
            
            // Add Task/Goal Bottom Sheet
            if (showBottomSheet) {
                AddTaskBottomSheet(
                    sheetState = sheetState,
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                        }
                    },
                    onAddTask = { title, description, type, dueDate ->
                        viewModel.addTask(title, description, type, dueDate)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                        }
                    },
                    onAddGoal = { title, description, targetDate ->
                        viewModel.addGoal(title, description, targetDate)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddTask: (title: String, description: String, type: TaskType, dueDate: LocalDate?) -> Unit,
    onAddGoal: (title: String, description: String, targetDate: LocalDate?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TaskType.QUICK) }
    val focusRequester = remember { FocusRequester() }
    
    // Auto-focus logic
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            Text(
                text = "New Item",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Type Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskType.entries.forEach { type ->
                    val label = when(type) {
                        TaskType.DAILY -> "Daily"
                        TaskType.QUICK -> "Quick"
                        TaskType.GOAL -> "Goal"
                    }
                    
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(label) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(if (selectedType == TaskType.GOAL) "Goal Title" else "Task Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description (Simplified)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            // TODO: Add Date Selection if needed
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Create Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        if (selectedType == TaskType.GOAL) {
                            onAddGoal(title, description, null) // Date picker TODO
                        } else {
                            onAddTask(title, description, selectedType, null) // Date picker TODO
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank()
            ) {
                Text("Create ${if (selectedType == TaskType.GOAL) "Goal" else "Task"}")
            }
        }
    }
}

@Composable
fun TaskSummaryCard(
    title: String,
    completed: Int,
    total: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$completed/$total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Completed",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun GoalCard(
    goalWithProgress: GoalWithProgress,
    onClick: () -> Unit
) {
    val progress = if (goalWithProgress.totalTasks > 0) {
        goalWithProgress.completedTasks.toFloat() / goalWithProgress.totalTasks
    } else {
        0f
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Placeholder
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("🎯")
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goalWithProgress.goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${goalWithProgress.completedTasks}/${goalWithProgress.totalTasks} Tasks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

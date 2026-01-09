package com.tta.todolistainew.feature.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp

import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tta.todolistainew.navigation.Route
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AppDrawer(
    settingsViewModel: com.tta.todolistainew.feature.settings.ui.SettingsViewModel,
    onNavigateTo: (Route) -> Unit,
    onNavigateToExternal: (String) -> Unit, // For URLs or Actions
    onLogout: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsStateWithLifecycle()
    val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val languageCode by settingsViewModel.languageCode.collectAsStateWithLifecycle()

    val context = LocalContext.current

    // Permission Launcher for Notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            settingsViewModel.toggleNotifications(isGranted)
            if (!isGranted) {
                 // TODO: Show rationale or link to settings
            }
        }
    )

    ModalDrawerSheet(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // User Profile Header
            DrawerHeader(
                onProfileClick = {
                    onNavigateTo(Route.UserInfo)
                    closeDrawer()
                }
            )

            HorizontalDivider()

            // Settings Section
            DrawerSectionTitle("Settings")
            DrawerItem(
                icon = Icons.Default.Settings,
                label = "Dark Theme",
                onClick = { settingsViewModel.toggleTheme(!isDarkTheme) },
                trailingContent = {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { settingsViewModel.toggleTheme(it) }
                    )
                }
            )
            DrawerItem(
                icon = Icons.Default.Notifications,
                label = "Notifications",
                onClick = { 
                     // Check and request permission
                     if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                         if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                             settingsViewModel.toggleNotifications(!notificationsEnabled)
                         } else {
                             permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                         }
                     } else {
                         // Android < 13, simple toggle
                         settingsViewModel.toggleNotifications(!notificationsEnabled)
                     }
                },
                trailingContent = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { 
                             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                  if (it) {
                                      permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                  } else {
                                      settingsViewModel.toggleNotifications(false)
                                  }
                             } else {
                                 settingsViewModel.toggleNotifications(it)
                             }
                        }
                    )
                }
            )
            DrawerItem(
                icon = Icons.Default.LocationOn,
                label = "Language: ${if(languageCode == "vi") "Tiếng Việt" else "English"}",
                onClick = { 
                    val newLang = if (languageCode == "en") "vi" else "en"
                    settingsViewModel.setLanguage(newLang)
                    // Note: In a real app, we would update the LocaleConfiguration here or trigger an activity recreation.
                    // For now, we will just save the preference.
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // More Section
            DrawerSectionTitle("More")
            DrawerItem(
                icon = Icons.Default.PlayArrow,
                label = "More Apps",
                onClick = {
                    onNavigateToExternal("market://search?q=pub:Trần Thế Anh") // Example query
                    closeDrawer()
                }
            )
            DrawerItem(
                icon = Icons.Default.Email,
                label = "Feedback",
                onClick = {
                    onNavigateToExternal("mailto:trantheanh.dev@gmail.com")
                    closeDrawer()
                }
            )
            DrawerItem(
                icon = Icons.Default.Info,
                label = "About Us",
                onClick = {
                    onNavigateTo(Route.AboutUs)
                    closeDrawer()
                }
            )
            DrawerItem(
                icon = Icons.Default.Settings,
                label = "Privacy Policy",
                onClick = {
                    onNavigateTo(Route.PrivacyPolicy)
                    closeDrawer()
                }
            )

            Spacer(modifier = Modifier.weight(1f))
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Logout
            DrawerItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                label = "Logout",
                onClick = {
                    onLogout()
                    closeDrawer()
                },
                color = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DrawerHeader(
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProfileClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // User Icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "User Name", // TODO: Get actual name
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "View Profile",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DrawerSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp)
    )
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null, tint = color) },
        label = { Text(label, color = color) },
        selected = false,
        onClick = onClick,
        badge = trailingContent,
        modifier = modifier.padding(horizontal = 12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent
        )
    )
}

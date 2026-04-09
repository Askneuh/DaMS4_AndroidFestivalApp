package com.example.festivalapp.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.festivalapp.data.datastore.UserPreferencesDs
import com.example.festivalapp.data.session.SessionRepository
import com.example.festivalapp.ui.navigation.AppDestinations
import com.example.festivalapp.ui.screen.login.LoginScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.festivalapp.data.auth.AuthRepository
import com.example.festivalapp.AppViewModelProvider
import com.example.festivalapp.FestivalApplication
import androidx.compose.ui.platform.LocalContext
import com.example.festivalapp.ui.screen.festival.FestivalListScreen
import com.example.festivalapp.ui.screen.festival.FestivalCreateScreen
import com.example.festivalapp.ui.screen.editor.EditorListRoute
import com.example.festivalapp.ui.screen.editor.EditorDetailRoute
import com.example.festivalapp.ui.screen.reservation.ReservationOverviewScreen
import com.example.festivalapp.ui.screen.reservation.ReservationListRoute
import com.example.festivalapp.ui.screen.reservation.ReservationDetailScreen
import com.example.festivalapp.ui.screen.reservation.FestivalGamesListScreen
import com.example.festivalapp.ui.screen.home.HomeScreen
import com.example.festivalapp.ui.screen.settings.SettingsScreen

@Composable
fun AppNavigation(
    model: NavigationModel,
    controller: NavigationController,
    userPreferences: UserPreferencesDs,
    authRepository: AuthRepository,
    sessionRepository: SessionRepository
) {
    val authToken by sessionRepository.accessCookieFlow.collectAsState(initial = null)
    val isAuthenticated = authToken != null
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    val app = context.applicationContext as FestivalApplication

    // Sync drawer state from model to UI
    LaunchedEffect(model.isDrawerOpen()) {
        if (model.isDrawerOpen()) {
            drawerState.open()
        } else {
            drawerState.close()
        }
    }

    // Sync UI drawer state back to model (when swiped closed)
    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.isClosed && model.isDrawerOpen()) {
            controller.closeDrawer()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isAuthenticated,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                
                NavigationDrawerItem(
                    label = { Text("Festivals") },
                    selected = model.backStack.last() is AppDestinations.FestivalList,
                    onClick = { controller.navigateTo(AppDestinations.FestivalList) }
                )
                NavigationDrawerItem(
                    label = { Text("Éditeurs") },
                    selected = model.backStack.last() is AppDestinations.EditorList,
                    onClick = { controller.navigateTo(AppDestinations.EditorList) }
                )
                NavigationDrawerItem(
                    label = { Text("Suivi Réservations") },
                    selected = model.backStack.last() is AppDestinations.ReservationOverview,
                    onClick = { controller.navigateTo(AppDestinations.ReservationOverview) }
                )
                NavigationDrawerItem(
                    label = { Text("Jeux du Festival") },
                    selected = model.backStack.last() is AppDestinations.FestivalGamesList,
                    onClick = { controller.navigateTo(AppDestinations.FestivalGamesList) }
                )
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("Paramètres") },
                    selected = model.backStack.last() is AppDestinations.Settings,
                    onClick = { controller.navigateTo(AppDestinations.Settings) }
                )
            }
        }
    ) {
        // Auth Guard: filter the backstack to show Login for private pages
        val currentBackStack = if (!isAuthenticated) {
            model.backStack.map { dest ->
                if (dest.isPublic) dest else AppDestinations.Login
            }.distinct()
        } else {
            model.backStack
        }

        NavDisplay(
            backStack = currentBackStack,
            onBack = { controller.navigateBack() },
        ) { key ->
            when (key) {
                is AppDestinations.Home -> NavEntry(key) {
                    HomeScreen(
                        onNavigateToSettings = { controller.navigateTo(AppDestinations.Settings) },
                        onMenuClick = { controller.toggleDrawer() }
                    )
                }
                is AppDestinations.Settings -> NavEntry(key) {
                    SettingsScreen(
                        currentTheme = model.themeMode,
                        onThemeChanged = { controller.setTheme(it) },
                        onNavigateBack = { controller.navigateBack() }
                    )
                }
                is AppDestinations.Login -> NavEntry(key) {
                    LoginScreen(
                        viewModel = viewModel(factory = AppViewModelProvider.Factory),
                        onLoginSuccess = {
                            controller.navigateTo(AppDestinations.Home)
                        }
                    )
                }
                is AppDestinations.FestivalList -> NavEntry(key) {
                    FestivalListScreen(
                        viewModel = viewModel(factory = AppViewModelProvider.Factory),
                        onNavigateToCreate = { controller.navigateTo(AppDestinations.FestivalCreate()) },
                        onNavigateToFestivalDetail = { name -> controller.navigateTo(AppDestinations.FestivalCreate(name)) },
                        onMenuClick = { controller.toggleDrawer() }
                    )
                }
                is AppDestinations.FestivalCreate -> NavEntry(key) {
                    FestivalCreateScreen(
                        festivalId = key.festivalId,
                        onNavigateBack = { controller.navigateBack() }
                    )
                }
                is AppDestinations.EditorList -> NavEntry(key) {
                    EditorListRoute(
                        editorRepository = app.container.editorRepository,
                        onEditorClick = { id -> controller.navigateTo(AppDestinations.EditorDetail(id)) }
                    )
                }
                is AppDestinations.EditorDetail -> NavEntry(key) {
                    EditorDetailRoute(
                        editorRepository = app.container.editorRepository,
                        editorId = key.editorId ?: 0,
                        onBackClick = { controller.navigateBack() }
                    )
                }
                is AppDestinations.ReservationOverview -> NavEntry(key) {
                    ReservationListRoute(
                        reservationRepository = app.container.reservationRepository,
                        festivalRepository = app.container.festivalRepository,
                        onLogoutClick = { /* TODO: logout */ }
                    )
                }
                is AppDestinations.ReservationDetail -> NavEntry(key) {
                    ReservationDetailScreen(
                        editorId = key.editorId,
                        onNavigateBack = { controller.navigateBack() }
                    )
                }
                is AppDestinations.FestivalGamesList -> NavEntry(key) {
                    FestivalGamesListScreen(
                        onMenuClick = { controller.toggleDrawer() }
                    )
                }
                else -> NavEntry(key) {
                    LoginScreen(
                        viewModel = viewModel(factory = AppViewModelProvider.Factory),
                        onLoginSuccess = { controller.navigateTo(AppDestinations.Home)}
                    )
                }
            }
        }
    }
}

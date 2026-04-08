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
import com.example.festivalapp.ui.screen.festival.FestivalListScreen
import com.example.festivalapp.ui.screen.festival.FestivalCreateScreen
import com.example.festivalapp.ui.screen.publisher.PublisherListScreen
import com.example.festivalapp.ui.screen.publisher.PublisherDetailScreen
import com.example.festivalapp.ui.screen.reservation.ReservationOverviewScreen
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

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            controller.navigateTo(AppDestinations.Login)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isAuthenticated,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text("Festival App", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.headlineSmall)
                
                NavigationDrawerItem(
                    label = { Text("Accueil") },
                    selected = model.currentDestination is AppDestinations.Home,
                    onClick = { controller.navigateTo(AppDestinations.Home); controller.toggleDrawer() }
                )
                NavigationDrawerItem(
                    label = { Text("Festivals") },
                    selected = model.currentDestination is AppDestinations.FestivalList,
                    onClick = { controller.navigateTo(AppDestinations.FestivalList); controller.toggleDrawer() }
                )
                NavigationDrawerItem(
                    label = { Text("Éditeurs") },
                    selected = model.currentDestination is AppDestinations.PublisherList,
                    onClick = { controller.navigateTo(AppDestinations.PublisherList); controller.toggleDrawer() }
                )
                NavigationDrawerItem(
                    label = { Text("Réservations") },
                    selected = model.currentDestination is AppDestinations.ReservationOverview,
                    onClick = { controller.navigateTo(AppDestinations.ReservationOverview); controller.toggleDrawer() }
                )
                NavigationDrawerItem(
                    label = { Text("Paramètres") },
                    selected = model.currentDestination is AppDestinations.Settings,
                    onClick = { controller.navigateTo(AppDestinations.Settings); controller.toggleDrawer() }
                )
            }
        }
    ) {
        NavDisplay(
            backstack = model.filteredBackstack,
            onBack = { controller.navigateBack() }
        ) { key ->
            when (key) {
                is AppDestinations.Home -> NavEntry(key) {
                   HomeScreen(onMenuClick = { controller.toggleDrawer() })
                }
                is AppDestinations.Settings -> NavEntry(key) {
                    SettingsScreen(
                        currentTheme = model.themeMode.collectAsState().value,
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
                        onNavigateToFestivalDetail = { id -> controller.navigateTo(AppDestinations.FestivalCreate(id)) },
                        onMenuClick = { controller.toggleDrawer() }
                    )
                }
                is AppDestinations.FestivalCreate -> NavEntry(key) {
                    FestivalCreateScreen(
                        festivalId = key.festivalId,
                        onNavigateBack = { controller.navigateBack() }
                    )
                }
                is AppDestinations.PublisherList -> NavEntry(key) {
                    PublisherListScreen(
                        onNavigateToAdd = { controller.navigateTo(AppDestinations.PublisherDetail()) },
                        onNavigateToDetail = { id -> controller.navigateTo(AppDestinations.PublisherDetail(id)) },
                        onMenuClick = { controller.toggleDrawer() }
                    )
                }
                is AppDestinations.PublisherDetail -> NavEntry(key) {
                    PublisherDetailScreen(
                        publisherId = key.publisherId,
                        onNavigateBack = { controller.navigateBack() }
                    )
                }
                is AppDestinations.ReservationOverview -> NavEntry(key) {
                    ReservationOverviewScreen(
                        onNavigateToReservationDetail = { id -> controller.navigateTo(AppDestinations.ReservationDetail(id)) },
                        onMenuClick = { controller.toggleDrawer() }
                    )
                }
                is AppDestinations.ReservationDetail -> NavEntry(key) {
                    ReservationDetailScreen(
                        publisherId = key.publisherId,
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
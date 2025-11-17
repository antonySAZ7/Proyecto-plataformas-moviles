package com.example.proyectoplatsadj.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectoplatsadj.feature.home.*
import com.example.proyectoplatsadj.feature.calendar.*
import com.example.proyectoplatsadj.feature.forgotpassword.ForgotPasswordScreen
import com.example.proyectoplatsadj.feature.newtask.*
import com.example.proyectoplatsadj.feature.splash.SplashScreen
import com.example.proyectoplatsadj.feature.onboarding.WelcomeScreen
import com.example.proyectoplatsadj.feature.login.LoginScreen
import com.example.proyectoplatsadj.feature.register.RegisterScreen
import com.example.proyectoplatsadj.feature.taskslist.*
import com.example.proyectoplatsadj.viewmodel.*
import kotlinx.serialization.Serializable

// ============================================
// DESTINATIONS TYPE-SAFE
// ============================================

@Serializable object SplashDestination
@Serializable object WelcomeDestination
@Serializable object LoginDestination
@Serializable object RegisterDestination
@Serializable object HomeDestination
@Serializable object CalendarDestination
@Serializable object TasksListDestination
@Serializable object NewTaskDestination

@Serializable object ForgotPasswordDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Solo mostrar bottom bar en pantallas principales
    val showBottomBar = currentRoute in listOf(
        HomeDestination::class.qualifiedName,
        CalendarDestination::class.qualifiedName,
        TasksListDestination::class.qualifiedName,
        NewTaskDestination::class.qualifiedName
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == HomeDestination::class.qualifiedName,
                        onClick = {
                            navController.navigate(HomeDestination) {
                                popUpTo(HomeDestination) { inclusive = true }
                            }
                        },
                        icon = { Icon(Icons.Filled.Home, "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == CalendarDestination::class.qualifiedName,
                        onClick = { navController.navigate(CalendarDestination) },
                        icon = { Icon(Icons.Filled.DateRange, "Calendar") },
                        label = { Text("Calendar") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == TasksListDestination::class.qualifiedName,
                        onClick = { navController.navigate(TasksListDestination) },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, "Tasks") },
                        label = { Text("Tasks") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == NewTaskDestination::class.qualifiedName,
                        onClick = { navController.navigate(NewTaskDestination) },
                        icon = { Icon(Icons.Filled.Add, "New") },
                        label = { Text("New") }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = SplashDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // ============================================
            // FLUJO DE AUTENTICACIÓN
            // ============================================

            // SPLASH
            composable<SplashDestination> {
                SplashScreen(
                    onTimeout = {
                        navController.navigate(WelcomeDestination) {
                            popUpTo(SplashDestination) { inclusive = true }
                        }
                    }
                )
            }

            // WELCOME (Onboarding)
            composable<WelcomeDestination> {
                WelcomeScreen(
                    onRegisterClick = { navController.navigate(RegisterDestination) },
                    onLoginClick = { navController.navigate(LoginDestination) }
                )
            }

            // LOGIN - CON VIEWMODEL
            composable<LoginDestination> {
                val loginViewModel: LoginViewModel = viewModel()
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(HomeDestination) {
                            popUpTo(LoginDestination) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(RegisterDestination) },
                    onforgotPasswordClick = { navController.navigate(ForgotPasswordDestination) },
                    viewModel = loginViewModel
                )
            }

            // REGISTER - CON VIEWMODEL
            composable<RegisterDestination> {
                val registerViewModel: RegisterViewModel = viewModel()
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(HomeDestination) {
                            popUpTo(RegisterDestination) { inclusive = true }
                        }
                    },
                    onLoginClick = { navController.popBackStack() },
                    viewModel = registerViewModel
                )
            }

            // ============================================
            // PANTALLAS PRINCIPALES - CON VIEWMODELS Y APIs
            // ============================================

            // HOME - CON VIEWMODEL
            composable<HomeDestination> {
                val homeViewModel: HomeViewModel = viewModel()
                val uiState by homeViewModel.uiState.collectAsState()

                HomeScreen(
                    state = uiState,
                    onRetry = { homeViewModel.retry() }
                )
            }

            // CALENDAR - Sin cambios (placeholder)
            composable<CalendarDestination> {
                val calendarState = remember {
                    mutableStateOf<CalendarUiState>(
                        CalendarUiState.Content(selectedLabel = "22 de octubre 2025")
                    )
                }

                CalendarScreen(
                    state = calendarState.value,
                    onRetry = { calendarState.value = CalendarUiState.Loading }
                )
            }

            // TASKS LIST - CON VIEWMODEL
            composable<TasksListDestination> {
                val tasksViewModel: TasksListViewModel = viewModel()
                val uiState by tasksViewModel.uiState.collectAsState()

                TasksListScreen(
                    state = uiState,
                    onRetry = { tasksViewModel.retry() },
                    onAddClick = { navController.navigate(NewTaskDestination) }
                )
            }

            // NEW TASK - CON VIEWMODEL
            composable<NewTaskDestination> {
                val newTaskViewModel: NewTaskViewModel = viewModel()
                val uiState by newTaskViewModel.uiState.collectAsState()

                // Escuchar cuando la tarea se crea
                LaunchedEffect(Unit) {
                    newTaskViewModel.taskCreated.collect {
                        navController.popBackStack()
                    }
                }

                NewTaskScreen(
                    state = uiState,
                    onSubmit = { title, detail, date, priority ->
                        newTaskViewModel.createTask(title, detail, date, priority)
                    },
                    onRetry = { newTaskViewModel.retry() }
                )
            }

            composable<ForgotPasswordDestination> {
                val forgotViewModel: ForgotPasswordViewModel = viewModel()
                ForgotPasswordScreen(
                    viewModel = forgotViewModel,
                    onBackToLogin = { navController.popBackStack() }
                )
            }
        }
    }
}
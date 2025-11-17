package com.example.proyectoplatsadj.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectoplatsadj.data.local.AppDatabase
import com.example.proyectoplatsadj.data.local.DataStoreManager
import com.example.proyectoplatsadj.data.model.ApiResult
import com.example.proyectoplatsadj.data.model.Task
import com.example.proyectoplatsadj.data.remote.RetrofitInstance
import com.example.proyectoplatsadj.data.repository.AuthRepository
import com.example.proyectoplatsadj.data.repository.TaskRepository
import com.example.proyectoplatsadj.feature.home.HomeUiState
import com.example.proyectoplatsadj.feature.home.HomeTaskUi
import com.example.proyectoplatsadj.feature.login.LoginUiState
import com.example.proyectoplatsadj.feature.register.RegisterUiState
import com.example.proyectoplatsadj.feature.taskslist.TaskRowUi
import com.example.proyectoplatsadj.feature.taskslist.TasksListUiState
import com.example.proyectoplatsadj.feature.newtask.NewTaskUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ============================================
// LOGIN VIEWMODEL
// ============================================

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(RetrofitInstance.authApi)
    private val dataStore = DataStoreManager(application)

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<Boolean>()
    val loginSuccess: SharedFlow<Boolean> = _loginSuccess.asSharedFlow()

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = LoginUiState.Error("Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            when (val result = authRepository.login(email, password)) {
                is ApiResult.Success -> {
                    dataStore.saveAuthToken(result.data.token)
                    dataStore.saveUserInfo(1, email)

                    _uiState.value = LoginUiState.Idle
                    _loginSuccess.emit(true)
                }
                is ApiResult.Error -> {
                    _uiState.value = LoginUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }
}

// ============================================
// REGISTER VIEWMODEL
// ============================================

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(RetrofitInstance.authApi)
    private val dataStore = DataStoreManager(application)

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _registerSuccess = MutableSharedFlow<Boolean>()
    val registerSuccess: SharedFlow<Boolean> = _registerSuccess.asSharedFlow()

    fun register(firstName: String, lastName: String, email: String, password: String) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _uiState.value = RegisterUiState.Error("Completa todos los campos")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            when (val result = authRepository.register(email, password, firstName, lastName)) {
                is ApiResult.Success -> {
                    dataStore.saveAuthToken(result.data.token)
                    dataStore.saveUserInfo(1, email)
                    _uiState.value = RegisterUiState.Idle
                    _registerSuccess.emit(true)
                }
                is ApiResult.Error -> {
                    _uiState.value = RegisterUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }
}

// ============================================
// HOME VIEWMODEL
// ============================================

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val taskRepository = TaskRepository(RetrofitInstance.taskApi, database.taskDao())

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTodayTasks()
    }

    fun loadTodayTasks() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            taskRepository.getTodayTasks().collect { tasks ->
                val homeTasks = tasks.map { task ->
                    HomeTaskUi(
                        id = task.id.toString(),
                        title = task.title,
                        detail = task.detail ?: "Sin detalles",
                        priority = task.priority
                    )
                }

                _uiState.value = if (homeTasks.isEmpty()) {
                    HomeUiState.Empty
                } else {
                    HomeUiState.Content(homeTasks)
                }
            }
        }
    }

    fun retry() {
        loadTodayTasks()
    }
}

// ============================================
// TASKS LIST VIEWMODEL
// ============================================

class TasksListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val taskRepository = TaskRepository(RetrofitInstance.taskApi, database.taskDao())

    private val _uiState = MutableStateFlow<TasksListUiState>(TasksListUiState.Loading)
    val uiState: StateFlow<TasksListUiState> = _uiState.asStateFlow()

    init {
        loadAllTasks()
    }

    fun loadAllTasks() {
        viewModelScope.launch {
            _uiState.value = TasksListUiState.Loading

            taskRepository.getAllTasks().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val taskRows = result.data.map { task ->
                            TaskRowUi(
                                id = task.id.toString(),
                                title = task.title,
                                detail = task.detail ?: "Sin detalles",
                                priority = task.priority
                            )
                        }

                        _uiState.value = if (taskRows.isEmpty()) {
                            TasksListUiState.Empty
                        } else {
                            TasksListUiState.Content(taskRows)
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.value = TasksListUiState.Error(result.message)
                    }
                    is ApiResult.Loading -> {
                        _uiState.value = TasksListUiState.Loading
                    }
                }
            }
        }
    }

    fun retry() {
        loadAllTasks()
    }
}

// ============================================
// NEW TASK VIEWMODEL
// ============================================

class NewTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val taskRepository = TaskRepository(RetrofitInstance.taskApi, database.taskDao())

    private val _uiState = MutableStateFlow<NewTaskUiState>(NewTaskUiState.Idle)
    val uiState: StateFlow<NewTaskUiState> = _uiState.asStateFlow()

    private val _taskCreated = MutableSharedFlow<Boolean>()
    val taskCreated: SharedFlow<Boolean> = _taskCreated.asSharedFlow()

    fun createTask(title: String, detail: String, date: String, priority: String) {
        if (title.isEmpty()) {
            _uiState.value = NewTaskUiState.Error("El título es obligatorio")
            return
        }

        viewModelScope.launch {
            _uiState.value = NewTaskUiState.Loading

            val priorityInt = when (priority.lowercase()) {
                "alta", "high", "1" -> 1
                "media", "medium", "2" -> 2
                else -> 3
            }

            when (val result = taskRepository.createTask(title, detail, date, priorityInt)) {
                is ApiResult.Success -> {
                    _uiState.value = NewTaskUiState.Idle
                    _taskCreated.emit(true)
                }
                is ApiResult.Error -> {
                    _uiState.value = NewTaskUiState.Error(result.message)
                }
                else -> {}
            }
        }
    }

    fun retry() {
        _uiState.value = NewTaskUiState.Idle
    }
}

// ============================================
// FORGOT PASSWORD VIEWMODEL
// ============================================

class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(RetrofitInstance.authApi)

    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun sendRecoveryEmail() {
        if (_email.value.isBlank()) {
            _uiState.value = ForgotPasswordUiState.Error("Por favor ingresa tu correo")
            return
        }

        _uiState.value = ForgotPasswordUiState.Loading

        viewModelScope.launch {
            authRepository.forgotPassword(_email.value).collect { result ->
                when (result) {
                    is ApiResult.Success<Unit> -> {  // ← CORREGIDO: ApiResult.Success<Unit>
                        _uiState.value = ForgotPasswordUiState.Success
                    }
                    is ApiResult.Error -> {
                        _uiState.value = ForgotPasswordUiState.Error(result.message)
                    }
                    is ApiResult.Loading -> {
                        _uiState.value = ForgotPasswordUiState.Loading
                    }
                }
            }
        }
    }

    fun retry() {
        _uiState.value = ForgotPasswordUiState.Idle
    }
}

// ============================================
// UI STATES PARA FORGOT PASSWORD
// ============================================

sealed interface ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState
    object Loading : ForgotPasswordUiState
    object Success : ForgotPasswordUiState
    data class Error(val message: String) : ForgotPasswordUiState
}
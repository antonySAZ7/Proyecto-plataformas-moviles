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
import com.example.proyectoplatsadj.feature.calendar.CalendarUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ============================================
// LOGIN FORM STATE
// ============================================

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null
) {
    val isValid: Boolean
        get() = email.isNotBlank() &&
                password.isNotBlank() &&
                emailError == null &&
                passwordError == null
}

// ============================================
// LOGIN VIEWMODEL
// ============================================

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val authRepository = AuthRepository(RetrofitInstance.authApi, database.userDao())
    private val dataStore = DataStoreManager(application)

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<Boolean>()
    val loginSuccess: SharedFlow<Boolean> = _loginSuccess.asSharedFlow()

    fun onEmailChange(email: String) {
        _formState.update { currentState ->
            currentState.copy(
                email = email,
                emailError = if (email.isNotBlank())
                    com.example.proyectoplatsadj.utils.EmailValidator.getEmailError(email)
                else null
            )
        }
    }

    fun onPasswordChange(password: String) {
        _formState.update { currentState ->
            currentState.copy(
                password = password,
                passwordError = if (password.isNotBlank())
                    com.example.proyectoplatsadj.utils.PasswordValidator.getPasswordError(password)
                else null
            )
        }
    }

    fun validateEmail() {
        _formState.update { currentState ->
            currentState.copy(
                emailError = com.example.proyectoplatsadj.utils.EmailValidator.getEmailError(currentState.email)
            )
        }
    }

    fun validatePassword() {
        _formState.update { currentState ->
            currentState.copy(
                passwordError = com.example.proyectoplatsadj.utils.PasswordValidator.getPasswordError(currentState.password)
            )
        }
    }

    fun login(email: String, password: String) {
        val emailError = com.example.proyectoplatsadj.utils.EmailValidator.getEmailError(email)
        val passwordError = com.example.proyectoplatsadj.utils.PasswordValidator.getPasswordError(password)

        if (emailError != null || passwordError != null) {
            _formState.update { it.copy(emailError = emailError, passwordError = passwordError) }
            _uiState.value = LoginUiState.Error("Por favor completa todos los campos correctamente")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            when (val result = authRepository.login(email, password)) {
                is ApiResult.Success -> {
                    dataStore.saveAuthToken(result.data.token)
                    dataStore.saveUserInfo(result.data.userId ?: 1, email)

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
// REGISTER FORM STATE
// ============================================

data class RegisterFormState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) {
    val isValid: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                firstNameError == null &&
                lastNameError == null &&
                emailError == null &&
                passwordError == null &&
                confirmPasswordError == null &&
                password == confirmPassword
}

// ============================================
// REGISTER VIEWMODEL
// ============================================

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val authRepository = AuthRepository(RetrofitInstance.authApi, database.userDao())
    private val dataStore = DataStoreManager(application)

    private val _formState = MutableStateFlow(RegisterFormState())
    val formState: StateFlow<RegisterFormState> = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _registerSuccess = MutableSharedFlow<Boolean>()
    val registerSuccess: SharedFlow<Boolean> = _registerSuccess.asSharedFlow()

    fun onFirstNameChange(firstName: String) {
        _formState.update { it.copy(
            firstName = firstName,
            firstNameError = if (firstName.isNotBlank() && firstName.length < 2)
                "El nombre debe tener al menos 2 caracteres"
            else null
        )}
    }

    fun onLastNameChange(lastName: String) {
        _formState.update { it.copy(
            lastName = lastName,
            lastNameError = if (lastName.isNotBlank() && lastName.length < 2)
                "El apellido debe tener al menos 2 caracteres"
            else null
        )}
    }

    fun onEmailChange(email: String) {
        _formState.update { it.copy(
            email = email,
            emailError = if (email.isNotBlank())
                com.example.proyectoplatsadj.utils.EmailValidator.getEmailError(email)
            else null
        )}
    }

    fun onPasswordChange(password: String) {
        _formState.update { currentState ->
            val passwordError = if (password.isNotBlank())
                com.example.proyectoplatsadj.utils.PasswordValidator.getPasswordError(password)
            else null

            val confirmError = if (currentState.confirmPassword.isNotBlank() && password != currentState.confirmPassword)
                "Las contraseñas no coinciden"
            else null

            currentState.copy(
                password = password,
                passwordError = passwordError,
                confirmPasswordError = confirmError
            )
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _formState.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (confirmPassword.isNotBlank() && confirmPassword != currentState.password)
                    "Las contraseñas no coinciden"
                else null
            )
        }
    }

    private fun validateAllFields(): Boolean {
        val firstNameError = if (_formState.value.firstName.isBlank())
            "El nombre es requerido"
        else if (_formState.value.firstName.length < 2)
            "El nombre debe tener al menos 2 caracteres"
        else null

        val lastNameError = if (_formState.value.lastName.isBlank())
            "El apellido es requerido"
        else if (_formState.value.lastName.length < 2)
            "El apellido debe tener al menos 2 caracteres"
        else null

        val emailError = com.example.proyectoplatsadj.utils.EmailValidator.getEmailError(_formState.value.email)
        val passwordError = com.example.proyectoplatsadj.utils.PasswordValidator.getPasswordError(_formState.value.password)

        val confirmPasswordError = if (_formState.value.confirmPassword != _formState.value.password)
            "Las contraseñas no coinciden"
        else null

        _formState.update {
            it.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        return firstNameError == null &&
                lastNameError == null &&
                emailError == null &&
                passwordError == null &&
                confirmPasswordError == null
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        if (!validateAllFields()) {
            _uiState.value = RegisterUiState.Error("Por favor completa todos los campos correctamente")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            when (val result = authRepository.register(email, password, firstName, lastName)) {
                is ApiResult.Success -> {
                    dataStore.saveAuthToken(result.data.token)
                    dataStore.saveUserInfo(result.data.userId ?: 1, email)
                    _uiState.value = RegisterUiState.Idle
                    _registerSuccess.emit(true)
                }
                is ApiResult.Error -> {
                    val errorMessage = when {
                        result.message.contains("already exists", ignoreCase = true) ||
                                result.message.contains("ya existe", ignoreCase = true) ||
                                result.message.contains("duplicate", ignoreCase = true) ||
                                result.message.contains("email already", ignoreCase = true) ||
                                result.message.contains("ya está registrado", ignoreCase = true) -> {
                            _formState.update { it.copy(emailError = "Este correo ya está registrado") }
                            "Este correo electrónico ya está registrado. Intenta con otro."
                        }
                        else -> result.message
                    }
                    _uiState.value = RegisterUiState.Error(errorMessage)
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
                        priority = task.priority,
                        difficulty = task.difficulty,
                        dueDate = task.dueDate,
                        dueTime = task.dueTime
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

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(Task(id = taskId.toInt(), userId = 0, title = "", completed = false))
            loadTodayTasks()
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
                                priority = task.priority,
                                difficulty = task.difficulty,
                                dueDate = task.dueDate,
                                dueTime = task.dueTime
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

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(Task(id = taskId.toInt(), userId = 0, title = "", completed = false))
            loadAllTasks()
        }
    }

    fun retry() {
        loadAllTasks()
    }
}

// ============================================
// NEW TASK VIEWMODEL - CON NOTIFICACIONES
// ============================================
// REEMPLAZA SOLO ESTA CLASE en ViewModels.kt

class NewTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val taskRepository = TaskRepository(RetrofitInstance.taskApi, database.taskDao())

    private val _uiState = MutableStateFlow<NewTaskUiState>(NewTaskUiState.Idle)
    val uiState: StateFlow<NewTaskUiState> = _uiState.asStateFlow()

    private val _taskCreated = MutableSharedFlow<Boolean>()
    val taskCreated: SharedFlow<Boolean> = _taskCreated.asSharedFlow()

    fun createTask(title: String, detail: String, date: String, time: String, difficulty: Int) {
        if (title.isEmpty()) {
            _uiState.value = NewTaskUiState.Error("El título es obligatorio")
            return
        }

        viewModelScope.launch {
            _uiState.value = NewTaskUiState.Loading

            when (val result = taskRepository.createTask(title, detail, date, time, difficulty)) {
                is ApiResult.Success -> {
                    val createdTask = result.data

                    // ✨ NUEVO: Mostrar notificación de tarea creada
                    com.example.proyectoplatsadj.utils.NotificationHelper.showTaskCreatedNotification(
                        getApplication(),
                        createdTask.id,
                        createdTask.title,
                        createdTask.detail
                    )

                    // ✨ NUEVO: Programar recordatorios
                    com.example.proyectoplatsadj.utils.NotificationScheduler.scheduleMultipleReminders(
                        getApplication(),
                        createdTask.id,
                        createdTask.title,
                        createdTask.detail,
                        createdTask.dueDate,
                        createdTask.dueTime,
                        createdTask.difficulty
                    )

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
// CALENDAR VIEWMODEL
// ============================================

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val taskRepository = TaskRepository(RetrofitInstance.taskApi, database.taskDao())

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadAllTasks()
    }

    private fun loadAllTasks() {
        viewModelScope.launch {
            _uiState.value = CalendarUiState.Loading

            taskRepository.getAllTasks().collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val tasksGroupedByDate = result.data
                            .filter { it.dueDate != null }
                            .groupBy { task ->
                                try {
                                    java.time.LocalDate.parse(task.dueDate)
                                } catch (e: Exception) {
                                    null
                                }
                            }
                            .filterKeys { it != null }
                            .mapKeys { it.key!! }
                            .mapValues { entry ->
                                entry.value.map { task ->
                                    com.example.proyectoplatsadj.feature.calendar.CalendarTaskUi(
                                        id = task.id.toString(),
                                        title = task.title,
                                        detail = task.detail ?: "Sin detalles",
                                        difficulty = task.difficulty,
                                        dueTime = task.dueTime
                                    )
                                }
                            }

                        _uiState.value = CalendarUiState.Content(tasksGroupedByDate)
                    }
                    is ApiResult.Error -> {
                        _uiState.value = CalendarUiState.Error(result.message)
                    }
                    is ApiResult.Loading -> {
                        _uiState.value = CalendarUiState.Loading
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
// FORGOT PASSWORD VIEWMODEL
// ============================================

class ForgotPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val authRepository = AuthRepository(RetrofitInstance.authApi, database.userDao())

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
                    is ApiResult.Success<Unit> -> {
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
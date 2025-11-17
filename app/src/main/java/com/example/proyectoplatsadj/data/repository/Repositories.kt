package com.example.proyectoplatsadj.data.repository

import android.content.Context
import com.example.proyectoplatsadj.data.local.AppDatabase
import com.example.proyectoplatsadj.data.local.TaskDao
import com.example.proyectoplatsadj.data.local.User
import com.example.proyectoplatsadj.data.local.UserDao
import com.example.proyectoplatsadj.data.model.*
import com.example.proyectoplatsadj.data.remote.AuthApiService
import com.example.proyectoplatsadj.data.remote.TaskApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// ============================================
// AUTH REPOSITORY (CON BASE DE DATOS LOCAL)
// ============================================

class AuthRepository(
    private val authApi: AuthApiService,
    private val userDao: UserDao
) {

    /**
     * Login local - verifica en la base de datos
     */
    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        return try {
            // Buscar usuario en la base de datos local
            val user = userDao.login(email, password)

            if (user != null) {
                // Usuario encontrado - login exitoso
                ApiResult.Success(
                    LoginResponse(
                        token = "local_token_${user.id}_${System.currentTimeMillis()}",
                        userId = user.id,
                        email = user.email,
                        firstName = user.firstName,
                        lastName = user.lastName
                    )
                )
            } else {
                ApiResult.Error("Credenciales inválidas")
            }
        } catch (e: Exception) {
            ApiResult.Error("Error al iniciar sesión: ${e.message}", e)
        }
    }

    /**
     * Registro local - guarda en la base de datos
     */
    suspend fun register(
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ): ApiResult<RegisterResponse> {
        return try {
            // Verificar si el email ya existe
            val existingUser = userDao.getUserByEmail(email)

            if (existingUser != null) {
                return ApiResult.Error("Este correo ya está registrado")
            }

            // Crear nuevo usuario
            val newUser = User(
                firstName = firstName ?: "",
                lastName = lastName ?: "",
                email = email,
                password = password
            )

            // Insertar en la base de datos
            val userId = userDao.insertUser(newUser)

            // Retornar respuesta exitosa
            ApiResult.Success(
                RegisterResponse(
                    token = "local_token_${userId}_${System.currentTimeMillis()}",
                    userId = userId.toInt(),
                    email = email,
                    firstName = firstName,
                    lastName = lastName
                )
            )
        } catch (e: Exception) {
            ApiResult.Error("Error al crear la cuenta: ${e.message}", e)
        }
    }

    /**
     * Recuperar contraseña - simulado localmente
     */
    fun forgotPassword(email: String): Flow<ApiResult<Unit>> = flow {
        emit(ApiResult.Loading)
        try {
            // Verificar si el email existe
            val user = userDao.getUserByEmail(email)

            if (user != null) {
                // Simular envío de correo exitoso
                emit(ApiResult.Success(Unit))
            } else {
                emit(ApiResult.Error("No existe una cuenta con este correo"))
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.message ?: "Error al recuperar contraseña"))
        }
    }
}

// ============================================
// TASK REPOSITORY
// ============================================

class TaskRepository(
    private val taskApi: TaskApiService,
    private val taskDao: TaskDao
) {

    // Obtener todas las tareas - SOLO LOCALES
    fun getAllTasks(): Flow<ApiResult<List<Task>>> = flow {
        emit(ApiResult.Loading)

        // Solo obtener tareas locales, ignorar la API
        taskDao.getAllTasks().collect { localTasks ->
            emit(ApiResult.Success(localTasks))
        }
    }

    // Obtener tareas de hoy
    fun getTodayTasks(): Flow<List<Task>> {
        return taskDao.getTodayTasks()
    }


    suspend fun createTask(
        title: String,
        detail: String,
        dueDate: String,
        dueTime: String,
        difficulty: Int
    ): ApiResult<Task> {
        return try {
            val response = taskApi.createTodo(
                CreateTaskRequest(title = title, userId = 1)
            )

            if (response.isSuccessful && response.body() != null) {
                val created = response.body()!!


                val newTask = Task(
                    id = created.id,
                    userId = created.userId,
                    title = created.title,
                    completed = created.completed,
                    detail = detail,
                    dueDate = dueDate,
                    dueTime = dueTime,
                    difficulty = difficulty,
                    priority = difficulty
                )


                taskDao.insertTask(newTask)

                ApiResult.Success(newTask)
            } else {
                ApiResult.Error("No se pudo crear la tarea")
            }
        } catch (e: Exception) {
            // Crear tarea solo local si falla la API
            val localTask = Task(
                id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                userId = 1,
                title = title,
                completed = false,
                detail = detail,
                dueDate = dueDate,
                dueTime = dueTime,
                difficulty = difficulty,
                priority = difficulty
            )
            taskDao.insertTask(localTask)
            ApiResult.Success(localTask)
        }
    }

    // Actualizar tarea
    suspend fun updateTask(task: Task): ApiResult<Task> {
        return try {
            // Actualizar en Room
            taskDao.updateTask(task)
            ApiResult.Success(task)
        } catch (e: Exception) {
            ApiResult.Error("Error al actualizar la tarea: ${e.message}", e)
        }
    }

    // Eliminar tarea
    suspend fun deleteTask(task: Task): ApiResult<Unit> {
        return try {

            taskDao.deleteTask(task)
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error("Error al eliminar la tarea: ${e.message}", e)
        }
    }
}
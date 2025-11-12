package com.example.proyectoplatsadj.data.repository

import com.example.proyectoplatsadj.data.local.TaskDao
import com.example.proyectoplatsadj.data.model.*
import com.example.proyectoplatsadj.data.remote.AuthApiService
import com.example.proyectoplatsadj.data.remote.TaskApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// ============================================
// AUTH REPOSITORY
// ============================================

class AuthRepository(private val authApi: AuthApiService) {

    suspend fun login(email: String, password: String): ApiResult<LoginResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("Credenciales inválidas")
            }
        } catch (e: Exception) {
            ApiResult.Error("Error de conexión: ${e.message}", e)
        }
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ): ApiResult<RegisterResponse> {
        return try {
            val response = authApi.register(
                RegisterRequest(email, password, firstName, lastName)
            )
            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
            } else {
                ApiResult.Error("No se pudo crear la cuenta")
            }
        } catch (e: Exception) {
            ApiResult.Error("Error de conexión: ${e.message}", e)
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

    // Obtener todas las tareas (primero de Room, luego actualizar desde API)
    fun getAllTasks(): Flow<ApiResult<List<Task>>> = flow {
        emit(ApiResult.Loading)

        // Emitir datos de Room primero (cache)
        taskDao.getAllTasks().collect { localTasks ->
            if (localTasks.isNotEmpty()) {
                emit(ApiResult.Success(localTasks))
            }

            // Luego intentar actualizar desde API
            try {
                val response = taskApi.getAllTodos()
                if (response.isSuccessful && response.body() != null) {
                    val apiTasks = response.body()!!.take(20) // Limitar a 20 tareas

                    // Guardar en Room
                    taskDao.insertTasks(apiTasks)

                    emit(ApiResult.Success(apiTasks))
                }
            } catch (e: Exception) {
                // Si falla la API pero tenemos cache, no emitimos error
                if (localTasks.isEmpty()) {
                    emit(ApiResult.Error("Error de conexión: ${e.message}", e))
                }
            }
        }
    }

    // Obtener tareas de hoy
    fun getTodayTasks(): Flow<List<Task>> {
        return taskDao.getTodayTasks()
    }

    // Crear nueva tarea
    suspend fun createTask(
        title: String,
        detail: String,
        dueDate: String,
        priority: Int
    ): ApiResult<Task> {
        return try {
            val response = taskApi.createTodo(
                CreateTaskRequest(title = title, userId = 1)
            )

            if (response.isSuccessful && response.body() != null) {
                val created = response.body()!!

                // Crear tarea completa con datos locales
                val newTask = Task(
                    id = created.id,
                    userId = created.userId,
                    title = created.title,
                    completed = created.completed,
                    detail = detail,
                    dueDate = dueDate,
                    priority = priority
                )

                // Guardar en Room
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
                priority = priority
            )
            taskDao.insertTask(localTask)
            ApiResult.Success(localTask)
        }
    }

    // Actualizar tarea
    suspend fun updateTask(task: Task): ApiResult<Task> {
        return try {
            val response = taskApi.updateTodo(task.id, task)

            // Actualizar en Room independientemente de la respuesta API
            taskDao.updateTask(task)

            if (response.isSuccessful) {
                ApiResult.Success(task)
            } else {
                ApiResult.Success(task) // Guardar local aunque falle API
            }
        } catch (e: Exception) {
            // Guardar cambios localmente aunque falle la API
            taskDao.updateTask(task)
            ApiResult.Success(task)
        }
    }

    // Eliminar tarea
    suspend fun deleteTask(task: Task): ApiResult<Unit> {
        return try {
            taskApi.deleteTodo(task.id)
            taskDao.deleteTask(task)
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            // Eliminar localmente aunque falle la API
            taskDao.deleteTask(task)
            ApiResult.Success(Unit)
        }
    }
}
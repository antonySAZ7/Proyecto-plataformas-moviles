package com.example.proyectoplatsadj.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


// MODELOS PARA AUTENTICACIÓN (ReqRes API)


data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

data class RegisterResponse(
    val id: Int,
    val token: String
)

data class User(
    val id: Int,
    val email: String,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?
)


// MODELOS PARA TAREAS (JSONPlaceholder + Room)


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: Int,
    val userId: Int,
    val title: String,
    val completed: Boolean,

    // Campos adicionales locales
    val detail: String? = null,
    val dueDate: String? = null,
    val priority: Int = 3, // 1=alta, 2=media, 3=baja
    val createdAt: Long = System.currentTimeMillis()
)

// Para crear nueva tarea
data class CreateTaskRequest(
    val title: String,
    val completed: Boolean = false,
    val userId: Int = 1
)

// Respuesta al crear tarea
data class CreateTaskResponse(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val userId: Int
)


// ESTADO DE UI (genérico)


sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val exception: Exception? = null) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}
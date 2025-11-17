package com.example.proyectoplatsadj.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName



data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val userId: Int? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

data class RegisterResponse(
    val id: Int? = null,
    val token: String,
    val userId: Int? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
)

data class User(
    val id: Int,
    val email: String,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?
)



@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val completed: Boolean = false,

    val detail: String? = null,
    val dueDate: String? = null,
    val dueTime: String? = null,
    val difficulty: Int = 2,
    val priority: Int = 3,
    val createdAt: Long = System.currentTimeMillis()
) {

    fun getDifficultyColor(): Long {
        return when (difficulty) {
            1 -> 0xFF4CAF50
            2 -> 0xFFFFC107
            3 -> 0xFFF44336
            else -> 0xFF9E9E9E
        }
    }


    fun getDifficultyText(): String {
        return when (difficulty) {
            1 -> "Fácil"
            2 -> "Medio"
            3 -> "Difícil"
            else -> "Sin clasificar"
        }
    }


    fun getFormattedDateTime(): String {
        val datePart = dueDate ?: "Sin fecha"
        val timePart = dueTime ?: ""
        return if (timePart.isNotEmpty()) {
            "$datePart a las $timePart"
        } else {
            datePart
        }
    }
}

data class CreateTaskRequest(
    val title: String,
    val completed: Boolean = false,
    val userId: Int = 1
)

data class CreateTaskResponse(
    val id: Int,
    val title: String,
    val completed: Boolean,
    val userId: Int
)



sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val exception: Exception? = null) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}
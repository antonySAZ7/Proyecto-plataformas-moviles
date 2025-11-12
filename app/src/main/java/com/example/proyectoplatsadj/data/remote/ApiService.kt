

package com.example.proyectoplatsadj.data.remote

import com.example.proyectoplatsadj.data.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// ============================================
// API INTERFACES
// ============================================

// API de Autenticación (ReqRes)
interface AuthApiService {
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") userId: Int): Response<User>
}

// API de Tareas (JSONPlaceholder)
interface TaskApiService {
    @GET("todos")
    suspend fun getAllTodos(): Response<List<Task>>

    @GET("todos/{id}")
    suspend fun getTodoById(@Path("id") id: Int): Response<Task>

    @POST("todos")
    suspend fun createTodo(@Body task: CreateTaskRequest): Response<CreateTaskResponse>

    @PUT("todos/{id}")
    suspend fun updateTodo(@Path("id") id: Int, @Body task: Task): Response<Task>

    @DELETE("todos/{id}")
    suspend fun deleteTodo(@Path("id") id: Int): Response<Unit>

    @GET("todos?userId={userId}")
    suspend fun getTodosByUser(@Path("userId") userId: Int): Response<List<Task>>
}

// ============================================
// RETROFIT INSTANCE
// ============================================

object RetrofitInstance {

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Retrofit para autenticación (ReqRes API)
    private val authRetrofit = Retrofit.Builder()
        .baseUrl("https://reqres.in/")
        .client(createOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit para tareas (JSONPlaceholder)
    private val taskRetrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .client(createOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApiService by lazy {
        authRetrofit.create(AuthApiService::class.java)
    }

    val taskApi: TaskApiService by lazy {
        taskRetrofit.create(TaskApiService::class.java)
    }
}
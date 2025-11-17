package com.example.proyectoplatsadj.utils

/**
 * Validador de contraseñas
 */
object PasswordValidator {

    private const val MIN_PASSWORD_LENGTH = 6

    /**
     * Valida si una contraseña cumple los requisitos mínimos
     * @param password La contraseña a validar
     * @return true si es válida, false en caso contrario
     */
    fun isValidPassword(password: String): Boolean {
        return password.isNotBlank() && password.length >= MIN_PASSWORD_LENGTH
    }

    /**
     * Obtiene un mensaje de error si la contraseña no es válida
     * @param password La contraseña a validar
     * @return Mensaje de error o null si es válida
     */
    fun getPasswordError(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es requerida"
            password.length < MIN_PASSWORD_LENGTH -> "La contraseña debe tener al menos $MIN_PASSWORD_LENGTH caracteres"
            else -> null
        }
    }
}
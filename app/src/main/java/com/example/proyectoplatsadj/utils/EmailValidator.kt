package com.example.proyectoplatsadj.utils

/**
 * Validador de correos electrónicos
 */
object EmailValidator {

    /**
     * Patrón regex para validar emails
     * Acepta formatos como: user@example.com, user.name@example.co.uk, etc.
     */
    private val EMAIL_REGEX = Regex(
        pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
    )

    /**
     * Valida si un email tiene formato correcto
     * @param email El email a validar
     * @return true si es válido, false en caso contrario
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && EMAIL_REGEX.matches(email)
    }

    /**
     * Obtiene un mensaje de error si el email no es válido
     * @param email El email a validar
     * @return Mensaje de error o null si es válido
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isBlank() -> "El correo electrónico es requerido"
            !EMAIL_REGEX.matches(email) -> "Formato de correo inválido"
            else -> null
        }
    }
}
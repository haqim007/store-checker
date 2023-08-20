package dev.haqim.pitjarusapp.domain.model

enum class LoginStatusEnum(val value: String) {
    FAILURE(value = "failure"),
    SUCCESS(value = "success")
}

sealed class LoginStatus{
    data class Failure(
        val message: String
    ): LoginStatus()
    object Success: LoginStatus()
}
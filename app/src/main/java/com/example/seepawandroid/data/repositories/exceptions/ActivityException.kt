package com.example.seepawandroid.data.repositories.exceptions

sealed class ActivityException(message: String) : Exception(message) {
    data class SlotAlreadyBookedException(
        val slotId: String? = null
    ) : ActivityException("Este horário já foi reservado por outro utilizador")

    data class InvalidSlotException(
        val reason: String
    ) : ActivityException(reason)

    data class ServerException(
        val code: Int
    ) : ActivityException("Erro do servidor (HTTP $code). Tente novamente mais tarde")

    data class NetworkException(
        override val cause: Throwable
    ) : ActivityException("Erro de rede. Verifique sua conexão")

    data class UnknownException(
        val code: Int,
        val errorBody: String?
    ) : ActivityException("Erro desconhecido (HTTP $code)")
}

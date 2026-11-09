package com.example.colecaojogos.model

/**
 * Modelo de dados do app: um jogo da coleção do usuário.
 * Quando [id] == 0 o jogo ainda não foi salvo no banco.
 */
data class Game(
    val id: Long = 0,
    val title: String,
    val platform: String,
    val genre: String,
    val year: Int,
    val rating: Float,
    val completed: Boolean
)

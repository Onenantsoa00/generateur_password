package com.password_generator.data

import kotlin.random.Random

class PasswordGenerator {
    fun generatePassword(difficulty: Difficulty): String {
        val length = when (difficulty) {
            Difficulty.EASY -> 8
            Difficulty.MEDIUM -> 12
            Difficulty.HARD -> 16
        }

        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+"
        return (1..length)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }
}

enum class Difficulty {
    EASY, MEDIUM, HARD
}
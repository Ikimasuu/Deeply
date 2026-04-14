package edu.cit.deeply.data.repositories

import edu.cit.deeply.data.models.User
import kotlinx.coroutines.delay

object AuthRepository {

    private var currentUser: User? = null

    suspend fun signIn(email: String, password: String): Result<Unit> {
        delay(500L)
        currentUser = User(name = "Jane Doe", email = email)
        return Result.success(Unit)
    }

    suspend fun signUp(email: String, password: String): Result<Unit> {
        delay(500L)
        currentUser = User(name = "Jane Doe", email = email)
        return Result.success(Unit)
    }

    suspend fun signInWithGoogle(): Result<Unit> {
        delay(500L)
        currentUser = User(name = "Jane Doe", email = "jane@gmail.com")
        return Result.success(Unit)
    }

    fun getCurrentUser(): User {
        return currentUser ?: User(name = "Jane Doe", email = "jane@example.com")
    }

    fun signOut() {
        currentUser = null
    }
}

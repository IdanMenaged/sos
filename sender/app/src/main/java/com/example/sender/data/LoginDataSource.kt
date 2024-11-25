package com.example.sender.data

import com.example.sender.ServerCommunicator
import com.example.sender.data.model.LoggedInUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private val serverCommunicator = ServerCommunicator()

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val res = withContext(Dispatchers.IO) {
                serverCommunicator.sendNRecv("login $username $password")
            }

            // Handle result based on server response
            if (res == "success") {
                val user = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                // Invoke the callback with success
                return Result.Success(user)
            } else {
                // Invoke the callback with an error result
                return Result.Error(Exception("Wrong credentials"))
            }
        } catch (e: Throwable) {
            // In case of failure, invoke callback with error
            return Result.Error(IOException("Error logging in", e))
        }
    }


    fun logout() {
        // TODO: revoke authentication
    }
}
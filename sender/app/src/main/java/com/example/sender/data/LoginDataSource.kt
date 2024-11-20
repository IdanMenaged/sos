package com.example.sender.data

import com.example.sender.ServerCommunicator
import com.example.sender.data.model.LoggedInUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private val serverCommunicator = ServerCommunicator()

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val res = serverCommunicator.sendNRecv("login $username $password")
                if (res == "success") {
                    val user = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                    return Result.Success(user)
                }
                return Result.Error(Exception("Wrong credentials"))
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}
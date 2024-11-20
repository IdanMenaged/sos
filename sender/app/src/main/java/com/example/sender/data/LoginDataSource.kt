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

    fun login(username: String, password: String, callback: (Result<LoggedInUser>) -> Unit) {
        // todo: modify the rest of the code to use the callback
        try {
            // Launch a new coroutine for background operation
            CoroutineScope(Dispatchers.IO).launch {
                // Perform the login operation asynchronously
                val res = serverCommunicator.sendNRecv("login $username $password")

                // Handle result based on server response
                if (res == "success") {
                    val user = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                    // Invoke the callback with success
                    callback(Result.Success(user))
                } else {
                    // Invoke the callback with an error result
                    callback(Result.Error(Exception("Wrong credentials")))
                }
            }
        } catch (e: Throwable) {
            // In case of failure, invoke callback with error
            callback(Result.Error(IOException("Error logging in", e)))
        }
    }


    fun logout() {
        // TODO: revoke authentication
    }
}
package com.example.seepawandroid.data.remote.api.services

import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service responsible for real-time communication with the backend via SignalR.
 *
 * Handles SignalR connection for receiving push notifications and updates.
 * Connects to the /notificationHub endpoint on the backend.
 *
 * Responsibilities:
 * - Establish SignalR connection with authentication
 * - Listen to "ReceiveNotification" events from backend
 * - Forward events to NotificationManager via callbacks
 * - NO business logic (only I/O communication)
 */
@Singleton
class NotificationService @Inject constructor(
    private val baseUrl: String
) {
    companion object {
        private const val TAG = "NotificationService"
        private const val HUB_ENDPOINT = "notificationHub"
    }

    /**
     * Callback interface for receiving notifications from SignalR.
     */
    fun interface OnNotificationReceived {
        /**
         * Called when a notification is received from the backend.
         *
         * @param type The type of notification (e.g., "OWNERSHIP_REQUEST_APPROVED").
         * @param payload The notification data as a map.
         */
        fun onReceived(type: String, payload: Map<String, Any>)
    }

    private var hubConnection: HubConnection? = null
    private var callback: OnNotificationReceived? = null
    private var isConnected = false

    /**
     * Connects to SignalR hub with authentication token.
     *
     * @param authToken The JWT token for authentication.
     */
    fun connect(authToken: String) {
        if (isConnected && hubConnection?.connectionState == HubConnectionState.CONNECTED) {
            Log.d(TAG, "Already connected to SignalR")
            return
        }

        try {
            // Build hub URL
            val hubUrl = "${baseUrl.removeSuffix("/")}/$HUB_ENDPOINT"
            Log.d(TAG, "Connecting to SignalR hub at: $hubUrl")

            // Create hub connection
            hubConnection = HubConnectionBuilder.create(hubUrl)
                .withAccessTokenProvider(Single.just(authToken))
                .build()

            // Register event handler for "ReceiveNotification"
            hubConnection?.on(
                "ReceiveNotification",
                { id: String, type: String, message: String,
                  animalId: String?, ownershipRequestId: String?, createdAt: String ->

                    Log.d(TAG, "Received notification: type=$type, id=$id")

                    // Build payload map
                    val payload = mutableMapOf<String, Any>(
                        "id" to id,
                        "type" to type,
                        "message" to message,
                        "createdAt" to createdAt
                    )

                    animalId?.let { payload["animalId"] = it }
                    ownershipRequestId?.let { payload["ownershipRequestId"] = it }

                    // Notify callback
                    callback?.onReceived(type, payload)
                },
                String::class.java,  // id
                String::class.java,  // type
                String::class.java,  // message
                String::class.java,  // animalId (nullable)
                String::class.java,  // ownershipRequestId (nullable)
                String::class.java   // createdAt
            )

            // Set connection state handlers
            hubConnection?.onClosed { error ->
                isConnected = false
                if (error != null) {
                    Log.e(TAG, "SignalR connection closed with error", error)
                } else {
                    Log.d(TAG, "SignalR connection closed")
                }
            }

            // Start connection asynchronously
            hubConnection?.start()?.subscribe(
                {
                    isConnected = true
                    Log.d(TAG, "Successfully connected to SignalR hub")
                },
                { error ->
                    isConnected = false
                    Log.e(TAG, "Failed to connect to SignalR hub", error)
                }
            )
        } catch (e: Exception) {
            isConnected = false
            Log.e(TAG, "Error connecting to SignalR", e)
        }
    }

    /**
     * Disconnects from SignalR hub.
     */
    fun disconnect() {
        try {
            hubConnection?.stop()?.subscribe(
                {
                    Log.d(TAG, "Disconnected from SignalR hub")
                },
                { error ->
                    Log.e(TAG, "Error disconnecting from SignalR", error)
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error during disconnect", e)
        } finally {
            isConnected = false
            callback = null
            hubConnection = null
        }
    }

    /**
     * Sets the callback for receiving notifications.
     *
     * @param callback The callback to invoke when notifications are received.
     */
    fun setOnNotificationReceivedCallback(callback: OnNotificationReceived) {
        this.callback = callback
    }

    /**
     * Checks if SignalR is currently connected.
     *
     * @return True if connected, false otherwise.
     */
    fun isConnected(): Boolean {
        return isConnected && hubConnection?.connectionState == HubConnectionState.CONNECTED
    }

    /**
     * Gets the current connection state.
     *
     * @return Current HubConnectionState or null if not initialized.
     */
    fun getConnectionState(): HubConnectionState? {
        return hubConnection?.connectionState
    }
}
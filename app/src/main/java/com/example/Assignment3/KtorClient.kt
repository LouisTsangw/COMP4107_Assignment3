//package com.example.Assignment3
//
//import android.util.Log
//import io.ktor.client.HttpClient
//import io.ktor.client.call.body
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.plugins.defaultRequest
//import io.ktor.client.request.accept
//import io.ktor.client.request.header
//import io.ktor.client.request.post
//import io.ktor.client.request.setBody
//import io.ktor.http.ContentType
//import io.ktor.http.contentType
//import io.ktor.serialization.kotlinx.json.json
//import kotlinx.serialization.Serializable
//
//object KtorClient {
//    private var token: String = "" // Token for authentication
//
//    private val httpClient = HttpClient {
//        install(ContentNegotiation) {
//            json() // Enable JSON serialization
//        }
//        defaultRequest {
//            contentType(ContentType.Application.Json)
//            accept(ContentType.Application.Json)
//            header("Authorization", "Bearer $token") // Include the token in requests
//        }
//    }
//
//    /**
//     * Fetches a list of feeds from the API.
//     *
//     * @return A list of Feed objects.
//     */
////    suspend fun getFeeds(): List<Feed> {
////        return try {
////            val data = httpClient.get("https://api.npoint.io/a8cea79c033ace1c8b8b").body<List<Feed>>()
////            Log.i("GetFeeds", "API Response: $data") // Log the API response
////            data
////        } catch (e: Exception) {
////            Log.e("GetFeeds", "Error fetching feeds: ${e.message}") // Log the error
////            emptyList()
////        }
////    }
//
//    /**
//     * Sends feedback to the server.
//     *
//     * @param feedback The feedback message to send.
//     * @return The server's response as a string.
//     */
//    suspend fun postFeedback(feedback: String): String {
//        return try {
//            // Send a POST request to the server
//            val response: HttpBinResponse = httpClient.post("https://httpbin.org/post") {
//                setBody(feedback) // Set the feedback message as the request body
//            }.body() // Deserialize the response into an HttpBinResponse object
//
//            // Log the response for debugging
//            Log.i("FeedbackResponse", "Response: $response")
//
//            // Extract the X-Amzn-Trace-Id header (for demonstration purposes)
//            val traceId = response.headers["X-Amzn-Trace-Id"] ?: "No Trace ID"
//
//            // Return the trace ID or the entire response as a string
//            "Trace ID: $traceId\nResponse: $response"
//        } catch (e: Exception) {
//            Log.e("FeedbackError", "Error posting feedback: ${e.message}") // Log the error
//            "Error: ${e.message}"
//        }
//    }
//
//    @Serializable
//    data class HttpBinResponse(
//        val args: Map<String, String>,
//        val data: String,
//        val files: Map<String, String>,
//        val form: Map<String, String>,
//        val headers: Map<String, String>,
//        val json: String?,
//        val origin: String,
//        val url: String
//    )
//}
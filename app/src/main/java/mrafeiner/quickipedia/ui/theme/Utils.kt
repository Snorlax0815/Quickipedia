package mrafeiner.quickipedia.ui.theme

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.headers
import org.json.JSONObject
import java.time.LocalDate

/**
 * This class contains utility functions for the App.
 * @author Markus Rafeiner
 * @version 2024-06-07
 */
class Utils {
    val TAG: String = "Utils"
    val methods: String = "GET, POST, PUT, DELETE"
    val defaultURL: String = "https://api.wikimedia.org/feed/v1/wikipedia/en/featured/"

    /**
     * This function sends a request to the specified URL with the specified method.
     * As standard, the URL is set to the featured Article of Wikipedia.
     * @param method The method of the request. Only GET, POST, PUT and DELETE are allowed.
     * @param url The URL to send the request to. Default is the featured Article of Wikipedia.
     */
    public suspend fun sendRequest(method: String, url: String = defaultURL): JSONObject{
        if (!methods.contains(method))
            Log.e(TAG, "sendRequest: $method is not a valid method. Only $methods are allowed.")
        var returnObj: JSONObject = JSONObject()
        val client = HttpClient()
        // get current date in form of "y/MM/dd"
        val date = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("y/MM/dd"))
        Log.d(TAG, "sendRequest: ${url + date}")
        val response = client.get(defaultURL+date)
        returnObj = JSONObject(response.body<String>())
        Log.d(TAG, "sendRequest: $returnObj")
        return returnObj
    }
}
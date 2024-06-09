package mrafeiner.quickipedia.ui.theme

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.headers
import org.json.JSONObject
import java.time.LocalDate

/**
 * This class contains utility functions for the App.
 * @author Markus Rafeiner
 * @version 2024-06-07
 */
class Utils {
    private val TAG: String = "Utils"
    private val methods: String = "GET, POST, PUT, DELETE"
    private val defaultURL: String = "https://api.wikimedia.org/feed/v1/wikipedia/en/featured/"

    /**
     * This function sends a request to the specified URL with the specified method.
     * As standard, the URL is set to the featured Article of Wikipedia.
     * @param method The method of the request. Only GET, POST, PUT and DELETE are allowed. Default is GET.
     * @param url The URL to send the request to. Default is the featured Article of Wikipedia.
     * @param body The body of the request. Default is an empty JSONObject. Not needed for GET and DELETE.
     */
    public suspend fun sendRequest(method: String = "GET", url: String = defaultURL, body: JSONObject = JSONObject()): JSONObject{
        if (!methods.contains(method))
            Log.e(TAG, "sendRequest: $method is not a valid method. Only $methods are allowed.")
        val returnObj: JSONObject
        val client = HttpClient()
        // get current date in form of "y/MM/dd"
        val date = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("y/MM/dd"))
        Log.d(TAG, "sendRequest: ${url + date}")
        returnObj = when(method){
            "GET" -> {
                Log.d(TAG, "sendRequest: GET")
                val response = client.get(defaultURL+date){
                    headers {
                        append("Content-Type", "application/json")
                    }
                }
                JSONObject(response.body<String>())
            }
            "POST" -> {
                val response2 = client.post(defaultURL+date){
                    headers {
                        append("Content-Type", "application/json")
                    }
                    setBody(body.toString())
                }
                JSONObject(response2.body<String>())
            }
            "PUT" -> {
                val response2 = client.put(defaultURL+date){
                    headers {
                        append("Content-Type", "application/json")
                    }
                    setBody(body.toString())
                }
                JSONObject(response2.body<String>())
            }
            "DELETE" -> {
                val response2 = client.delete(defaultURL+date){
                    headers {
                        append("Content-Type", "application/json")
                    }
                }
                JSONObject(response2.body<String>())
            }
            else -> {JSONObject()}
        }
        Log.d(TAG, "sendRequest: $returnObj")
        return returnObj
    }

    public fun loadDefaults(context: Context) : JSONObject{
        // load JSON file "sample2.json" and return the file from it
        val jsonString: String = context.assets.open("sample2.json").bufferedReader().use { it.readText() }
        return JSONObject(jsonString)
    }
}
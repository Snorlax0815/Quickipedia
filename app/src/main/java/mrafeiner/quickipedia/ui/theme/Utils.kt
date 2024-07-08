package mrafeiner.quickipedia.ui.theme

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.headers
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.streams.asOutput
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.File
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
    public suspend fun sendRequest(method: String = "GET", url: String = defaultURL, body: JSONObject = JSONObject(), context: Context): JSONObject{
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
        // get imageURL
        if (returnObj.has("tfa")){
            val tfa = returnObj.getJSONObject("tfa")
            if(tfa.has("thumbnail") || tfa.has("originalimage")){
                // save the image to the internal storage
                val imageUrl = if(tfa.has("thumbnail")) tfa.getJSONObject("thumbnail").getString("source") else tfa.getJSONObject("originalimage").getString("source")
                val path = "defaultImage"+imageUrl.substring(imageUrl.lastIndexOf('.'), imageUrl.length)
                returnObj.put("imagePath", path)
            }
        }
        Log.d(TAG, "sendRequest: Saving to defaults...")
        saveDefaults(context = context, body = returnObj)
        return returnObj
    }

    public fun loadDefaults(context: Context) : JSONObject{
        // load JSON file "sample2.json" and return the file from it
        // first check if it exists, if yes, load it, if no, load sample2 from assets.
        if(context.fileList().contains("defaults.json")){
            try{
                val jsonString: String = context.openFileInput("defaults.json").bufferedReader().use { it.readText() }
                return JSONObject(jsonString)
            }catch (e: JSONException){
                Log.e(TAG, "loadDefaults: A JSON Exception has occurred while attempting to load the saved defaults. Fallback samples will load.")
                val jsonString: String = context.assets.open("sample2.json").bufferedReader().use { it.readText() }
                return JSONObject(jsonString)
            }

        }
        // of not, load sample2.json
        return context.assets.open("sample2.json").bufferedReader().use { it.readText() }.let { JSONObject(it) }
    }

    public fun saveDefaults(context: Context, body: JSONObject){
        // save the response body to "defaults.json" in the assets folder. Overwrites the file if it already exists. Save as JSON, not binary.
        context.openFileOutput("defaults.json", Context.MODE_PRIVATE).use {
            it.write(body.toString().toByteArray())
        }
        // download and save the article image needed for the glance widget
        // https://api.wikimedia.org/wiki/Core_REST_API/Reference/Media_files/Get_file
        if(body.has("tfa")){
            val tfa = body.getJSONObject("tfa")
            if(tfa.has("thumbnail") || tfa.has("originalimage")){
                // save the image to the internal storage
                val imageUrl = if(tfa.has("thumbnail")) tfa.getJSONObject("thumbnail").getString("source") else tfa.getJSONObject("originalimage").getString("source")
                val path = "defaultImage"+imageUrl.substring(imageUrl.lastIndexOf('.'), imageUrl.length)
                CoroutineScope(Dispatchers.IO).launch {
                    downloadImage(imageUrl, path, context)
                }
                Log.i(TAG, "saveDefaults: Saved image to internal storage.")
            }
            else{
                // if no image is available, write a default wikipedia logo image into the file.
                // the image is saved under assets/sampleImage.png
                val file = File(context.filesDir, "defaultImage.png")
                val inputStream = context.assets.open("sampleImage.png")
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                Log.i(TAG, "saveDefaults: No image available, saved default image.")
            }
        }

        Log.i(TAG, "saveDefaults: Saved to defaults.json")
    }

    suspend fun downloadImage(imageUrl: String, imageName: String, context: Context) {
        val client = HttpClient()

        val httpResponse: HttpResponse = client.get(imageUrl)

        // Get a reference to the app's internal files directory
        val filesDir = context.filesDir

        val file = File(filesDir, imageName)
        Log.d(TAG, "downloadImage: ${filesDir.toString() + imageName}")

        withContext(Dispatchers.IO) {
            // Ensure parent directory exists (not necessary for filesDir)
            // file.parentFile?.mkdirs()

            // Open output file stream
            file.outputStream().use { outputStream ->
                // Receive image data as a byte array
                val imageBytes = httpResponse.body<ByteArray>()

                // Write to output stream
                outputStream.write(imageBytes)
            }
        }

    }
}
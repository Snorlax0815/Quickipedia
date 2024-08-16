package mrafeiner.quickipedia

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

private val utils = Utils()


class Widget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            // create your AppWidget here
            GlanceTheme{
                Content()
            }
        }
    }

    @Composable
    fun Content() {
        val context = LocalContext.current
        val sampleData: JSONObject = utils.loadDefaults(context)
        val content: MutableState<JSONObject> = if(sampleData.has("tfa")){
            remember {
                mutableStateOf<JSONObject>(sampleData.getJSONObject("tfa"))
            }
        } else{
            remember {
                mutableStateOf<JSONObject>(sampleData)
            }
        }
        val imagePath = remember { mutableStateOf("") }
        imagePath.value = context.getSharedPreferences("lastUpdate", Context.MODE_PRIVATE).getString("imagePath", "").toString()



        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .clickable {
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
                    Log.d("Widget", "Clicked")
                    // Handle click
                    CoroutineScope(Dispatchers.IO).launch {
                        content.value = utils.loadDefaults(context)//utils.sendRequest(context = context)
                        val sh = context.getSharedPreferences("lastUpdate", Context.MODE_PRIVATE).edit()
                        sh.putLong("lastUpdate", System.currentTimeMillis())
                        // only
                        if(content.value.has("imagePath") && content.value.getJSONObject("tfa").has("thumbnail")){
                            Log.d("", "path: ${content.value.getString("imagePath")}")
                            sh.putString("imagePath", content.value.getString("imagePath"))
                        }
                        content.value = content.value.getJSONObject("tfa")
                        sh.apply()
                        sh.commit()
                    }
                }
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(GlanceTheme.colors.secondaryContainer),
            ){
                Row {
                    if (content.value.has("thumbnail")) {
                        val imageFile = File(context.filesDir, imagePath.value)
                        val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        val provider = ImageProvider(imageBitmap)

                        androidx.glance.Image(
                            provider = provider,
                            contentDescription = "Image",
                            modifier = GlanceModifier
                                .width(200.dp)
                                .padding(start = 8.dp)
                        )
                    }
                    Column (

                    ){
                        if(content.value.has("titles")){
                            Text(
                                text = content.value.getJSONObject("titles").getString("normalized"),
                                modifier = GlanceModifier
                                    .padding(start = 8.dp, top = 8.dp),
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                        }
                        if(content.value.has("content_urls")){
                            val url = content.value.getJSONObject("content_urls").getJSONObject("desktop").getString("page")
                            Text(
                                text = "Open in Wikipedia",
                                modifier = GlanceModifier
                                    .padding(start = 8.dp, top = 8.dp)
                                    .clickable {
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        intent.data = Uri.parse(url)
                                        context.startActivity(intent)
                                    },
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSecondaryContainer,
                                    fontSize = 12.sp
                                ),

                                )

                        }

                    }

                }
            }


            if (content.value.has("extract")){
                Row(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .background(GlanceTheme.colors.secondaryContainer)
                ){
                    LazyColumn(
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .background(GlanceTheme.colors.onSecondaryContainer)
                            .cornerRadius(4.dp),
                    ){
                        item {
                            Text(
                                text = content.value.getString("extract"),
                                modifier = GlanceModifier.padding(16.dp),
                                style = TextStyle(
                                    color = GlanceTheme.colors.inversePrimary,
                                )
                            )
                        }
                    }
                }

            }

        }
    }

}
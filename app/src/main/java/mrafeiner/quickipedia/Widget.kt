package mrafeiner.quickipedia

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.lifecycle.LifecycleOwner

import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mrafeiner.quickipedia.ui.theme.Utils
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
        val sampleData: JSONObject = utils.loadDefaults(LocalContext.current)
        val content: MutableState<JSONObject> = if(sampleData.has("tfa")){
            remember {
                mutableStateOf<JSONObject>(sampleData.getJSONObject("tfa"))
            }
        } else{
            remember {
                mutableStateOf<JSONObject>(sampleData)
            }
        }
        // val imageUrl = content.value.getJSONObject("thumbnail").getString("source")
        val context = LocalContext.current


        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(16.dp)
                .clickable {
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
                    Log.d("Widget", "Clicked")
                    // Handle click
                    CoroutineScope(Dispatchers.IO).launch {
                        content.value = utils.sendRequest(context = context).getJSONObject("tfa")
                    }
                }
        ) {
            Row {
                // check if the image in the internal storage is available, it not, dont draw the image.
                val sh = context.getSharedPreferences("lastUpdate", Context.MODE_PRIVATE)
                // if no image is saved, i think we can just skip. Sometimes there is no image with these
                val path = sh.getString("imagePath", "")
                Log.d("Widget", "Content: $path")
                if(path != ""){
                    val imageFile = File(context.filesDir, path)
                    val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                    val provider = ImageProvider(imageBitmap)
                    androidx.glance.Image(
                        provider = provider,
                        contentDescription = "Image",
                        modifier = GlanceModifier
                            .width(200.dp)
                    )
                }
                if(content.value.has("thumbnail")){
                    Text(
                        text = content.value.getString("title"),
                        modifier = GlanceModifier
                            .padding(start = 8.dp, top = 8.dp),
                        style = TextStyle(
                            color = GlanceTheme.colors.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
            }

            if (content.value.has("extract")){
                LazyColumn(

                ){
                    item {
                        Text(
                            text = content.value.getString("extract"),
                            modifier = GlanceModifier.padding(top = 8.dp),
                            style = TextStyle(
                                color = GlanceTheme.colors.onBackground
                            )
                        )
                    }
                }
            }

        }
    }

}
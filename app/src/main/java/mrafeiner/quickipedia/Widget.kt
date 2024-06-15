package mrafeiner.quickipedia

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import mrafeiner.quickipedia.ui.theme.Utils
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val content = sampleData.getJSONObject("tfa")
        val imageUrl = content.getJSONObject("thumbnail").getString("source")

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(16.dp)
        ) {
            Row {
                /*androidx.glance.Image(
                    provider = ImageProvider(
                        uri = Uri.parse(imageUrl)
                    ),
                    contentDescription = "thumbnail",
                    contentScale = androidx.glance.layout.ContentScale.Fit,
                    modifier = GlanceModifier.fillMaxSize()
                )*/
                Text(
                    text = content.getString("description"),
                    modifier = GlanceModifier
                        .padding(start = 8.dp),
                    style = TextStyle(
                        color = GlanceTheme.colors.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            LazyColumn(

            ){
                item {
                    Text(
                        text = content.getString("extract"),
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
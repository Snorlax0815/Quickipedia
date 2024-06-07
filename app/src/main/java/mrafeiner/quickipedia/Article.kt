package mrafeiner.quickipedia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mrafeiner.quickipedia.ui.theme.Utils
import org.json.JSONObject

private val utils: Utils = Utils()

private val sampleContent: JSONObject = JSONObject("{\n" +
        "  \"tfa\": {\n" +
        "    \"thumbnail\": {\n" +
        "      \"source\": \"https:\\/\\/upload.wikimedia.org\\/wikipedia\\/commons\\/a\\/a3\\/Munseys_Magazine_May_1911.jpg\",\n" +
        "      \"width\": 300,\n" +
        "      \"height\": 424\n" +
        "    },\n" +
        "    \"lang\": \"en\",\n" +
        "    \"timestamp\": \"2024-06-07T14:31:13Z\",\n" +
        "    \"description\": \"American magazine (1889–1929)\",\n" +
        "    \"content_urls\": {\n" +
        "      \"desktop\": {\n" +
        "        \"page\": \"https:\\/\\/en.wikipedia.org\\/wiki\\/Munsey's_Magazine\",\n" +
        "        \"revisions\": \"https:\\/\\/en.wikipedia.org\\/wiki\\/Munsey's_Magazine?action=history\",\n" +
        "        \"edit\": \"https:\\/\\/en.wikipedia.org\\/wiki\\/Munsey's_Magazine?action=edit\",\n" +
        "        \"talk\": \"https:\\/\\/en.wikipedia.org\\/wiki\\/Talk:Munsey's_Magazine\"\n" +
        "      }\n" +
        "    },\n" +
        "    \"extract\": \"Munsey's Magazine was an American magazine founded by Frank Munsey in 1889 as Munsey's Weekly, a humor magazine edited by John Kendrick Bangs. It was unsuccessful, and by late 1891 had lost \$100,000. Munsey converted it into an illustrated general monthly in October of that year, retitled Munsey's Magazine and priced at twenty-five cents. Richard Titherington became the editor, and remained in that role throughout the magazine's existence. In 1893 Munsey cut the price to ten cents. This brought him into conflict with the American News Company, which had a near-monopoly on magazine distribution, as they were unwilling to handle the magazine at the cost Munsey proposed. Munsey started his own distribution company and was quickly successful: the first ten cent issue began with a print run of 20,000 copies but eventually sold 60,000, and within a year circulation had risen to over a quarter of a million issues.\",\n" +
        "    \"normalizedtitle\": \"Munsey's Magazine\"\n" +
        "  }\n" +
        "}")

@Composable
fun Article(modifier: Modifier = Modifier, c: JSONObject = sampleContent) {
    val content = remember { mutableStateOf(sampleContent)}
    val displayContent = remember { mutableStateOf(true) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Quickipedia",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ){
                Button(
                    onClick = {
                        // launch Coroutine with dispatchers IO
                        CoroutineScope(Dispatchers.IO).launch {
                            content.value = utils.sendRequest("GET")
                            displayContent.value = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .padding(16.dp),

                    ) {
                    Text(text ="Send Request")
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            if(displayContent.value){
                // Text(text = content.value.toString())
                OutlinedCard (
                    modifier = Modifier.fillMaxWidth(0.95f),
                    shape = MaterialTheme.shapes.medium,
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.onSecondary,
                                            MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    )
                                )
                                .padding(16.dp),

                        ) {
                            Box(
                                modifier = Modifier
                                    .widthIn(0.dp, 200.dp)
                                    .aspectRatio((content.value.getJSONObject("tfa").getJSONObject("thumbnail").getDouble("width") / content.value.getJSONObject("tfa").getJSONObject("thumbnail").getDouble("height")).toFloat()),

                                contentAlignment = Alignment.TopStart
                            ) {
                                Card(
                                    shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 0.dp),
                                    modifier = Modifier.wrapContentSize()
                                ){
                                    val painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(content.value.getJSONObject("tfa").getJSONObject("thumbnail").getString("source"))
                                            .size(Size.ORIGINAL)
                                            .build()
                                    )
                                    Image(
                                        painter = painter,
                                        contentDescription = "thumbnail",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    /*
                                    AsyncImage(
                                        model = content.value.getJSONObject("tfa").getJSONObject("thumbnail").getString("source"),
                                        contentDescription = "thumbnail",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )*/
                                }
                            }
                            Box(
                                contentAlignment = Alignment.TopStart,

                            ){
                                Text(
                                    text = content.value.getJSONObject("tfa").getString("description"),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        Text(
                            text = content.value.getJSONObject("tfa").getString("extract"),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

            }
            else{
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Press the button to load content",
                    )
                }

            }
        }
    }
}

@Composable
@Preview
fun ArticlePreview() {
    Article()
}
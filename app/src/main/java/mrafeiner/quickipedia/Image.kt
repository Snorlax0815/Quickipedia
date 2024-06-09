package mrafeiner.quickipedia

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mrafeiner.quickipedia.ui.theme.Utils
import org.json.JSONObject


val sampleImage: State<JSONObject> = mutableStateOf(JSONObject("{" +
        "\"image\": {\n" +
        "    \"title\": \"File:Katholische Pfarrkirche St. Julitta und Quiricus, Andiast. (actm) 06.jpg\",\n" +
        "    \"thumbnail\": {\n" +
        "      \"source\": \"https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Katholische_Pfarrkirche_St._Julitta_und_Quiricus%2C_Andiast._%28actm%29_06.jpg/640px-Katholische_Pfarrkirche_St._Julitta_und_Quiricus%2C_Andiast._%28actm%29_06.jpg\",\n" +
        "      \"width\": 640,\n" +
        "      \"height\": 1308\n" +
        "    },\n" +
        "    \"image\": {\n" +
        "      \"source\": \"https://upload.wikimedia.org/wikipedia/commons/3/3f/Katholische_Pfarrkirche_St._Julitta_und_Quiricus%2C_Andiast._%28actm%29_06.jpg\",\n" +
        "      \"width\": 2349,\n" +
        "      \"height\": 4799\n" +
        "    },\n" +
        "    \"file_page\": \"https://commons.wikimedia.org/wiki/File:Katholische_Pfarrkirche_St._Julitta_und_Quiricus,_Andiast._(actm)_06.jpg\",\n" +
        "    \"artist\": {\n" +
        "      \"html\": \"<a href=\\\"//commons.wikimedia.org/wiki/User:Agnes_Monkelbaan\\\" title=\\\"User:Agnes Monkelbaan\\\">Agnes Monkelbaan</a>\",\n" +
        "      \"text\": \"Agnes Monkelbaan\"\n" +
        "    },\n" +
        "    \"credit\": {\n" +
        "      \"html\": \"<span class=\\\"int-own-work\\\" lang=\\\"en\\\">Own work</span>\",\n" +
        "      \"text\": \"Own work\"\n" +
        "    },\n" +
        "    \"license\": {\n" +
        "      \"type\": \"CC BY-SA 4.0\",\n" +
        "      \"code\": \"cc-by-sa-4.0\",\n" +
        "      \"url\": \"https://creativecommons.org/licenses/by-sa/4.0\"\n" +
        "    },\n" +
        "    \"description\": {\n" +
        "      \"html\": \"This <a rel=\\\"mw:WikiLink/Interwiki\\\" href=\\\"https://en.wikipedia.org/wiki/Stained%20glass\\\" title=\\\"en:Stained glass\\\" class=\\\"extiw\\\">stained glass</a> window by Oskar Berbig (1884–1930) depicts <a rel=\\\"mw:WikiLink/Interwiki\\\" href=\\\"https://en.wikipedia.org/wiki/Immaculate%20Heart%20of%20Mary\\\" title=\\\"en:Immaculate Heart of Mary\\\" class=\\\"extiw\\\">the Immaculate Heart of Mary</a>. It is located in Katholische Kapelle Sogn Antoni von Padua <a rel=\\\"mw:WikiLink/Interwiki\\\" href=\\\"https://en.wikipedia.org/wiki/Andiast\\\" title=\\\"en:Andiast\\\" class=\\\"extiw\\\">(Andiast, Switzerland)</a>. Today is the <a rel=\\\"mw:WikiLink/Interwiki\\\" href=\\\"https://en.wikipedia.org/wiki/Calendar%20of%20saints\\\" title=\\\"en:Calendar of saints\\\" class=\\\"extiw\\\">feast day</a> of the Immaculate Heart of Mary in the <a rel=\\\"mw:WikiLink/Interwiki\\\" href=\\\"https://en.wikipedia.org/wiki/Roman%20Catholic%20Church\\\" title=\\\"en:Roman Catholic Church\\\" class=\\\"extiw\\\">Roman Catholic Church</a>.\",\n" +
        "      \"text\": \"This stained glass window by Oskar Berbig (1884–1930) depicts the Immaculate Heart of Mary. It is located in Katholische Kapelle Sogn Antoni von Padua (Andiast, Switzerland). Today is the feast day of the Immaculate Heart of Mary in the Roman Catholic Church.\",\n" +
        "      \"lang\": \"en\"\n" +
        "    },\n" +
        "    \"wb_entity_id\": \"M77213908\",\n" +
        "    \"structured\": {\n" +
        "      \"captions\": {\n" +
        "        \"nl\": \"Gebrandschilderd raam.\"\n" +
        "      }\n" +
        "    }\n" +
        "  }" +
        "}"))


private val utils: Utils= Utils()

@Composable
fun Image(modifier: Modifier = Modifier, c: State<JSONObject> = sampleImage){
    val content = c
    val displayContent = remember { mutableStateOf(true) }
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            /*Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Quickipedia",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            }*/
        },
        bottomBar = {
            /*Box(
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
            }*/
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
                                    .aspectRatio((content.value.getJSONObject("image").getJSONObject("thumbnail").getDouble("width") / content.value.getJSONObject("image").getJSONObject("thumbnail").getDouble("height")).toFloat()),

                                contentAlignment = Alignment.TopStart
                            ) {
                                Card(
                                    shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 0.dp),
                                    modifier = Modifier.wrapContentSize()
                                ){
                                    val painter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(content.value.getJSONObject("image").getJSONObject("thumbnail").getString("source"))
                                            .size(Size.ORIGINAL)
                                            .build()
                                    )
                                    androidx.compose.foundation.Image(
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
                            Column(
                            ){
                                Text(
                                    text = content.value.getJSONObject("image").getString("title").removePrefix("File:").substringBeforeLast("."),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    modifier = Modifier.padding(start = 8.dp, top = 16.dp, end = 0.dp, bottom =0.dp)
                                )
                                Text(
                                    text = "Open in Wikipedia",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                    modifier = Modifier.padding(8.dp).clickable {
                                        // Open the Link from the JSON.
                                        val url = content.value.getJSONObject("image").getString("file_page")
                                        val intent = Intent(Intent.ACTION_VIEW)
                                        intent.data = Uri.parse(url)
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                        Column(
                            // make this column scrollable
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(0.dp, 500.dp)
                                .verticalScroll(rememberScrollState(), true)
                        ){
                            Text(
                                text = content.value.getJSONObject("image").getJSONObject("description").getString("text"),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.background,
                                                Color.Transparent
                                            ),
                                            startY = 0f,
                                            endY = Float.POSITIVE_INFINITY
                                        )
                                    )
                                    .fillMaxSize()
                            )
                        }


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
fun preview(){
    Image()
}
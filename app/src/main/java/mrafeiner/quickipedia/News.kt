package mrafeiner.quickipedia

import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import org.json.JSONArray
import org.json.JSONObject

private val utils = Utils()

@Composable
fun News(modifier: Modifier = Modifier, c: State<JSONObject> = mutableStateOf(utils.loadDefaults(LocalContext.current))) {
    val content = c
    val displayContent = remember { mutableStateOf(true) }
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "News",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            }
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
    ){
        Box(modifier = Modifier.padding(it)){
            // create list of JSONObjects from content, then create NewsArticle for each
            if(displayContent.value && (content.value.has("news"))){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState(), true)
                        .heightIn(0.dp, 1000.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    val currentNews: JSONArray = content.value.getJSONArray("news").getJSONObject(0).getJSONArray("links")
                    LazyColumn(

                    ){
                        items(currentNews.length()){ index ->
                            NewsArticle(article = mutableStateOf(currentNews.getJSONObject(index)), modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
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
                        text = "Press the button to load content\nIf nothing happens, just relax and try again later.",
                    )
                }
            }
        }
    }
}


@Composable
fun NewsArticle(modifier: Modifier = Modifier, article: State<JSONObject>){
    val context = LocalContext.current
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(0.95f)
            .heightIn(0.dp, 400.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                    )
                    .padding(8.dp),
            ){
                Log.d("News", "NewsArticle: ${article.value}")
                if(article.value.has("thumbnail")){
                    Box(
                        modifier = Modifier
                            .widthIn(0.dp, 150.dp)
                            .aspectRatio(
                                (article.value
                                    .getJSONObject("thumbnail")
                                    .getDouble("width") / article.value
                                    .getJSONObject("thumbnail")
                                    .getDouble("height")).toFloat()
                            ),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Card(
                            shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 0.dp),
                            modifier = Modifier
                                .wrapContentWidth()
                        ){
                            val painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(article.value.getJSONObject("thumbnail").getString("source"))
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
                }
                Column(
                ){
                    if(article.value.has("titles")){
                        Text(
                            text = article.value.getJSONObject("titles").getString("normalized"),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize,
                            modifier = Modifier.padding(start = 8.dp, top = 16.dp, end = 0.dp, bottom =0.dp)
                        )
                    }
                    if(article.value.has("content_urls")){
                        Text(
                            text = "Open in Wikipedia",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    // Open the Link from the JSON.
                                    val url = article.value
                                        .getJSONObject("content_urls")
                                        .getJSONObject("desktop")
                                        .getString("page")
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse(url)
                                    context.startActivity(intent)
                                }
                        )
                    }
                }
            }

        }
        if(article.value.has("extract")){
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    // make this column scrollable
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(0.dp, 500.dp)
                        .verticalScroll(rememberScrollState(), true)
                ){
                    Text(
                        text = article.value.getString("extract"),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
                    )
                }
            }

        }
    }
}



@Composable
@Preview
fun Preview(){
    News()
}
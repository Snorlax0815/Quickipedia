package mrafeiner.quickipedia

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mrafeiner.quickipedia.ui.theme.QuickipediaTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val utils: Utils = Utils()

/**
 * This is the main activity of the app.
 * Still very early, lots of things to do.
 * @author Markus Rafeiner
 * @version 2024-06-07
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickipediaTheme {
                MainScreen()
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MainScreen() {
        val context = LocalContext.current
        val content = remember { mutableStateOf(utils.loadDefaults(context))}
        val displayContent = remember { mutableStateOf(true) }
        val displayDate = remember {
            mutableStateOf(context.getSharedPreferences("lastUpdate", Context.MODE_PRIVATE).getLong("lastUpdate", System.currentTimeMillis()))
        }


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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Button(
                        onClick = {
                            // launch Coroutine with dispatchers IO
                            CoroutineScope(Dispatchers.IO).launch {
                                content.value = utils.sendRequest("GET", context = context)
                                displayContent.value = true
                                // save last Update in SharedPreferences
                                val sh = context.getSharedPreferences("lastUpdate", Context.MODE_PRIVATE).edit()
                                sh.putLong("lastUpdate", System.currentTimeMillis())
                                if(content.value.has("imagePath")){
                                    sh.putString("imagePath", content.value.getString("imagePath"))
                                }
                                sh.commit()

                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .padding(0.dp),

                    ) {
                        Text(text ="Refresh")
                    }
                    if(context.getSharedPreferences("lastUpdate", MODE_PRIVATE).contains("lastUpdate")){
                        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                        val formattedDate = formatter.format(displayDate.value)
                        Text(
                            text = "Last Update: $formattedDate", // Use formattedDate here
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }

                }
            }
        ){
            Box (
                modifier = Modifier.padding(it)
            ){
                if(displayContent.value){
                    val pagerState = rememberPagerState(pageCount = {4}, initialPage = 0, initialPageOffsetFraction = 0f)
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        when (page) {
                            0 -> Article(c = content)
                            1 -> MostRead(c = content)
                            2 -> Image(c = content)
                            3 -> News(c = content)
                        }
                    }
                }

            }
        }

    }
}

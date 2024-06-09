package mrafeiner.quickipedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mrafeiner.quickipedia.ui.theme.QuickipediaTheme
import mrafeiner.quickipedia.ui.theme.Utils
import org.json.JSONObject

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
                        Text(text ="Refresh")
                    }
                }
            }
        ){
            Box (
                modifier = Modifier.padding(it)
            ){
                if(displayContent.value){
                    val pagerState = rememberPagerState(pageCount = {2}, initialPage = 0, initialPageOffsetFraction = 0f)
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        when (page) {
                            0 -> Article(c = content)
                            1 -> Image(c = content)
                        }
                    }
                }

            }
        }

    }
}

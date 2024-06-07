package mrafeiner.quickipedia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mrafeiner.quickipedia.ui.theme.QuickipediaTheme
import mrafeiner.quickipedia.ui.theme.Utils

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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ){
                            Text(text = "Quickipedia")
                        }
                    },
                    bottomBar = {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ){
                            Button(
                                onClick = {
                                    val utils: Utils = Utils()
                                    // launch Coroutine with dispatchers IO
                                    CoroutineScope(Dispatchers.IO).launch {
                                        utils.sendRequest("GET", )
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
                    Text(text = "Hallo...", modifier = Modifier.padding(it))
                }

            }
        }
    }
}

package app.android.aidl.client

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.android.aidl.client.ui.theme.AIDLClientTheme
import app.android.aidl.server.IAIDLFeatureInterface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var iAIDLFeatureInterface: IAIDLFeatureInterface? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iAIDLFeatureInterface = IAIDLFeatureInterface.Stub.asInterface(service)
            Log.e("Akhil TAG", "Remote Feature service connected")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e("Akhil TAG", "Remote Feature service disconnected")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AIDLClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ColorScreen()
                }
            }
        }
    }

    @Preview
    @Composable
    fun ColorScreen() {
        var changingColor by remember {
            mutableStateOf(Color.Blue)
        }
        var changingText by remember {
            mutableStateOf("")
        }
        var count by remember {
            mutableStateOf(0)
        }
        val colorList =
            listOf(Color.Black, Color.DarkGray)
        val color by animateColorAsState(targetValue = changingColor, animationSpec = tween(
            durationMillis = 500, delayMillis = 50, easing = LinearOutSlowInEasing
        ))
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .background(color = color, shape = RoundedCornerShape(15.dp))
                .fillMaxSize(0.5f)
                .clickable {

                }
                .verticalScroll(scrollState)
        ) {
            Text(
                text = changingText, style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    color = Color.White
                ), modifier = Modifier.align(Alignment.Center)
            )
        }
        LaunchedEffect(key1 = changingText, block = {
            changingText = iAIDLFeatureInterface
                ?.getDataList(count++)
                ?.joinToString() ?: "Null List"
        })
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent("AIDLFeatureService")
        intent.setPackage("app.android.aidl.server")
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)

    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(serviceConnection)
    }
}

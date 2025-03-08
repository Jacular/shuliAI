package com.shuli.cc.app.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shuli.cc.app.Greeting
import com.shuli.cc.app.presentation.ChatScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Koin 初始化
        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        setContent {
            MyApplicationTheme {
                Surface {
                    ChatScreen(sessionId = "default_session")
                }
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Surface {
            ChatScreen(sessionId = "default_session")
        }
    }
}

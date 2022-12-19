package com.sebix.testinfuraapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebix.testinfuraapi.ui.theme.TestInfuraAPITheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TestInfuraAPITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        var text by rememberSaveable { mutableStateOf("Text") }
                        Row(Modifier.fillMaxWidth()) {
                            TextField(
                                value = text,
                                onValueChange = {
                                    text = it
                                },
                                label = {
                                    Text("Enter network endpoint (eg. https://mainnet.infura.io/v3/xxxxxxxxxx")
                                }
                            )
                        }
                        Button(
                            onClick = { runRequest() },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Kliknij mnie")
                        }
                    }
                }
            }
        }
    }
}

fun runRequest(){

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestInfuraAPITheme {
        Greeting("Android")
    }
}
package com.sebix.testinfuraapi

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sebix.testinfuraapi.ui.theme.TestInfuraAPITheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

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
                        var text by rememberSaveable { mutableStateOf("") }
                        Row(Modifier.fillMaxWidth()) {
                            TextField(
                                value = text,
                                onValueChange = {
                                    text = it
                                },
                                label = {
                                    Text("Enter network endpoint eg. https://mainnet.infura.io/v3/xxxxxxxxxx")
                                }
                            )
                        }
                        Button(
                            onClick = { runBlockRequest(this@MainActivity, text) },
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                        ) {
                            Text(text = "Get block number")
                        }
                        Button(
                            onClick = { runGasRequest(this@MainActivity, text) },
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp)
                        ) {
                            Text(text = "Get gas")
                        }
                    }
                }
            }
        }
    }
}

val ioScope = CoroutineScope(Dispatchers.IO)
val mainScope = CoroutineScope(Dispatchers.Main)

fun runBlockRequest(context: Context, apiUrl: String) {
    ioScope.launch {
        try {
            val web3j =
                Web3j.build(HttpService(apiUrl))
            val blockNumber = web3j.ethBlockNumber().sendAsync().get().blockNumber
            mainScope.launch {
                Toast.makeText(context, "Block number: $blockNumber", Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
            mainScope.launch {
                Toast.makeText(context, "An error has occurred.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun runGasRequest(context: Context, apiUrl: String) {
    ioScope.launch {
        try {
            val web3j =
                Web3j.build(HttpService(apiUrl))
            val gas = web3j.ethGasPrice().sendAsync().get().gasPrice
            val gasDouble:Double = gas.toDouble() / 1000000000.0
            mainScope.launch {
                Toast.makeText(context, "Gas: ${gasDouble}", Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
            mainScope.launch {
                Toast.makeText(context, "An error has occurred.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestInfuraAPITheme {
    }
}
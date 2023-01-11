package com.sebix.testinfuraapi.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sebix.testinfuraapi.ui.pages.main.MainViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainPage(navController: NavController) {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        Column {

            Text(text = "WALLET ADDRESS : ${mainViewModel.getWalletCredentials().address}")

            var apiKey by rememberSaveable { mutableStateOf("") }
            Row(Modifier.fillMaxWidth()) {
                TextField(
                    value = apiKey,
                    onValueChange = {
                        apiKey = it
                    },
                    label = {
                        Text("Enter network endpoint eg. https://mainnet.infura.io/v3/xxxxxxxxxx")
                    }
                )
            }
            Button(
                onClick = { mainViewModel.runBlockRequest(context, apiKey) },
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
            ) {
                Text(text = "Get block number")
            }
            Text(text = "Get gas")
            Button(
                onClick = { mainViewModel.runGasRequest(context, apiKey) },
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
            ) {
                Text(text = "Get gas")
            }
            Button(
                onClick = { mainViewModel.getWalletBalance(context, apiKey) },
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
            ) {
                Text(text = "Get wallet ballance")
            }
            Button(
                onClick = { mainViewModel.makeTransaction(context) },
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
            ) {
                Text(text = "Make transaction")
            }
        }
    }

}
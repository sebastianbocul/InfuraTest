package com.sebix.testinfuraapi

import ImportWalletPage
import com.sebix.testinfuraapi.ui.pages.newwallet.NewWalletPage
import StartPage
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sebix.testinfuraapi.ui.theme.TestInfuraAPITheme
import kotlin.math.pow
import com.sebix.testinfuraapi.ui.composables.MainPage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setupBouncyCastle()
//        verifyStoragePermissions(this)
        setContent {
            TestInfuraAPITheme {
                WalletApplication()
            }
        }
    }

    @Composable
    fun WalletApplication() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "start_page", builder = {
            composable("main_page", content = { MainPage(navController = navController) })
            composable("start_page", content = { StartPage(navController = navController) })
            composable(
                "new_wallet_page",
                content = { NewWalletPage(navController = navController) })
            composable(
                "import_wallet_page",
                content = { ImportWalletPage(navController = navController) })
        })
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TestInfuraAPITheme {
    }
}


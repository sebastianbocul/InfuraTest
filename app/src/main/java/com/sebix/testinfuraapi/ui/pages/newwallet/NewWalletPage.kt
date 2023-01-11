package com.sebix.testinfuraapi.ui.pages.newwallet

import GradientButton
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sebix.testinfuraapi.R

@Composable
fun NewWalletPage(navController: NavController) {
    val mainViewModel = hiltViewModel<NewWalletViewModel>()
    val context = LocalContext.current
    Log.d("MYTAG", "View generating")
    val mnemonicSeeds = mainViewModel.generateMnemonicSeeds()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(
                color = Color.Transparent,
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter),
        ) {
            Image(
                painter = painterResource(id = R.drawable.pencill),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth(),

                )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Please, ensure to save those words in safe place.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 200.dp)
                        .fillMaxWidth(),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = mnemonicSeeds,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                )

                Spacer(modifier = Modifier.padding(0.dp))

                TextButton(onClick = {
                }) {
                    Text(
                        text = "Copy",
                        letterSpacing = 1.sp,
                    )
                }
                Spacer(modifier = Modifier.padding(20.dp))

                val gradientColor = listOf(Color(0xFF484BF1), Color(0xFF673AB7))
                val cornerRadius = 16.dp
                GradientButton(
                    gradientColors = gradientColor,
                    cornerRadius = cornerRadius,
                    nameButton = "Create",
                    roundedCornerShape = RoundedCornerShape(topStart = 30.dp, bottomEnd = 30.dp)
                ) {
                    mainViewModel.createNewWalletRunBlocking(
                        context = context,
                        mnemonic = mnemonicSeeds
                    )
                    navController.navigate("main_page") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }

                Spacer(modifier = Modifier.padding(50.dp))
            }
        }
    }
}
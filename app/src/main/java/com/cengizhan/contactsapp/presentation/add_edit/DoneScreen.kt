package com.cengizhan.contactsapp.presentation.add_edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cengizhan.contactsapp.R

/**
 * Yeni kişi kaydedildikten sonra gösterilen Lottie ekranı.
 */
@Composable
fun DoneScreen(
    onFinish: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.done)
    )

    // Animasyon bir kere oynasın
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    // Animasyon bittiğinde otomatik olarak geri dön
    LaunchedEffect(progress) {
        if (progress == 1f) {
            onFinish()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "All Done!\nNew contact saved 🎉",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

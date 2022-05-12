package io.github.takusan23.zeromirror.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.zeromirror.R

/**
 * ビットレート、fpsとかを表示する
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamEncodeStatusUI() {
    Column(modifier = Modifier.padding(10.dp)) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = "映像/音声 情報",
            style = TextStyle(fontWeight = FontWeight.Bold),
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.padding(5.dp),
            text = "ビットレートを上げると画質が向上しますが負荷が大きくなります、フレームレートも同様です。"
        )
        Spacer(modifier = Modifier.size(10.dp))

        Column(modifier = Modifier.padding(5.dp)) {
            Text(color = MaterialTheme.colorScheme.primary, text = "映像 ビットレート")
            Text(text = "5Mbps")
        }
        Column(modifier = Modifier.padding(5.dp)) {
            Text(color = MaterialTheme.colorScheme.primary, text = "映像 フレームレート")
            Text(text = "30")
        }
        Column(modifier = Modifier.padding(5.dp)) {
            Text(color = MaterialTheme.colorScheme.primary, text = "音声 ビットレート")
            Text(text = "192kbps")
        }

        OutlinedButton(
            modifier = Modifier
                .align(Alignment.End),
            onClick = { }
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_outline_settings_24), contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "設定を変更する")
        }
    }
}

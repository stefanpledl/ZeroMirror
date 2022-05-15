package io.github.takusan23.zeromirror.ui.screen

import android.app.Activity
import android.content.Context
import android.media.projection.MediaProjectionManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.takusan23.zeromirror.ScreenMirrorService
import io.github.takusan23.zeromirror.tool.IpAddressTool
import io.github.takusan23.zeromirror.tool.PermissionTool
import io.github.takusan23.zeromirror.ui.components.*

/**
 * ホーム画面、ミラーリングの開始など。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    // IPアドレスをFlowで受け取る
    val idAddress = remember { IpAddressTool.collectIpAddress(context) }.collectAsState(initial = null)

    // マイク録音権限があるか、Android 10 以前は対応していないので一律 false、Android 10 以降は権限がなければtrueになる
    val isGrantedRecordAudio = remember {
        mutableStateOf(if (PermissionTool.isAndroidQAndHigher()) {
            !PermissionTool.isGrantedRecordPermission(context)
        } else false)
    }

    // 権限を求めてサービスを起動する
    val mediaProjectionManager = remember { context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager }
    val requestCapture = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // ミラーリングサービス開始
        if (result.resultCode == ComponentActivity.RESULT_OK && result.data != null) {
            // Activity 以外は無いはず...
            ScreenMirrorService.startService((context as Activity), result.resultCode, result.data!!)
        } else {
            Toast.makeText(context, "開始が拒否されました、(ヽ´ω`)", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            // 現在時刻
            CurrentTimeTitle(modifier = Modifier.fillMaxWidth())

            // 開始 / 終了 ボタン
            MirroringButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                onStartClick = {
                    // キャプチャー権限を求める
                    requestCapture.launch(mediaProjectionManager.createScreenCaptureIntent())
                },
                onStopClick = {
                    // 終了させる
                    ScreenMirrorService.stopService(context)
                }
            )

            // URL表示
            UrlCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                url = "http://${idAddress.value}:2828"
            )

            // 内部音声にはマイク権限
            if (isGrantedRecordAudio.value) {
                InternalAudioPermissionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    permissionResult = { isGranted ->
                        // trueなら非表示にするためfalseを入れる
                        isGrantedRecordAudio.value = !isGranted
                    }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
            ) {
                // エンコーダー
                StreamInfo(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
            }
        }
    }
}
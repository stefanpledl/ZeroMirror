package io.github.takusan23.zeromirror.ui.screen.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.zeromirror.R
import io.github.takusan23.zeromirror.data.MirroringSettingData
import io.github.takusan23.zeromirror.tool.DisplayConverter
import io.github.takusan23.zeromirror.ui.components.SwitchSettingItem
import io.github.takusan23.zeromirror.ui.components.TextBoxInitValueSettingItem
import kotlinx.coroutines.launch

/**
 * 画面共有 設定画面
 *
 * @param onBack 戻ってほしいときに呼ばれる
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MirroringSettingScreen(onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mirroringData = remember { MirroringSettingData.loadDataStore(context) }.collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }

    /**
     * 設定を更新する
     * @param onUpdateData データクラスをコピーして返して
     */
    fun updateSetting(onUpdateData: (MirroringSettingData) -> MirroringSettingData) {
        scope.launch {
            MirroringSettingData.setDataStore(context, onUpdateData(mirroringData.value!!))
        }
    }

    /** ミラーリング設定を初期値に戻す */
    fun resetMirrorSetting() {
        updateSetting {
            it.copy(
                portNumber = MirroringSettingData.DEFAULT_PORT_NUMBER,
                intervalMs = MirroringSettingData.DEFAULT_INTERVAL_MS,
                videoBitRate = MirroringSettingData.DEFAULT_VIDEO_BIT_RATE,
                videoFrameRate = MirroringSettingData.DEFAULT_VIDEO_FRAME_RATE,
                audioBitRate = MirroringSettingData.DEFAULT_AUDIO_BIT_RATE,
                isRecordInternalAudio = false
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(id = R.string.setting_stream_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painter = painterResource(id = R.drawable.ic_outline_arrow_back_24), contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Snackbar で本当に消していいか
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(context.getString(R.string.mirroring_setting_reset_message), context.getString(R.string.mirroring_setting_reset))
                            if (result == SnackbarResult.ActionPerformed) {
                                resetMirrorSetting()
                            }
                        }
                    }) { Icon(painter = painterResource(id = R.drawable.ic_outline_restart_alt_24), contentDescription = null) }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            if (mirroringData.value != null) {
                Card(modifier = Modifier.padding(10.dp)) {

                    TextBoxInitValueSettingItem(
                        label = stringResource(id = R.string.mirroring_setting_video_interval_title),
                        description = stringResource(id = R.string.mirroring_setting_video_interval_description),
                        initValue = (mirroringData.value!!.intervalMs / 1000).toString(),
                        iconRes = R.drawable.ic_outline_timer_24,
                        onValueChange = {
                            it.toLongOrNull()?.also { intervalMs ->
                                // 更新する
                                updateSetting { it.copy(intervalMs = intervalMs * 1000) }
                            }
                        }
                    )

                    Divider()

                    TextBoxInitValueSettingItem(
                        label = stringResource(id = R.string.mirroring_setting_video_bitrate_title),
                        description = stringResource(id = R.string.mirroring_setting_video_bitrate_description),
                        initValue = (mirroringData.value!!.videoBitRate / 1000).toString(),
                        inputUnderText = DisplayConverter.convert(mirroringData.value!!.videoBitRate),
                        iconRes = R.drawable.ic_outline_videocam_24,
                        onValueChange = {
                            it.toIntOrNull()?.also { videoBitRate ->
                                updateSetting { it.copy(videoBitRate = videoBitRate * 1000) }
                            }
                        }
                    )

                    TextBoxInitValueSettingItem(
                        label = stringResource(id = R.string.mirroring_setting_video_fps_title),
                        description = stringResource(id = R.string.mirroring_setting_video_fps_description),
                        initValue = mirroringData.value!!.videoFrameRate.toString(),
                        iconRes = R.drawable.ic_outline_videocam_24,
                        onValueChange = {
                            it.toIntOrNull()?.also { videoFps ->
                                updateSetting { it.copy(videoFrameRate = videoFps) }
                            }
                        }
                    )

                    Divider()

                    SwitchSettingItem(
                        title = stringResource(id = R.string.mirroring_setting_internal_audio_title),
                        description = stringResource(id = R.string.mirroring_setting_internal_audio_description),
                        iconRes = R.drawable.ic_outline_audiotrack_24,
                        isEnable = mirroringData.value!!.isRecordInternalAudio,
                        onValueChange = { isChecked ->
                            updateSetting { it.copy(isRecordInternalAudio = isChecked) }
                        }
                    )

                    TextBoxInitValueSettingItem(
                        label = stringResource(id = R.string.mirroring_setting_audio_bitrate_title),
                        description = stringResource(id = R.string.mirroring_setting_audio_bitrate_description),
                        initValue = (mirroringData.value!!.audioBitRate / 1000).toString(),
                        inputUnderText = DisplayConverter.convert(mirroringData.value!!.audioBitRate),
                        iconRes = R.drawable.ic_outline_audiotrack_24,
                        onValueChange = {
                            it.toIntOrNull()?.also { audioBitRate ->
                                updateSetting { it.copy(audioBitRate = audioBitRate * 1000) }
                            }
                        }
                    )

                    Divider()

                    TextBoxInitValueSettingItem(
                        label = stringResource(id = R.string.mirroring_setting_port_title),
                        description = stringResource(id = R.string.mirroring_setting_port_description),
                        initValue = mirroringData.value!!.portNumber.toString(),
                        iconRes = R.drawable.ic_outline_open_in_browser_24,
                        onValueChange = {
                            it.toIntOrNull()?.also { portNumber ->
                                updateSetting { it.copy(portNumber = portNumber) }
                            }
                        }
                    )
                }
            }
        }
    }
}
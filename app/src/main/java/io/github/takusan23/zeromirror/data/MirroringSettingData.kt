package io.github.takusan23.zeromirror.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import io.github.takusan23.zeromirror.setting.SettingKeyObject
import io.github.takusan23.zeromirror.setting.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * ミラーリング情報データ
 *
 * @param portNumber ポート番号
 * @param videoBitRate 映像ビットレート、ビット
 * @param audioBitRate 音声ビットレート、ビット
 * @param videoFrameRate 映像フレームレート、fps
 * @param intervalMs 動画を切り出す間隔、ミリ秒
 * @param isRecordInternalAudio 内部音声を入れる場合はtrue、権限があるかどうかまでは見ていません。
 */
data class MirroringSettingData(
    val portNumber: Int,
    val intervalMs: Long,
    val videoBitRate: Int,
    val videoFrameRate: Int,
    val audioBitRate: Int,
    val isRecordInternalAudio: Boolean,
) {

    companion object {

        /** デフォルトポート番号 */
         const val DEFAULT_PORT_NUMBER = 2828

        /** デフォルトファイル生成間隔 */
        const val DEFAULT_INTERVAL_MS = 5_000L

        /** デフォルト映像ビットレート */
        const val DEFAULT_VIDEO_BIT_RATE = 1_000_000

        /** デフォルト音声ビットレート */
        const val DEFAULT_AUDIO_BIT_RATE = 128_000

        /** デフォルト映像フレームレート */
        const val DEFAULT_VIDEO_FRAME_RATE = 30

        /**
         * データストアから読み出してデータクラスを返す
         *
         * @param context [context]
         */
        fun loadDataStore(context: Context): Flow<MirroringSettingData> {
            // FlowでDataStoreの変更を受け取って、データクラスに変換して返す
            return context.dataStore.data.map { data ->
                MirroringSettingData(
                    portNumber = data[SettingKeyObject.PORT_NUMBER] ?: DEFAULT_PORT_NUMBER,
                    intervalMs = data[SettingKeyObject.INTERVAL_MS] ?: DEFAULT_INTERVAL_MS,
                    videoBitRate = data[SettingKeyObject.VIDEO_BIT_RATE] ?: DEFAULT_VIDEO_BIT_RATE,
                    videoFrameRate = data[SettingKeyObject.VIDEO_FRAME_RATE] ?: DEFAULT_VIDEO_FRAME_RATE,
                    audioBitRate = data[SettingKeyObject.AUDIO_BIT_RATE] ?: DEFAULT_AUDIO_BIT_RATE,
                    isRecordInternalAudio = data[SettingKeyObject.IS_RECORD_INTERNAL_AUDIO] ?: false,
                )
            }
        }

        /**
         * [MirroringSettingData]をデータストアへ格納する
         *
         * @param context [Context]
         * @param mirroringSettingData ミラーリング情報
         */
        suspend fun setDataStore(context: Context, mirroringSettingData: MirroringSettingData) {
            context.dataStore.edit {
                it[SettingKeyObject.PORT_NUMBER] = mirroringSettingData.portNumber
                it[SettingKeyObject.INTERVAL_MS] = mirroringSettingData.intervalMs
                it[SettingKeyObject.VIDEO_BIT_RATE] = mirroringSettingData.videoBitRate
                it[SettingKeyObject.VIDEO_FRAME_RATE] = mirroringSettingData.videoFrameRate
                it[SettingKeyObject.AUDIO_BIT_RATE] = mirroringSettingData.audioBitRate
                it[SettingKeyObject.IS_RECORD_INTERNAL_AUDIO] = mirroringSettingData.isRecordInternalAudio
            }
        }

    }

}
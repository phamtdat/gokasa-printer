package cz.gokasa.gokasaprinter.support

import android.bluetooth.BluetoothClass.Device

class BtDeviceUtil() {

    companion object {
        fun getStringDeviceClass(code: Int): String {
            when (code) {
                Device.AUDIO_VIDEO_CAMCORDER -> return "AUDIO_VIDEO_CAMCORDER"
                Device.AUDIO_VIDEO_CAR_AUDIO -> return "AUDIO_VIDEO_CAR_AUDIO"
                Device.AUDIO_VIDEO_HANDSFREE -> return "AUDIO_VIDEO_HANDSFREE"
                Device.AUDIO_VIDEO_HEADPHONES -> return "AUDIO_VIDEO_HEADPHONES"
                Device.AUDIO_VIDEO_HIFI_AUDIO -> return "AUDIO_VIDEO_HIFI_AUDIO"
                Device.AUDIO_VIDEO_LOUDSPEAKER -> return "AUDIO_VIDEO_LOUDSPEAKER"
                Device.AUDIO_VIDEO_MICROPHONE -> return "AUDIO_VIDEO_MICROPHONE"
                Device.AUDIO_VIDEO_PORTABLE_AUDIO -> return "AUDIO_VIDEO_PORTABLE_AUDIO"
                Device.AUDIO_VIDEO_SET_TOP_BOX -> return "AUDIO_VIDEO_SET_TOP_BOX"
                Device.AUDIO_VIDEO_UNCATEGORIZED -> return "AUDIO_VIDEO_UNCATEGORIZED"
                Device.AUDIO_VIDEO_VCR -> return "AUDIO_VIDEO_VCR"
                Device.AUDIO_VIDEO_VIDEO_CAMERA -> return "AUDIO_VIDEO_VIDEO_CAMERA"
                Device.AUDIO_VIDEO_VIDEO_CONFERENCING -> return "AUDIO_VIDEO_VIDEO_CONFERENCING"
                Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER -> return "AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER"
                Device.AUDIO_VIDEO_VIDEO_GAMING_TOY -> return "AUDIO_VIDEO_VIDEO_GAMING_TOY"
                Device.AUDIO_VIDEO_VIDEO_MONITOR -> return "AUDIO_VIDEO_VIDEO_MONITOR"
                Device.AUDIO_VIDEO_WEARABLE_HEADSET -> return "AUDIO_VIDEO_WEARABLE_HEADSET"
                Device.COMPUTER_DESKTOP -> return "COMPUTER_DESKTOP"
                Device.COMPUTER_HANDHELD_PC_PDA -> return "COMPUTER_HANDHELD_PC_PDA"
                Device.COMPUTER_LAPTOP -> return "COMPUTER_LAPTOP"
                Device.COMPUTER_PALM_SIZE_PC_PDA -> return "COMPUTER_PALM_SIZE_PC_PDA"
                Device.COMPUTER_SERVER -> return "COMPUTER_SERVER"
                Device.COMPUTER_UNCATEGORIZED -> return "COMPUTER_UNCATEGORIZED"
                Device.COMPUTER_WEARABLE -> return "COMPUTER_WEARABLE"

                Device.PHONE_CELLULAR -> return "PHONE_CELLULAR"
                Device.PHONE_CORDLESS -> return "PHONE_CORDLESS"
                Device.PHONE_ISDN -> return "PHONE_ISDN"
                Device.PHONE_MODEM_OR_GATEWAY -> return "PHONE_MODEM_OR_GATEWAY"
                Device.PHONE_SMART -> return "PHONE_SMART"
                Device.PHONE_UNCATEGORIZED -> return "PHONE_UNCATEGORIZED"
                Device.TOY_CONTROLLER -> return "TOY_CONTROLLER"
                Device.TOY_DOLL_ACTION_FIGURE -> return "TOY_DOLL_ACTION_FIGURE"
                Device.TOY_GAME -> return "TOY_GAME"
                Device.TOY_ROBOT -> return "TOY_ROBOT"
                Device.TOY_UNCATEGORIZED -> return "TOY_UNCATEGORIZED"
                Device.TOY_VEHICLE -> return "TOY_VEHICLE"
                Device.WEARABLE_GLASSES -> return "WEARABLE_GLASSES"
                Device.WEARABLE_HELMET -> return "WEARABLE_HELMET"
                Device.WEARABLE_JACKET -> return "WEARABLE_JACKET"
                Device.WEARABLE_PAGER -> return "WEARABLE_PAGER"
                Device.WEARABLE_UNCATEGORIZED -> return "WEARABLE_UNCATEGORIZED"
                Device.WEARABLE_WRIST_WATCH -> return "WEARABLE_WRIST_WATCH"
                else -> return "OTHER"
            }
        }
    }
}
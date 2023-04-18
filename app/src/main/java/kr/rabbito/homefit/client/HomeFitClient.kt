package kr.rabbito.homefit.client

import android.graphics.Bitmap
import android.util.Log
import kr.rabbito.homefit.utils.calc.Converter
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class HomeFitClient {
    private var serverIP = "192.168.0.132"
    private var serverPort = 10001

    lateinit var socket: Socket
    lateinit var inputStream: InputStream
    lateinit var outputStream: OutputStream

    fun sendRequest() {
        socket = Socket(serverIP, serverPort)
        inputStream = socket.getInputStream()
        outputStream = socket.getOutputStream()
        Log.d("connection", "$socket")
    }

    // 임시 - 단순 텍스트 송신
    fun sendText(text: String) {
        outputStream.write(text.toByteArray())
        Log.d("connection", "send message")
    }

    fun sendUserName(name: String) {
        val message = makeMessage(1, name)
        outputStream.write(message)
    }


    fun sendImage(bitmap: Bitmap){
        val imageByteArray = Converter.bitmapToByteArray(bitmap, "jpeg")

        val message = makeMessage(2, imageByteArray)
        outputStream.write(message)
        outputStream.write(imageByteArray)

//        Log.d("image", imageByteArray.contentToString())
    }

    // 메시지 수신
    fun getData(): List<Any>? {
        val buffer = ByteArray(1024)
        var check = inputStream.read(buffer)

        // 받은 데이터 없으면 -1 반환
        while (check != -1) {
            val messageNumber = checkMessage(buffer)
            if (messageNumber == 32) {
                return parseMessage(buffer)
            }

            check = inputStream.read(buffer)
        }

        return null
    }

    fun closeSocket() {
        socket.close()
    }
}
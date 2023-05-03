package kr.rabbito.homefit.client

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kr.rabbito.homefit.utils.calc.Converter
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class HomeFitClient {
    private var serverIP = "192.168.35.69"
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
            } else if (messageNumber == 33) {
                val fileSize = buffer[3].toUInt() * 128u + buffer[4].toUInt()

                // JSON 파일 문자열로 받는 부분
                val byteArrayOutputStream = ByteArrayOutputStream()
                byteArrayOutputStream.write(buffer, 6, check - 6) // 오프셋을 6으로 변경
                var receivedBytes = 0
                receivedBytes += check - 6

                while (receivedBytes < fileSize.toInt()) {
                    val bytesRead = inputStream.read(buffer)
                    if (bytesRead == -1) break
                    byteArrayOutputStream.write(buffer, 0, bytesRead)
                    receivedBytes += bytesRead
                }

                val jsonFileString = byteArrayOutputStream.toString()
                Log.d("jsonFile", jsonFileString)
            }

            check = inputStream.read(buffer)
        }

        return null
    }

    fun closeSocket() {
        socket.close()
    }
}
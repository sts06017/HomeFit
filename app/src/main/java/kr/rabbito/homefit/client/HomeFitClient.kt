package kr.rabbito.homefit.client

import android.graphics.Bitmap
import android.util.Log
import kr.rabbito.homefit.utils.calc.Converter
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class HomeFitClient {
    private var serverIP = "192.168.35.243"   // 집 기준
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

    fun sendImage(image: Bitmap){
        val imageByteArray = Converter.bitmapToByteArray(image, "png")
        val message = makeMessage(2, imageByteArray)
        outputStream.write(message)
        outputStream.write(imageByteArray)

        Log.d("image", imageByteArray.contentToString())
    }

    // 메시지 수신
    fun getData() {
        val check = inputStream.available()

        // 받은 데이터 없으면 -1 반환
        if (check != -1) {
            val response = ByteArray(check)
            inputStream.read(response)   // inputStream 에서 데이터 받아 message 에 저장
            val message = String(response)
            Log.d("connection", "message received: $message")
        } else {
            Log.d("connection", "message not received")
        }
    }

    private fun makeMessage(messageNumber: Int, data: Any): ByteArray? {
        val start = "[".toByteArray()
        val number = messageNumber.toByte()
        val end = "]".toByteArray()

        var message: ByteArray? = null

        when(messageNumber) {
            1 -> {
                // 사용자명을 String 으로 입력 받음
                if (data is String) {
                    val size = (data.length + 4).toByte()
                    val userName = data.toByteArray()

                    message = start + number + size + userName + end
                }
            }
            2 -> {
                // 이미지를 Bitmap 으로 입력 받음
                if (data is ByteArray) {
                    val msgSize = (11).toByte()
                    val fileSize = Converter.convertToBigEndian(data.size)
                    Log.d("connection", "file size: ${data.size}")
                    val ext = "png".toByteArray()

                    message = start + number + msgSize + ext + fileSize + end
                }
            }
        }


        return message
    }

    fun closeSocket() {
        socket.close()
    }
}
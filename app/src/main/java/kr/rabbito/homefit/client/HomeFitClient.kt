package kr.rabbito.homefit.client

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import kr.rabbito.homefit.utils.calc.Converter
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.ConnectException
import java.net.Socket
import java.net.SocketException

class HomeFitClient {
    private var serverIP = "192.168.35.69"
    private var serverPort = 10001

    lateinit var socket: Socket
    lateinit var inputStream: InputStream
    lateinit var outputStream: OutputStream

    fun sendRequest() {
        try {
            socket = Socket(serverIP, serverPort)
            inputStream = socket.getInputStream()
            outputStream = socket.getOutputStream()
            Log.d("connection", "$socket")
        } catch (e: ConnectException) {
            Log.d("connection", "server not started")
        }
    }

    // 임시 - 단순 텍스트 송신
    fun sendText(text: String) {
        outputStream.write(text.toByteArray())
        Log.d("connection", "send message")
    }

    fun sendUserName(name: String) {
        val message = makeMessage(1, name)
        try {
            outputStream.write(message)
        } catch (e: UninitializedPropertyAccessException) {
            Log.d("connection", "name send failed")
        }
    }


    fun sendImage(context: Context, bitmap: Bitmap) {
        val resizedBitmap = Converter.resizeBitmap(bitmap)
        val imageByteArray = Converter.bitmapToByteArray(resizedBitmap, "jpeg")

        val message = makeMessage(2, imageByteArray)
        try {
            outputStream.write(message)
            outputStream.write(imageByteArray)
        } catch (e: UninitializedPropertyAccessException) {
            Log.d("connection", "image send failed")
        }
    }

    // 메시지 수신
    fun getData(context: Context): String? {
        try {
            val buffer = ByteArray(1024)
            var check = inputStream.read(buffer)

            // 받은 데이터 없으면 -1 반환
            while (check != -1) {
                val messageNumber = checkMessage(buffer)
                if (messageNumber == 32) {  // 임시 결과 수신
                    return parseMessage(buffer).toString()
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

                    return jsonFileString
                }

                check = inputStream.read(buffer)
            }
        } catch (e: SocketException) {
            // 양 추정 도중에 소켓이 닫힌 경우
            Log.d("connection", "socket closed")
        } catch (e: UninitializedPropertyAccessException) {
            Log.d("connection", "server not started")
        }

        return null
    }

    fun closeSocket() {
        try {
            socket.close()
        } catch (e: UninitializedPropertyAccessException) {
            Log.d("connection", "socket close failed")
        }
    }
}
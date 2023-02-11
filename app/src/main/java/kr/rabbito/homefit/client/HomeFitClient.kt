package kr.rabbito.homefit.client

import android.util.Log
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class HomeFitClient {
    private var serverIP = "192.168.0.21"   // 연구실 기준
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

    fun sendImage(){

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

    private fun makeMessage(messageNumber: Int, data: String): ByteArray {
        val start = "[".toByteArray()
        val number = messageNumber.toByte()
        val size = (data.length + 4).toByte()
        val userName = data.toByteArray()
        val end = "]".toByteArray()

        val message = start + number + size + userName + end

        return message
    }

    fun closeSocket() {
        socket.close()
    }
}
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

    // 메시지 송신
    fun sendMessage(message: String) {
        outputStream.write(message.toByteArray())
        Log.d("connection", "send message")
    }

    // 메시지 수신
    fun getMessage() {
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

    fun closeSocket() {
        socket.close()
    }
}
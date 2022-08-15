package com.example.fyp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val socket = IO.socket("http://0.0.0.0:5000")

        socket.on(Socket.EVENT_CONNECT, Emitter.Listener{
            socket.emit("messages", "hi")
        });

        socket.connect()
    }
}
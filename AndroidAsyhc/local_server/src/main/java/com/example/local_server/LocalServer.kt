package com.example.local_server

import android.content.Context
import android.content.res.AssetManager
import android.os.SystemClock
import android.util.Log
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.callback.CompletedCallback
import com.koushikdutta.async.http.WebSocket
import com.koushikdutta.async.http.server.AsyncHttpServer

class LocalServer(val context: Context) {
    val mAssets: AssetManager = context.resources.assets

    var addresses : MutableList<String> = mutableListOf()
    var sockets: MutableList<WebSocket> = mutableListOf()
    val localServerPort = 8000

fun start(){
     val server = AsyncHttpServer()


    server.get("^/.*"
    ) { request, response ->
        val line = request.path
        val start = line.indexOf('/') + 1
        val end = line.length


        val route = if (line.length>1) line.substring(start, end) else "home.html"


        val bytes: ByteArray = if (route.startsWith("audio")) {
            Utils.loadContent("audio_stats.json", mAssets)!!

        } else if (route.startsWith("video")) {
            Utils.loadContent("video_stats.json", mAssets)!!
        } else {
            Utils.loadContent(route, mAssets)!!
        }
        response.send(Utils.detectMimeType(route), bytes)


    }

    server.listen(localServerPort)

    startWebSocket()
    //address = NetworkUtils.getAddressLog(context,localServerPort)
    addresses = NetworkUtils.getAddress(localServerPort)
}

    private fun startWebSocket() {
        val httpServer = AsyncHttpServer()
        httpServer.listen(AsyncServer.getDefault(), 8001)

        httpServer.websocket(
            "/live"
        ) { webSocket, _ ->
            if(sockets.isEmpty()){
                sockets.add(webSocket)
                startBroadCasting()
            }else
                sockets.add(webSocket)

            //Use this to clean up any references to your websocket
            webSocket.closedCallback = CompletedCallback { ex ->
                try {
                    if (ex != null) Log.e("WebSocket", "An error occurred", ex)
                } finally {
                    sockets.remove(webSocket)
                }
            }
            webSocket.setStringCallback {
                webSocket.send("Welcome Client!")

            }
        }
    }

    fun broadCastToClients(data: String?){
        for (socket in sockets) socket.send(data)
    }

    fun startBroadCasting(){
        var count = 1
        while (sockets.isNotEmpty()){
            val data = if (count%2 == 0) "audio_stats.json" else "video_stats.json"
            broadCastToClients(Utils.getJsonFromAssets(mAssets,data))
            count++
            SystemClock.sleep(3000)
        }
    }

}
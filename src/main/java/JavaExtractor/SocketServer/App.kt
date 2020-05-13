package JavaExtractor.SocketServer

import java.io.DataInputStream
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

fun main() {
    val server = ServerSocket(9999)
    println("Server is running on port ${server.localPort}")
    while (true) {
        val client = server.accept()
        thread(isDaemon = true) { ClientHandler(client).serve() }
    }
}

class ClientHandler(val client: Socket) {
    fun serve() {

        client.getInputStream().bufferedReader().use { buffer ->
            val lineCount = buffer.readLine().orEmpty().toIntOrNull() ?: 0
            println("lineCount=$lineCount")
            val lines = (0..lineCount).map { buffer.readLine().orEmpty() }
            println(lines.joinToString("\n"))
        }
    }


}
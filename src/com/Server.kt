package com

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Server
{
    companion object
    {
        private val SERVER_PORT = 6666
        private val list = ArrayList<Client>()

        @JvmStatic
        fun main(args: Array<String>)
        {
            println("创建服务端")
            val serverSocket = ServerSocket(SERVER_PORT)
            val scheduledThreadPool = Executors.newScheduledThreadPool(1)
            scheduledThreadPool.scheduleAtFixedRate({
                list.forEach {
                    println("心跳" + it.id)
                    if (!sendUrgentData(it))
                    {
                        println(it.id + "连接已断开")
                        it.socket.close()
                        it.isClosed = true
                        list.remove(it)
                    }
                }
            }, 0, 5, TimeUnit.SECONDS)
            while (true)
            {
                val socket = serverSocket.accept()//同意建立连接
                if (!socket.isConnected)
                    continue
                val id = receiveID(socket) ?: continue
                val client = Client(socket, id)
                list.add(client)
                Thread(ServerThread(client, list)).start()//创建线程
            }
        }

        private fun receiveID(socket: Socket): String?
        {
            var response: String? = null
            try
            {
                val inputStream = socket.getInputStream()
                response = BufferedReader(InputStreamReader(inputStream)).readLine()
                println("连接请求id：" + response)
            }
            catch (e: IOException)
            {
                e.printStackTrace()
            }
            return response
        }

        private fun sendUrgentData(client: Client): Boolean
        {
            return try
            {
                val socket = client.socket
                val outputStream = socket.getOutputStream()
                outputStream.write(("test\n").toByteArray())
                outputStream.flush()
                true
            }
            catch (e: Exception)
            {
                false
            }
        }
    }
}
package com

import java.net.Socket

data class Client(var socket: Socket, var id: String)
{
    var isClosed = false
}
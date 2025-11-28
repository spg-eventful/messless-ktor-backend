package at.eventful.messless.errors

import at.eventful.messless.plugins.socket.WebSocketService

data class ServiceAlreadyRegistered(val service: WebSocketService) :
    Error("Service ${service.name} is already registered")
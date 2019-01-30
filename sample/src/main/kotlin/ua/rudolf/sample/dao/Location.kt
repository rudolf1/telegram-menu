package ua.rudolf.sample.dao

import org.telegram.telegrambots.api.objects.Message
import java.util.UUID

class Location(
        val id: UUID,
        var time: Long,
        var longitude: Float,
        var latitude: Float,
        var messageId: Int
) {


    companion object {
        fun new(message: Message, location: org.telegram.telegrambots.api.objects.Location): Location {
            return Location(
                    UUID.randomUUID(),
                    System.currentTimeMillis(),
                    location.latitude,
                    location.longitude,
                    message.messageId
            )
        }
    }
}
package ua.rudolf.telega.menu

import org.telegram.telegrambots.api.objects.Update

fun Update.extractMessage() = this.message ?: this.editedMessage ?: this.callbackQuery.message
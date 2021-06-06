import java.util.concurrent.Executor;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.example.unicast.AsyncSubscriber;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;

class CandleApiSubscriber extends AsyncSubscriber<StreamingEvent.Candle> {

    private final Logger logger;
    private AbsSender chatSender;
    private Long chatId;

    CandleApiSubscriber(@NotNull final Logger logger, @NotNull final Executor executor) {
        super(executor);
        this.logger = logger;
    }

    CandleApiSubscriber(@NotNull final Logger logger, @NotNull final Executor executor, AbsSender absSender, Long chatId) {
        super(executor);
        this.logger = logger;
        this.chatSender = absSender;
        this.chatId = chatId;
    }

    public AbsSender getSender() {
        return this.chatSender;
    }

    public void setSender(AbsSender sender) {
        this.chatSender = sender;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    protected boolean whenNext(final StreamingEvent.Candle event) {
        logger.info("Пришло новое событие из Streaming API\n" + event);

        if (this.chatSender == null) {
            return false;
        }
        String messageText = String.format("UPDATE\n%s - %s", event.getFigi(), event.getHighestPrice());
        sendMessage(this.chatSender, this.chatId, messageText);

        return true;
    }

    @Override
    protected void whenError(Throwable error) {
        //logger.info("Пришло новое событие из Streaming API\n");

        String messageText = String.format("ERROR\n%s\n> %s", error.getMessage(), error.getStackTrace());
        sendMessage(this.chatSender, this.chatId, messageText);
    }

    private void sendMessage(AbsSender absSender, Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            String errMsg = String.format("$s: %s", CandleApiSubscriber.class.getName(), e.getMessage());
            this.logger.severe(errMsg);
        }
    }

}
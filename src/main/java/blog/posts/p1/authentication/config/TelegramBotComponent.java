package blog.posts.p1.authentication.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBotComponent extends TelegramWebhookBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.webhook.url}")
    private String webhookUrl;

    @Value("${telegram.webapp.url}")
    private String webAppUrl;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return webhookUrl;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            log.info("Received update: {}", update);
            if (update.hasChannelPost()) {
                return handleChannelPost(update.getChannelPost());
            } else if (update.hasMessage() && update.getMessage().hasText()) {
                return handlePrivateMessage(update.getMessage());
            }
        } catch (Exception e) {
            log.error("Error processing update", e);
        }
        return null;
    }

    private BotApiMethod<?> handleChannelPost(Message channelPost) {
        log.info("Handling channel post: {}", channelPost);
        SendMessage buttonMessage = createChannelButtonMessage(channelPost.getChatId().toString(), channelPost.getMessageId());
        try {
            execute(buttonMessage);
            log.info("Button message sent after channel post");
        } catch (TelegramApiException e) {
            log.error("Failed to send button message", e);
        }
        return null;
    }

    private SendMessage createChannelButtonMessage(String chatId, Integer replyToMessageId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Access Swipe Joy App");
        message.setReplyToMessageId(replyToMessageId);
        message.setReplyMarkup(createChannelInlineKeyboardMarkup());
        return message;
    }

    private InlineKeyboardMarkup createChannelInlineKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText("Open Swipe Joy App");

        // Create a deep link URL
        String deepLink = "https://t.me/" + botUsername + "?start=webapp";
        inlineKeyboardButton.setUrl(deepLink);

        rowInline.add(inlineKeyboardButton);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    private BotApiMethod<?> handlePrivateMessage(Message message) {
        if (message.getText().startsWith("/start webapp")) {
            SendMessage webAppMessage = new SendMessage();
            webAppMessage.setChatId(message.getChatId().toString());
            webAppMessage.setText("Open Swipe Joy App");
            webAppMessage.setReplyMarkup(createWebAppInlineKeyboardMarkup());

            try {
                execute(webAppMessage);
            } catch (TelegramApiException e) {
                log.error("Failed to send web app message", e);
            }
        }
        return null;
    }

    private InlineKeyboardMarkup createWebAppInlineKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton webAppButton = new InlineKeyboardButton();
        webAppButton.setText("Open App");
        WebAppInfo webAppInfo = new WebAppInfo();
        webAppInfo.setUrl(webAppUrl);
        webAppButton.setWebApp(webAppInfo);

        rowInline.add(webAppButton);
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
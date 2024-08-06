package org.example.modules.regular_messages;

import org.example.models.UserInfo;
import org.example.services.UserInfoService;
import org.example.NetworkingBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class DailyMessageTask {

    private final UserInfoService userInfoService;
    private final DailyMessageService dailyMessageService;
    private final NetworkingBot networkingBot;
    private final Logger logger = LoggerFactory.getLogger(DailyMessageTask.class);

    @Autowired
    public DailyMessageTask(UserInfoService userInfoService, DailyMessageService dailyMessageService, NetworkingBot networkingBot) {
        this.userInfoService = userInfoService;
        this.dailyMessageService = dailyMessageService;
        this.networkingBot = networkingBot;
    }

    @Scheduled(cron = "0 0 18 * * *") // 18:00
    public void sendDailyMessage() {
        List<UserInfo> users = userInfoService.getAllUsers();
        dailyMessageService.getUnsentDailyMessage().ifPresentOrElse(dailyMessage -> {
            String text = dailyMessage.getText();
            users.forEach(user -> networkingBot.sendTextMessage(user.getUserId(), text));
            dailyMessage.setSent(true);
            dailyMessageService.saveDailyMessage(dailyMessage);
            logger.info("Daily message sent to all users.");
        }, () -> logger.info("No unsent daily messages found."));
    }
}
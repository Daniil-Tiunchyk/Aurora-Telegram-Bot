package org.example.modules.statistics;

import org.example.repositories.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@Component
public class ProfileStatisticsTask {

    private static final Logger logger = LoggerFactory.getLogger(ProfileStatisticsTask.class);

    private final UserInfoRepository userInfoRepository;
    private final ProfileStatisticsService profileStatisticsService;

    @Autowired
    public ProfileStatisticsTask(UserInfoRepository userInfoRepository, ProfileStatisticsService profileStatisticsService) {
        this.userInfoRepository = userInfoRepository;
        this.profileStatisticsService = profileStatisticsService;
    }

    @Scheduled(cron = "0 0 18 * * *") // 18:00
    public void collectProfileStatistics() {
        logger.info("Collecting detailed profile statistics.");

        long totalProfiles = userInfoRepository.count();
        long activeProfiles = userInfoRepository.countByIsVisibleTrue();
        long bannedProfiles = userInfoRepository.countByIsBannedTrue();
        long botBlockedProfiles = userInfoRepository.countByIsBotBlockedTrue();
        long eligibleProfiles = userInfoRepository.countByIsVisibleTrueAndIsBannedFalseAndIsBotBlockedFalse(); // Добавлено

        ProfileStatistics profileStatistics = new ProfileStatistics();
        profileStatistics.setDate(LocalDate.now());
        profileStatistics.setTotalProfiles(totalProfiles);
        profileStatistics.setActiveProfiles(activeProfiles);
        profileStatistics.setBannedProfiles(bannedProfiles);
        profileStatistics.setBotBlockedProfiles(botBlockedProfiles);
        profileStatistics.setEligibleProfiles(eligibleProfiles);

        profileStatisticsService.saveProfileStatistics(profileStatistics);

        logger.info("Profile statistics collected: Total = {}, Active = {}, Banned = {}, BotBlocked = {}, Eligible = {}",
                totalProfiles, activeProfiles, bannedProfiles, botBlockedProfiles, eligibleProfiles);
    }
}

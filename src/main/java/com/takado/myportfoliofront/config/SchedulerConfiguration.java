package com.takado.myportfoliofront.config;

import com.takado.myportfoliofront.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfiguration {
    private final SchedulerService schedulerService;

    @Scheduled(fixedDelay = 5000)
    public void scheduledRefresh() {
        schedulerService.refresh();
    }
}
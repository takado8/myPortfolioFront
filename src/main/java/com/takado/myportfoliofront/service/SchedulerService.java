package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.view.ScheduledRefresh;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {
    private ScheduledRefresh scheduledRefresh;

    public void setScheduledRefresh(ScheduledRefresh scheduledRefresh) {
        this.scheduledRefresh = scheduledRefresh;
    }

    public void refresh() {
        if (scheduledRefresh != null){
            scheduledRefresh.refresh();
        }
    }
}

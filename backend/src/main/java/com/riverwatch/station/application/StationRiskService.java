package com.riverwatch.station.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StationRiskService {

    private static final Logger log = LoggerFactory.getLogger(StationRiskService.class);

    public StationRisk classifyLevel(int value) {
        log.info("Level: " + value);

        if (value >= 0) {
            if (value < 300) {
                return StationRisk.NORMAL;
            } else {
                if (value < 500) {
                    return StationRisk.ATTENTION;
                } else {
                    if (value < 700) {
                        return StationRisk.ALERT;
                    } else {
                        return StationRisk.FLOOD;
                    }
                }
            }
        }

        return StationRisk.NORMAL;
    }

    public boolean isSameOrHigher(StationRisk currentRisk, StationRisk minimumRisk) {
        return currentRisk.ordinal() >= minimumRisk.ordinal();
    }

    public enum StationRisk {
        NORMAL,
        ATTENTION,
        ALERT,
        FLOOD
    }
}

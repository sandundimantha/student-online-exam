package com.exam.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {
    private static final Logger logger = LoggerFactory.getLogger("AUDIT_LOG");

    public void log(String action, String performedBy, String details) {
        logger.info("ACTION: [{}] | BY: [{}] | DETAILS: [{}]", action, performedBy, details);
    }
}

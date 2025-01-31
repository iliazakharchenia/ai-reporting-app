package com.ai.reporting.core;

import java.time.LocalDateTime;

public record ReportResponse(String objective, String report, LocalDateTime time) {
    public static ReportResponse fromContent(ReportData data, String content, LocalDateTime date) {
        return new ReportResponse(
                data.objective(),
                content,
                date
        );
    }
}

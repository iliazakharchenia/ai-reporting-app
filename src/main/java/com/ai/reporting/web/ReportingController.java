package com.ai.reporting.web;

import com.ai.reporting.core.ReportData;
import com.ai.reporting.core.ReportResponse;
import com.ai.reporting.core.multiple.MultipleDatasourceReportData;
import com.ai.reporting.service.ReportingService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "report")
public class ReportingController {
    private final ReportingService reportingService;

    @PutMapping(path = "message")
    public Mono<ReportResponse> reportSingle(@RequestBody ReportData reportData) {
        return reportingService.reportSingle(reportData);
    }

    @PutMapping(path = "messages")
    public Mono<ReportResponse> reportMultiple(@RequestBody MultipleDatasourceReportData reportData) {
        return reportingService.reportMultipleQueue(reportData);
    }

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }
}

package com.ai.reporting.web.reporting;

import com.ai.reporting.core.ReportData;
import com.ai.reporting.core.ReportResponse;
import com.ai.reporting.core.multiple.MultipleDatasourceReportData;
import com.ai.reporting.service.AuthenticationService;
import com.ai.reporting.service.ReportingService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "report")
public class ReportingController {
    private final ReportingService reportingService;
    private final AuthenticationService authenticationService;

    @PutMapping(path = "message")
    public Mono<ReportResponse> reportSingle(@RequestBody ReportData reportData,
                                             @RequestHeader(name = "username") String username,
                                             @RequestHeader(name = "password") String password) {
        return authenticationService.verify(username, password)
                .then(reportingService.reportSingle(reportData));
    }

    @PutMapping(path = "messages")
    public Mono<ReportResponse> reportMultiple(@RequestBody MultipleDatasourceReportData reportData,
                                               @RequestHeader(name = "username") String username,
                                               @RequestHeader(name = "password") String password) {
        return authenticationService.verify(username, password)
                .then(reportingService.reportMultipleQueue(reportData));
    }

    public ReportingController(ReportingService reportingService,
                               AuthenticationService authenticationService) {
        this.reportingService = reportingService;
        this.authenticationService = authenticationService;
    }
}

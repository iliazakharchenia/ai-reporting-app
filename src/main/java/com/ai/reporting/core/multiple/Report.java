package com.ai.reporting.core.multiple;

import com.ai.reporting.core.ReportResponse;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

public class Report {
    private final UUID id;
    private final String objective;
    private final List<String> dataList;

    public static Report prepare(String objective, List<String> responseDataParts) {
        return new Report(objective, responseDataParts);
    }

    public UUID getId() {
        return id;
    }

    public String getObjective() {
        return objective;
    }

    public StringBuilder prepareResponsePrompt() {
        int i = 1;
        StringBuilder sb = new StringBuilder("Having the multiple reports such as: ");
        for (String data : this.dataList) {
            sb.append(i).append(')').append(' ').append(data).append(',').append(' ');
            i++;
        }
        sb.append('.').append(' ');
        sb.append("All that reports are made with respect of objective '");
        sb.append(this.objective);
        sb.append("'. Combine them into the one single report following the objective they made.");

        return sb;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Report report)) return false;
        return Objects.equals(id, report.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    private Report(String objective, List<String> dataParts) {
        this.id = UUID.randomUUID();
        this.objective = objective;
        this.dataList = dataParts;
    }
}

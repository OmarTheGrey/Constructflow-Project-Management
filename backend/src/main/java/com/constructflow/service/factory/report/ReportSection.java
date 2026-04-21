package com.constructflow.service.factory.report;

public interface ReportSection {
    String title();
    Object render(ReportContext ctx);
}

package com.constructflow.service.factory.report;

import java.util.List;

public interface ReportArtifactFactory {
    ReportKind kind();
    String header(ReportContext ctx);
    List<ReportSection> sections();
}

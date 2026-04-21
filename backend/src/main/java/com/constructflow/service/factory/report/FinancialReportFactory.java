package com.constructflow.service.factory.report;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FinancialReportFactory implements ReportArtifactFactory {

    @Override
    public ReportKind kind() { return ReportKind.FINANCIAL; }

    @Override
    public String header(ReportContext ctx) {
        return "Financial Report — ConstructFlow";
    }

    @Override
    public List<ReportSection> sections() {
        return List.of(
            new ReportSection() {
                public String title() { return "Budget vs Actual"; }
                public Object render(ReportContext ctx) {
                    double budget = ctx.get("totalBudget") instanceof Number n ? n.doubleValue() : 0.0;
                    double actual = ctx.get("totalActualCost") instanceof Number n ? n.doubleValue() : 0.0;
                    return Map.of(
                        "totalBudget",     budget,
                        "totalActualCost", actual,
                        "variance",        budget - actual
                    );
                }
            },
            new ReportSection() {
                public String title() { return "Cost Efficiency"; }
                public Object render(ReportContext ctx) {
                    double budget = ctx.get("totalBudget") instanceof Number n ? n.doubleValue() : 0.0;
                    double actual = ctx.get("totalActualCost") instanceof Number n ? n.doubleValue() : 0.0;
                    double pct = budget > 0 ? (actual / budget) * 100 : 0.0;
                    return Map.of("costUtilisationPct", Math.round(pct));
                }
            }
        );
    }
}

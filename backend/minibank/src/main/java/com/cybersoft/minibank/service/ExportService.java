package com.cybersoft.minibank.service;

import com.cybersoft.minibank.strategy.ExportStrategy;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface ExportService {
    void registerStrategies(List<ExportStrategy> strategyList);
    void executeExport(String format, HttpServletResponse response);
}

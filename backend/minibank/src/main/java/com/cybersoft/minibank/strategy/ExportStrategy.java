package com.cybersoft.minibank.strategy;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface ExportStrategy {
    // Logic chính để tạo file
    void export(List<?> data, HttpServletResponse response) throws IOException;

    // Để Factory biết đây là loại file nào (excel/pdf)
    String getFormat();
}

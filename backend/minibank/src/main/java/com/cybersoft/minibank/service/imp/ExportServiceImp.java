package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.TransactionDTO;
import com.cybersoft.minibank.entity.TransactionEntity;
import com.cybersoft.minibank.repository.TransactionRepository;
import com.cybersoft.minibank.service.ExportService;
import com.cybersoft.minibank.strategy.ExportStrategy;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExportServiceImp implements ExportService {
    @Autowired
    private TransactionRepository transactionRepository;

    // Map này lưu trữ: Key là "pdf"/"excel", Value là Class xử lý tương ứng
    private final Map<String, ExportStrategy> strategies = new HashMap<>();

    // Spring sẽ tự tìm tất cả các Class có @Component mà implement ExportStrategy và bỏ vào strategyList.
    @Autowired
    @Override
    public void registerStrategies(List<ExportStrategy> strategyList) {
        // Duyệt danh sách, bỏ vào Map để sau này lấy ra cho nhanh bằng Key
        strategyList.forEach(s -> strategies.put(s.getFormat(), s));
    }

    @Override
    public void executeExport(String format, HttpServletResponse response) {
        List<TransactionEntity> transactionList = transactionRepository.findAll();
        ExportStrategy strategy = strategies.get(format.toLowerCase());

        if (strategy == null) throw new IllegalArgumentException("Invalid format!");

        response.setContentType("application/octet-stream");
        String fileName = "data." + (format.equals("excel") ? "xlsx" : "pdf");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try {
            strategy.export(transactionList, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

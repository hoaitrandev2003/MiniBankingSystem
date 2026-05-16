package com.cybersoft.minibank.strategy;

import com.cybersoft.minibank.entity.TransactionEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class ExcelExportStrategy implements ExportStrategy {


    @Override
    public String getFormat() {
        return "excel";
    }

    @Override
    public void export(List<?> data, HttpServletResponse response) throws IOException {
        // 1. Tạo một file Excel mới trong bộ nhớ
        Workbook workbook = new XSSFWorkbook();

        // 2. Tạo một trang (Sheet) có tên là "Transactions"
        Sheet sheet = workbook.createSheet("Transactions");

        // 3. Tạo dòng đầu tiên (Header - chỉ số 0) và điền tên cột
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Date");
        header.createCell(1).setCellValue("Description");
        header.createCell(2).setCellValue("Status");
        header.createCell(3).setCellValue("Amount");

        // 4. Duyệt danh sách data và đổ vào từng dòng (Row) tiếp theo
        int rowIdx = 1;
        for (Object obj : data) {
            TransactionEntity transactionEntity = (TransactionEntity) obj; // Ép kiểu từ Object chung về Product cụ thể
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(transactionEntity.getCreatedAt());
            row.createCell(1).setCellValue(transactionEntity.getDescription());
            row.createCell(2).setCellValue(transactionEntity.getStatus());
            row.createCell(3).setCellValue(transactionEntity.getAmount());
        }

        // 5. Ghi dữ liệu từ bộ nhớ vào luồng xuất (OutputStream) của Response để tải về
        workbook.write(response.getOutputStream());

        // 6. Đóng file để giải phóng tài nguyên hệ thống
        workbook.close();
    }
}

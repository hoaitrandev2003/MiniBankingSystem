package com.cybersoft.minibank.strategy;

import com.cybersoft.minibank.entity.TransactionEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
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
        // Tạo một file Excel mới trong bộ nhớ
        Workbook workbook = new XSSFWorkbook();

        // Tạo một trang (Sheet) có tên là "Transactions"
        Sheet sheet = workbook.createSheet("Transactions");

        // Tạo dòng đầu tiên (Header - chỉ số 0) và điền tên cột
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Date");
        header.createCell(1).setCellValue("Description");
        header.createCell(2).setCellValue("Status");
        header.createCell(3).setCellValue("Amount");

        // Duyệt danh sách data và đổ vào từng dòng (Row) tiếp theo
        int rowIdx = 1;

        // Tạo định dạng số có dấu phẩy phân cách hàng nghìn và hiển thị số thập phân nếu có
        CellStyle decimalStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        decimalStyle.setDataFormat(format.getFormat("#,##0.00"));

        CellStyle dateStyle = workbook.createCellStyle();
        DataFormat dateFormat = workbook.createDataFormat();
        dateStyle.setDataFormat(dateFormat.getFormat("dd-mm-yyyy"));

        for (Object obj : data) {
            TransactionEntity transactionEntity = (TransactionEntity) obj; // Ép kiểu từ Object
            Row row = sheet.createRow(rowIdx++);

            Cell dateCell = row.createCell(0);
            if (transactionEntity.getCreatedAt() != null) {
                // Ghi trực tiếp LocalDateTime/Date vào cell
                dateCell.setCellValue(transactionEntity.getCreatedAt());
            }
            dateCell.setCellStyle(dateStyle);

            row.createCell(1).setCellValue(transactionEntity.getDescription());
            row.createCell(2).setCellValue(transactionEntity.getStatus());

            Cell amountCell = row.createCell(3);
            if (transactionEntity.getAmount() != null) {
                // Chuyển giá trị BigDecimal về double
                amountCell.setCellValue(transactionEntity.getAmount().doubleValue());
            } else {
                amountCell.setCellValue(0.0);
            }
            // Áp dụng định dạng
            amountCell.setCellStyle(decimalStyle);
        }

        // Tự động co giãn độ rộng cột Date
        sheet.autoSizeColumn(0);

        // Ghi dữ liệu từ bộ nhớ vào luồng xuất (OutputStream) của Response để tải về
        workbook.write(response.getOutputStream());

        // Đóng file để giải phóng tài nguyên hệ thống
        workbook.close();
    }
}

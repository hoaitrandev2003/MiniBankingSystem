package com.cybersoft.minibank.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Lưu trữ thông tin về mail như người nhận, chủ đề, nội dung thư và tệp đính kèm.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetailsEntity {
    private String recipient;
    private String msgBody;
    private String subject;
    private String attachment;
}

$(document).ready(function() {
    var currentTransactionCode = '';

    $('#btn-transfer').on('click', function(e) {
        e.preventDefault();

        var token = localStorage.getItem('access_token');
        if (!token) {
            alert("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại!");
            return;
        }

        //Lấy số tài khoản gửi
        var transferData = {
            fromAccountNumber: $('#tf-from-account').val().trim(),
            toAccountNumber: $('#tf-to-account').val().trim(),
            amount: parseFloat($('#m-send').val()),
            description: $('#textmsde').val()
        }

        // Validate dữ liệu cơ bản
        if (!transferData.fromAccountNumber || !transferData.toAccountNumber || isNaN(transferData.amount) || transferData.amount <= 0) {
            alert("Vui lòng điền đầy đủ thông tin tài khoản và số tiền hợp lệ!");
            return;
        }

        $.ajax({
            method: "POST",
            url: "http://localhost:8080/api/accounts/transfer",
            contentType: "application/json",
            headers: { 'Authorization': 'Bearer ' + token },
            data: JSON.stringify(transferData)
        })
        .done(function(response) {
            currentTransactionCode = response.transactionCode;
            alert(response.message);
            
            // Mở modal xác thực OTP
            $('#otp-modal').modal('show');
        })
        .fail(function(xhr) {
            alert("Khởi tạo giao dịch thất bại: " + xhr.responseText);
        });
    });

    $('#btn-confirm-transfer').on('click', function(e) {
        e.preventDefault();

        var token = localStorage.getItem('access_token');
        var otpCode = $('#otp-input').val().trim();

        if(!otpCode || otpCode.length !== 6) {
            alert("Vui lòng nhập đúng mã OTP gồm 6 chữ số!")
            return;
        }

        var confirmData = {
            transactionCode: currentTransactionCode,
            otp: otpCode
        };

        var $btn = $(this);
        $btn.prop('disabled', true).text('Đang xác thực...');

        $.ajax({
            method: "POST",
            url: "http://localhost:8080/api/accounts/transfer/confirm",
            contentType: "application/json",
            headers: { 
                'Authorization': 'Bearer ' + token 
            },
            data: JSON.stringify(confirmData)
        })
        .done(function(response) {
            alert("Success: " + response);

            $('#otp-modal').modal('hide');
            $('#otp-input').val('');
            $('#transfer-form')[0].reset();
        })
        .fail(function(xhr) {
            // Thất bại (Sai OTP hoặc hết hạn): Hiện thông báo lỗi từ Backend trả về
            alert("Lỗi xác thực: " + xhr.responseText);
        })
        .always(function() {
            // Trả lại trạng thái bình thường cho nút bấm sau khi API chạy xong
            $btn.prop('disabled', false).text('Xác nhận giao dịch');
        });
    });

    $('#btn-close-modal').on('click', function() {
        $('#otp-modal').modal('hide'); // Đóng modal
        $('#otp-input').val('');       // Xóa chữ đã gõ
    });
})
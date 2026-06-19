$(document).ready(function() {
    
    loadTransactionLog();

    $('#pdf-btn').on('click', function(e) {
        e.preventDefault();
        exportFile('pdf');
    });

    $('#excel-btn').on('click', function(e) {
        e.preventDefault();
        console.log("Đã kích hoạt bấm nút Excel thành công!");
        exportFile('excel');
    });

    function loadTransactionLog() {
        var token = localStorage.getItem('access_token');

        if (!token) {
            alert("Không tìm thấy token. Vui lòng đăng nhập lại!");
            return;
        }

        $.ajax({
            method: "GET",
            url: "http://localhost:8080/api/transactions",
            headers: {
                'Authorization': 'Bearer ' + token
            }
        })
        .done(function(result) {
            console.log(result);
            var html = '';

            var list = result;

            if (list && list.length > 0) {
                for(var i=0; i < list.length; i++) {
                    var item = list[i];

                    html += `<tr>
                                <td>${item.createdAt || ''}</td>
                                <td>${item.description || ''}</td>
                                <td>${item.status || ''}</td>
                                <td>${item.amount || 0}</td>
                            </tr>`;
                }
            } else {
                html = '<tr><td colspan="4" style="text-align: center; vertical-align: middle;">Không có dữ liệu giao dịch</td></tr>';
            }

            $('#transaction-data').html(html)
        })
    }

    function exportFile(format) {
        var token = localStorage.getItem('access_token');

        if (!token) {
            alert("Không tìm thấy token. Vui lòng đăng nhập lại!");
            return;
        }

        var url = 'http://localhost:8080/api/transactions/export/' + format;

        var xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        xhr.responseType = 'blob';

        xhr.setRequestHeader('Authorization', 'Bearer ' + token);

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) { // Khi request đã hoàn thành xong
                if (xhr.status === 200) {
                    // Hứng mảng byte từ Backend trả về và tạo đường dẫn ảo để tải file
                    var blob = this.response;
                    var url = window. URL.createObjectURL(blob);

                    // Giả lập thẻ <a> để trình duyệt tự kích hoạt tải file
                    var a = document.createElement('a');
                    a.href = url;

                    // Đặt tên đuôi file theo định dạng chuẩn
                    var extension = (format === 'excel') ? 'xlsx' : 'pdf';
                    a.download = 'transaction_log_' + new Date().getTime() + '.' + extension;

                    document.body.appendChild(a);
                    a.click();

                    // Xóa thẻ ảo sau khi tải xong
                    window.URL.revokeObjectURL(url);
                    document.body.removeChild(a);
                }   else if (xhr.status !== 0) { 
                    alert("Xuất file thất bại! Lỗi " + xhr.status);
                }
            }
        };

        xhr.send();
    }
})
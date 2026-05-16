$(document).ready(function() {
    
    loadTransactionLog();

    function loadTransactionLog() {
        var token = localStorage.getItem('access_token');

        if (!token) {
            console.error("Không tìm thấy token. Vui lòng đăng nhập lại.");
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
})
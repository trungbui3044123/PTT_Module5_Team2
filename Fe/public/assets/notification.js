function getToken() {
    return sessionStorage.getItem("token");
}
async function loadNotifications() {
    try {
        const response = await fetch(
            "http://localhost:8080/api/user/notifications?page=0&size=10",
            {
                headers: {
                    "Authorization": "Bearer " + getToken()
                }
            }
        );

        if (!response.ok) return;

        const result = await response.json();
        const data = result.data.content; // vì bạn dùng Pageable

        const list = document.getElementById("notificationList");
        const badge = document.getElementById("notificationBadge");

        list.innerHTML = `
            <li class="dropdown-header fw-bold d-flex justify-content-between align-items-center">
                Thông báo
                <button class="btn btn-sm btn-link p-0" onclick="markAllAsRead()">
                    Đánh dấu tất cả
                </button>
            </li>
            <li><hr class="dropdown-divider"></li>
        `;

        if (!data || data.length === 0) {
            list.innerHTML += `
                <li class="text-center text-muted py-2">
                    Không có thông báo
                </li>`;
            badge.style.display = "none";
            return;
        }

        let unreadCount = 0;

        data.forEach(noti => {
            if (!noti.isRead) unreadCount++;

            list.innerHTML += `
                <li>
                    <a href="#"
                       class="dropdown-item small ${noti.isRead ? '' : 'fw-bold'}"
                       onclick="handleNotificationClick(${noti.id}, '${noti.orderId}')">
                        ${noti.title}
                        <br/>
                        <span class="text-muted">${noti.content}</span>
                    </a>
                </li>
            `;
        });
        if (unreadCount > 0) {
            badge.innerText = unreadCount;
            badge.style.display = "inline";
        } else {
            badge.style.display = "none";
        }

    } catch (error) {
        console.error("Lỗi load notification:", error);
    }
}

async function handleNotificationClick(notificationId, orderId) {
    try {
        const response = await fetch(
            `http://localhost:8080/api/user/notifications/${notificationId}/read`,
            {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + getToken()
                }
            }
        );

        if (response.ok) {
            // Xóa badge ngay lập tức
            updateBadgeCount(-1);

            // Điều hướng sang order
            window.location.href = `orderManagement.html?id=${orderId}`;
        }

    } catch (error) {
        console.error("Lỗi handleNotificationClick:", error);
    }
}

async function cancelOrder(orderId) {

    const confirmCancel = confirm("Bạn có chắc muốn hủy đơn hàng #" + orderId + " không?");

    if (!confirmCancel) return;

    try {

        const response = await fetch(
            `http://localhost:8080/api/orders/${orderId}/cancel`,
            {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + getToken()
                }
            }
        );

        if (!response.ok) {
            const error = await response.text();
            alert("Hủy đơn thất bại: " + error);
            return;
        }

        alert("Hủy đơn thành công");

        // reload lại trang hoặc danh sách order
        window.location.reload();

    } catch (error) {
        console.error("Lỗi cancel order:", error);
        alert("Có lỗi xảy ra khi hủy đơn");
    }
}

function updateBadgeCount(change) {
    const badge = document.getElementById("notificationBadge");

    if (!badge.innerText) return;

    let current = parseInt(badge.innerText);

    current += change;

    if (current <= 0) {
        badge.style.display = "none";
    } else {
        badge.innerText = current;
    }
}

async function markAllAsRead() {
    try {
        await fetch(
            "http://localhost:8080/api/user/notifications/read-all",
            {
                method: "PUT",
                headers: {
                    "Authorization": "Bearer " + getToken()
                }
            }
        );

        document.getElementById("notificationBadge").style.display = "none";

        loadNotifications();

    } catch (error) {
        console.error("Lỗi markAllAsRead:", error);
    }
}

loadNotifications();
(function () {
    const tbody = document.getElementById('guests-tbody');
    const statTotal = document.getElementById('stat-total');
    const statAttending = document.getElementById('stat-attending');
    const statNotAttending = document.getElementById('stat-not-attending');
    const btnRefresh = document.getElementById('btn-refresh');

    function formatDate(isoString) {
        if (!isoString) return '—';
        const d = new Date(isoString);
        return d.toLocaleString('ru-RU', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function renderTable(guests) {
        if (!guests || guests.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="loading">Нет данных</td></tr>';
            return;
        }

        tbody.innerHTML = guests.map(function (guest, index) {
            const attending = guest.attending;
            const badgeClass = attending ? 'badge-yes' : 'badge-no';
            const badgeText = attending ? 'Придёт' : 'Не придёт';
            const comment = guest.comment ? guest.comment.trim() : '—';
            const date = formatDate(guest.createdAt);
            const id = guest.id;

            return (
                '<tr>' +
                '<td>' + (index + 1) + '</td>' +
                '<td>' + escapeHtml(guest.name) + '</td>' +
                '<td><span class="' + badgeClass + '">' + badgeText + '</span></td>' +
                '<td class="comment-cell">' + escapeHtml(comment) + '</td>' +
                '<td class="date-cell">' + date + '</td>' +
                '<td class="actions-cell"><button type="button" class="btn-delete" data-id="' + id + '" title="Удалить">Удалить</button></td>' +
                '</tr>'
            );
        }).join('');

        tbody.querySelectorAll('.btn-delete').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var guestId = this.getAttribute('data-id');
                if (guestId && confirm('Удалить гостя из списка?')) {
                    deleteGuest(guestId);
                }
            });
        });
    }

    function deleteGuest(id) {
        fetch('/api/guests/' + encodeURIComponent(id), {
            method: 'DELETE'
        })
            .then(function (response) {
                if (!response.ok) throw new Error('Ошибка удаления');
                loadGuests();
            })
            .catch(function () {
                alert('Не удалось удалить гостя. Попробуйте ещё раз.');
            });
    }

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    function updateStats(guests) {
        const total = guests ? guests.length : 0;
        const attending = guests ? guests.filter(function (g) { return g.attending; }).length : 0;
        const notAttending = total - attending;

        statTotal.textContent = total;
        statAttending.textContent = attending;
        statNotAttending.textContent = notAttending;
    }

    function setLoading(loading) {
        if (loading) {
            tbody.innerHTML = '<tr><td colspan="6" class="loading">Загрузка...</td></tr>';
        }
    }

    function loadGuests() {
        setLoading(true);

        fetch('/api/guests')
            .then(function (response) {
                if (!response.ok) throw new Error('Ошибка загрузки');
                return response.json();
            })
            .then(function (data) {
                updateStats(data);
                renderTable(data);
            })
            .catch(function () {
                tbody.innerHTML = '<tr><td colspan="6" class="loading">Не удалось загрузить данные. Проверьте, что бэкенд запущен.</td></tr>';
                updateStats([]);
            });
    }

    if (btnRefresh) {
        btnRefresh.addEventListener('click', loadGuests);
    }

    loadGuests();
})();

(function () {
    const FAVORITES_KEY = 'grap.realEstate.favorites.v1';
    const RECENT_KEY = 'grap.realEstate.recent.v1';
    const FAVORITE_LIMIT = 50;
    const RECENT_LIMIT = 20;

    function readItems(key) {
        try {
            const parsed = JSON.parse(window.localStorage.getItem(key) || '[]');
            return Array.isArray(parsed) ? parsed : [];
        } catch (error) {
            return [];
        }
    }

    function writeItems(key, items) {
        window.localStorage.setItem(key, JSON.stringify(items));
    }

    function normalizeRecord(record) {
        return {
            id: String(record.id || ''),
            displayName: record.displayName || '',
            address: record.address || '',
            transactionLabel: record.transactionLabel || '',
            formattedDisplayAmount: record.formattedDisplayAmount || '',
            dealYearMonth: record.dealYearMonth || '',
            savedAt: record.savedAt || new Date().toISOString()
        };
    }

    function recordFromDataset(dataset, prefix) {
        return normalizeRecord({
            id: dataset[prefix + 'Id'],
            displayName: dataset[prefix + 'Name'],
            address: dataset[prefix + 'Address'],
            transactionLabel: dataset[prefix + 'TransactionLabel'],
            formattedDisplayAmount: dataset[prefix + 'AmountLabel'],
            dealYearMonth: dataset[prefix + 'DealMonth'],
            savedAt: new Date().toISOString()
        });
    }

    function upsertItem(key, record, limit) {
        const normalized = normalizeRecord(record);
        if (!normalized.id) {
            return;
        }
        const nextItems = readItems(key)
            .filter((item) => String(item.id) !== normalized.id);
        nextItems.unshift(normalized);
        writeItems(key, nextItems.slice(0, limit));
    }

    function removeItem(key, id) {
        writeItems(key, readItems(key).filter((item) => String(item.id) !== String(id)));
    }

    function hasFavorite(id) {
        return readItems(FAVORITES_KEY).some((item) => String(item.id) === String(id));
    }

    function toggleFavorite(record) {
        if (hasFavorite(record.id)) {
            removeItem(FAVORITES_KEY, record.id);
        } else {
            upsertItem(FAVORITES_KEY, record, FAVORITE_LIMIT);
        }
        refreshFavoriteButtons();
        renderStorageLists();
    }

    function refreshFavoriteButtons() {
        document.querySelectorAll('[data-real-estate-favorite-button]').forEach((button) => {
            const record = recordFromDataset(button.dataset, 'property');
            const active = hasFavorite(record.id);
            button.textContent = active ? '관심 해제' : '관심 등록';
            button.classList.toggle('is-active', active);
        });
    }

    function createStorageItem(item, key) {
        const wrapper = document.createElement('div');
        wrapper.className = 'real-estate-storage-item';

        const link = document.createElement('a');
        link.href = '/grap/user/content/real-estate/' + encodeURIComponent(item.id);
        link.className = 'real-estate-storage-link';

        const title = document.createElement('strong');
        title.textContent = item.displayName || '부동산 거래';
        const meta = document.createElement('span');
        meta.textContent = [item.transactionLabel, item.formattedDisplayAmount, item.address]
            .filter(Boolean)
            .join(' · ');

        link.appendChild(title);
        link.appendChild(meta);

        const removeButton = document.createElement('button');
        removeButton.type = 'button';
        removeButton.className = 'real-estate-storage-remove';
        removeButton.textContent = '삭제';
        removeButton.addEventListener('click', () => {
            removeItem(key, item.id);
            refreshFavoriteButtons();
            renderStorageLists();
        });

        wrapper.appendChild(link);
        wrapper.appendChild(removeButton);
        return wrapper;
    }

    function renderStorageLists() {
        document.querySelectorAll('[data-real-estate-storage-list]').forEach((container) => {
            const type = container.dataset.realEstateStorageList;
            const key = type === 'favorites' ? FAVORITES_KEY : RECENT_KEY;
            const items = readItems(key);
            container.replaceChildren();

            if (!items.length) {
                const empty = document.createElement('p');
                empty.className = 'real-estate-storage-empty';
                empty.textContent = container.dataset.emptyMessage || '저장된 거래가 없습니다.';
                container.appendChild(empty);
                return;
            }

            items.forEach((item) => {
                container.appendChild(createStorageItem(item, key));
            });
        });
    }

    function initFavoriteButtons() {
        document.querySelectorAll('[data-real-estate-favorite-button]').forEach((button) => {
            button.addEventListener('click', () => {
                toggleFavorite(recordFromDataset(button.dataset, 'property'));
            });
        });
        refreshFavoriteButtons();
    }

    function initRecentTransaction() {
        const current = document.querySelector('[data-real-estate-current-id]');
        if (!current) {
            return;
        }
        upsertItem(RECENT_KEY, recordFromDataset(current.dataset, 'realEstateCurrent'), RECENT_LIMIT);
    }

    document.addEventListener('DOMContentLoaded', () => {
        initRecentTransaction();
        initFavoriteButtons();
        renderStorageLists();
    });
}());

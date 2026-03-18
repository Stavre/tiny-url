
    (function () {
        // Utility: trim and ignore empty
        function normalizeTag(t) {
            return t ? t.trim() : '';
        }

        // Parse initial tags from hidden input (comma-separated)
        function getInitialTags() {
            const raw = document.getElementById('tagsHidden').value || '';
            if (!raw) return [];
            return raw.split(',').map(t => normalizeTag(t)).filter(Boolean);
        }

        // Render chips
        function renderChips() {
            const chipsContainer = document.getElementById('tagChips');
            chipsContainer.innerHTML = '';
            tags.forEach((tag, idx) => {
                const span = document.createElement('span');
                span.className = 'tag-badge';
                span.style.cssText = 'display:inline-flex; align-items:center; gap:0.4rem; padding:0.25rem 0.5rem; border-radius:12px; background:#eef; font-size:0.85rem;';
                span.textContent = tag;

                const removeBtn = document.createElement('button');
                removeBtn.type = 'button';
                removeBtn.textContent = '✕';
                removeBtn.style.cssText = 'background:transparent; border:none; cursor:pointer; margin-left:0.4rem; font-size:0.85rem;';
                removeBtn.onclick = function () {
                    removeTag(idx);
                };

                span.appendChild(removeBtn);
                chipsContainer.appendChild(span);
            });
            updateHiddenTags();
        }

        // Add tag
        function addTag(tag) {
            const t = normalizeTag(tag);
            if (!t) return;
            // avoid duplicates (case-insensitive)
            const exists = tags.some(existing => existing.toLowerCase() === t.toLowerCase());
            if (!exists) {
                tags.push(t);
                renderChips();
            }
        }

        // Remove tag by index
        function removeTag(index) {
            tags.splice(index, 1);
            renderChips();
        }

        // Update hidden input value
        function updateHiddenTags() {
            document.getElementById('tagsHidden').value = tags.join(',');
        }

        // Add tag from visible input
        window.addTagFromInput = function () {
            const input = document.getElementById('tagInput');
            addTag(input.value);
            input.value = '';
            input.focus();
        };

        // Handle Enter key in tag input
        window.onTagInputKeydown = function (e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                addTagFromInput();
            }
        };

        // Expose removeTag for inline use (already used above)
        window.removeTag = removeTag;

        // Initialize tags array and render
        const tags = getInitialTags();
        document.addEventListener('DOMContentLoaded', function () {
            renderChips();
        });

        // Ensure tags are merged before form submit (in case validateForm returns true)
        const originalValidateForm = window.validateForm;
        window.validateForm = function () {
            updateHiddenTags();
            if (typeof originalValidateForm === 'function') {
                return originalValidateForm();
            }
            return true;
        };

        // Expose helper for external scripts if needed
        window._tagEditor = {
            addTag: addTag,
            removeTag: removeTag,
            getTags: () => tags.slice()
        };
    })();

    function validateDates() {
        const validFrom = document.getElementById('validFrom').value;
        const validUntil = document.getElementById('validUntil').value;
        const dateError = document.getElementById('dateError');
        const dateWarning = document.getElementById('dateWarning');
        const dateWarningMessage = document.getElementById('dateWarningMessage');
        const submitBtn = document.getElementById('submitBtn');

        // Reset
        dateError.style.display = 'none';
        dateWarning.style.display = 'none';
        submitBtn.disabled = false;

        // If both are empty, it's valid (permanent link)
        if (!validFrom && !validUntil) {
            return true;
        }

        // If one is filled but not both
        if ((validFrom && !validUntil) || (!validFrom && validUntil)) {
            dateError.style.display = 'block';
            dateError.innerHTML = '❌ Both Valid From and Valid Until must be filled together';
            submitBtn.disabled = true;
            return false;
        }

        const fromDate = new Date(validFrom);
        const untilDate = new Date(validUntil);
        const now = new Date();

        // Check if dates are valid
        if (isNaN(fromDate.getTime()) || isNaN(untilDate.getTime())) {
            dateError.style.display = 'block';
            dateError.innerHTML = '❌ Please enter valid dates';
            submitBtn.disabled = true;
            return false;
        }

        // Check if from date is before until date
        if (fromDate >= untilDate) {
            dateError.style.display = 'block';
            dateError.innerHTML = '❌ Valid From must be before Valid Until';
            submitBtn.disabled = true;
            return false;
        }

        // Check if until date is in the past
        if (untilDate < now) {
            dateError.style.display = 'block';
            dateError.innerHTML = '❌ Valid Until cannot be in the past';
            submitBtn.disabled = true;
            return false;
        }

        // Warning if from date is in the past
        if (fromDate < now) {
            dateWarning.style.display = 'block';
            dateWarningMessage.innerHTML = '⚠️ Valid From is in the past. The link will be active immediately.';
        }

        return true;
    }

    function setDateRange(value, unit) {
        const now = new Date();
        const fromDate = new Date(now);
        const untilDate = new Date(now);

        if (unit === 'days') {
            untilDate.setDate(now.getDate() + value);
        } else if (unit === 'year') {
            untilDate.setFullYear(now.getFullYear() + value);
        }

        document.getElementById('validFrom').value = formatDateForInput(fromDate);
        document.getElementById('validUntil').value = formatDateForInput(untilDate);

        validateDates();
    }

    function clearDates() {
        document.getElementById('validFrom').value = '';
        document.getElementById('validUntil').value = '';
        validateDates();
    }

    function formatDateForInput(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${year}-${month}-${day}T${hours}:${minutes}`;
    }

    function validateForm() {
        return validateDates();
    }

    function confirmDelete(linkId) {
        if (confirm('Are you sure you want to delete this link? This action cannot be undone.')) {
            window.location.href = '/links/delete/' + linkId;
        }
    }

    function validateUrl(input) {
        const url = input.value;
        const urlError = document.getElementById('urlError');
        const urlWarning = document.getElementById('urlWarning');

        // Basic URL validation
        const pattern = /^(http|https):\/\/[^ "]+$/;

        if (url && !pattern.test(url)) {
            input.classList.add('is-invalid');
            urlError.style.display = 'block';
            urlError.innerHTML = '❌ URL must start with http:// or https://';
            return false;
        } else if (url) {
            input.classList.remove('is-invalid');
            urlError.style.display = 'none';

            // Check for common URL issues
            if (url.includes('bit.ly') || url.includes('tinyurl.com') || url.includes('goo.gl')) {
                urlWarning.innerHTML = '⚠️ This appears to be a shortened URL already';
            } else {
                urlWarning.innerHTML = '';
            }
            return true;
        } else {
            input.classList.remove('is-invalid');
            urlError.style.display = 'none';
            urlWarning.innerHTML = '';
            return false;
        }
    }

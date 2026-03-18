document.addEventListener('DOMContentLoaded', function () {
  const checkbox = document.getElementById('enableDates');
  const dateFields = document.getElementById('dateFields');
  const form = document.getElementById('linkForm');

  // Restore visibility if server returned values
  if (document.getElementById('validFrom').value || document.getElementById('validUntil').value) {
    checkbox.checked = true;
    dateFields.classList.add('visible');
    dateFields.setAttribute('aria-hidden', 'false');
  }

  checkbox.addEventListener('change', () => {
    const visible = checkbox.checked;
    dateFields.classList.toggle('visible', visible);
    dateFields.setAttribute('aria-hidden', String(!visible));
    if (visible) fillDefaultDatesIfEmpty();
    else {
      document.getElementById('validFrom').value = '';
      document.getElementById('validUntil').value = '';
    }
  });

  function fillDefaultDatesIfEmpty() {
    const now = new Date();
    const fromInput = document.getElementById('validFrom');
    const untilInput = document.getElementById('validUntil');

    if (!fromInput.value) {
      const fromDate = new Date(now);
      fromDate.setHours(0,0,0,0);
      fromInput.value = fromDate.toISOString().slice(0,16);
    }
    if (!untilInput.value) {
      const untilDate = new Date(now);
      untilDate.setDate(now.getDate() + 30);
      untilDate.setHours(23,59,0,0);
      untilInput.value = untilDate.toISOString().slice(0,16);
    }
  }

  form.addEventListener('submit', function (e) {
    const url = document.getElementById('url').value.trim();
    const validFrom = document.getElementById('validFrom').value;
    const validUntil = document.getElementById('validUntil').value;

    if (!url.startsWith('http://') && !url.startsWith('https://')) {
      e.preventDefault();
      alert('URL must start with http:// or https://');
      return;
    }

    if (validFrom && validUntil) {
      const fromDate = new Date(validFrom);
      const untilDate = new Date(validUntil);
      if (fromDate >= untilDate) {
        e.preventDefault();
        alert('Valid From must be before Valid Until');
        return;
      }
      if (untilDate < new Date()) {
        e.preventDefault();
        alert('Valid Until cannot be in the past');
        return;
      }
    }
  });
});

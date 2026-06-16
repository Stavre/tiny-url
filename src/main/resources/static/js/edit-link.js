(function () {
  var fromEl    = document.getElementById('activeFrom');
  var untilEl   = document.getElementById('activeUntil');
  var errorEl   = document.getElementById('dateError');
  var warnEl    = document.getElementById('dateWarning');
  var submitBtn = document.getElementById('submitBtn');

  function validateDates() {
    var from  = fromEl.value;
    var until = untilEl.value;
    errorEl.style.display = 'none';
    warnEl.style.display  = 'none';
    submitBtn.disabled    = false;

    if (!from && !until) return true;

    if (Boolean(from) !== Boolean(until)) {
      errorEl.textContent   = 'Both dates must be set together, or both left empty.';
      errorEl.style.display = 'block';
      submitBtn.disabled    = true;
      return false;
    }

    var fromDate  = new Date(from);
    var untilDate = new Date(until);
    var now       = new Date();

    if (fromDate >= untilDate) {
      errorEl.textContent   = '"Active From" must be before "Active Until".';
      errorEl.style.display = 'block';
      submitBtn.disabled    = true;
      return false;
    }
    if (untilDate < now) {
      errorEl.textContent   = '"Active Until" cannot be in the past.';
      errorEl.style.display = 'block';
      submitBtn.disabled    = true;
      return false;
    }
    if (fromDate < now) {
      warnEl.textContent   = '"Active From" is in the past — the link will activate immediately.';
      warnEl.style.display = 'block';
    }
    return true;
  }

  // Preset buttons wired via data-preset / data-value / data-unit
  document.querySelectorAll('[data-preset]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var value = parseInt(btn.dataset.value, 10);
      var unit  = btn.dataset.unit;
      var base  = fromEl.value ? new Date(fromEl.value) : new Date();
      var until = new Date(base);
      if (unit === 'days') until.setDate(until.getDate() + value);
      else until.setFullYear(until.getFullYear() + value);
      if (!fromEl.value) fromEl.value = toInputFmt(base);
      untilEl.value = toInputFmt(until);
      validateDates();
    });
  });

  var clearBtn = document.querySelector('[data-clear-dates]');
  if (clearBtn) {
    clearBtn.addEventListener('click', function () {
      fromEl.value  = '';
      untilEl.value = '';
      validateDates();
    });
  }

  fromEl.addEventListener('change', validateDates);
  untilEl.addEventListener('change', validateDates);

  // Surface any pre-existing invalid state immediately on page load
  validateDates();

  document.getElementById('editForm').addEventListener('submit', function (e) {
    var url = document.getElementById('originalUrl').value.trim();
    if (!url.startsWith('http://') && !url.startsWith('https://')) {
      e.preventDefault();
      alert('URL must start with http:// or https://');
      return;
    }
    if (!validateDates()) e.preventDefault();
  });
}());

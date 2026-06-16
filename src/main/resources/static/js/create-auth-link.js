(function () {
  var details  = document.getElementById('dateDetails');
  var fromEl   = document.getElementById('activeFrom');
  var untilEl  = document.getElementById('activeUntil');
  var clearBtn = document.getElementById('clearDatesBtn');

  function setDefaults() {
    if (!fromEl.value) {
      fromEl.value = toInputFmt(new Date());
    }
    if (!untilEl.value) {
      var until = new Date();
      until.setDate(until.getDate() + 30);
      until.setHours(23, 59, 0, 0);
      untilEl.value = toInputFmt(until);
    }
  }

  // If server re-rendered with values (e.g. after a validation error), keep section open
  if (fromEl.value || untilEl.value) {
    details.open = true;
  }

  details.addEventListener('toggle', function () {
    if (details.open) setDefaults();
    // Dates are NOT cleared on close — use the Clear button to discard them
  });

  if (clearBtn) {
    clearBtn.addEventListener('click', function () {
      fromEl.value  = '';
      untilEl.value = '';
    });
  }

  document.getElementById('linkForm').addEventListener('submit', function (e) {
    var url = document.getElementById('url').value.trim();
    if (!url.startsWith('http://') && !url.startsWith('https://')) {
      e.preventDefault();
      alert('URL must start with http:// or https://');
      return;
    }
    var from  = fromEl.value;
    var until = untilEl.value;
    if (Boolean(from) !== Boolean(until)) {
      e.preventDefault();
      alert('Both "Active From" and "Active Until" must be set together, or both left empty.');
      return;
    }
    if (from && until) {
      if (new Date(from) >= new Date(until)) {
        e.preventDefault();
        alert('"Active From" must be before "Active Until"');
        return;
      }
      if (new Date(until) < new Date()) {
        e.preventDefault();
        alert('"Active Until" cannot be in the past');
        return;
      }
    }
  });
}());

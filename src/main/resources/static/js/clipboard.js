document.addEventListener('DOMContentLoaded', function () {
  var btn = document.getElementById('copyBtn');
  if (!btn) return;

  btn.addEventListener('click', async function () {
    var anchor = document.querySelector('.url-box a');
    if (!anchor) return;

    var url    = anchor.href;
    var orig   = btn.textContent;
    var copied = false;

    try {
      await navigator.clipboard.writeText(url);
      copied = true;
    } catch (_) {
      try {
        var ta = document.createElement('textarea');
        ta.value = url;
        ta.style.cssText = 'position:fixed;top:-9999px;opacity:0';
        document.body.appendChild(ta);
        ta.select();
        copied = document.execCommand('copy');
        document.body.removeChild(ta);
      } catch (_2) {}
    }

    if (copied) {
      btn.textContent = 'Copied!';
      setTimeout(function () { btn.textContent = orig; }, 2000);
    } else {
      alert('Could not copy — please copy the URL manually.');
    }
  });
});

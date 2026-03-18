document.addEventListener('DOMContentLoaded', function () {
  const copyBtn = document.getElementById('copyBtn');
  if (!copyBtn) return;

  copyBtn.addEventListener('click', async function () {
    const anchor = document.querySelector('.short-url a');
    if (!anchor) return;

    const url = anchor.href;
    try {
      await navigator.clipboard.writeText(url);
      const original = copyBtn.innerHTML;
      copyBtn.textContent = '✓ Copied!';
      copyBtn.classList.add('copied');
      setTimeout(() => {
        copyBtn.innerHTML = original;
        copyBtn.classList.remove('copied');
      }, 2000);
    } catch (err) {
      console.error('Copy failed', err);
      alert('Failed to copy URL to clipboard');
    }
  });
});

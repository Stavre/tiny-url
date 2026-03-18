    function copyToClipboard() {
        const shortUrl = document.querySelector('.short-url a').href;
        const copyBtn = document.getElementById('copyBtn');

        navigator.clipboard.writeText(shortUrl).then(() => {
            // Change button text temporarily
            const originalText = copyBtn.innerHTML;
            copyBtn.innerHTML = '✓ Copied!';
            copyBtn.classList.add('copied');

            setTimeout(() => {
                copyBtn.innerHTML = originalText;
                copyBtn.classList.remove('copied');
            }, 2000);
        }).catch(err => {
            console.error('Failed to copy: ', err);
            alert('Failed to copy URL to clipboard');
        });
    }
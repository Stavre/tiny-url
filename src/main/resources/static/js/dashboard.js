    function confirmDelete(event, form) {
        if (!confirm('Are you sure you want to delete this link? This action cannot be undone.')) {
            event.preventDefault();
            return false;
        }
        return true;
    }
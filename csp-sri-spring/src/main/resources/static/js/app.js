// Trusted Types policy: Enforce safe DOM sink usage
const escapePolicy = globalThis.trustedTypes?.createPolicy('app-policy', {
    createHTML: (input) => {
        const div = document.createElement('div');
        div.textContent = input;
        return div.innerHTML;
    }
});

document.addEventListener("DOMContentLoaded", function() {

    // 1. Payload test buttons
    document.querySelectorAll('.quick-payload-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const payloadType = this.getAttribute('data-payload');
            injectPayload(payloadType);
        });
    });

    // 2. Clear posts with CSRF token header
    const btnClearPosts = document.getElementById('btn-clear-posts');
    if (btnClearPosts) {
        btnClearPosts.addEventListener('click', function() {
            const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

            const headers = {};
            if (csrfToken && csrfHeader) {
                headers[csrfHeader] = csrfToken;
            }

            fetch('/api/posts/clear', { method: 'POST', headers: headers })
                .then(() => window.location.reload());
        });
    }
});

function injectPayload(type) {
    const titleInput = document.getElementById('post-title');
    const contentInput = document.getElementById('post-content');

    if (type === 'script') {
        titleInput.value = 'Script Tag Test';
        contentInput.value = '<script>alert("XSS Test");<\/script><p>Post content</p>';
    } else if (type === 'onerror') {
        titleInput.value = 'Img Onerror Test';
        contentInput.value = '<img src="x" onerror="alert(\'onerror Test\')"><p>Post content</p>';
    } else if (type === 'sqli') {
        titleInput.value = "SQL Injection Test ' OR '1'='1";
        contentInput.value = "'; DROP TABLE posts; -- UNION SELECT 1, 'admin', 'hacked', now() --";
    }
}

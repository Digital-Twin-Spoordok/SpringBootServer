const API_BASE = `${window.location.origin}/api`;

const form = document.querySelector('#login-form')
    || document.querySelector('#register-form');

const message = document.querySelector('#auth-message');
const submitButton = document.querySelector('#auth-submit');

function showMessage(text, type = 'error') {
    message.textContent = text;
    message.className = `auth-message ${type}`;
}

async function sendRequest(endpoint, body) {
    const response = await fetch(`${API_BASE}${endpoint}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    });

    const raw = await response.text();
    let data = {};

    if (raw) {
        try {
            data = JSON.parse(raw);
        } catch {
            data = { message: raw };
        }
    }

    if (!response.ok) {
        throw new Error(
            data.detail || data.message || data.error || 'Er is iets misgegaan.'
        );
    }

    return data;
}

form?.addEventListener('submit', async event => {
    event.preventDefault();

    message.className = 'auth-message';
    submitButton.disabled = true;
    submitButton.textContent = 'Even wachten...';

    try {
        const email = document.querySelector('#email').value.trim();
        const password = document.querySelector('#password').value;

        let endpoint;
        let body;

        if (form.id === 'register-form') {
            const name = document.querySelector('#name').value.trim();
            const confirmPassword = document.querySelector('#confirm-password').value;

            if (password !== confirmPassword) {
                throw new Error('De wachtwoorden komen niet overeen.');
            }

            if (password.length < 8) {
                throw new Error('Je wachtwoord moet minimaal 8 tekens bevatten.');
            }

            endpoint = '/auth/register';
            body = { name, email, password };
        } else {
            endpoint = '/auth/login';
            body = { email, password };
        }

        const data = await sendRequest(endpoint, body);

        if (!data.token) {
            throw new Error('De server heeft geen inlogtoken teruggestuurd.');
        }

        // Token en gebruikersgegevens bewaren.
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));

        showMessage(
            form.id === 'register-form'
                ? 'Account aangemaakt! Je wordt doorgestuurd...'
                : 'Succesvol ingelogd! Je wordt doorgestuurd...',
            'success'
        );

        setTimeout(() => {
            window.location.href = '/dev/index.html';
        }, 700);

    } catch (error) {
        showMessage(error.message);
    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML = form.id === 'register-form'
            ? 'Account aanmaken <span>→</span>'
            : 'Inloggen <span>→</span>';
    }
});

protectDevPage();
async function protectDevPage() {
    // Verberg de pagina zolang we de rechten controleren.
    document.documentElement.style.visibility = 'hidden';

    const token = localStorage.getItem('token');

    // Geen token? Terug naar de loginpagina.
    if (!token) {
        window.location.replace('/login.html');
        return;
    }

    try {
        const response = await fetch('/api/auth/me', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        // Ongeldige of verlopen token.
        if (!response.ok) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');

            window.location.replace('/login.html');
            return;
        }

        const user = await response.json();

        // Alleen admins mogen de devpagina openen.
        if (user.role !== 'ADMIN') {
            window.location.replace('/index.html');
            return;
        }

        // Admin is ingelogd: pagina zichtbaar maken.
        document.documentElement.style.visibility = 'visible';

    } catch (error) {
        console.error('Controle van gebruikersrechten mislukt:', error);
        window.location.replace('/login.html');
    }
};

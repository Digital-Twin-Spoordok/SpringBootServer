const API_BASE = `${window.location.origin}/api`;

async function request(path, options = {}) {
    const token = localStorage.getItem('token');

    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(options.headers || {})
        }
    });

    const raw = await response.text();
    let data = null;

    if (raw) {
        try {
            data = JSON.parse(raw);
        } catch {
            data = raw;
        }
    }

    if (!response.ok) {
        const error = data && typeof data === 'object'
            ? (data.detail || data.message || data.error)
            : data;

        throw new Error(error || `HTTP ${response.status}`);
    }

    return data;
}

export const api = {
    status: () => request('/status'),

    getAiSettings: () => request('/ai-settings'),
    saveAiSettings: body => request('/ai-settings', {
        method: 'PUT',
        body: JSON.stringify(body)
    }),

    testAi: () => request('/ai-settings/test', {
        method: 'POST'
    }),

    getPrompt: () => request('/ai-settings/prompt'),
    savePrompt: prompt => request('/ai-settings/prompt', {
        method: 'PUT',
        body: JSON.stringify({ prompt })
    }),

    list: resource => request(`/${resource}`),

    create: (resource, body) => request(`/${resource}`, {
        method: 'POST',
        body: JSON.stringify(body)
    }),

    update: (resource, id, body) => request(`/${resource}/${encodeURIComponent(id)}`, {
        method: 'PUT',
        body: JSON.stringify(body)
    }),

    remove: (resource, id) => request(`/${resource}/${encodeURIComponent(id)}`, {
        method: 'DELETE'
    }),

    logs: () => request('/logs'),

    chat: message => request('/assistant/chat', {
        method: 'POST',
        body: JSON.stringify({ message })
    })
};

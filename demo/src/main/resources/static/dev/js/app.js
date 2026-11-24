import { api } from './api.js';

const pages = ['dashboard', 'ai', 'playground', 'users', 'projects', 'assets', 'logs'];
const titles = { dashboard:'Dashboard', ai:'AI Configuration', playground:'AI Playground', users:'Users', projects:'Projects', assets:'Assets', logs:'API & Logs' };
const state = { users: [], projects: [], assets: [], logs: [], editing: null, currentAsset: null };

const $ = selector => document.querySelector(selector);
const $$ = selector => [...document.querySelectorAll(selector)];
const escapeHtml = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const showToast = (message, type = '') => {
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.textContent = message;
  $('#toast-region').append(el);
  setTimeout(() => el.remove(), 4200);
};
const initials = name => String(name || '?').split(/\s+/).slice(0,2).map(x => x[0]).join('').toUpperCase();


function navigate(page) {
    const pageTitle = document.getElementById('pageTitle');

    const titles = {
        dashboard: 'Dashboard',
        ai: 'AI Configuration',
        playground: 'AI Playground',
        users: 'Users',
        projects: 'Projects',
        assets: 'Assets',
        logs: 'API & Logs'
    };

    if (pageTitle) {
        pageTitle.textContent = titles[page] || page;
    }

    document.querySelectorAll('.page').forEach(element => {
        element.classList.remove('active');
    });

    const selectedPage = document.getElementById(`page-${page}`);

    if (selectedPage) {
        selectedPage.classList.add('active');
    } else {
        console.error(`Dashboard-pagina niet gevonden: page-${page}`);
        return;
    }

    document.querySelectorAll('[data-page]').forEach(element => {
        element.classList.toggle('active', element.dataset.page === page);
    });

    // Laad de gegevens van de geselecteerde pagina
    switch (page) {
        case 'ai':
            loadAiSettings();
            break;

        case 'users':
            loadResource('users');
            break;

        case 'projects':
            loadResource('projects');
            break;

        case 'assets':
            loadResource('assets');
            break;

        case 'logs':
            loadLogs();
            break;
    }
}

$$('.nav-item').forEach(el => el.addEventListener('click', e => { e.preventDefault(); navigate(el.dataset.page); }));
$$('[data-go]').forEach(el => el.addEventListener('click', () => navigate(el.dataset.go)));
$('#mobile-menu').addEventListener('click', () => $('#sidebar').classList.toggle('open'));
$('#refresh-page').addEventListener('click', refreshDashboard);
$$('[data-action="refresh"]').forEach(el => el.addEventListener('click', refreshDashboard));
window.addEventListener('hashchange', () => navigate(location.hash.slice(1)));

async function refreshDashboard() {
  try {
    const data = await api.status();
    const backend = data.backend ?? data.status ?? 'Online';
    const db = data.mongodb ?? data.database ?? 'Unknown';
    const ai = data.ollama ?? data.ai ?? 'Unknown';
    setMetric('backend', backend, data.backendMessage || 'Spring Boot API bereikbaar');
    setMetric('db', db, data.databaseMessage || 'MongoDB status');
    setMetric('ai', ai, data.aiMessage || 'Ollama status');
    $('#metric-model').textContent = data.model || 'Niet ingesteld';
    $('#sidebar-server-status').textContent = 'Server connected';
  } catch (error) {
    setMetric('backend', 'Offline', 'Status-API niet bereikbaar');
    setMetric('db', 'Unknown', 'Geen status ontvangen');
    setMetric('ai', 'Unknown', 'Geen status ontvangen');
    $('#metric-model').textContent = '—';
    $('#sidebar-server-status').textContent = 'API not connected';
  }
}
function setMetric(key, value, note) {
  $(`#metric-${key}`).textContent = String(value);
  $(`#metric-${key}-note`).textContent = note;
  const dot = $(`#dot-${key}`);
  const ok = /online|connected|up|healthy|true/i.test(String(value));
  dot.style.background = ok ? 'var(--green)' : /offline|down|false|error/i.test(String(value)) ? 'var(--red)' : 'var(--orange)';
}
async function loadAiSettings() {
  try {
    const [settings, prompt] = await Promise.all([api.getAiSettings(), api.getPrompt().catch(() => null)]);
    $('#ai-model').value = settings.model || '';
    $('#ai-base-url').value = settings.baseUrl || '';
    $('#ai-temperature').value = settings.temperature ?? 0.7;
    $('#ai-max-tokens').value = settings.maxTokens ?? 2048;
    if (prompt) $('#system-prompt').value = typeof prompt === 'string' ? prompt : (prompt.prompt || '');
    $('#ai-connection-status').textContent = 'Instellingen geladen';
  } catch (error) {
    $('#ai-connection-status').textContent = 'API nog niet aangesloten';
  }
}
$('#ai-settings-form').addEventListener('submit', async e => {
  e.preventDefault();
  const body = { model: $('#ai-model').value.trim(), baseUrl: $('#ai-base-url').value.trim(), temperature: Number($('#ai-temperature').value), maxTokens: Number($('#ai-max-tokens').value) };
  try { await api.saveAiSettings(body); showToast('AI-instellingen opgeslagen.', 'success'); }
  catch (error) { showToast(`Opslaan mislukt: ${error.message}`, 'error'); }
});
$('#test-ai').addEventListener('click', async () => {
  try { const result = await api.testAi(); 
    showToast(result?.message || 'AI-verbinding succesvol.', 'success'); }
  catch (error) { showToast(`AI-test mislukt: ${error.message}`, 'error'); }
});
$('#prompt-form').addEventListener('submit', async e => {
  e.preventDefault();
  try { await api.savePrompt($('#system-prompt').value); showToast('System prompt opgeslagen.', 'success'); }
  catch (error) { showToast(`Opslaan mislukt: ${error.message}`, 'error'); }
});

async function loadResource(resource) {
  const table = $(`#${resource}-table`);
  table.innerHTML = '<tr><td colspan="5" class="table-message">Gegevens ophalen…</td></tr>';
  try {
    const result = await api.list(resource);
    state[resource] = Array.isArray(result) ? result : (result?.content || result?.items || []);
    renderResource(resource);
  } catch (error) {
    table.innerHTML = `<tr><td colspan="5" class="table-message">Backend-endpoint niet beschikbaar: ${escapeHtml(error.message)}<br><small>Verwacht: GET /api//${resource}</small></td></tr>`;
    $(`#${resource}-count`).textContent = 'API niet aangesloten';
  }
}
function renderResource(resource) {
  const rows = state[resource];
  $(`#${resource}-count`).textContent = `${rows.length} ${resource}`;
  const query = ($(`[data-search="${resource}"]`)?.value || '').toLowerCase();
  const filtered = rows.filter(row => JSON.stringify(row).toLowerCase().includes(query));
  const body = filtered.map(row => {
    const id = row.id || row._id;
    if (resource === 'users') return `<tr><td><div class="table-user"><span class="mini-avatar">${escapeHtml(initials(row.name || row.username))}</span><span class="primary-cell">${escapeHtml(row.name || row.username || 'Unnamed user')}<span class="sub-cell">${escapeHtml(id)}</span></span></div></td><td>${escapeHtml(row.email || '—')}</td><td><span class="role-pill">${escapeHtml(row.role || 'USER')}</span></td><td><span class="state-pill ${row.active === false ? 'inactive':'active'}">${row.active === false ? 'Inactive':'Active'}</span></td><td>${actions(resource,id)}</td></tr>`;
    if (resource === 'projects') return `<tr><td class="primary-cell">${escapeHtml(row.name || 'Unnamed project')}<span class="sub-cell">${escapeHtml(id)}</span></td><td>${escapeHtml(row.description || '—')}</td><td><span class="state-pill active">${escapeHtml(row.status || 'ACTIVE')}</span></td><td>${escapeHtml(formatDate(row.createdAt))}</td><td>${actions(resource,id)}</td></tr>`;
    return `<tr data-asset-id="${escapeHtml(id)}"><td class="primary-cell">${escapeHtml(row.name || 'Unnamed asset')}<span class="sub-cell">${escapeHtml(id)}</span></td><td><span class="type-pill">${escapeHtml(row.assetType || row.type || 'Asset')}</span></td><td>${escapeHtml(row.projectName || row.siteName || row.projectId || row.siteId || '—')}</td><td>${escapeHtml(row.modelUrl || row.modelFile || '—')}</td><td><div class="row-actions"><button class="small-action" data-view-asset="${escapeHtml(id)}">Details</button>${actions(resource,id)}</div></td></tr>`;
  }).join('');
  $(`#${resource}-table`).innerHTML = body || `<tr><td colspan="5" class="table-message">Geen resultaten gevonden.</td></tr>`;
  $(`#${resource}-table`).querySelectorAll('[data-edit]').forEach(b => b.addEventListener('click', () => openModal(resource,b.dataset.edit)));
  $(`#${resource}-table`).querySelectorAll('[data-delete]').forEach(b => b.addEventListener('click', () => deleteItem(resource,b.dataset.delete)));
  $(`#${resource}-table`).querySelectorAll('[data-view-asset]').forEach(b => b.addEventListener('click', () => showAsset(b.dataset.viewAsset)));
}
function actions(resource,id) { return `<div class="row-actions"><button class="small-action" data-edit="${escapeHtml(id)}">Edit</button><button class="small-action" data-delete="${escapeHtml(id)}">Delete</button></div>`; }
function formatDate(value) { if (!value) return '—'; const date = new Date(value); return Number.isNaN(date.getTime()) ? String(value) : date.toLocaleDateString('nl-NL'); }
$$('[data-search]').forEach(input => input.addEventListener('input', () => renderResource(input.dataset.search)));

function openModal(resource, id = null) {
  const existing = id ? state[resource].find(x => String(x.id || x._id) === String(id)) : null;
  state.editing = { resource, id, existing };
  $('#modal-title').textContent = `${existing ? 'Edit' : 'Create'} ${resource.slice(0,-1)}`;
  const fields = resource === 'users' ? [
    ['name','Name','text'],['email','Email','email'],['role','Role','select',['USER','ADMIN']],['active','Active','select',['true','false']]
  ] : resource === 'projects' ? [
    ['name','Project name','text'],['description','Description','textarea'],['status','Status','select',['ACTIVE','INACTIVE']]
  ] : [
    ['name','Asset name','text'],['assetType','Asset type','select',['Building','Machine','Robot','Conveyor']],['projectId','Project ID','text'],['modelUrl','Model URL','url'],['width','Width (m)','number'],['depth','Depth (m)','number'],['height','Height (m)','number'],['positionX','Position X','number'],['positionY','Position Y','number'],['positionZ','Position Z','number'],['rotationX','Rotation X','number'],['rotationY','Rotation Y','number'],['rotationZ','Rotation Z','number']
  ];
  $('#modal-fields').innerHTML = fields.map(([key,label,type,options]) => {
    const value = existing?.[key] ?? (key === 'active' ? true : '');
    if (type === 'select') return `<label>${label}<select name="${key}">${options.map(o=>`<option value="${o}" ${String(value)===o?'selected':''}>${o}</option>`).join('')}</select></label>`;
    if (type === 'textarea') return `<label>${label}<textarea name="${key}" rows="3">${escapeHtml(value)}</textarea></label>`;
    return `<label>${label}<input name="${key}" type="${type}" value="${escapeHtml(value)}" ${key==='name'?'required':''}></label>`;
  }).join('');
  $('#modal-backdrop').hidden = false;
}
$$('[data-create]').forEach(button => button.addEventListener('click', () => openModal(button.dataset.create)));
function closeModal() { $('#modal-backdrop').hidden = true; state.editing = null; }
$('#modal-close').addEventListener('click', closeModal);
$('#modal-cancel').addEventListener('click', closeModal);
$('#modal-backdrop').addEventListener('click', e => { if (e.target === $('#modal-backdrop')) closeModal(); });
$('#entity-form').addEventListener('submit', async e => {
  e.preventDefault();
  const { resource, id } = state.editing || {};
  if (!resource) return;
  const body = Object.fromEntries(new FormData(e.currentTarget).entries());
  if (resource === 'users') body.enabled = body.active === 'true';
  if (resource === 'assets') for (const key of Object.keys(body)) if (/^(width|depth|height|position|rotation)/.test(key) && body[key] !== '') body[key] = Number(body[key]);
  try {
    if (id) await api.update(resource,id,body); else await api.create(resource,body);
    closeModal(); showToast(`${resource} succesvol opgeslagen.`, 'success'); await loadResource(resource);
  } catch (error) { showToast(`Opslaan mislukt: ${error.message}`, 'error'); }
});
async function deleteItem(resource,id) {
  if (!confirm(`Weet je zeker dat je dit item wilt verwijderen? (${id})`)) return;
  try { await api.remove(resource,id); showToast('Item verwijderd.', 'success'); await loadResource(resource); }
  catch (error) { showToast(`Verwijderen mislukt: ${error.message}`, 'error'); }
}
function showAsset(id) {
  const asset = state.assets.find(x => String(x.id || x._id) === String(id));
  if (!asset) return;
  state.currentAsset = asset;
  const fields = [
    ['Asset type',asset.assetType || asset.type],['Model URL',asset.modelUrl || asset.modelFile],
    ['Breedte',asset.width ?? asset.dimensions?.width],['Diepte',asset.depth ?? asset.dimensions?.depth],['Hoogte',asset.height ?? asset.dimensions?.height],
    ['Positie X',asset.positionX ?? asset.position?.x],['Positie Y',asset.positionY ?? asset.position?.y],['Positie Z',asset.positionZ ?? asset.position?.z],
    ['Rotatie X',asset.rotationX ?? asset.rotation?.x],['Rotatie Y',asset.rotationY ?? asset.rotation?.y],['Rotatie Z',asset.rotationZ ?? asset.rotation?.z],
    ['Project ID',asset.projectId],['Site ID',asset.siteId],['Aangemaakt',formatDate(asset.createdAt)]
  ];
  $('#asset-detail').innerHTML = `<div class="detail-title"><div><div class="eyebrow">ASSET INSPECTOR</div><h2>${escapeHtml(asset.name || 'Unnamed asset')}</h2></div><span class="type-pill">${escapeHtml(asset.assetType || asset.type || 'Asset')}</span></div><div class="detail-grid">${fields.map(([label,value])=>`<div class="detail-item"><span>${escapeHtml(label)}</span><strong>${escapeHtml(value ?? '—')}</strong></div>`).join('')}</div>`;
}

$('#chat-form').addEventListener('submit', async e => {
  e.preventDefault();
  const input = $('#chat-input');
  const message = input.value.trim();
  if (!message) return;
  const container = $('#chat-messages');
  container.querySelector('.chat-welcome')?.remove();
  appendBubble('user',message);
  input.value = '';
  const pending = appendBubble('assistant','Thinking…');
  try {
    const result = await api.chat(message);
    const answer = result?.answer || result?.content || result?.message || JSON.stringify(result,null,2);
    pending.textContent = answer;
    if (result?.actions?.length) pending.textContent += `\n\nActions:\n${JSON.stringify(result.actions,null,2)}`;
  } catch (error) { pending.textContent = `Request mislukt: ${error.message}`; }
  container.scrollTop = container.scrollHeight;
});
function appendBubble(role,text) {
  const el = document.createElement('div');
  el.className = `chat-bubble ${role}`;
  const label = document.createElement('span'); label.className = 'bubble-label'; label.textContent = role === 'user' ? 'YOU' : 'ASSISTANT';
  el.append(label,document.createTextNode(text));
  $('#chat-messages').append(el);
  $('#chat-messages').scrollTop = $('#chat-messages').scrollHeight;
  return el;
}
async function loadLogs() {
  try {
    const result = await api.logs();
    state.logs = Array.isArray(result) ? result : (result?.items || []);
    renderLogs();
  } catch (error) { $('#logs-table').innerHTML = `<tr><td colspan="4" class="table-message">Logs-endpoint nog niet beschikbaar: ${escapeHtml(error.message)}</td></tr>`; }
}
function renderLogs() {
  const level = $('#log-level').value;
  const rows = state.logs.filter(x => level === 'all' || x.level === level);
  $('#logs-table').innerHTML = rows.map(log => `<tr><td>${escapeHtml(log.timestamp || log.time || '—')}</td><td><span class="level-pill ${escapeHtml(log.level || 'INFO')}">${escapeHtml(log.level || 'INFO')}</span></td><td>${escapeHtml(log.component || 'Backend')}</td><td>${escapeHtml(log.message || '')}</td></tr>`).join('') || '<tr><td colspan="4" class="table-message">Geen logregels beschikbaar.</td></tr>';
}
$('#refresh-logs').addEventListener('click',loadLogs);
$('#log-level').addEventListener('change',renderLogs);
function updateClock() { $('#clock').textContent = new Date().toLocaleString('nl-NL',{dateStyle:'medium',timeStyle:'short'}); }
updateClock(); setInterval(updateClock,30000);
refreshDashboard();
if (location.hash) navigate(location.hash.slice(1));

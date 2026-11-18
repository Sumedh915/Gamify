import { el } from './util.js';

const $app = document.getElementById('app'), $nav = document.getElementById('nav');
const uid = () => crypto.randomUUID ? crypto.randomUUID() : 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, () => (Math.random() * 16 | 0).toString(16));
const cid = localStorage.gh_cid || (localStorage.gh_cid = uid());
const LEVELS = [['easy', 'Easy', 'Makes mistakes. Good for warming up.'], ['hard', 'Hard', 'Thinks a few moves ahead.'], ['unbeatable', 'Unbeatable', 'Searches as deep as it can. Good luck.']];
let games = [], cleanup = () => {};

async function api(path, params = {}, method = 'GET') {
  const r = await fetch(`/api/${path}?${new URLSearchParams({ ...params, cid })}`, { method });
  const j = await r.json();
  if (!r.ok) throw new Error(j.error || 'Request failed');
  return j;
}
const sess = c => { try { return JSON.parse(sessionStorage['gh_' + c]); } catch { return null; } };
const save = (j) => sessionStorage['gh_' + j.code] = JSON.stringify({ slot: j.slot, token: j.token });
const roomPath = j => j.game === 'tournament' ? `/tournament/${j.code}` : `/${j.game}/${j.code}`;
const link = (href, txt, cls = 'btn') => el('a', { href, class: cls, 'data-link': true }, txt);
export const go = p => { history.pushState({}, '', p); route(); };
const flash = t => { const n = el('div', { class: 'toast', role: 'alert' }, t); document.body.append(n); setTimeout(() => n.remove(), 3500); };

function nav() {
  $nav.replaceChildren(
    el('button', { class: 'btn', onclick: () => history.back() }, '← Back'),
    link('/', 'Home'),
    el('details', { class: 'menu' }, el('summary', { class: 'btn' }, 'Games'),
      el('div', { class: 'menu-list' }, games.map(g => link('/' + g.id, `${g.icon}  ${g.name}`, 'menu-item')))),
    link('/tournament', 'Tournament'), link('/stats', 'Stats'));
}

document.addEventListener('click', e => {
  const a = e.target.closest('a[data-link]');
  if (a && !e.metaKey && !e.ctrlKey && !e.shiftKey) { e.preventDefault(); go(a.getAttribute('href')); }
});
window.addEventListener('popstate', () => route());

/* ---------- shared bits ---------- */
async function joinWith(code, errEl) {
  try { const j = await api('join', { code: code.trim().toUpperCase() }, 'POST'); save(j); go(roomPath(j)); }
  catch (e) { errEl.textContent = e.message; }
}
function joinBox() {
  const err = el('p', { class: 'err', role: 'alert' });
  const input = el('input', { class: 'codein', maxlength: 5, placeholder: 'CODE', 'aria-label': 'Game code', autocapitalize: 'characters', autocomplete: 'off' });
  const submit = () => input.value.trim() && joinWith(input.value, err);
  input.addEventListener('keydown', e => e.key === 'Enter' && submit());
  return el('div', { class: 'joinbox' }, el('label', {}, 'Got a code from a friend?'), el('div', { class: 'row' }, input, el('button', { class: 'btn sun', onclick: submit }, 'Join game')), err);
}
async function create(params) {
  try { const j = await api('create', params, 'POST'); save(j); go(roomPath(j)); } catch (e) { flash(e.message); }
}
const levelButtons = base => LEVELS.map(([id, name, d]) =>
  el('button', { class: `level ${id}`, onclick: () => create({ ...base, mode: id }) }, el('b', {}, name), el('span', {}, d)));

/* ---------- pages ---------- */
function home() {
  const tint = ['#ffd1c9', '#d4e1ff', '#ffe9a8', '#d3f0dc', '#e6d9ff', '#ffd9ec'];
  $app.replaceChildren(
    el('h1', { class: 'hero' }, 'Pick a game. Send a code. Play.'),
    el('p', { class: 'lede' }, 'Two players, two computers. Or take on the computer at three difficulty levels.'),
    joinBox(),
    el('div', { class: 'tiles' },
      games.map((g, i) => el('a', { href: '/' + g.id, class: 'tile', 'data-link': true, style: `--c:${tint[i % tint.length]}` }, el('span', { class: 'ico' }, g.icon), el('b', {}, g.name), el('span', {}, g.desc))),
      el('a', { href: '/tournament', class: 'tile wide', 'data-link': true, style: '--c:#ffc933' }, el('span', { class: 'ico' }, '🏆'), el('b', {}, 'Tournament'), el('span', {}, 'Five random games, best of five wins.')),
      el('a', { href: '/stats', class: 'tile wide', 'data-link': true, style: '--c:#c9f0ee' }, el('span', { class: 'ico' }, '📊'), el('b', {}, 'Your stats'), el('span', {}, 'Wins, losses and draws by difficulty.'))));
}

function lobby(g) {
  document.title = g.name + ' · GameHub';
  $app.replaceChildren(
    el('h1', {}, el('span', { class: 'ico' }, g.icon), ' ', g.name), el('p', { class: 'lede' }, g.desc),
    el('section', { class: 'panel' }, el('h2', {}, 'Play a friend'),
      el('p', {}, 'Create a game and share the code, or enter a code you were given.'),
      el('div', { class: 'row' }, el('button', { class: 'btn p', onclick: () => create({ game: g.id, mode: 'pvp' }) }, 'Create 2-player game')), joinBox()),
    el('section', { class: 'panel' }, el('h2', {}, 'Play the computer'), el('div', { class: 'levels' }, levelButtons({ game: g.id }))));
}

function tournamentLobby() {
  document.title = 'Tournament · GameHub';
  $app.replaceChildren(
    el('h1', {}, '🏆 Tournament'),
    el('p', { class: 'lede' }, 'Five different games are picked at random, one at a time. First to three wins takes it; if it is level after five, it is a draw.'),
    el('section', { class: 'panel' }, el('h2', {}, 'Choose your opponent'),
      el('div', { class: 'levels four' },
        el('button', { class: 'level pvp', onclick: () => create({ tournament: 1, mode: 'pvp' }) }, el('b', {}, '2 players'), el('span', {}, 'Create a game, share the code.')),
        levelButtons({ tournament: 1 })), joinBox()));
}

async function stats() {
  document.title = 'Stats · GameHub';
  $app.replaceChildren(el('h1', {}, 'Your stats'), el('p', { class: 'lede' }, 'Loading…'));
  let s; try { s = await api('stats'); } catch (e) { return fail(e.message); }
  const names = { easy: 'Easy', hard: 'Hard', unbeatable: 'Unbeatable', pvp: '2 players' };
  const rows = Object.keys(names);
  const tbl = (head, body) => el('div', { class: 'scroll' }, el('table', {}, el('thead', {}, el('tr', {}, head.map(h => el('th', {}, h)))), el('tbody', {}, body)));
  const row = (label, a) => el('tr', {}, el('th', { scope: 'row' }, label), ...[a[0] + a[1] + a[2], a[0], a[1], a[2]].map(n => el('td', {}, n)));
  $app.replaceChildren(
    el('h1', {}, 'Your stats'), el('p', { class: 'lede' }, 'Tracked on this browser, so keep playing from the same one.'),
    el('section', { class: 'panel' }, el('h2', {}, 'By difficulty'),
      tbl(['Opponent', 'Played', 'Won', 'Lost', 'Drawn'], rows.map(m => row(names[m], s.modes[m] || [0, 0, 0])))),
    el('section', { class: 'panel' }, el('h2', {}, 'Tournaments'),
      tbl(['Opponent', 'Played', 'Won', 'Lost', 'Drawn'], rows.map(m => row(names[m], s.tournaments[m] || [0, 0, 0])))),
    el('section', { class: 'panel' }, el('h2', {}, 'By game (won – lost – drawn)'),
      tbl(['Game', ...rows.map(m => names[m])], games.map(g => el('tr', {}, el('th', { scope: 'row' }, g.name),
        ...rows.map(m => { const a = (s.games[g.id] || {})[m] || [0, 0, 0]; return el('td', {}, `${a[0]} – ${a[1]} – ${a[2]}`); }))))));
}

function fail(msg) {
  $app.replaceChildren(el('h1', {}, 'Hmm.'), el('p', { class: 'lede' }, msg), el('p', {}, link('/', 'Back to home', 'btn p')));
}

/* ---------- a live game room ---------- */
async function room(code, isTour) {
  let me = sess(code);
  if (!me) {
    try { const j = await api('join', { code }, 'POST'); save(j); me = sess(code); }
    catch (e) { return fail(e.message); }
  }
  const head = el('div', { class: 'room-head' }), tbar = el('div', { class: 'tbar' }), status = el('div', { class: 'status', 'aria-live': 'polite' }),
    board = el('div', { class: 'board' }), actions = el('div', { class: 'actions' });
  $app.replaceChildren(head, tbar, status, board, actions);
  let ver = 0, busy = false, alive = true, mods = {}, seq = 0;
  const call = (path, extra = {}) => api(path, { code, slot: me.slot, t: me.token, ...extra }, 'POST');

  const A = {
    move: async m => {
      if (busy) return; busy = true;
      status.className = 'status wait'; status.textContent = cur.mode === 'pvp' ? 'Sending…' : 'Computer is thinking…';
      try { const v = await call('move', { m: m.join(',') }); busy = false; await show(v); }
      catch (e) { busy = false; flash(e.message); poll(true); }
    }
  };
  let cur = { mode: 'pvp' };

  async function show(v) {
    const my = ++seq; ver = v.version; cur = v;
    document.title = (isTour ? 'Tournament' : v.name) + ' · GameHub';
    let mod = mods[v.game]; if (!mod) mod = mods[v.game] = await import(`/games/${v.game}.js`);
    if (!alive || my !== seq) return;
    const diff = { pvp: '2 players', easy: 'vs Computer · Easy', hard: 'vs Computer · Hard', unbeatable: 'vs Computer · Unbeatable' }[v.mode];
    head.replaceChildren(el('h1', {}, isTour ? `Game ${v.t.index + 1} of 5: ${v.name}` : v.name),
      el('div', { class: 'chips' }, el('span', { class: 'chip' }, diff), el('span', { class: `chip p${v.slot}` }, `You are ${v.slot ? 'Blue' : 'Red'}`),
        v.mode === 'pvp' ? el('button', { class: 'chip code', title: 'Copy invite link', onclick: () => { navigator.clipboard?.writeText(`${location.origin}${location.pathname}`); flash('Invite link copied'); } }, `Code ${v.code}`) : null));
    tbar.replaceChildren();
    if (isTour) {
      const t = v.t;
      tbar.append(el('div', { class: 'pills' }, t.games.map((g, i) => el('span', { class: 'pill' + (i === t.index ? ' now' : i < t.index ? ' past' : '') }, i <= t.index ? g.name : '?'))),
        el('div', { class: 'tscore' }, `You ${t.wins[v.slot]} – ${t.wins[1 - v.slot]} ${v.mode === 'pvp' ? 'Opponent' : 'Computer'}`, t.draws ? ` · ${t.draws} drawn` : ''));
    }
    board.replaceChildren(); actions.replaceChildren();
    if (!v.started) {
      status.className = 'status wait'; status.textContent = 'Waiting for your friend…';
      const url = `${location.origin}${location.pathname}`;
      board.append(el('div', { class: 'invite' }, el('p', {}, 'Tell your friend to open GameHub and enter this code:'), el('div', { class: 'bigcode' }, v.code),
        el('p', {}, 'or send them this link:'), el('input', { class: 'linkin', readonly: true, value: url, onclick: e => e.target.select() })));
      return;
    }
    let txt, cls = 'status';
    if (v.over) { txt = v.winner === 2 ? 'Draw.' : v.winner === v.slot ? 'You win!' : 'You lose.'; cls += v.winner === v.slot ? ' win' : v.winner === 2 ? '' : ' lose'; }
    else if (v.canMove) { txt = 'Your turn'; cls += ' mine'; }
    else { txt = v.mode === 'pvp' ? 'Opponent’s turn…' : 'Computer is thinking…'; cls += ' wait'; }
    if (!v.over && v.canMove && mod.status) txt = mod.status(v) || txt;
    if (!v.over && !v.canMove && mod.status) txt = mod.status(v) || txt;
    status.className = cls; status.textContent = txt;
    mod.default(board, v, A);
    if (v.over) {
      if (isTour) {
        if (v.t.done) {
          const w = v.t.winner;
          actions.append(el('p', { class: 'verdict' }, w === 2 ? 'Tournament drawn.' : w === v.slot ? 'You won the tournament! 🏆' : 'You lost the tournament.'), link('/tournament', 'New tournament', 'btn p'));
        } else actions.append(el('button', { class: 'btn p', onclick: async () => { try { await show(await call('next')); } catch (e) { flash(e.message); } } }, 'Next game'));
      } else actions.append(el('button', { class: 'btn p', onclick: async () => { try { await show(await call('rematch')); } catch (e) { flash(e.message); } } }, 'Play again'), link('/' + v.game, 'Change mode'));
    }
  }

  async function poll(force) {
    if ((busy && !force) || !alive) return;
    try { const v = await api('state', { code, slot: me.slot, t: me.token, v: ver }); if (!v.same && !busy) await show(v); }
    catch (e) { if (alive) { status.className = 'status lose'; status.textContent = e.message; } }
  }
  const timer = setInterval(poll, 1000);
  cleanup = () => { alive = false; clearInterval(timer); };
  poll(true);
}

/* ---------- router ---------- */
function route() {
  cleanup(); cleanup = () => {};
  document.querySelector('.menu')?.removeAttribute('open');
  document.title = 'GameHub';
  const p = location.pathname.split('/').filter(Boolean);
  if (!p.length) return home();
  if (p[0] === 'stats') return stats();
  if (p[0] === 'tournament') return p[1] ? room(p[1].toUpperCase(), true) : tournamentLobby();
  const g = games.find(x => x.id === p[0]);
  if (g) return p[1] ? room(p[1].toUpperCase(), false) : lobby(g);
  fail('That page does not exist.');
}

(async () => {
  try { games = await api('games'); } catch { games = []; }
  nav(); route();
})();

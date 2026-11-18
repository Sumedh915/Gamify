import { el, sleep } from '../util.js';

/* Replays one move on a copy of the board: which pits get a stone, where the last one lands, and what that earns. */
const sow = (pit, i, p) => {
  const a = pit.slice(), count = a[i]; let n = count, idx = i; a[i] = 0;
  const mine = p === 0 ? 6 : 13, opp = p === 0 ? 13 : 6, steps = [];
  while (n > 0) { idx = (idx + 1) % 14; if (idx === opp) continue; a[idx]++; steps.push(idx); n--; }
  const own = p === 0 ? idx <= 5 : idx >= 7 && idx <= 12;
  const extra = idx === mine;
  const cap = !extra && own && a[idx] === 1 && a[12 - idx] > 0 ? a[12 - idx] : 0;   // stones taken from the pit across
  return { steps, last: idx, count, extra, own, cap };
};

export default async (root, r, api, prev) => {
  const s = r.s, v = r.slot, ok = new Set(r.legal.map(m => m[0])), L = r.last;
  const pvp = r.mode === 'pvp', mover = L ? L.by : -1, you = mover === v;
  const name = you ? 'You' : pvp ? 'Opponent' : 'Computer';
  const poss = you ? 'your' : pvp ? 'their' : 'its', pron = you ? 'you' : pvp ? 'they' : 'it', verb = you || pvp ? 'go' : 'goes';
  const token = root._mk = {};
  const live = () => root._mk === token;

  // Was this the very next move after the board the player was already looking at? If so we can replay it.
  const same = prev && prev.s && prev.game === r.game && prev.gameNo === r.gameNo && L && L.n === (prev.last ? prev.last.n : 0) + 1;
  const sim = same ? sow(prev.s.pit, L.m[0], L.by) : null;

  const describe = () => {
    if (!sim) return '';
    const n = sim.count, plural = k => `${k} stone${k === 1 ? '' : 's'}`;
    let t = `${name} played the pit with ${plural(n)}.`;
    if (sim.extra) t += ` The last stone landed in ${poss} store, so ${pron} ${verb} again.`;
    else if (sim.cap) t += ` The last stone landed in an empty pit on ${poss} side, so ${pron} captured ${plural(sim.cap)} from the pit across.`;
    else if (sim.own) t += ` The last stone landed on ${poss} own side.`;
    else t += ` The last stone landed on ${you ? 'your opponent’s' : 'your'} side.`;
    if (r.over) t += ' One side is empty, so the game ends.';
    return t;
  };

  const mine = [0, 1, 2, 3, 4, 5].map(k => v * 7 + k), opp = [5, 4, 3, 2, 1, 0].map(k => (1 - v) * 7 + k);
  const draw = (pit, f = {}, interactive = false, text = '') => {
    const cls = i => (f.from === i ? ' from' : '') + (f.drop === i ? ' drop' : '') + (f.land === i ? ' land' : '');
    const pitEl = i => el('button', { class: `pit p${i < 7 ? 0 : 1}${cls(i)}`, 'aria-label': `Pit with ${pit[i]} stones`, disabled: !(interactive && ok.has(i)), onclick: () => api.move([i]) }, pit[i]);
    const store = (i, p) => el('div', { class: `store p${p}${cls(i)}` }, pit[i]);
    root.replaceChildren(
      el('div', { class: 'mancala' },
        store((1 - v) * 7 + 6, 1 - v),
        el('div', { class: 'rows' }, el('div', { class: 'row' }, opp.map(pitEl)), el('div', { class: 'row' }, mine.map(pitEl))),
        store(v * 7 + 6, v)),
      el('p', { class: 'mnote', 'aria-live': 'polite' }, text),
      el('p', { class: 'note' }, 'Stones travel right along your row into your store. Land in your store to go again.'));
  };

  const from = L ? L.m[0] : undefined;
  if (sim && !you) {
    // Replay the opponent's move: pick the pit, then drop the stones one by one.
    api.say(pvp ? 'Opponent is moving…' : 'Computer is moving…');
    const a = prev.s.pit.slice();
    draw(a, { from });                       if (!await pause(650, live)) return;
    a[from] = 0; draw(a, { from });          if (!await pause(450, live)) return;
    for (const idx of sim.steps) { a[idx]++; draw(a, { from, drop: idx }); if (!await pause(330, live)) return; }
    api.say(null);
  }
  draw(s.pit, { from, land: sim ? sim.last : undefined }, r.canMove, describe());
};

async function pause(ms, live) { await sleep(ms); return live(); }

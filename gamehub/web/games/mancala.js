import { el } from '../util.js';
export default (root, r, api) => {
  const s = r.s, v = r.slot, ok = new Set(r.legal.map(m => m[0]));
  const pit = (i, mine) => el('button', { class: `pit p${i < 7 ? 0 : 1}`, 'aria-label': `Pit with ${s.pit[i]} stones`, disabled: !(mine && ok.has(i)), onclick: () => api.move([i]) }, s.pit[i]);
  const mine = [0, 1, 2, 3, 4, 5].map(k => v * 7 + k), opp = [5, 4, 3, 2, 1, 0].map(k => (1 - v) * 7 + k);
  root.append(el('div', { class: 'mancala' },
    el('div', { class: `store p${1 - v}` }, s.pit[(1 - v) * 7 + 6]),
    el('div', { class: 'rows' }, el('div', { class: 'row' }, opp.map(i => pit(i, false))), el('div', { class: 'row' }, mine.map(i => pit(i, true)))),
    el('div', { class: `store p${v}` }, s.pit[v * 7 + 6])),
    el('p', { class: 'note' }, 'Stones travel right along your row into your store. Land in your store to go again.'));
};

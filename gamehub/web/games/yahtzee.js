import { el } from '../util.js';
export default (root, r, api) => {
  const s = r.s, me = r.slot, held = new Set(), mineTurn = r.canMove;
  const rollBtn = el('button', { class: 'btn sun', onclick: () => { let m = 0; for (let i = 0; i < 5; i++) if (!held.has(i)) m |= 1 << i; api.move([0, m]); } });
  const upd = () => { rollBtn.textContent = `Roll ${5 - held.size} dice (${s.rolls} left)`; rollBtn.disabled = !(mineTurn && s.rolls > 0 && held.size < 5); };
  upd();
  const dice = el('div', { class: 'ydice' }, s.dice.map((d, i) => el('button', { class: 'ydie', 'aria-label': `Die ${d}`, disabled: !(mineTurn && s.rolls > 0),
    onclick: e => { held.has(i) ? held.delete(i) : held.add(i); e.currentTarget.classList.toggle('held'); upd(); } }, d)));
  const cell = (p, c) => {
    const v = s.card[p][c];
    if (v >= 0) return el('td', {}, v);
    if (p === me && mineTurn) return el('td', {}, el('button', { class: 'btn', onclick: () => api.move([1, c]) }, s.pot[c]));
    return el('td', {}, '');
  };
  const rows = s.cats.map((n, c) => el('tr', {}, el('th', { scope: 'row' }, n), cell(me, c), cell(1 - me, c)));
  rows.splice(6, 0, el('tr', {}, el('th', { scope: 'row' }, 'Upper bonus (63+ = 35)'), el('td', {}, `${s.upper[me]}/63`), el('td', {}, `${s.upper[1 - me]}/63`)));
  rows.push(el('tr', { class: 'tot-row' }, el('th', { scope: 'row' }, 'Total'), el('td', {}, s.total[me]), el('td', {}, s.total[1 - me])));
  root.append(el('p', { class: 'note' }, r.over ? 'Final dice' : (mineTurn ? 'Click dice to keep them, then roll the rest or score a category.' : 'Opponent’s dice')),
    dice, mineTurn ? rollBtn : null,
    el('div', { class: 'ycard scroll' }, el('table', {}, el('thead', {}, el('tr', {}, el('th', {}, 'Category'), el('th', {}, 'You'), el('th', {}, 'Opponent'))), el('tbody', {}, rows))));
};

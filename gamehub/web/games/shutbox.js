import { el } from '../util.js';
export default (root, r, api) => {
  const s = r.s, me = r.slot, legal = new Set(r.legal.map(m => m[0])); let sel = 0;
  const die = n => el('span', { class: 'die' }, '⚀⚁⚂⚃⚄⚅'[n - 1]);
  const btn = el('button', { class: 'btn sun', disabled: true, onclick: () => api.move([sel]) }, 'Shut selected tiles');
  const rem = p => { let t = 0; for (let i = 0; i < 9; i++) if (s.open[p] >> i & 1) t += i + 1; return t; };
  const row = p => el('div', { class: 'box-row' }, el('span', { class: `who p${p}` }, p === me ? 'You' : 'Opponent'),
    [...Array(9).keys()].map(i => {
      const open = (s.open[p] >> i & 1) === 1;
      const t = el('button', { class: 'stile' + (open ? '' : ' shut'), disabled: !(p === me && r.canMove && open), onclick: () => { sel ^= 1 << i; t.classList.toggle('sel'); btn.disabled = !legal.has(sel); } }, i + 1);
      return t;
    }), el('span', { class: 'tot' }, s.score[p] >= 0 ? `Final: ${s.score[p]}` : `Open: ${rem(p)}`));
  root.append(el('div', { class: 'dice', 'aria-label': 'Dice' }, r.over ? null : [die(s.d1), s.d2 ? die(s.d2) : null, el('span', { class: 'sum' }, '= ' + (s.d1 + s.d2))]),
    row(me), row(1 - me), r.canMove ? btn : null, s.note ? el('p', { class: 'note' }, s.note) : null,
    el('p', { class: 'note' }, 'Pick tiles that add up to the roll. Lowest total left wins.'));
};
export const status = r => r.canMove ? 'Your turn: pick tiles that add up to the dice' : null;

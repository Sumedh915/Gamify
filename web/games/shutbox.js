import { el } from '../util.js';
export default (root, r, api) => {
  const s = r.s, me = r.slot, legal = new Set(r.legal.map(m => m[0])); let sel = 0;
  const who = p => p === me ? 'You' : r.mode === 'pvp' ? 'Opponent' : 'Computer';
  const stuck = r.canMove && legal.has(0);       // no set of open tiles adds up to the roll
  const die = n => el('span', { class: 'die' }, '⚀⚁⚂⚃⚄⚅'[n - 1]);
  const shut = el('button', { class: 'btn sun', disabled: true, onclick: () => api.move([sel]) }, 'Shut selected tiles');
  const pass = el('button', { class: 'btn sun', onclick: () => api.move([0]) }, 'No available move');
  const rem = p => { let t = 0; for (let i = 0; i < 9; i++) if (s.open[p] >> i & 1) t += i + 1; return t; };
  const row = p => el('div', { class: 'box-row' }, el('span', { class: `who p${p}` }, p === me ? 'You' : r.mode === 'pvp' ? 'Opponent' : 'Computer'),
    [...Array(9).keys()].map(i => {
      const open = (s.open[p] >> i & 1) === 1;
      const t = el('button', { class: 'stile' + (open ? '' : ' shut'), disabled: !(p === me && r.canMove && open && !stuck), onclick: () => { sel ^= 1 << i; t.classList.toggle('sel'); shut.disabled = !legal.has(sel); } }, i + 1);
      return t;
    }), el('span', { class: 'tot' }, s.score[p] >= 0 ? `Final: ${s.score[p]}` : `Open: ${rem(p)}`));
  // What happened on the last roll, e.g. "Computer rolled 7 and shut 3 + 4."
  const [ep, roll, mask, fin] = s.ev;
  const tiles = m => [...Array(9).keys()].filter(i => m >> i & 1).map(i => i + 1).join(' + ');
  const last = ep < 0 ? null : mask === 0 ? `${who(ep)} rolled ${roll} and had no available move. Final total: ${fin}.`
    : `${who(ep)} rolled ${roll} and shut ${tiles(mask)}.${fin === 0 ? ' The box is shut!' : ''}`;
  root.append(
    r.over ? null : el('p', { class: 'dice-label' }, r.canMove ? 'Your roll' : `${who(s.turn)}’s roll`),
    el('div', { class: 'dice', 'aria-label': 'Dice' }, r.over ? null : [die(s.d1), s.d2 ? die(s.d2) : null, el('span', { class: 'sum' }, '= ' + (s.d1 + s.d2))]),
    row(me), row(1 - me), r.canMove ? (stuck ? pass : shut) : null,
    last ? el('p', { class: 'note' }, last) : null,
    el('p', { class: 'note' }, 'Pick tiles that add up to the roll. Lowest total left wins.'));
};
export const status = r => r.canMove ? (r.s.stuck ? 'No tiles fit this roll. Click “No available move”' : 'Your turn: pick tiles that add up to the dice') : null;

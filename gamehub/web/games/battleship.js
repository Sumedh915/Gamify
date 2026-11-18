import { el } from '../util.js';
export const status = r => {
  if (r.over || r.s.phase !== 0) return null;
  return r.s.ready[r.slot] ? 'Fleet locked in. Waiting for your opponent…' : 'Arrange your fleet, then press Ready';
};
export default (root, r, api) => {
  const s = r.s, ok = new Set(r.legal.map(m => m[0])), idx = [...Array(100).keys()];
  const grid = (ships, shots, fire, label) => el('div', { class: 'bs', role: 'grid', 'aria-label': label }, idx.map(i => {
    const sh = shots[i], cls = ['bsc'];
    if (ships && ships[i] >= 0) cls.push('ship');
    if (sh === 1) cls.push('miss'); if (sh === 2) cls.push('hit'); if (sh === 3) cls.push('sunk');
    if (s.lastShot === i && ((s.lastBy === r.slot) === fire)) cls.push('last');
    return el('button', { class: cls.join(' '), disabled: !(fire && r.canMove && ok.has(i)), 'aria-label': `${'ABCDEFGHIJ'[i % 10]}${Math.floor(i / 10) + 1}`, onclick: () => api.move([i]) }, sh === 1 ? '•' : sh >= 2 ? '✕' : '');
  }));
  const fleet = left => el('div', { class: 'fleet' }, s.names.map((n, i) => el('span', { class: left[i] === 0 ? 'gone' : '' }, `${n} (${s.lens[i]})`)));
  if (s.phase === 0) {
    root.append(el('h2', {}, 'Your fleet'), grid(s.mine, s.incoming, false, 'Your fleet'), fleet([1, 1, 1, 1, 1]),
      el('div', { class: 'row' },
        el('button', { class: 'btn', disabled: !r.canMove, onclick: () => api.move([0]) }, 'Shuffle fleet'),
        el('button', { class: 'btn sun', disabled: !r.canMove, onclick: () => api.move([1]) }, 'Ready')));
    return;
  }
  root.append(el('div', { class: 'bss' },
    el('div', { class: 'bsw' }, el('h2', {}, 'Enemy waters'), grid(s.enemy, s.outgoing, true, 'Enemy waters'), fleet(s.enemyLeft)),
    el('div', { class: 'bsw' }, el('h2', {}, 'Your fleet'), grid(s.mine, s.incoming, false, 'Your fleet'), fleet(s.myLeft))),
    s.note ? el('p', { class: 'note' }, `${s.lastBy === r.slot ? 'You' : 'Opponent'}: ${s.note}`) : null);
};

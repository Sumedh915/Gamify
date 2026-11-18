import { el } from '../util.js';
export default (root, r, api) => {
  const s = r.s, n = s.size, ok = new Set(r.legal.map(m => m[0] * n + m[1])), me = r.slot;
  const cells = [];
  for (let y = 0; y < n; y++) for (let x = 0; x < n; x++) {
    const p = s.r[0] === y && s.c[0] === x ? 0 : s.r[1] === y && s.c[1] === x ? 1 : -1;
    cells.push(el('button', { class: 't', disabled: !ok.has(y * n + x), 'aria-label': p >= 0 ? `Player ${p + 1}` : `Row ${y + 1} column ${x + 1}`, onclick: () => api.move([y, x]) },
      p >= 0 ? el('span', { class: `pawn p${p}${s.it === p ? ' it' : ''}` }, s.it === p ? 'IT' : '') : null));
  }
  const who = p => p === me ? 'You' : 'Opponent';
  root.append(el('div', { class: 'tag' }, cells),
    el('p', { class: 'tag-info' }, `${who(s.it)} ${s.it === me ? 'are' : 'is'} it${s.cool > 0 ? ' (tag-back protection)' : ''}. Turns left: ${s.left}`),
    el('p', { class: 'note' }, `Turns spent as “it”: you ${s.itCount[me]} · opponent ${s.itCount[1 - me]}. Fewest wins. Step next to the runner to tag them.`));
};

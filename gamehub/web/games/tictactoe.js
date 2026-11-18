import { el } from '../util.js';
export default (root, r, api) => {
  const s = r.s, ok = new Set(r.legal.map(m => m[0]));
  root.append(el('div', { class: 'ttt' }, s.board.map((v, i) =>
    el('button', { class: `cell p${v}${s.line && s.line.includes(i) ? ' win' : ''}`, 'aria-label': `Square ${i + 1}`, disabled: !ok.has(i), onclick: () => api.move([i]) }, v < 0 ? '' : v ? '○' : '✕'))));
};

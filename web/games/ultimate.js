import { el } from '../util.js';
export default (root, r, api) => {
  const s = r.s, ok = new Set(r.legal.map(m => m[0] * 9 + m[1])), idx = [...Array(9).keys()];
  root.append(el('div', { class: 'ult' }, idx.map(b =>
    el('div', { class: 'sub' + (!r.over && s.bw[b] === -1 && (s.next === -1 || s.next === b) ? ' active' : '') },
      idx.map(k => { const v = s.c[b * 9 + k], last = s.last && s.last[0] === b && s.last[1] === k;
        return el('button', { class: `cell p${v}${last ? ' last' : ''}`, disabled: !ok.has(b * 9 + k), onclick: () => api.move([b, k]) }, v < 0 ? '' : v ? '○' : '✕'); }),
      s.bw[b] >= 0 ? el('div', { class: `big p${s.bw[b]}` }, s.bw[b] === 2 ? '–' : s.bw[b] ? '○' : '✕') : null))));
};
export const status = r => !r.over && r.s.next === -1 ? (r.canMove ? 'Your turn: play in any open board' : null) : null;

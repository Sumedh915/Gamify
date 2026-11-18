import { el } from '../util.js';
export default (root, r, api) => {
  const s = r.s, ok = new Set(r.legal.map(m => m[0])), line = new Set(s.line || []);
  root.append(el('div', { class: 'c4' }, [...Array(7).keys()].map(c =>
    el('button', { class: 'c4col', 'aria-label': `Drop in column ${c + 1}`, disabled: !ok.has(c), onclick: () => api.move([c]) },
      [...Array(6).keys()].map(q => { const i = q * 7 + c, v = s.b[i];
        return el('span', { class: `disc${v >= 0 ? ' p' + v : ''}${line.has(i) ? ' win' : ''}${s.last === i ? ' last' : ''}` }); })))),
    el('p', { class: 'note' }, 'Red vs Yellow. Four in a row wins.'));
};

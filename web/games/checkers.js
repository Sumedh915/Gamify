import { el } from '../util.js';
export default (root, r, api) => {
  const s = r.s, flip = r.slot === 1, legal = r.legal; let path = [];
  const pre = (m, p) => p.every((q, i) => m[i] === q);
  const draw = () => {
    const starts = new Set(legal.map(m => m[0]));
    const dests = new Set(legal.filter(m => path.length && pre(m, path) && m.length > path.length).map(m => m[path.length]));
    const cells = [];
    for (let rr = 0; rr < 8; rr++) for (let cc = 0; cc < 8; cc++) {
      const r0 = flip ? 7 - rr : rr, c0 = flip ? 7 - cc : cc, sq = r0 * 8 + c0, v = s.b[sq];
      cells.push(el('div', { class: `ck ${(r0 + c0) % 2 ? 'dark' : 'light'}${path.includes(sq) ? ' sel' : ''}${dests.has(sq) ? ' dest' : ''}${!path.length && starts.has(sq) ? ' can' : ''}${s.last && s.last.includes(sq) ? ' last' : ''}`, onclick: () => click(sq) },
        v ? el('span', { class: `piece p${(v - 1) >> 1}` }, v % 2 === 0 ? '♛' : '') : null));
    }
    root.replaceChildren(el('div', { class: 'checkers' }, cells), el('p', { class: 'note' }, 'Click a piece, then where it goes. Jumps are forced; click each landing square for multi-jumps.'));
  };
  const click = sq => {
    if (!r.canMove) return;
    const p2 = [...path, sq];
    if (path.length && legal.some(m => pre(m, p2))) {
      path = p2;
      if (legal.some(m => m.length === path.length && pre(m, path))) return api.move(path);
    } else path = legal.some(m => m[0] === sq) ? [sq] : [];
    draw();
  };
  draw();
};

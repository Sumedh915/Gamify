import { el, sv } from '../util.js';
let mode = 'move';
export default (root, r, api) => {
  const s = r.s, me = r.slot, flip = me === 1, C = 50, G = 10, S = C + G, W = 9 * S + 10;
  const X = c => 10 + (flip ? 8 - c : c) * S, Y = q => 10 + (flip ? 8 - q : q) * S;
  const WX = c => 10 + (flip ? 7 - c : c) * S, WY = q => 10 + (flip ? 7 - q : q) * S;
  const pawn = new Set(r.legal.filter(m => m[0] === 0).map(m => m[1] * 9 + m[2]));
  const wall = new Set(r.legal.filter(m => m[0] > 0).map(m => m.join()));
  if (mode !== 'move' && !wall.size) mode = 'move';
  const draw = () => {
    root.replaceChildren();
    const svg = sv('svg', { viewBox: `0 0 ${W} ${W}`, class: 'quor', role: 'img', 'aria-label': 'Quoridor board' });
    for (let q = 0; q < 9; q++) for (let c = 0; c < 9; c++) {
      const ok = mode === 'move' && pawn.has(q * 9 + c);
      svg.append(sv('rect', { x: X(c), y: Y(q), width: C, height: C, rx: 6, class: 'qcell' + (q === 0 ? ' g0' : q === 8 ? ' g1' : '') + (ok ? ' ok' : ''), onclick: ok ? () => api.move([0, q, c]) : null }));
    }
    s.hw.forEach(([q, c]) => svg.append(sv('rect', { x: WX(c), y: WY(q) + C, width: 2 * C + G, height: G, rx: 3, class: 'qwall' })));
    s.vw.forEach(([q, c]) => svg.append(sv('rect', { x: WX(c) + C, y: WY(q), width: G, height: 2 * C + G, rx: 3, class: 'qwall' })));
    if (mode !== 'move') for (let q = 0; q < 8; q++) for (let c = 0; c < 8; c++) {
      const t = mode === 'h' ? 1 : 2;
      if (!wall.has([t, q, c].join())) continue;
      const slot = t === 1 ? { x: WX(c), y: WY(q) + C - 4, width: 2 * C + G, height: G + 8 } : { x: WX(c) + C - 4, y: WY(q), width: G + 8, height: 2 * C + G };
      svg.append(sv('rect', { ...slot, rx: 4, class: 'qslot', onclick: () => api.move([t, q, c]) }));
    }
    for (const p of [0, 1]) svg.append(sv('circle', { cx: X(s.pc[p]) + C / 2, cy: Y(s.pr[p]) + C / 2, r: 18, class: 'pawn-c p' + p }));
    root.append(svg);
    if (r.canMove) root.append(el('div', { class: 'seg' },
      [['move', 'Move pawn'], ['h', 'Wall: horizontal'], ['v', 'Wall: vertical']].map(([m, t]) =>
        el('button', { class: 'btn' + (mode === m ? ' on' : ''), disabled: m !== 'move' && !wall.size, onclick: () => { mode = m; draw(); } }, t))));
    root.append(el('p', { class: 'note' }, `Walls left: you ${s.walls[me]} · opponent ${s.walls[1 - me]}. Reach the far edge first.`));
  };
  draw();
};

import { el, sv } from '../util.js';
export default (root, r, api) => {
  const s = r.s, N = s.N, G = 80, P = 30, H = N * (N + 1), W = N * G + 2 * P, ok = new Set(r.legal.map(m => m[0]));
  const svg = sv('svg', { viewBox: `0 0 ${W} ${W}`, class: 'dots', role: 'img', 'aria-label': 'Dots and boxes board' });
  for (let i = 0; i < N * N; i++) if (s.own[i] >= 0)
    svg.append(sv('rect', { x: P + (i % N) * G + 8, y: P + Math.floor(i / N) * G + 8, width: G - 16, height: G - 16, rx: 8, class: 'box p' + s.own[i] }));
  for (let e = 0; e < 2 * H; e++) {
    let x1, y1, x2, y2;
    if (e < H) { x1 = P + (e % N) * G; y1 = P + Math.floor(e / N) * G; x2 = x1 + G; y2 = y1; }
    else { const j = e - H; x1 = P + (j % (N + 1)) * G; y1 = P + Math.floor(j / (N + 1)) * G; x2 = x1; y2 = y1 + G; }
    const o = s.eo[e];
    svg.append(sv('line', { x1, y1, x2, y2, class: 'edge' + (o >= 0 ? ' drawn p' + o : ok.has(e) ? ' open' : '') }));
    if (ok.has(e)) svg.append(sv('line', { x1, y1, x2, y2, class: 'hit', onclick: () => api.move([e]) }));
  }
  for (let i = 0; i <= N; i++) for (let j = 0; j <= N; j++) svg.append(sv('circle', { cx: P + j * G, cy: P + i * G, r: 6, class: 'dot' }));
  root.append(svg, el('div', { class: 'score' }, `Red ${s.sc[0]} – ${s.sc[1]} Blue`));
};
export const status = r => !r.over && r.canMove && r.s.turn === r.slot ? 'Your turn: draw a line (close a box to go again)' : null;

export const el = (tag, props = {}, ...kids) => {
  const e = document.createElement(tag);
  for (const [k, v] of Object.entries(props)) {
    if (k === 'class') e.className = v;
    else if (k.startsWith('on')) e.addEventListener(k.slice(2), v);
    else if (v !== false && v != null) e.setAttribute(k, v === true ? '' : v);
  }
  for (const k of kids.flat(2)) if (k != null && k !== false) e.append(k.nodeType ? k : document.createTextNode(k));
  return e;
};
export const sv = (tag, props = {}, ...kids) => {
  const e = document.createElementNS('http://www.w3.org/2000/svg', tag);
  for (const [k, v] of Object.entries(props)) {
    if (k.startsWith('on')) e.addEventListener(k.slice(2), v);
    else if (v != null && v !== false) e.setAttribute(k, v);
  }
  for (const k of kids.flat(2)) if (k != null) e.append(k.nodeType ? k : document.createTextNode(k));
  return e;
};
export const PLAYER = ['Red', 'Blue'];
export const sleep = ms => new Promise(r => setTimeout(r, ms));
/** Big X (player 0) or O (player 1) drawn as SVG so it always fills the same share of its square. */
export const mark = v => sv('svg', { viewBox: '0 0 100 100', class: 'mark', 'aria-hidden': 'true' },
  v ? sv('circle', { cx: 50, cy: 50, r: 30 }) : [sv('line', { x1: 20, y1: 20, x2: 80, y2: 80 }), sv('line', { x1: 80, y1: 20, x2: 20, y2: 80 })]);

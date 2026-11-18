import { el } from './util.js';

/* how: the rules of the game itself. app: how to play it on GameHub. */
export const RULES = {
  tictactoe: {
    how: [
      'Two players take turns marking squares on a 3 by 3 grid.',
      'Red plays X. Blue plays O. Red goes first.',
      'Get three of your marks in a row to win. Rows, columns and diagonals all count.',
      'If all nine squares fill up and nobody has three in a row, the game is a draw.'],
    app: [
      'Click an empty square to place your mark. You can only click on your turn.',
      'The winning line turns green.',
      'Against the computer, you are Red and you move first.']
  },
  ultimate: {
    how: [
      'The board is nine small Tic Tac Toe boards in a 3 by 3 grid.',
      'On your turn, place one mark in a small board.',
      'The square you pick sends your opponent to the matching small board. If you play the top-right square of a small board, your opponent must play in the top-right small board.',
      'If the board you are sent to is already won or full, you may play in any open board.',
      'Win a small board by getting three in a row inside it. That board is now yours.',
      'Win the game by owning three small boards in a row.',
      'If every board is decided and nobody has a line, the player with more boards wins. Equal boards is a draw.'],
    app: [
      'The board you must play in is shaded yellow. If every open board is yellow, you can play anywhere.',
      'Click an empty square inside a yellow board.',
      'The most recent mark has a pale yellow background.',
      'A won board shows a big X or O over it. A dash means that board was drawn.']
  },
  dots: {
    how: [
      'The grid is 4 by 4 boxes, drawn between 5 by 5 dots.',
      'On your turn, draw one line between two neighboring dots.',
      'When you draw the fourth side of a box, you claim it and take another turn.',
      'When every box is claimed, the player with more boxes wins.',
      'Tip: avoid drawing the third side of a box. It lets your opponent claim it.'],
    app: [
      'Click a line between two dots. Lines you can draw light up when you hover over them.',
      'Claimed boxes are shaded in the color of the player who closed them.',
      'The score is under the board.',
      'If you close a box, it stays your turn. The status box keeps saying "Your turn".']
  },
  mancala: {
    how: [
      'Each player has six pits and one store. Your store is the big pit at the end of your row. Every pit starts with 4 stones.',
      'On your turn, pick one of your pits. Pick up all its stones and drop them one at a time into the next pits, going around the board.',
      'Drop a stone in your own store when you pass it. Skip your opponent\'s store.',
      'If your last stone lands in your store, you go again.',
      'If your last stone lands in an empty pit on your side, and the pit across from it has stones, you capture them. They go to your store along with your last stone.',
      'The game ends when one side has no stones. Each player adds the stones left on their own side to their store.',
      'The player with more stones in their store wins.'],
    app: [
      'Your pits are the bottom row. Your store is on the right.',
      'Stones travel counter-clockwise: left to right along your row, into your store, then right to left along your opponent\'s row.',
      'Pits you can pick have a colored ring. Click one.',
      'When your opponent moves, their pit is highlighted, the stones drop one at a time, and a line of text under the board explains what happened.']
  },
  shutbox: {
    how: [
      'Each player has their own box with tiles numbered 1 to 9.',
      'Players take turns, one roll at a time. You roll two dice. Once tiles 7, 8 and 9 are all shut, you roll one die instead.',
      'After a roll, shut any open tiles that add up to the roll. For a roll of 8 you could shut 8, or 5 and 3, or 1, 2 and 5.',
      'If no open tiles add up to your roll, you are finished. Your score is the total of your open tiles.',
      'If you shut all nine tiles, your score is 0.',
      'A finished player is skipped. The other player keeps rolling until they finish too.',
      'The lowest score wins. The same score is a draw.'],
    app: [
      'The game picks at random who rolls first.',
      'The dice are shown above the boxes. Your box is the top row.',
      'Click the open tiles you want to shut, then press "Shut selected tiles". The button turns on when your tiles add up to the roll.',
      'If you cannot make the roll, a "No available move" button appears. Click it to finish your box.',
      'The computer\'s roll is shown before it plays. A line of text tells you what it did.',
      'Shut tiles turn dark gray. The text next to each box shows the open total, or the final score.']
  },
  yahtzee: {
    how: [
      'The game lasts 13 rounds. In each of your turns you roll five dice, up to three times in total.',
      'After a roll you can keep some dice and re-roll the rest.',
      'At the end of your turn, score your dice in one open category. Each category can only be used once. If your dice do not fit, you score 0 there.',
      'Ones to Sixes: add up the dice that match. If these six add up to 63 or more, you get a 35 point bonus.',
      'Three of a kind and Four of a kind: the total of all five dice. Chance: the total of all five dice.',
      'Full house (three of one number and two of another): 25. Small straight (four in a row): 30. Large straight (five in a row): 40. Yahtzee (five of a kind): 50.',
      'The highest total after 13 rounds wins. This version has no extra Yahtzee bonus and no joker rule.'],
    app: [
      'Your first roll of each turn is done for you.',
      'Click dice to keep them. Kept dice turn yellow. Then press the roll button. It shows how many dice you will roll and how many rolls are left.',
      'To score, click the number button in the category row, in your column. The number on the button is what you would score.',
      'Your column is "You". Your opponent\'s column is "Opponent".']
  },
  quoridor: {
    how: [
      'Each player has one pawn and 10 walls. Reach the opposite edge of the board to win.',
      'On your turn, either move your pawn one square (up, down, left or right) or place one wall.',
      'A wall is two squares long. Pawns cannot pass through walls.',
      'If your opponent\'s pawn is next to yours, you can jump over it. If you cannot jump straight, you may step to either side of it.',
      'You cannot place a wall that completely cuts a player off from their goal.',
      'If nobody has won after 300 turns, the player closer to their goal wins.'],
    app: [
      'You always play from the bottom of the screen and race to the top.',
      'In "Move pawn" mode, yellow squares show where you can go. Click one.',
      'To build, press "Wall: horizontal" or "Wall: vertical". Then click the gap between squares where the wall should go. The gap lights up when you hover.',
      'The number of walls each player has left is shown under the board.']
  },
  checkers: {
    how: [
      'This is American checkers on an 8 by 8 board. Each player starts with 12 pieces on the dark squares.',
      'A piece moves one square diagonally forward onto an empty dark square.',
      'To capture, jump diagonally over an opponent\'s piece into the empty square behind it. The captured piece is removed.',
      'Jumps are forced. If you can jump, you must.',
      'If your piece can jump again after landing, it must keep jumping in the same turn.',
      'A piece that reaches the far row becomes a king. A king can move and jump both forward and backward. Becoming a king ends that turn.',
      'You win when your opponent has no pieces or no legal moves.',
      'If 80 turns in a row pass with only king moves and no captures, the game is a draw.'],
    app: [
      'Red moves first. Your pieces start at the bottom of the screen.',
      'Click one of your pieces. Pieces that can move have a yellow ring.',
      'Green rings show where that piece can go. Click a green square to move.',
      'For a multi-jump, click each landing square in order. The move is made after the last jump.',
      'Kings show a crown. The squares of the last move are shaded.']
  },
  connect4: {
    how: [
      'The board has 7 columns and 6 rows.',
      'Players take turns dropping a disc into a column. The disc falls to the lowest empty spot.',
      'Get four of your discs in a row to win. Rows, columns and diagonals all count.',
      'If the board fills up with no four in a row, the game is a draw.'],
    app: [
      'Click a column to drop your disc.',
      'Red goes first. Red uses red discs and the other player uses yellow discs.',
      'The latest disc has a lighter center. The winning four get a white outline.']
  },
  battleship: {
    how: [
      'Each player hides a fleet of five ships on a 10 by 10 grid: Carrier (5 squares), Battleship (4), Cruiser (3), Submarine (3) and Destroyer (2).',
      'Players take turns firing at one square of the enemy grid. You are told if the shot was a hit or a miss.',
      'When every square of a ship has been hit, it sinks and its name is announced.',
      'Each turn is one shot, whether it hits or misses.',
      'The first player to sink all five enemy ships wins.'],
    app: [
      'Your fleet is placed for you. Press "Shuffle fleet" for a new layout, then "Ready". The game starts when both players are ready.',
      'Click a square in "Enemy waters" to fire. A dot is a miss, an X is a hit and dark red is a sunk ship.',
      '"Your fleet" shows where the enemy has fired. The latest shot has a yellow outline.',
      'The lists under the boards show each fleet. Sunk ships are crossed out.',
      'A line under the boards says what happened on the last shot.']
  }
};

let closeFn = null;
export const closeRules = () => closeFn && closeFn();

export function showRules(id, name) {
  const r = RULES[id]; if (!r) return;
  closeRules();
  const prev = document.activeElement;
  const list = a => el('ul', {}, a.map(t => el('li', {}, t)));
  const x = el('button', { class: 'modal-x', 'aria-label': 'Close rules', onclick: () => closeRules() }, '×');
  const box = el('div', { class: 'modal', role: 'dialog', 'aria-modal': 'true', 'aria-labelledby': 'rules-title' },
    x, el('h2', { id: 'rules-title' }, `${name}: rules`),
    el('h3', {}, 'How the game works'), list(r.how),
    el('h3', {}, 'How to play it here'), list(r.app));
  const back = el('div', { class: 'modal-back', onclick: e => { if (e.target === back) closeRules(); } }, box);
  const onKey = e => { if (e.key === 'Escape') closeRules(); };
  closeFn = () => { document.removeEventListener('keydown', onKey); back.remove(); closeFn = null; prev && prev.focus && prev.focus(); };
  document.addEventListener('keydown', onKey);
  document.body.append(back); x.focus();
}

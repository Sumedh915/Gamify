# Gamify

Eleven two-player games, playable between two computers (join by game code) or against the computer (easy / hard / unbeatable), plus a tournament mode and a stats page.

Java 17+ backend (JDK only, no libraries), plain JavaScript frontend (no build step).

## Run it

- **VS Code:** install "Extension Pack for Java", open this folder, press F5 (or Run on `Main.java`).
- **Terminal:** `./run.sh` (Windows: `run.bat`), then open http://localhost:8080.

Two players on one network: the host shares `http://<host-ip>:8080`. Two browser tabs on one machine also work.

## URLs

`/` home, `/tictactoe` (any game id), `/tictactoe/AB3XY` (a live game), `/tournament`, `/stats`.
Every page has Back, Home, a Games menu, Tournament and Stats in the top bar.

## Layout

```
src/gamehub/
  Main.java              HTTP server + static files (single-page-app fallback)
  Api.java               /api/* endpoints
  Room.java, Rooms.java  game rooms, codes, tournament logic, AI turns
  Stats.java             win/loss/draw counters -> data/stats.properties
  GameRegistry.java      the list of games (one line each)
  core/                  GameState, AI, Minimax (shared alpha-beta), Json
  games/                 one rules file per game (TicTacToe.java, Checkers.java, ...)
  ai/                    one AI file per game (TicTacToeAI.java, CheckersAI.java, ...)
web/
  index.html, style.css, app.js   router, lobby, live room, stats
  games/<id>.js                   one renderer per game
```

## Add a game (4 steps)

1. `games/MyGame.java` extends `GameState`: `copy()`, `moves(p)`, `apply(p, move)`, `fill(map, viewer)`.
2. `ai/MyGameAI.java`: extend `Minimax` (supply `eval()`), or implement `AI` directly for dice / hidden-information games.
3. Add one `reg(new GameDef("mygame", ...))` line in `GameRegistry.java`.
4. `web/games/mygame.js`: `export default (root, room, api) => { ... }` draws the board and calls `api.move([...])`.

The lobby, URL, game codes, computer modes, tournament pool and stats pick it up automatically.

## Put it online with a domain

The server reads `PORT` and listens on all interfaces.

- **Render / Railway / Fly.io:** push this folder to GitHub, create a web service from the included `Dockerfile`, then add your custom domain in their dashboard. You get a free default address and HTTPS straight away. Mount a persistent disk at `/app/data` to keep stats between deploys.
- **Your own VPS:** install JDK 21, run `./run.sh`, and put Caddy in front for HTTPS: `yourdomain.com { reverse_proxy localhost:8080 }`
- Rooms live in memory, so a restart ends games in progress.

## How the AIs work

| Game | Easy | Hard | Unbeatable |
|---|---|---|---|
| Tic tac toe | mostly random | 3 moves ahead | full solve, never loses |
| Ultimate TTT, Dots and boxes, Mancala, Quoridor, Tag, Checkers, Connect 4 | mostly random | shallow alpha-beta | alpha-beta with iterative deepening, about 2.5-3 s per move |
| Shut the box | mostly random | minimises expected score (exact DP) | exact DP, plays for win probability against your score |
| Yahtzee | simple | keep-most-common heuristic | exact expectimax over all rerolls |
| Battleship | random shots | hunt and target | probability density, public information only |

Caveat: only Tic Tac Toe is mathematically solved. The others are time-limited search: very strong, not proven perfect. Shut the Box, Yahtzee and Battleship involve luck or hidden information, so no fair AI can guarantee you zero wins; the stats page counts what really happens. To strengthen a search AI, raise `unbeatableMs()` in its AI file.

Rules notes: Yahtzee has no extra-Yahtzee bonus or joker. Tag is turn-based: whoever spent fewer turns as "it" after 60 turns wins. Battleship fleets are auto-placed (Shuffle until happy).

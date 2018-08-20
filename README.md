# keyboard-warrior

Keyboard Warrior is a typing speed game that you can play on your web browser.

The game is a fresh venture into ClojureScript and web game development. It is in alpha, development currently on hold.

### play

~~[agaric.net/dev/keyboard-warrior](https://agaric.net/dev/keyboard-warrior)~~ (hosting currently unavailable)

### run locally

- [clone](https://git-scm.com/docs/git-clone)
- [`lein`](https://leiningen.org/)` do clean; lein cljsbuild once min; lein run <port>`
    - first deploy the clientside scripts, then run the server
- visit `localhost:<port>` in a browser

### anticipated features

- clean, minimal, and pleasant look & feel
- highly responsive multi-play
    - first with strangers, eventually in rooms/channels
    - spectating support
    - authentication and detailed statistics
- various types of play
    - single, multi
    - time-limit, word-limit, no limit
    - story
    - combative
- wide range of text
    - literary
    - programming
    - news articles
    - eventually support plugging in user-customised text

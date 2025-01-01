# Networking with Replicant tutorial

This repo demonstrates data-driven networking with Replicant as part of a
tutorial. To get it up and running:

1. Run `make shadow` to start the shadow-cljs build.
2. Run `make tailwind` to start the tailwind build.
3. Start a Clojure REPL, and evaluate `(def server (start-server 8088))` in the
   comment block of `toil.server`.

(defproject keyboard-warrior "0.1.0-SNAPSHOT"
  :description "typing speed game"
  :url "https://alocy.be/app/keyboard-warrior"
  :license {:name "mit"}
  :min-lein-version "2.7.1"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.854"]
                 [org.clojure/core.async "0.3.443"
                  :exclusions [org.clojure/tools.reader]]
                 [compojure "1.6.0"]
                 [hiccup "1.0.5"]
                 [http-kit "2.2.0"]
                 [reagent "0.7.0"]
                 [ring/ring-defaults "0.3.1"]
                 [ring "1.6.2"]
                 [com.taoensso/sente "1.11.0"]]
  :plugins [[lein-ancient "0.6.10"]
            [lein-cljsbuild "1.1.5"
             :exclusions [[org.clojure/clojure]]]
            [lein-figwheel "0.5.10"]]
  :source-paths ["src/clj"]
  :main keyboard-warrior-server.core
  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src/cljs"]
                :figwheel {:on-jsload "keyboard-warrior.core/on-js-reload"}
                :compiler {:main keyboard-warrior.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/kw.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true
                           :preloads [devtools.preload]}}
               {:id "min"
                :source-paths ["src/cljs"]
                :compiler {:output-to "resources/public/js/compiled/kw.js"
                           :main keyboard-warrior.core
                           :optimizations :advanced
                           :pretty-print false}}]}
  :figwheel {:css-dirs ["resources/public/css"]
             ;; :nrepl-port 7888
             ;; :ring-handler hello_world.server/handler
             ;; :open-file-command "myfile-opener"
             ;; :open-file-command "emacsclient"
             ;; :repl false
             ;; :server-logfile "tmp/logs/figwheel-logfile.log"
             ;; :server-logfile false
             }
  :profiles
  {:dev {:dependencies [[binaryage/devtools "0.9.4"]
                        [figwheel-sidecar "0.5.10"]
                        [com.cemerick/piggieback "0.2.2"]]
         :source-paths ["src" "dev"]
         ;; :plugins [[cider/cider-nrepl "0.12.0"]]
         :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
         :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                           :target-path]}}
  :jvm-opts ["-Xmx1g"])

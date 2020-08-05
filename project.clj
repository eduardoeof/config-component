(defproject eduardoeof/config-component "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/eduardoeof/config-component"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.stuartsierra/component "1.0.0"]
                 [clj-commons/clj-yaml "0.7.0"]
                 [cheshire "5.10.0"]]

  :repl-options {:init-ns eduardoeof.config-component})

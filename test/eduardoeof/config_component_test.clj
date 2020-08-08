(ns eduardoeof.config-component-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [eduardoeof.config-component :refer [new-config]]))

(defn- create-and-start-system-map [file-name]
  (component/start
    (component/system-map
      :config (new-config file-name))))

(defn- stop-system-map [file-name]
  (component/stop (create-and-start-system-map file-name)))

(deftest load-config-json-file-test
  (let [system-map (create-and-start-system-map "test/resources/config.json")]
    (is (.equals (:config system-map)
                 {:db {:host "localhost"
                       :port (int 35000) ; credits to cheshire issue#105
                       :user "Nina"}
                  :file {:name "config"
                         :type "json"}
                  :file-name "test/resources/config.json"}))))

(deftest load-config-yaml-file-test
  (let [system-map (create-and-start-system-map "test/resources/config.yml")]
    (is (.equals (:config system-map)
                 {:file-name "test/resources/config.yml"
                  :db {:host "localhost"
                       :port 35000
                       :user "Nina"}
                  :file {:name "config"
                         :type "yaml"}}))))

(deftest load-config-edn-file-test
  (let [system-map (create-and-start-system-map "test/resources/config.edn")]
    (is (.equals (:config system-map)
                 {:file-name "test/resources/config.edn"
                  :db {:host "localhost"
                       :port 35000
                       :user "Nina"}
                  :file {:name "config"
                         :type "edn"}}))))

(deftest stop-config-component-test
  (let [system-map (stop-system-map "test/resources/config.yml")]
    (is (= nil
           (:config system-map)))))

(comment
  (use 'clojure.test)
  (run-tests))

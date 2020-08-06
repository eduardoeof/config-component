(ns eduardoeof.config-component-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [eduardoeof.config-component :refer [new-config]]))

(defn- create-and-start-system-map
  [file-name]
  (component/start
    (component/system-map
      :config (new-config file-name))))

(deftest load-config-json-file-test
  (let [system-map (create-and-start-system-map "test/resources/config.json")]
    (is (= {:db {:host "localhost"
                 :port 35000
                 :user "Nina"}
            :file {:name "config"
                   :type "json"}} 
           (:config system-map)))))

(deftest load-config-yaml-file-test
  (let [system-map (create-and-start-system-map "test/resources/config.yml")]
    (is (= {:db {:host "localhost"
                 :port 35000
                 :user "Nina"}
            :file {:name "config"
                   :type "yaml"}} 
           (:config system-map)))))


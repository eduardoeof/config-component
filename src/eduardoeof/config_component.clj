(ns eduardoeof.config-component
  (:require [com.stuartsierra.component :as component]
            [cheshire.core :as json]
            [clj-yaml.core :as yaml]
            [clojure.string :as str]))

(defn- yml? [type]
  (= type :yml))

(defn- get-file-type [file-name]
  (-> file-name
      (str/split #"\.")
      last
      keyword
      ((fn [type] (if (yml? type) :yaml type)))))

(defn- parse-yaml [file-name]
  (-> file-name
      slurp
      yaml/parse-string))

(defn- parse-json [file-name]
  (-> file-name
      slurp 
      (json/parse-string true)))

(defrecord Config [file-name] 
  component/Lifecycle
  (start [this]
    (case (get-file-type file-name) 
      :json (parse-json file-name)
      :yaml (parse-yaml file-name)))

  (stop [this]
    nil))

(defn new-config [file-name]
  (map->Config {:file-name file-name}))


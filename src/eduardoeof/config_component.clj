(ns eduardoeof.config-component
  (:require [com.stuartsierra.component :as component]
            [cheshire.core :as json]
            [clj-yaml.core :as yaml]
            [clojure.string :as str]
            [clojure.edn :as edn]))

(defn- yml? [type]
  (= type :yml))

(defn- get-file-format [file-name]
  (let [splits (str/split file-name #"\.")]
    (if (= 1 (count splits)) 
      :unknown
      (-> splits 
          last
          keyword
          ((fn [format] (if (yml? format) :yaml format)))))))

(defn- parse-yaml [file-name]
  (-> file-name
      slurp
      yaml/parse-string))

(defn- parse-json [file-name]
  (-> file-name
      slurp 
      (json/parse-string true)))

(defn- parse-edn [file-name]
  (-> file-name
      slurp
      edn/read-string))

(defn- build-not-supported-exception
  [file-name format]
  (ex-info "File format not supported"
           {:reason ::file-format-not-supported-exception
            :file-name file-name
            :format format
            :tip (str "Check if the file name has the format explicited (e.g. \".json\") " 
                      "or it is in an unsupported format.")}))

(defn- load-config [file-name]
  (let [format (get-file-format file-name)]
    (case format 
      :json (parse-json file-name)
      :yaml (parse-yaml file-name)
      :edn (parse-edn file-name)
      (throw (build-not-supported-exception file-name
                                            format)))))

(defrecord Config [file-name] 
  component/Lifecycle

  (start [this]
    (let [config (load-config file-name)]
      (merge this config)))

  (stop [this]
    nil))

(defn new-config [file-name]
  (map->Config {:file-name file-name}))


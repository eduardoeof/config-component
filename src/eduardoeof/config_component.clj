(ns eduardoeof.config-component
  (:require [com.stuartsierra.component :as component]
            [cheshire.core :as json]
            [clj-yaml.core :as yaml]
            [clojure.string :as str]
            [clojure.edn :as edn]))

(def ^:private format->parser {:json #(json/parse-string % true)
                               :yaml yaml/parse-string
                               :edn  edn/read-string})

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

(defn- file-format-not-supported-exception
  [file-name format]
  (ex-info "File format not supported"
           {:exception-type ::file-format-not-supported-exception
            :file-name file-name
            :format format
            :tip (str "Check if the file name has the format explicited (e.g. \".json\") " 
                      "or it is in an unsupported format.")}))

(defn- parse [parser file-name]
  (-> file-name
      slurp
      parser))

(defn- load-config [file-name]
  (let [format (get-file-format file-name)]
    (if-let [parser (format->parser format)]
      (parse parser file-name)
      (throw (file-format-not-supported-exception file-name
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


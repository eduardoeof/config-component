(ns eduardoeof.config-component-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [matcher-combinators.test :refer [match?]]
            [eduardoeof.config-component :refer [new-config]]))

(defn- create-and-start-system-map [file-name]
  (component/start
    (component/system-map
      :config (new-config file-name))))

(deftest start-test
  (testing "Load a json config file"
    (let [system-map (create-and-start-system-map "test/resources/config.json")]
      (is (match? {:config {:db {:host "localhost"
                                 :port (int 35000) ; credits to cheshire issue#105
                                 :user "Nina"}
                            :file {:name "config"
                                   :type "json"}
                            :file-name "test/resources/config.json"}}
                  system-map))))
  
  (testing "Load a yaml config file"
    (let [system-map (create-and-start-system-map "test/resources/config.yml")]
      (is (match? {:config {:file-name "test/resources/config.yml"
                            :db {:host "localhost"
                                 :port 35000
                                 :user "Nina"}
                            :file {:name "config"
                                   :type "yaml"}}}
                  system-map))))

  (testing "Load a edn config file"
    (let [system-map (create-and-start-system-map "test/resources/config.edn")]
      (is (match? {:config {:file-name "test/resources/config.edn"
                            :db {:host "localhost"
                                 :port 35000
                                 :user "Nina"}
                            :file {:name "config"
                                   :type "edn"}}}
                  system-map))))

  (testing "Load config file without format and throw an exception"
    (try
      (create-and-start-system-map "test/resources/fake-config.xyz")
      (catch Throwable t
        (let [ex (-> t 
                     Throwable->map 
                     :via 
                     last)] 
          (is (= "File format not supported"
                 (:message  ex)))
          (is (= {:exception-type :eduardoeof.config-component/file-format-not-supported-exception
                  :file-name "test/resources/fake-config.xyz"
                  :format :xyz
                  :tip "Check if the file name has the format explicited (e.g. \".json\") or it is in an unsupported format."}
                 (:data ex)))))))

  (testing "Load config file with not supported format and throw an exception"
    (try
      (create-and-start-system-map "test/resources/fake-config")
      (catch Throwable t
        (let [ex (-> t 
                     Throwable->map 
                     :via 
                     last)] 
          (is (= "File format not supported"
                 (:message  ex)))
          (is (= {:exception-type :eduardoeof.config-component/file-format-not-supported-exception
                  :file-name "test/resources/fake-config"
                  :format :unknown
                  :tip "Check if the file name has the format explicited (e.g. \".json\") or it is in an unsupported format."}
                 (:data ex))))))))

(deftest stop-test
  (testing "Clean loaded data from a config file"
    (let [system-map (create-and-start-system-map "test/resources/config.yml")]
      (is (match? {:config nil}
                  (component/stop system-map))))))


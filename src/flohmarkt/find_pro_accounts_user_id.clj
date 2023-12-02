(ns flohmarkt.find-pro-accounts-user-id
  (:require [babashka.cli :as cli]
            [babashka.http-client :as http]
            [clojure.string :as str]
            [babashka.process :refer [shell process exec]]))

(def spec
  {:help {:default false,
          :ref "help",
          :alias :h,
          :desc "Prints this help message.",
          :default-desc "false"},
   :verbose {:default false,
             :ref "verbose",
             :alias :v,
             :desc "Prints debug messages.",
             :default-desc "false"},
   :url {:default "",
         :alias :u,
         :coerce :string,
         :default-desc "<none>",
         :desc "URL of an article of the pro account.",
         :require true}})

(defn show-help
  [spec]
  (let [headers ["alias" "option" "ref" "default" "description"]
        rows (concat [headers] (cli/opts->table {:spec spec}))
        help (cli/format-table {:rows rows, :indent 2})]
    (println)
    (println help)))

(defn parse-account-id
  [opts]
  (let [url (:url opts)
        res (or (:dummy-body opts) (:body (http/get url)))
        id (re-find #"\d+" (last (str/split res #"posterid")))]
    (when (:verbose opts) (prn (str "Pro account ID: " id "\n")))
    {:user-id (Integer/parseInt id)}))

;; main function
(defn -main
  [& _]
  (let [opts (cli/parse-opts *command-line-args* {:spec spec})]
    (if (or (:help opts) (empty? (:url opts)))
      (show-help spec)
      (parse-account-id opts))))

#!/usr/bin/env bb

(ns flohmarkt.scrape
  (:require [babashka.cli :as cli]
            [babashka.http-client :as http]
            [babashka.fs :as fs]
            [cheshire.core :as json]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [babashka.process :refer [shell process exec]]))

(def root-url "https://www.flohmarkt.test")
(def url-replace-pattern (re-pattern "\\$i"))

(def spec
  {:help {:default false,
          :ref "help",
          :alias :h,
          :desc "Prints this help message.",
          :default-desc "false"},
   :verbose {:default false,
             :ref "verbose",
             :alias :d,
             :desc "Prints debug messages.",
             :default-desc "false"},
   :user-id {:default "",
             :alias :u,
             :coerce :string,
             :default-desc "<none>",
             :desc "User ID of the pro account.",
             :require true},
   :max-sleep {:default 15,
               :alias :s,
               :coerce :int,
               :default-desc "15",
               :desc "Maximum sleep time in seconds.",
               :require false},
   :pages {:default java.lang.Integer/MAX_VALUE,
           :alias :p,
           :coerce :int,
           :default-desc "Integer.MAX_VALUE",
           :desc "Maximum number of pages to crawl.",
           :require false},
   :to-json {:default false,
             :alias :t,
             :coerce :boolean,
             :default-desc "false",
             :desc "Instead of clojure data, output JSON.",
             :require false}})

(defn prepare-files
  []
  (when-not (fs/exists? "./.cache") (fs/create-dir "./.cache"))
  (when (fs/exists? "./result.edn")
    (fs/copy "./result.edn" "./result.edn.bak" {:replace-existing true})))

(defn create-page-url
  [url-template pattern subs]
  (str/replace url-template pattern (str subs)))

(defn pretty-spit-edn
  [filename content]
  (->> content
       pprint
       with-out-str
       (spit filename)))

(defn extract-elements
  [filename operation]
  (-> (shell {:out :string} (str "htmlq " "-f " filename " " operation))
      :out
      str/split-lines))

(defn filename-cached
  [filename fileending]
  (str "./.cache/" filename "." fileending))

(defn url-to-filename-cached
  [url]
  (filename-cached (str/join "" (re-seq #"\w" url)) "html"))

(defn parse-details-from-item-file
  [filename]
  (-> (process "htmlq -f " filename " ul.addetailslist")
      (process {:out :string} "htmlq li --text")
      deref
      :out
      str/split-lines))

(defn htmlq-trim-newline-trim
  [filename operation]
  (-> (shell {:out :string} (str "htmlq " "-f " filename " " operation))
      :out
      str/trim-newline
      str/trim))

(defn rich-item
  [item]
  (let [body (:body (http/get (:url item)))
        filename (url-to-filename-cached (:url item))
        _ (spit filename body)
        details (let [parsed-details (parse-details-from-item-file filename)
                      parsed-details-trimmed (mapv #(str/trim %) parsed-details)
                      parsed-details-cleaned (remove empty?
                                               parsed-details-trimmed)]
                  (apply array-map parsed-details-cleaned))
        description-html (htmlq-trim-newline-trim filename
                                                  "#viewad-description-text")
        name (htmlq-trim-newline-trim filename "#viewad-title --text")
        price (htmlq-trim-newline-trim filename "#viewad-price --text")]
    (fs/delete-if-exists filename)
    (into item
          [{:name name} {:price price} {:description-html description-html}
           {:details details}])))

(defn get-all-pages
  [base-url pages max-sleep]
  (loop [continue true
         page 1
         result []]
    (let [url (create-page-url base-url url-replace-pattern page)
          filename (filename-cached page "html")]
      (if (and continue (<= page pages))
        (do (spit filename (:body (http/get url)))
            (let [elements (extract-elements filename ".ellipsis")
                  elements-titles (extract-elements filename ".ellipsis --text")
                  elements-urls
                    (map #(str root-url %)
                      (extract-elements filename "--attribute href .ellipsis"))
                  element-map (map vector elements-titles elements-urls)]
              (fs/delete-if-exists filename)
              (if (empty? (remove #(= "" %) elements))
                (recur false (inc page) result)
                ;; min sleep time is 5 seconds
                (do (Thread/sleep (* 1000 (rand-nth (range 5 (inc max-sleep)))))
                    (recur true
                           (inc page)
                           (into result
                                 (map #(into {}
                                             [[:name (str (first %))]
                                              [:url (str (last %))]])
                                   element-map)))))))
        (do (pretty-spit-edn "result.edn" result) result)))))

(defn get-all-items
  [input-map max-sleep to-json]
  (loop [urls input-map
         collection []]
    (if (empty? urls)
      (let [res {:data collection}
            res-as-json (json/generate-string res {:pretty true})]
        (pretty-spit-edn "result.edn" res)
        (spit "result.json" res-as-json)
        (if to-json res-as-json res))
      (do (Thread/sleep (* 1000 (rand-nth (range 3 (inc max-sleep)))))
          (recur (rest (shuffle urls))
                 (conj collection (rich-item (first urls))))))))

(defn base-url-user
  [user-id]
  (str root-url
       "/s-bestandsliste.html?userId="
       user-id
       "&sortingField=PRICE_AMOUNT&pageNum=$i"))

(defn show-help
  [spec]
  (let [headers ["alias" "option" "ref" "default" "description"]
        rows (concat [headers] (cli/opts->table {:spec spec}))
        help (cli/format-table {:rows rows, :indent 2})]
    (println)
    (println help)))

(defn -main
  [& _]
  (let [opts (cli/parse-opts *command-line-args* {:spec spec})
        max-sleep (:max-sleep opts)
        pages (:pages opts)
        user-id (:user-id opts)
        verbose (:verbose opts)
        to-json (:to-json opts)]
    (if (or (:help opts) (empty? (:user-id opts)))
      (show-help spec)
      (do (when verbose
            (println (str "User ID: " user-id))
            (println (str "Max sleep: " max-sleep))
            (println (str "Pages: " pages)))
          (prepare-files)
          (-> (base-url-user user-id)
              (get-all-pages pages max-sleep)
              (get-all-items max-sleep to-json))))))
(ns logger
  (:require [clojure.pprint :refer [pprint]]))

(defn request-logger [handler]
  (fn [request]
    (dotimes [x 5]
      (println))
    (println "------------------ REQUEST --------------------")
    (pprint request)

    (handler request)))

(defn response-logger [handler]
  (fn [request]
    (let [response (handler request)]

      (println)
      (println "------------------ RESPONSE --------------------")
      (pprint response)
      response)))

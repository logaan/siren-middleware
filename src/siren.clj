(ns siren
  (:require [clojure.string :refer [split]]))

(defmulti render-siren (comp second list))

(defn siren-converter [handler]
  (fn [request]
    (let [accept     (get-in request [:headers "accept"])
          upper-type (last (split accept #"\+"))]
      (-> (handler request)
          (render-siren upper-type)
          (assoc-in [:headers "Content-Type"]
                    (str "application/vnd.siren+" accept))))))


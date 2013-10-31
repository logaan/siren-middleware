(ns content-types.smile
  (:require [siren :refer :all]
            [cheshire.core :as json])
  (:import [java.io ByteArrayInputStream]))

(defn render-body [body]
  (ByteArrayInputStream. (json/generate-smile body)))

(defmethod render-siren "smile" [response type]
  (update-in response [:body] render-body))


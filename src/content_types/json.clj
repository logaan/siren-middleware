(ns content-types.json
  (:require [siren :refer :all]
            [cheshire.core :as json])
  (:import [java.io ByteArrayInputStream]) )

(defmethod render-siren "json" [response type]
  (update-in response [:body] json/generate-string))


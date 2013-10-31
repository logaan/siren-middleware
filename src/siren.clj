(ns siren
  (:require [clojure.string :refer [split]]))

(defmulti render-siren (comp second list))


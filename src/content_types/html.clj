(ns content-types.html
  (:require [siren :refer :all]
            [clojure.string :refer [join split]]
            [hiccup.core :as hiccup]))

(defn render-property [[k v]]
  [[:dt k] [:dd v]])

(defn render-link [{:keys [rel href]}]
  (let [str-rel (join " " rel)]
    [:li [:a {:href href :rel str-rel} str-rel]]))

(defn generate-html [{:keys [class properties entities actions links]}]
  (hiccup/html
    [:body
     [:h1 (join " " class)]
     [:h2 "Properties"]
     [:dl (mapcat render-property properties)]
     [:h2 "Entities"]
     [:p "Entities go here. In a form that supports nesting."]
     [:h2 "Links"]
     [:ul (map render-link links)]
     [:h2 "Actions"]
     [:p "Forms go here."]])) 

(defmethod render-siren "html" [response type]
  (update-in response [:body] generate-html))

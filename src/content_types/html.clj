(ns content-types.html
  (:require [siren :refer :all]
            [clojure.string :refer [join split]]
            [hiccup.core :as hiccup]))

(defn render-property [[k v]]
  [[:dt k] [:dd v]])

(defn render-properties [properties]
  (if-not (empty? properties)
    (list [:h2 "Properties"]
          [:dl (mapcat render-property properties)])))

;------------------------------------------------------------------------------

; Should do something with the rel of the embedded-representation
(defn render-embedded-representation [entity]
  (render-entity entity))

(defn render-embedded-link [{:keys [class rel href]}]
  (list [:a {:href href} (join " " rel)]
        (str " ( " (join ", " class) " )")))

(defn embedded-link? [{:keys [href]}]
  href)

(defn render-sub-entity [sub-entity]
  [:div {:class "sub-entity"}
   (if (embedded-link? sub-entity)
     (render-embedded-link sub-entity)
     (render-embedded-representation sub-entity))])

(defn render-sub-entities [sub-entities]
  (if-not (empty? sub-entities)
    (list [:h2 "Entities"]
          (map render-sub-entity sub-entities))))

;------------------------------------------------------------------------------

(defn render-link [{:keys [rel href]}]
  (let [str-rel (join " " rel)]
    [:li [:a {:href href :rel str-rel} str-rel]]))

(defn render-links [links]
  (if-not (empty? links)
    (list [:h2 "Links"]
          [:ul (map render-link links)])))

;------------------------------------------------------------------------------

(defn render-actions [actions]
  (if-not (empty? actions)
    (list [:h2 "Actions"]
          [:p "Forms go here."])))   

;------------------------------------------------------------------------------

(defn render-entity [{:keys [class properties entities links actions]}]
  (list [:h1 (join " " class)]
        (render-properties properties)
        (render-sub-entities entities)
        (render-links links)
        (render-actions actions)))

;------------------------------------------------------------------------------

(defn generate-html [siren]
   (hiccup/html [:body (render-entity siren)]))

(defmethod render-siren "html" [response type]
  (update-in response [:body] generate-html))


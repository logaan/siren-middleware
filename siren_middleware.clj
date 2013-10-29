(ns siren-middleware
  (:require [clojure.pprint :refer [pprint]]
            [clojure.string :refer [join split]]
            [hiccup.core :as hiccup]
            [compojure
             [core :refer :all]
             [handler :as handler]
             [route :as route]]
            [cheshire.core :as json]
            [org.httpkit.server :as httpkit]
            [midje.sweet :refer :all])
  (:import [java.io ByteArrayInputStream]))

(def siren-response
  {:class ["order"]
   :properties {:orderNumber 42 
                :itemCount 3
                :status "pending"}
   :entities [{:class ["items" "collection"] 
               :rel ["http://x.io/rels/order-items"] 
               :href "http://api.x.io/orders/42/items"}
              {:class ["info" "customer"]
               :rel ["http://x.io/rels/customer"] 
               :properties {:customerId "pj123"
                            :name "Peter Joseph"}
               :links [{:rel ["self"]
                        :href "http://api.x.io/customers/pj123"}]}]
   :actions [{:name "add-item"
              :title "Add Item"
              :method "POST"
              :href "http://api.x.io/orders/42/items"
              :type "application/x-www-form-urlencoded"
              :fields [{:name "orderNumber" :type "hidden" :value "42"}
                       {:name "productCode" :type "text"}
                       {:name "quantity" :type "number"}]}]
   :links [{:rel ["self"] :href "http://api.x.io/orders/42"}
           {:rel ["previous"] :href "http://api.x.io/orders/41"}
           {:rel ["next"] :href "http://api.x.io/orders/43"}]})

(defroutes all-routes
  (GET "/" []
    {:status 200
     :body siren-response})
  (route/not-found
    "<h1>Page not found</h1>"))

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

(defmulti render-siren (comp second list))

(defmethod render-siren "json" [response type]
  (update-in response [:body] json/generate-string))

(defmethod render-siren "smile" [response type]
  (update-in response [:body] (fn [body] (ByteArrayInputStream. (json/generate-smile body)))))

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

(defn siren-converter [handler]
  (fn [request]
    (let [accept     (get-in request [:headers "accept"])
          upper-type (last (split accept #"\+"))]
      (-> (handler request)
          (render-siren upper-type)
          (assoc-in [:headers "Content-Type"]
                    (str "application/vnd.siren+" accept))))))

(def most-fiddled
  (-> all-routes request-logger siren-converter response-logger))

(def least-fiddled
  (-> all-routes response-logger siren-converter request-logger))

(def handler most-fiddled)

(defn start-server [port]
  (httpkit/run-server #'handler {:port port}))

(defn -main [& arguments]
  (start-server 8080))



(ns siren-middleware
  (:require [clojure.string :refer [join split]]
            [hiccup.core :as hiccup]
            [compojure
             [core :refer :all]
             [handler :as handler]
             [route :as route]]
            [cheshire.core :as json]
            [org.httpkit.server :as httpkit]
            [logger :refer :all])
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

(def most-fiddled
  (-> all-routes request-logger siren-converter response-logger))

(def least-fiddled
  (-> all-routes response-logger siren-converter request-logger))

(def handler most-fiddled)

(defn start-server [port]
  (httpkit/run-server #'handler {:port port}))

(defn -main [& arguments]
  (start-server 8080))



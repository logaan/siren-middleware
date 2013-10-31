(ns siren-middleware
  (:require
    [clojure.string :refer [split]]
    [org.httpkit.server :as httpkit]
    [compojure
     [core :refer :all]
     [handler :as handler]
     [route :as route]
     [response :as response]]
    [siren :refer :all]
    [content-types html json smile]
    [logger :refer :all]))

(defrecord SirenResponse [class properties entities actions links])

(extend-type SirenResponse
  response/Renderable
  (render [response request]
    (let [accept     (get-in request [:headers "accept"])
          upper-type (last (split accept #"\+"))]
      (-> {:status 200 :body response}
          (render-siren upper-type)
          (assoc-in [:headers "Content-Type"]
                    (str "application/vnd.siren+" accept))))))

(defn siren-response [& {:as response}]
  (map->SirenResponse response))

(def test-response
  (siren-response
    :class ["order"]
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
            {:rel ["next"] :href "http://api.x.io/orders/43"}]))

(defroutes all-routes
  (GET "/" []
       test-response)
  (route/not-found
    "<h1>Page not found</h1>"))

(def most-fiddled
  (-> all-routes request-logger response-logger))

(def least-fiddled
  (-> all-routes response-logger request-logger))

(def handler most-fiddled)

(defn start-server [port]
  (httpkit/run-server #'handler {:port port}))

(defn -main [& arguments]
  (start-server 8080))

(comment
  
  (def server (-main))
  
  )

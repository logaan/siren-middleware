(defproject siren-middleware "0.1.0-SNAPSHOT"
  :description "Ring middleware that enables content negotiation for siren with
               json, smile, html, etc"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"} 
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.10"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.4"]
                 [cheshire "5.2.0"]])


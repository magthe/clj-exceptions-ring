(ns simple-exceptions.handler
  (:require [clojure.spec.alpha :as s]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as r]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [simple-exceptions.spec :as spec]
            [cheshire.core :as json]))

(s/check-asserts true)

(defn handle-div [m]
  (s/assert ::spec/div-req m)
  (r/response {:success true
               :result (/ (:dividend m) (:divisor m))}))

(defn handle-throw-exception [m]
  (s/assert ::spec/throw-exception-req m)
  (if (= "true" (:throw m))
    (throw (ex-info "I was told to!"
                    {:case "user request"
                     :info "exception thrown by handle-throw-exception on user's request"}))
    (r/response {:success true})))

(defroutes app-routes
  (POST "/div" {body :body} (handle-div body))
  (POST "/throw-exception" {body :body} (handle-throw-exception body))
  (route/not-found (r/response {:success false :info "no such function"})))

(defn wrap-handle-exceptions [handler]
  (fn
    ([req]
     (try (handler req)
          (catch clojure.lang.ExceptionInfo e
            (-> (r/response {:success false :exception (ex-data e)})
                (r/status 501)))))
    ([req res raise]
     (try (handler req res raise)
          (catch clojure.lang.ExceptionInfo e
            (-> (r/response {:success false :exception (ex-data e)})
                (r/status 501)))))))

(defn wrap-handle-java-exceptions [handler]
  (fn
    ([req]
     (try (handler req)
          (catch Exception e
            (-> (r/response (json/generate-string {:success false :exception (str e)
                                                   :info "wrap-handle-exceptions-simple/1"}
                                                  {:pretty true}))
                (r/status 500)))))
    ([req res raise]
     (try (handler req res raise)
          (catch Exception e
            (-> (r/response (json/generate-string {:success false :exception (str e)
                                                   :info "wrap-handle-exceptions-simple/3"}
                                                  {:pretty true}))
                (r/status 500)))))))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      (wrap-json-body {:keywords? true
                       :malformed-response (-> (r/response {:success false
                                                            :info "malformed JSON in request"})
                                               (r/status 400))})
      (wrap-handle-exceptions)
      (wrap-json-response {:pretty true})
      (wrap-handle-java-exceptions)))

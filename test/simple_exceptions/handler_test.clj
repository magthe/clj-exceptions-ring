(ns simple-exceptions.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [simple-exceptions.handler :refer [app]]
            [cheshire.core :as json]))

(deftest not-found-route
  (testing "not-found route"
    (let [response (-> (app (mock/request :get "/invalid"))
                       (update :body json/parse-string true))]
      (is (= (:status response) 404)))))

(deftest div-route
  (testing "/div route"
    (let [get-response (fn [a b] (-> (app (-> (mock/request :post "/div")
                                              (mock/content-type "application/json")
                                              (mock/json-body {:dividend a :divisor b})))
                                     (update :body json/parse-string true)))
          response0 (get-response 2 1)
          response1 (get-response 3 2)]
      (is (= (:status response0) 200))
      (is (= (:body response0) {:success true :result 2}))

      (is (= (:status response1) 200))
      (is (= (:body response1) {:success true :result (double (/ 3 2))}))))

  (testing "/div error (divide by zero)"
    (let [response (-> (app (-> (mock/request :post "/div")
                                (mock/content-type "application/json")
                                (mock/json-body {:dividend 2 :divisor 0})))
                       (update :body json/parse-string true))]
      (is (= (:status response) 500))
      (is (= (get-in response [:body :success]) false))))

(testing "/div error (bad JSON)"
    (let [response (-> (app (-> (mock/request :post "/div")
                                (mock/content-type "application/json")
                                (mock/json-body {:dividen 2 :divisor 0})))
                       (update :body json/parse-string true))]
      (is (= (:status response) 501))
      (is (= (get-in response [:body :success]) false))))
  )

(deftest throw-exception-route
  (testing "/throw-exception false"
    (let [response (-> (app (-> (mock/request :post "/throw-exception")
                                (mock/content-type "application/json")
                                (mock/json-body {:throw "false"})))
                       (update :body json/parse-string true))]
      (is (= (:status response) 200))
      (is (= (get-in response [:body :success]) true))))

  (testing "/throw-exception true"
    (let [response (-> (app (-> (mock/request :post "/throw-exception")
                                (mock/content-type "application/json")
                                (mock/json-body {:throw "true"})))
                       (update :body json/parse-string true))]
      (is (= (:status response) 501))
      (is (= (get-in response [:body :success]) false)))))

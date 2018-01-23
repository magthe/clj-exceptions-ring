(ns simple-exceptions.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::dividend int?)
(s/def ::divisor int?)
(s/def ::throw #{"true" "false"})

(s/def ::div-req
  (s/keys :req-un [::dividend
                   ::divisor]))

(s/def ::throw-exception-req
  (s/keys :req-un [::throw]))

(ns lttb.core-test
  (:require [lttb.core :as sut]
            [clojure.test :refer [deftest is]]))

(deftest downsample
  (is (= [[0 0] [1 1] [3 3] [6 6] [9 9]]
         (sut/downsample 5 (map #(-> [%1 %2]) (range 10) (range 10))))))

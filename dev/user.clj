(ns user
  (:require [lttb.core :refer [downsample]]
            [oz.core :as oz]
            [clojure.edn :as edn]))

(defn vega-spec [data]
  {:width 1200
   :height 600
   :data {:values (map (fn [[x y]] {:x x :y y})
                       data)}
   :mark {:type :line}
   :encoding {:x {:field :x :type :quantitative}
              :y {:field :y :type :quantitative}}})

(defn view-demo-data [i & {:keys [points downsample?] :or {points 200 downsample? true}}]
  (if-not (#{0 1 2} i)
    (println "Please select data from range [0 2]")
    (let [demo-data (-> "demo_data.edn" slurp edn/read-string)
          data (nth demo-data i)]
      (oz/view! (vega-spec (if downsample?
                             (downsample points data)
                             data))))))

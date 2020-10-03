(ns user
  (:require [lttb.core :refer [downsample]]
            [oz.core :as oz]
            [clojure.edn :as edn]))

(defn view-demo-data [i points]
  (if-not (#{0 1 2} i)
    (println "Please select data from range [0 2]")
    (let [demo-data (-> "demo_data.edn" slurp edn/read-string)]
      (oz/view! {:width 1200
                 :height 600
                 :data {:values (->> (nth demo-data i) (downsample points) (map (fn [[x y]] {:x x :y y})))}
                 :mark {:type :line}
                 :encoding {:x {:field :x :type :quantitative}
                            :y {:field :y :type :quantitative}}}))))

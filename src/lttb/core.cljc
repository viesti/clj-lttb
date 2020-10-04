(ns lttb.core)

(defn downsample [threshold data]
  (let [data-length (count data)]
    (if (or (zero? threshold) (zero? data-length))
      data
      ;; Bucket size. Leave room for start and end data points
      (let [every (/ (- data-length 2)
                     (- threshold 2))]
        (loop [sampled (transient [(first data)])
               a       0 ;; Initially a is the first point in the triangle
               i       0]
          (if (< i (- threshold 2))
            (let [;; Calculate point average for next bucket (containing c)
                  avg_range_start               (inc (Math/floor (* (inc i) every)))
                  avg_range_end                 (inc (Math/floor (* (+ i 2) every)))
                  avg_range_end                 (if (< avg_range_end data-length)
                                                  avg_range_end
                                                  data-length)
                  avg_range_length              (- avg_range_end avg_range_start)
                  [x_range_start y_range_start] (nth data avg_range_start)
                  avg_x                         (/ x_range_start
                                                   avg_range_length)
                  avg_y                         (/ y_range_start
                                                   avg_range_length)
                  ;; Get the range for this bucket
                  range_offs                    (inc (Math/floor (* i every)))
                  range_to                      (inc (Math/floor (* (inc i) every)))
                  ;; Point a
                  point-a                       (nth data a)
                  point_a_x                     (first point-a)
                  point_a_y                     (second point-a)
                  [max_area_point next_a]       (loop [max_area       -1.0
                                                       max_area_point nil
                                                       next_a         nil
                                                       range_offs     range_offs]
                                            (if (< range_offs range_to)
                                              (let [[point_x point_y] (nth data range_offs)
                                                    ;; Calculate triangle area over three buckets
                                                    area              (* (Math/abs (- (* (- point_a_x avg_x)
                                                                                         (- point_y point_a_y))
                                                                                      (* (- point_a_x point_x)
                                                                                         (- avg_y point_a_y))))
                                                                         0.5)]
                                                (if (> area max_area)
                                                  (recur area
                                                         (nth data range_offs)
                                                         range_offs ;; Next a is this b
                                                         (inc range_offs))
                                                  (recur max_area
                                                         max_area_point
                                                         next_a
                                                         (inc range_offs))))
                                              [max_area_point next_a]))]
              (recur (conj! sampled max_area_point) ;; Pick this point from the bucket
                     next_a ;; This a is the next a (chosen b)
                     (inc i)))
            ;; Always add last
            (persistent! (conj! sampled (nth data (dec data-length))))))))))

(ns flash-spike.app
  (:require [flash-spike.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)

(ns flash-spike.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[flash-spike started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[flash-spike has shut down successfully]=-"))
   :middleware identity})

(ns flash-spike.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [flash-spike.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[flash-spike started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[flash-spike has shut down successfully]=-"))
   :middleware wrap-dev})

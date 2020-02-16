(ns flash-spike.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [flash-spike.core-test]))

(doo-tests 'flash-spike.core-test)


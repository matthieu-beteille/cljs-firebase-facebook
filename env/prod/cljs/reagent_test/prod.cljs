(ns reagent-test.prod
  (:require [reagent-test.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)

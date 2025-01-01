(ns toil.dev
  (:require [toil.core :as app]))

(defonce store (atom {}))
(defonce el (js/document.getElementById "app"))

(defn ^:dev/after-load main []
  ;; Add additional dev-time tooling here
  (app/main store el))

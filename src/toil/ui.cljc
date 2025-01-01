(ns toil.ui)

(defn render-frontpage [_]
  [:h1 "Toil and trouble: Todos over the network"])

(defn render-not-found [_]
  [:h1 "Not found"])

(defn render-page [state]
  (let [f (case (:location/page-id (:location state))
            :pages/frontpage render-frontpage
            render-not-found)]
    (f state)))

(ns toil.ui)

(defn render-frontpage [state]
  [:main.p-8.max-w-screen-lg
   [:h1.text-2xl.mb-4 "Toil and trouble: Todos over the network"]
   (when-let [todos (:todo-items state)]
     [:ul.mb-4
      (for [item todos]
        [:li.my-2
         [:span.pr-2
          (if (:todo/done? item)
            "✓"
            "▢")]
         (:todo/title item)])])
   [:button.btn.btn-primary
    (if (:loading-todos? state)
      {:disabled true}
      {:on {:click [[:backend/fetch-todo-items]]}})
    (when (:loading-todos? state)
      [:span.loading.loading-spinner])
    "Fetch todos"]])

(defn render-not-found [_]
  [:h1 "Not found"])

(defn render-page [state]
  (let [f (case (:location/page-id (:location state))
            :pages/frontpage render-frontpage
            render-not-found)]
    (f state)))

(ns toil.core
  (:require [cljs.reader :as reader]
            [clojure.walk :as walk]
            [replicant.alias :as alias]
            [replicant.dom :as r]
            [toil.router :as router]
            [toil.ui :as ui]))

(defn routing-anchor [attrs children]
  (let [routes (-> attrs :replicant/alias-data :routes)]
    (into [:a (cond-> attrs
                (:ui/location attrs)
                (assoc :href (router/location->url routes
                                                   (:ui/location attrs))))]
          children)))

(alias/register! :ui/a routing-anchor)

(defn find-target-href [e]
  (some-> e .-target
          (.closest "a")
          (.getAttribute "href")))

(defn get-current-location []
  (->> js/location.pathname
       (router/url->location router/routes)))

(defn interpolate-actions [event actions]
  (walk/postwalk
   (fn [x]
     (case x
       :event/target.value (.. event -target -value)
       ;; Add more cases as needed
       x))
   actions))

(defn receive-todo-items [state response]
  (cond-> (dissoc state :loading-todos?)
    (:success? response)
    (assoc :todo-items (:result response))

    (not (:success? response))
    (assoc :error "Failed to load todos")))

(defn fetch-todo-items [store]
  (swap! store assoc :loading-todos? true)
  (-> (js/fetch "/query" #js {:method "post"
                              :body (pr-str {:query/kind :query/todo-items})})
      (.then #(.text %))
      (.then #(swap! store receive-todo-items (reader/read-string %)))))

(defn execute-actions [store actions]
  (doseq [[action & args] actions]
    (case action
      :store/assoc-in (apply swap! store assoc-in args)
      :backend/fetch-todo-items (fetch-todo-items store)
      (println "Unknown action" action "with arguments" args))))

(defn route-click [e store routes]
  (let [href (find-target-href e)]
    (when-let [location (router/url->location routes href)]
      (.preventDefault e)
      (if (router/essentially-same? location (:location @store))
        (.replaceState js/history nil "" href)
        (.pushState js/history nil "" href))
      (swap! store assoc :location location))))

(defn main [store el]
  (add-watch
   store ::render
   (fn [_ _ _ state]
     (r/render el (ui/render-page state) {:alias-data {:routes router/routes}})))

  (r/set-dispatch!
   (fn [event-data actions]
     (->> actions
          (interpolate-actions
           (:replicant/dom-event event-data))
          (execute-actions store))))

  (js/document.body.addEventListener
   "click"
   #(route-click % store router/routes))

  (js/window.addEventListener
   "popstate"
   (fn [_] (swap! store assoc :location (get-current-location))))

  ;; Trigger the initial render
  (swap! store assoc
         :app/started-at (js/Date.)
         :location (get-current-location)))

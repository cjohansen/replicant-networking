(ns toil.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as response]))

(defonce todos
  (atom
   [{:todo/id "74e67"
     :todo/title "Write project documentation"
     :todo/done? false
     :todo/created-at "2024-12-30T10:15:00Z"
     :todo/created-by "alice"}

    {:todo/id "a94e4"
     :todo/title "Refactor rendering logic"
     :todo/done? false
     :todo/created-at "2024-12-29T14:30:00Z"
     :todo/created-by "bob"}

    {:todo/id "c53b1"
     :todo/title "Implement state synchronization"
     :todo/done? true
     :todo/created-at "2024-12-28T09:00:00Z"
     :todo/created-by "carol"}

    {:todo/id "8d546"
     :todo/title "Implement user authentication"
     :todo/done? false
     :todo/created-at "2024-12-30T11:45:00Z"
     :todo/created-by "alice"}

    {:todo/id "a20d5"
     :todo/title "Optimize query responses"
     :todo/done? true
     :todo/created-at "2024-12-27T16:00:00Z"
     :todo/created-by "bob"}

    {:todo/id "2f4e1"
     :todo/title "Add unit tests for new features"
     :todo/done? false
     :todo/created-at "2024-12-29T13:20:00Z"
     :todo/created-by "carol"}

    {:todo/id "2b4fd"
     :todo/title "Update website design"
     :todo/done? false
     :todo/created-at "2024-12-30T09:50:00Z"
     :todo/created-by "alice"}

    {:todo/id "b085f"
     :todo/title "Research deployment strategies"
     :todo/done? false
     :todo/created-at "2024-12-29T10:40:00Z"
     :todo/created-by "bob"}]))

(defonce users
  [{:user/id "alice"
    :user/given-name "Alice"
    :user/family-name "Johnson"
    :user/email "alice.johnson@acme-corp.com"}

   {:user/id "bob"
    :user/given-name "Bob"
    :user/family-name "Smith"
    :user/email "bob.smith@acme-corp.com"}

   {:user/id "carol"
    :user/given-name "Carol"
    :user/family-name "Lee"
    :user/email "carol.lee@acme-corp.com"}])

(defn query [req]
  (if-let [query (try
                   (read-string (slurp (:body req)))
                   (catch Exception e
                     (println "Failed to parse query body")
                     (prn e)))]
    (try
      (case (:query/kind query)
        :query/todo-items
        {:success? true
         :result @todos}

        :query/user
        (let [{:keys [user-id]} (:query/data query)]
          (if-let [user (first (filter (comp #{user-id} :user/id) users))]
            {:success? true
             :result user}
            {:error "No such user"}))
        {:error "Unknown query type"
         :query query})
      (catch Exception e
        (println "Failed to handle query")
        (prn e)
        {:error "Failed while executing query"}))
    {:error "Unparsable query"}))

(defn handler [{:keys [uri] :as req}]
  (cond
    (= "/" uri)
    (response/resource-response "/index.html" {:root "public"})

    (= "/query" uri)
    {:status 200
     :headers {"content-type" "application/edn"}
     :body (pr-str (query req))}

    :else
    {:status 404
     :headers {"content-type" "text/html"}
     :body "<h1>Page not found</h1>"}))

(defn start-server [port]
  (jetty/run-jetty
   (-> #'handler
       (wrap-resource "public"))
   {:port port :join? false}))

(defn stop-server [server]
  (.stop server))

(comment

  (def server (start-server 8088))
  (stop-server server)

)

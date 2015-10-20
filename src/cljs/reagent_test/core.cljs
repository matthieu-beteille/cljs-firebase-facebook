(ns reagent-test.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [matchbox.core :as m])
    (:import goog.History))

;; firebase connection
(defonce firebase-app-name "blazing-fire-3944")
(defonce firebase-url (str "https://" firebase-app-name ".firebaseio.com"))
(defonce root (m/connect firebase-url))

;; app state
(defonce state (atom {:user nil}))

;; facebook login function
(defn login []
  (.authWithOAuthPopup root "facebook"
   (fn [error user] (swap! state assoc :user (aget user "facebook" "displayName")))))

;; logout function
(defn logout []
  (swap! state assoc :user nil))

;; style
(defonce style {:link {:cursor "pointer"}})

;; -------------------------
;; Views
(defn home-page []
  (if (= (:user @state) nil)
    [:div
      [:h2 "Login Page"]
      [:div
        [:a {:style (:link style) :on-click login} "Login with Facebook"]]]
    [:div
      [:h2 "Logged-In"]
      [:h3 "User: " (:user @state)]
      [:a {:style (:link style) :on-click logout} "Logout"]]))

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))

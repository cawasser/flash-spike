(ns flash-spike.core
  (:require
    [day8.re-frame.http-fx]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as events]
    [goog.history.EventType :as HistoryEventType]
    [markdown.core :refer [md->html]]
    [flash-spike.ajax :as ajax]
    [flash-spike.events]
    [reitit.core :as reitit]
    [reitit.frontend.easy :as rfe]
    [clojure.string :as string])
  (:import goog.History))

(defn nav-link [uri title page]
  [:a.navbar-item
   {:href  uri
    :class (when (= page @(rf/subscribe [:page])) :is-active)}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "flash-spike"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click    #(swap! expanded? not)
        :class       (when @expanded? :is-active)}
       [:span] [:span] [:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "#/" "Home" :home]
       [nav-link "#/about" "About" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])


(defn widget-contents
  "analogous to (make-chart/make-chart) ?
   Should change only when the data-source updates"

  [id source]
  (prn "widget-contents " id)
  (str id " => " @source))


(defn widget
  "analogous to widgets.core/make-widget, but not really.
   This may be where our issue is..."

  [w s]
  (prn "widget " (:id w))
  ^{:key (:id w)}
  [:li
   {:on-click #(rf/dispatch [:remove-widget (:id w)])}
   (widget-contents (:id w) s)])


(defn widget-setup
  "analogous to (widgets.core/setup-widget)"

  [w]
  (prn "widget-setup " (:id w))
  (let [source (rf/subscribe [:source (:source w)])]
    [widget w source]))


(defn widget-list
  "analogous to 'grid'
   Should only re-render on adding/removing a widget"

  [widgets]
  (prn "widget-list " widgets)
  [:ul
   (for [w widgets]
     ^{:key (:id w)}
     [widget-setup w])])


(defn data-source-list
  "analogous to ':data-sources'"

  [sources]
  ;(prn "data-source-list " sources)
  [:div
   (for [[k s] sources]
     (do
       ;(prn "source " k "," s)
       ^{:key (:id s)}
       [:button.button {:on-click #(rf/dispatch-sync [:update-source (:id s)])}
        (str (:id s) " => " (:value s))]))])


(defonce next-widget-id (atom 2))

(defn home-page []
  (let [widgets        (rf/subscribe [:widgets])
        sources        (rf/subscribe [:sources])]

    (fn []
      [:section.section>div.container>div.content
       [:div
        [widget-list @widgets]
        [:button.button {:on-click
                         #(do
                            (rf/dispatch [:add-widget @next-widget-id :timer])
                            (swap! next-widget-id inc))}
         "Add Timer"]
        [:button.button {:on-click
                         #(do
                            (rf/dispatch [:add-widget @next-widget-id :counter])
                            (swap! next-widget-id inc))}
         "Add Counter"]]
       [:div
        [data-source-list @sources]]])))



(def pages
  {:home  #'home-page
   :about #'about-page})

(defn page []
  (if-let [page @(rf/subscribe [:page])]
    [:div
     [navbar]
     [page]]))

(defn navigate! [match _]
  (rf/dispatch-sync [:navigate match]))

(def router
  (reitit/router
    [["/" {:name        :home
           :view        #'home-page
           :controllers [{:start (fn [_] (rf/dispatch [:page/init-home]))}]}]
     ["/about" {:name :about
                :view #'about-page}]]))

(defn start-router! []
  (rfe/start!
    router
    navigate!
    {}))

;; -------------------------
;; Initialize app
(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (start-router!)
  (ajax/load-interceptors!)
  (rf/dispatch-sync [:add-source :timer])
  (rf/dispatch-sync [:add-source :counter])
  (rf/dispatch-sync [:add-widget 0 :timer])
  (rf/dispatch-sync [:add-widget 1 :counter])
  (mount-components))

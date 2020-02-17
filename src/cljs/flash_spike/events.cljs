(ns flash-spike.events
  (:require
    [re-frame.core :as rf]
    [day8.re-frame.tracing :refer-macros [fn-traced defn-traced]]
    [ajax.core :as ajax]
    [reitit.frontend.easy :as rfe]
    [reitit.frontend.controllers :as rfc]))

;;dispatchers

(rf/reg-event-db
  :navigate
  (fn-traced [db [_ match]]
    (let [old-match (:common/route db)
          new-match (assoc match :controllers
                                 (rfc/apply-controllers (:controllers old-match) match))]
      (assoc db :route new-match))))

(rf/reg-fx
  :navigate-fx!
  (fn-traced [[k & [params query]]]
    (rfe/push-state k params query)))

(rf/reg-event-fx
  :navigate!
  (fn-traced [_ [_ url-key params query]]
    {:navigate-fx! [url-key params query]}))

(rf/reg-event-db
  :common/set-error
  (fn-traced [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-fx
  :page/init-home
  (fn-traced [_ _]
    {:dispatch [:fetch-docs]}))


(rf/reg-event-db
  :add-widget
  (fn-traced [db [_ widget-id source]]
    (assoc db :widgets (conj
                         (:widgets db)
                         {:id     widget-id
                          :source source}))))

(rf/reg-event-db
  :add-source
  (fn-traced [db [_ source]]
    (prn ":add-source " source)
    (assoc db :sources
              (assoc (:sources db)
                source {:id source :value 0}))))


(rf/reg-event-db
  :update-source
  (fn-traced [db [_ source]]
    (let [last-val (get-in db [:sources source :value])]
      (assoc-in db [:sources source :value] (inc last-val)))))


;;subscriptions

(rf/reg-sub
  :widgets
  (fn [db _]
    (-> db :widgets)))


(rf/reg-sub
  :sources
  (fn [db _]
    (-> db :sources)))


(rf/reg-sub
  :source
  (fn [db [_ source]]
    (prn ":source sub " source)
    (-> db :sources source)))


(rf/reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(rf/reg-sub
  :page-id
  :<- [:route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :page
  :<- [:route]
  (fn [route _]
    (-> route :data :view)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))

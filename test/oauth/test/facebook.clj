(ns oauth.test.facebook
  (:require [oauth.v2 :as v2])
  (:use [clojure.java.browse :only (browse-url)]
        clojure.test
        oauth.facebook))

(def facebook-access-token
  "AAAEFLdD911QBAHwPDOLF2bJZC20CqxYvxFdYZALNc3Qah9sFBFHNKenQ4E3ZBZBG2sEpB69SEGOv0vrNcYyUNnBCO5rTwZBLsTznO0xK8PwZDZD")

(def facebook-client-id "287169314674516")

(def facebook-client-secret "d003ce598f9977a4d0745af687335c88")

(def facebook-redirect-uri "http://localhost/oauth/facebook/callback")

(def facebook-code
  (str "AQDvwFJukHaOiG2llxqRQJFb8_jmOXx01cux_eeOUWm4m-MNtheROpQlDVJMgP-5Kk64CWrZQSXhtz_e_Rtdm45YkMC4rAfAmFnbDRiktL-"
       "gtEqEjDxwVKWknkEzfI_lMKp26KVObRwPOLg58NoOCTOdlzIRjVQvINDc-veRX5C5eLcY1SIb4zGTepg4_HcYK8o#_=_"))

(deftest test-me-endpoint
  (let [user ((oauth-client facebook-access-token) {:method :get :url "https://graph.facebook.com/me"})]
    (is (map? user))
    (is (= "100001026171775" (:id user)))
    (let [response (meta user)]
      (is (= 200 (:status response)))
      (is (not (contains? (set (keys response)) :body))))))

(deftest test-oauth-access-token
  (let [access-token
        (oauth-access-token
         facebook-client-id
         facebook-client-secret
         facebook-code
         facebook-redirect-uri)]
    (is (string? (:access-token access-token)))
    (is (string? (:expires access-token)))))

(deftest test-oauth-authorization-url
  (is (= (str "https://www.facebook.com/dialog/oauth?"
              "client_id=287169314674516&"
              "redirect_uri=http%3A%2F%2Flocalhost%2Foauth%2Ffacebook%2Fcallback")
         (oauth-authorization-url facebook-client-id facebook-redirect-uri)))
  (is (= (str "https://www.facebook.com/dialog/oauth?"
              "client_id=287169314674516&"
              "redirect_uri=http%3A%2F%2Flocalhost%2Foauth%2Ffacebook%2Fcallback&"
              "scope=email%2Cread_stream")
         (oauth-authorization-url facebook-client-id facebook-redirect-uri :scope "email,read_stream"))))

(deftest test-oauth-authorize
  (let [client-id facebook-client-id
        redirect-uri "http://example.com"]
    (with-redefs [browse-url (fn [target] (is (= (v2/oauth-authorization-url *oauth-authorization-url* client-id redirect-uri) target)))]
      (oauth-authorize client-id redirect-uri))))

(deftest test-oauth-client
  (is (fn? (oauth-client facebook-access-token))))

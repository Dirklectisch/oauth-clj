(ns oauth.test.v1
  (:require [clj-http.client :as http])
  (:use [clojure.java.browse :only (browse-url)]
        clojure.test
        oauth.test.twitter
        oauth.v1))
        
(def rfc-request  { :method :post
                    :scheme "http"
                    :server-name "example.com"
                    :content-type "application/x-www-form-urlencoded"
                    :uri "/request"
                    :query-params { :b5 "=%3D"
                                    :a3 "a"
                                    (keyword "c@") ""
                                    :a2 "r b"}
                    :oauth-consumer-key "9djdj82h48djs9d2"
                    :oauth-nonce "7d8f3e4a"
                    :oauth-signature-method "HMAC-SHA1"
                    :oauth-timestamp "137131201"
                    :oauth-token "kkk9d7dh3k39sjv7"
                    :body "c2&a3=2+q"})

(deftest test-oauth-authorization-header
  (is (= (str "OAuth "
              "oauth_consumer_key=\"xvz1evFS4wEEPTGEFPHBog\", "
              "oauth_nonce=\"kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg\", "
              "oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"1318622958\", "
              "oauth_token=\"370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb\", "
              "oauth_version=\"1.0\"")
         (oauth-authorization-header twitter-update-status)))
  (is (= (str "OAuth "
              "oauth_callback=\"http%3A%2F%2Flocalhost%3A3005%2Fthe_dance%2Fprocess_callback%3Fservice_provider_id%3D11\", "
              "oauth_consumer_key=\"GDdmIQH6jhtmLUypg82g\", "
              "oauth_nonce=\"QP70eNmVz8jvdPevU3oJD2AfF7R7odC2XJcn4XlZJqk\", "
              "oauth_signature=\"8wUi7m5HFQy76nowoCThusfgB%2BQ%3D\", "
              "oauth_signature_method=\"HMAC-SHA1\", "
              "oauth_timestamp=\"1272323042\", "
              "oauth_version=\"1.0\"")
         (oauth-authorization-header
          (oauth-sign-request twitter-request-token "MCD8BKwGdgPHvAuvgvz4EQpqDAtx89grbuNMRd7Eh98" nil)))))

(deftest test-oauth-authorize
  (let [expected "https://api.twitter.com/oauth/authorize?oauth_token=9BVHFCl8PCvGekptmdtL1169QkppJG6PgpUDQDWow"]
    (with-redefs [browse-url (fn [url] (is (= expected url)))]
      (oauth-authorize "https://api.twitter.com/oauth/authorize" "9BVHFCl8PCvGekptmdtL1169QkppJG6PgpUDQDWow"))))

(deftest test-oauth-callback-confirmed?
  (is (not (oauth-callback-confirmed? {})))
  (is (not (oauth-callback-confirmed? {:oauth-callback-confirmed false})))
  (is (not (oauth-callback-confirmed? {:oauth-callback-confirmed "false"})))
  (is (not (oauth-callback-confirmed? {:oauth-callback-confirmed "x"})))
  (is (oauth-callback-confirmed? {:oauth-callback-confirmed true}))
  (is (oauth-callback-confirmed? {:oauth-callback-confirmed "true"})))

(deftest test-oauth-nonce
  (is (string? (oauth-nonce)))
  (is (not (= (oauth-nonce) (oauth-nonce)))))

(deftest test-oauth-parameter-string
  (is (= (str "include_entities=true&"
              "oauth_consumer_key=xvz1evFS4wEEPTGEFPHBog&"
              "oauth_nonce=kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg&"
              "oauth_signature_method=HMAC-SHA1&"
              "oauth_timestamp=1318622958&"
              "oauth_token=370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb&"
              "oauth_version=1.0&"
              "status=Hello%20Ladies%20%2B%20Gentlemen%2C%20a%20signed%20OAuth%20request%21")
         (oauth-parameter-string twitter-update-status)))
  (is (= (str "a2=r%20b&a3=2%20q&a3=a&b5=%3D%253D&c%40=&c2=&oauth_consumer_key=9dj"
              "dj82h48djs9d2&oauth_nonce=7d8f3e4a&oauth_signature_method=HMAC-SHA1"
              "&oauth_timestamp=137131201&oauth_token=kkk9d7dh3k39sjv7")
         (oauth-parameter-string rfc-request))))

(deftest test-oauth-signature-parameters
  (is (= {} (oauth-signature-parameters nil)))
  (is (= {} (oauth-signature-parameters {})))
  (let [params (oauth-signature-parameters twitter-update-status)]
    (is (= 8 (count params)))
    (is (= true (get params "include_entities")))
    (is (= "xvz1evFS4wEEPTGEFPHBog" (get params "oauth_consumer_key")))
    (is (= "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg" (get params "oauth_nonce")))
    (is (= "HMAC-SHA1" (get params "oauth_signature_method")))
    (is (= "1318622958" (get params "oauth_timestamp")))
    (is (= "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb" (get params "oauth_token")))
    (is (= "1.0" (get params "oauth_version")))
    (is (= "Hello Ladies + Gentlemen, a signed OAuth request!" (get params "status")))))
    
    

(deftest test-oauth-request-signature
  (is (= "tnnArxj06cWHq44gCs1OSKk/jLY="
         (oauth-request-signature twitter-update-status "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw" "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE")))
  (is (= "8wUi7m5HFQy76nowoCThusfgB+Q="
         (oauth-request-signature twitter-request-token "MCD8BKwGdgPHvAuvgvz4EQpqDAtx89grbuNMRd7Eh98" nil))))
         

(deftest test-oauth-signature-base
  (is (= (str "POST&"
              "https%3A%2F%2Fapi.twitter.com%2F1%2Fstatuses%2Fupdate.json&"
              "include_entities%3Dtrue%26"
              "oauth_consumer_key%3Dxvz1evFS4wEEPTGEFPHBog%26"
              "oauth_nonce%3DkYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg%26"
              "oauth_signature_method%3DHMAC-SHA1%26"
              "oauth_timestamp%3D1318622958%26"
              "oauth_token%3D370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb%26"
              "oauth_version%3D1.0%26"
              "status%3DHello%2520Ladies%2520%252B%2520Gentlemen%252C%2520a%2520signed%2520OAuth%2520request%2521")
         (oauth-signature-base twitter-update-status)))
  (is (= (str "POST&"
              "https%3A%2F%2Fapi.twitter.com%2Foauth%2Frequest_token&"
              "oauth_callback%3Dhttp%253A%252F%252Flocalhost%253A3005%252Fthe_dance%252Fprocess_callback%253Fservice_provider_id%253D11%26"
              "oauth_consumer_key%3DGDdmIQH6jhtmLUypg82g%26"
              "oauth_nonce%3DQP70eNmVz8jvdPevU3oJD2AfF7R7odC2XJcn4XlZJqk%26"
              "oauth_signature_method%3DHMAC-SHA1%26"
              "oauth_timestamp%3D1272323042%26"
              "oauth_version%3D1.0")
         (oauth-signature-base twitter-request-token))) 
  (is (= "POST&http%3A%2F%2Fexample.com%2Frequest&a2%3Dr%2520b%26a3%3D2%2520q%26a3%3Da%26b5%3D%253D%25253D%26c%2540%3D%26c2%3D%26oauth_consumer_key%3D9djdj82h48djs9d2%26oauth_nonce%3D7d8f3e4a%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D137131201%26oauth_token%3Dkkk9d7dh3k39sjv7" (oauth-signature-base rfc-request) )))

(deftest test-oauth-signing-key
  (are [consumer-secret token-secret expected]
    (is (= expected (oauth-signing-key consumer-secret token-secret)))
    "MCD8BKwGdgPHvAuvgvz4EQpqDAtx89grbuNMRd7Eh98" nil
    "MCD8BKwGdgPHvAuvgvz4EQpqDAtx89grbuNMRd7Eh98&"
    "MCD8BKwGdgPHvAuvgvz4EQpqDAtx89grbuNMRd7Eh98" ""
    "MCD8BKwGdgPHvAuvgvz4EQpqDAtx89grbuNMRd7Eh98&"
    "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw" "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE"
    "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw&LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE"))

(deftest test-oauth-timestamp
  (is (number? (oauth-timestamp))))

(deftest test-wrap-oauth-signature
  ((wrap-oauth-signature
    #(is (= "tnnArxj06cWHq44gCs1OSKk/jLY=" (:oauth-signature %1))))
   (assoc twitter-update-status
     :oauth-consumer-secret "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw"
     :oauth-token-secret "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE")))

(deftest test-make-consumer
  (let [consumer (make-consumer)]
    (is (fn? consumer))))

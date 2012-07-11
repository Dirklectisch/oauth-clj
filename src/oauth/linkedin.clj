(ns oauth.linkedin
  (:require [oauth.v1 :as v1])
  (:use oauth.util))

(def ^:dynamic *oauth-access-token-url*
  "https://api.linkedin.com/uas/oauth/accessToken")

(def ^:dynamic *oauth-authentication-url*
  "https://api.linkedin.com/uas/oauth/authenticate")

(def ^:dynamic *oauth-authorization-url*
  "https://api.linkedin.com/uas/oauth/authorize")

(def ^:dynamic *oauth-request-token-url*
  "https://api.linkedin.com/uas/oauth/requestToken")

(defn oauth-access-token
  "Obtain a OAuth access token from LinkedIn."
  [oauth-consumer-key oauth-consumer-secret oauth-token oauth-token-secret oauth-verifier]
  (v1/oauth-access-token *oauth-access-token-url* oauth-consumer-key oauth-consumer-secret oauth-token oauth-token-secret oauth-verifier))

(defn oauth-authentication-url
  "Returns LinkedIn's OAuth authentication url."
  [oauth-token] (format "%s?oauth_token=%s" *oauth-authentication-url* oauth-token))

(defn oauth-authorization-url
  "Returns LinkedIn's OAuth authorization url."
  [oauth-token] (format "%s?oauth_token=%s" *oauth-authorization-url* oauth-token))

(defn oauth-authorize
  "Sends the user to LinkedIn's authorization endpoint."
  [oauth-token] (v1/oauth-authorize *oauth-authorization-url* oauth-token))

(defn oauth-client
  "Returns a OAuth LinkedIn client."
  [oauth-consumer-key oauth-consumer-secret oauth-token oauth-token-secret]
  (v1/oauth-client oauth-consumer-key oauth-consumer-secret oauth-token oauth-token-secret))

(defn oauth-request-token
  "Obtain a OAuth request token from LinkedIn to request user authorization."
  ([oauth-consumer-key oauth-consumer-secret oauth-callback]
  (v1/oauth-request-token *oauth-request-token-url* oauth-consumer-key oauth-consumer-secret oauth-callback))
  ([oauth-consumer-key oauth-consumer-secret]
  (v1/oauth-request-token *oauth-request-token-url* oauth-consumer-key oauth-consumer-secret)))

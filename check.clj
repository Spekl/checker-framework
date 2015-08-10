(ns spekl-package-manager.check
 (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]))


(defcheck default
  (log/info  "This package does not provide any checks. Please use one of the checker-framework-* packages to install checks."))


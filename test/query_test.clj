(ns query-test
  (:require [clojure.test :refer :all]
            [logical-interpreter :refer :all]))

(def parent-database "
	varon(juan).
  padre(pepe, juan).
	hijo(X, Y) :- varon(X), padre(Y, X).
")

(deftest query-fact-test
  (testing "invalid fact query returns nil"
    (is (= (evaluate-query parent-database "varon(juan")
           nil))
  )
  (testing "valid fact query and there is fact returns true"
    (is (= (evaluate-query parent-database "varon(juan)")
           true))
  )
  (testing "valid fact query, there is no fact returns false"
    (is (= 
          (evaluate-query parent-database "varon(maria)")
          false
        )
    )
  )
  (testing "empty query returns nil"
    (is (= 
          (evaluate-query parent-database "")
          nil
        )
    )
  )
)


(deftest query-rule-test
  (testing "invalid rule query returns nil"
    (is (= (evaluate-query parent-database "hijo(juan, pepe")
           nil))
  )
  (testing "valid rule query and there is fact returns true"
    (is (= (evaluate-query parent-database "hijo(juan, pepe)")
           true))
  )
  (testing "valid rule query, there is no fact returns false"
    (is (= 
          (evaluate-query parent-database "hijo(pepe, juan)")
          false
        )
    )
  )
)
(ns parse-database-test
  (:require [clojure.test :refer :all]
            [logical-interpreter :refer :all]))

(def valid-database "
	varon(juan).
	varon(pepe).
	varon(hector).
	varon(roberto).
	varon(alejandro).
	mujer(maria).
	mujer(cecilia).
	padre(juan, pepe).
	padre(juan, pepa).
	padre(hector, maria).
	padre(roberto, alejandro).
	padre(roberto, cecilia).
	hijo(X, Y) :- varon(X), padre(Y, X).
	hija(X, Y) :- mujer(X), padre(Y, X).
")

(def invalid-database-onefact "
	varon(juan).
	varon(pepe).
	varon(hector).
	varon(roberto
	varon(alejandro).
	mujer(maria).
	mujer(cecilia).
	padre(juan, pepe).
	padre(juan, pepa).
	padre(hector, maria).
	padre(roberto, alejandro).
	padre(roberto, cecilia.
	hijo(X, Y) :- varon(X), padre(Y, X).
	hija(X, Y) :- mujer(X), padre(Y, X).
")

(def invalid-database-onerule "
	varon(juan).
	varon(pepe).
	varon(hector).
	varon(roberto
	varon(alejandro).
	mujer(maria).
	mujer(cecilia).
	padre(juan, pepe).
	padre(juan, pepa).
	padre(hector, maria).
	padre(roberto, alejandro).
	padre(roberto, cecilia.
	hijo(X, Y) : varon(X), padre(Y, X).
	hija(X, Y) :- mujer(X), padre(Y, X).
")

(deftest parent-database-fact-line-test
 (testing "varon(juan) should be valid"
   (is (=
         (validate-line "varon(juan)")
         true
	   )

	 )
 )
 (testing "varon(juan should be invalid"
    (is (=
				  (validate-line "varon(juan")
		      false
         )
     )
 )
 (testing "varonjuan) should be invalid"
  (is (=
        (validate-line "varonjuan)")
        false
	    )

  )
 )
 (testing "varon should be invalid"
	(is (=
	      (validate-line "varon")
	      false
	    )
	)
 )
 (testing "empty should be valid."
	(is (=
	      (validate-line "")
	      true
	    )
	)
 )
)

(deftest parent-database-rule-line-test
 (testing "hijo(X, Y) : varon(X), padre(Y, X). should be valid"
   (is (=
         (validate-line "hijo(X, Y) :- varon(X), padre(Y, X).")
         true
	   )

	 )
 )
 (testing "invalid declaration (missing closing parenthesis ) of rule should be invalid"
    (is (=
				  (validate-line "hijo(X, Y :- varon(X), padre(Y, X).")
		      false
         )
     )
 )
 (testing "invalid declaration (missing opening parenthesis ) of rule should be invalid"
  (is (=
        (validate-line "hijoX, Y) :- varon(X), padre(Y, X).")
        false
	    )

  )
 )
 (testing "invalid declaration (invalid fact parameter) of rule should be invalid"
  (is (=
        (validate-line "hijo(X, Y) :- varon(Z), padre(Y, X).")
        false
	    )

  )
 )
; (testing "invalid declaration (invalid fact) of rule should be invalid"
;  (is (=
;        (validate-line "hijo(X, Y) :- varon(Z, padre(Y, X).")
;        false
;	    )
;
;  )
; )
; (testing "invalid declaration (invalid delimeter) of rule should be invalid"
;  (is (=
;        (validate-line "hijo(X, Y) : varon(Z), padre(Y, X).")
;        false
;	    )
;
;  )
; )
; (testing "invalid declaration (no delimeter) of rule should be invalid"
;  (is (=
;        (validate-line "hijo(X, Y)  varon(Z), padre(Y, X).")
;        false
;	    )
;
;  )
; )
)

(deftest database-valid-test
	(testing "valid database is valid"
		(is(=
			(validate-database valid-database)
			true
		   )
		)
	)
	(testing "one fact invalid database is invalid"
		(is(=
			(validate-database invalid-database-onefact)
			false
		   )
		)
	)
 (testing "one rule invalid database is invalid"
		(is(=
			(validate-database invalid-database-onerule)
			false
		   )
		)
	)
)


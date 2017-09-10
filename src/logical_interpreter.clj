(ns logical-interpreter)

(defn not-empty?
  "how, or rather why, is this not a core function??. returns true if coll not empty"
  [coll]
  (if (empty? coll)
    false
    true
  )
)

(def fact-regex #"([a-zA-Z]*)(\(([\w\s,$]*)\))")

(require '[clojure.string :as str])

(defn getParameters
  [group]
  "gets the paramters from in between (  ), as a list, or returns nil"
     (if (> (count group) 3)
       (let[only-paramters (nth group 3 "")]
          (str/split only-paramters #", ")
       )
       nil
     )
)

(defn validate-fact
  "return true if fact is valid"
  [fact]
  (let[parsed-fact (re-find fact-regex fact)]
    (not-empty? (getParameters parsed-fact))
  )
)

(defn present-in?
  "returns true if x is in coll. nil if not"
  [x coll]
  (some #(= % x) coll)
)

(defn all-present-in?
  "ret trues if, each elem in coll1 is present at coll2"
  [coll1 coll2]
  (every? #(present-in? % coll2) coll1)
)

(defn validate-rule
  "return true if rule is valid"
  [rule-line]
   (let [parsed-groups (re-seq fact-regex rule-line)
         rule-variables (getParameters (first parsed-groups))
         fact-variables (apply concat (map getParameters (next parsed-groups)))]
     (and
       (>= (count parsed-groups) 2)
       (not-empty? rule-variables)
       (not-empty? fact-variables)
       (all-present-in? fact-variables rule-variables)
     )
   )
)

(defn validate-line
	"returns true if it is valid, false if not"
	[line]
	(if	(empty? line)
    true
    (if(str/includes? line ":-")
      (validate-rule line)
	    (validate-fact line)
    )
  )
)

(defn validate-database
	"returns true if it is valid, false if not"
	[db]
	(let[lines (clojure.string/split-lines db)] ;modify this if it's a file
      (every? validate-line lines) 
	)
)

(defn validate-query
  "returns true if query is valid"
  [query]
  (and
    (not-empty? query)
    (validate-fact query)
  )
)

(defn evaluate-fact
  "returns true if fact exists in database"
  [database query]
  (let[lines (clojure.string/split-lines database)];modify this if it's a file
    (some? (some #(str/includes? % query) lines))
  ) 
)

(defn fetch-query
  "returnes the rule or fact (not parsed) from database that matches the name. nil if not found"
  [db query-name]
  (let[lines (clojure.string/split-lines db)] ;modify this if it's a file
      (str/replace 
        (first(filter 
                #(= query-name (second (re-find fact-regex %) ))
                lines
        ) ) ;filter is lazy, so it only goes through database until it finds first match
        "\t"
        ""
      )
	)
)

(defn build-query
  "builds (and returns) query "
  [parsed-fact variable-parameter-map]
  (str (second parsed-fact)
       "("
       (str/replace (nth parsed-fact 3 "") #"\w+" variable-parameter-map)
       ")"
  )
)


(defn build-multiple-querys
  "builds (and returns) querys for each fact(and rules) in rule"
  [rule parameters]
  (let [parsed-groups (re-seq fact-regex rule)
        rule-variables (getParameters (first parsed-groups))
        variable-parameter-map (zipmap rule-variables parameters)]
    (into [] (map #(build-query % variable-parameter-map) (next parsed-groups)))
  )
)

(defn evaluate-query
  "Returns true if the rules and facts in database imply query, false if not. If
  either input can't be parsed, returns nil"
  [database query]
  (if (and (validate-database database) (validate-query query))
    (let[parsed-query (re-find fact-regex query)
         query-name (second parsed-query)
         query-parameters (getParameters parsed-query)
         fetched (fetch-query database query-name)]
	      (if(str/includes? fetched ":-")
		      (every? #(evaluate-query database %) (build-multiple-querys fetched query-parameters))
           ; recursive, it can have nested rules. Infinite loop threat?
           
			    (evaluate-fact database query)
			  )
    )
    nil
  )
)
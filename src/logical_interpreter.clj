(ns logical-interpreter)

(defn evaluate-query
  "Returns true if the rules and facts in database imply query, false if not. If
  either input can't be parsed, returns nil"
  [database query]
  nil
)

(defn validate-line
	"returns true if it is valid, false if not"
	[line]
	(let[no-dot-line (replace line ".clojure.string/split-lines" "")
		name-and-params-separated (clojure.string/split line "(" )]
		nil
	)
)

(defn validate-database
	"returns true if it is valid, false if not"
	[db]
	(let[lines (clojure.string/split-lines db)]
	(every? validate-line lines) 
	)
)
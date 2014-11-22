
void tab() {
    print("\t")
}

lines = new File("demo/demo.diff").readLines()
def hunk = lines.findIndexOf { it.startsWith("@@") }
def (info, context) = lines[hunk].split("@@").collect { it.trim() }[1..2]

println("patch")

info = info.replaceAll(/[\-\+]/,'').split(/,| /).collect { Integer.valueOf(it) }
tab(); println("find\t\"$context\"")
3.times { t ->
    def text = lines[hunk + t +1].replaceAll(/\t/,"\\\\t")
    tab(); println("next\t\"${text}\"")
}
println()
i = hunk + 4
def mark = lines[i][0]
while(mark == '-' || mark == '+') {
    def text = lines[i].substring(1).replaceAll(/\t/,"\\\\t")
    if(mark == "-") {
        tab(); println "delete\t\"${text}\""
    } else if(mark == "+"){
        tab(); println "add\t\"${text}\""
    }
    i++
    mark = lines[i][0]
}
println()
3.times { t ->
    if(lines[i + t].length() <= 1) {
        tab(); println("next\t\"\"")
    } else {
        def text = lines[i + t].substring(1).replaceAll(/\t/,"\\\\t")
        tab(); println("next\t\"${text}\"")
    }
}

tab(); println("commit")
println("done")
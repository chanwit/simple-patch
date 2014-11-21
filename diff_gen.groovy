lines = new File("demo.diff").readLines()
def hunk = lines.findIndexOf { it.startsWith("@@") }
def (info, context) = lines[hunk].split("@@").collect { it.trim() }[1..2]

info = info.replaceAll(/[\-\+]/,'').split(/,| /).collect { Integer.valueOf(it) }
println("find\t\"$context\"")
3.times { t ->
    def text = lines[hunk + t +1].replaceAll(/\t/,"\\\\t")
    println("next\t\"${text}\"")
}
println()
i = hunk + 4
def mark = lines[i][0]
while(mark == '-' || mark == '+') {
    def text = lines[i].substring(1).replaceAll(/\t/,"\\\\t")
    if(mark == "-") {
        println "delete\t\"${text}\""
    } else if(mark == "+"){
        println "add\t\"${text}\""
    }
    i++
    mark = lines[i][0]
}
3.times { t ->
    if(lines[i + t].length() <= 1) {
        println("next\t\"\"")
    } else {
        def text = lines[i + t].substring(1).replaceAll(/\t/,"\\\\t")
        println("next\t\"${text}\"")
    }
}
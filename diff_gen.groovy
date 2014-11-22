
void tab() {
    print("\t")
}

lines = new File("demo/0001-added-tab-indentation.patch").readLines()

def subject = lines.findIndexOf { it.startsWith("Subject:") }
def name = lines[subject] - "Subject: [PATCH] "
println("name\t\"$name\"")

def diffCmd = lines.findIndexOf { it.startsWith("diff --git") }
def cmd = lines[diffCmd].split()
def filename = cmd[2] - "a/"

println("patch\t\"$filename\"")

def i = 0

while(true) {
    def hunk = lines.findIndexOf(i){ it.startsWith("@@") }
    if(hunk == -1) {
        // all hunk processed
        break
    }

    def hunkData = lines[hunk].split("@@")
    def info = hunkData[1].trim()
    def context = hunkData.size() == 3 ? hunkData[2].trim() : ""

    //println ">> DEBUG: ${info}"

    info = info.replaceAll(/[\-\+]/,'').split(/,| /).collect { Integer.valueOf(it) }
    if(context) {
        tab(); println("find\t\"$context\"")
    } else {
        tab()
        println "goto\t1"
    }

    i = hunk + 1
    def mark = lines[i][0]
    def find = true

    while(i < lines.size()) {

        while(mark == '-' || mark == '+') {
            def text = lines[i].substring(1).replaceAll(/\t/,"\\\\t")
            if(mark == "-") {
                tab(); println "delete\t\"${text}\""
            } else if(mark == "+"){
                tab(); println "add\t\"${text}\""
            }
            i++; if(i >= lines.size()) break
            mark = lines[i][0]
        }
        println()

        while(mark != '-' && mark != '+') {
            def text = lines[i].substring(1).replaceAll(/\t/,"\\\\t")
            if(find) {
                tab(); println("find\t\"$text\"")
                find = false
            } else {
                tab(); println("next\t\"$text\"")
            }
            i++; if(i >= lines.size()) break
            mark = lines[i][0]
            if(mark == "@") {
                break;
            }
        }
        println()
        if(mark == "@") {
            break;
        }

        if(i >= lines.size()) break
    }

}

tab(); println("commit")
println("done")
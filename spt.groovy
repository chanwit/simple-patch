lines = []
current = -1
filename = ""
signatureStr = ""
useTabs   = false
useSpaces = false
nameStr = ""
markCommit = false

name = {
    nameStr = it
}

patch = { name ->
    markCommit = false

    def f = new File(name)
    lines = f.readLines()
    filename = name
    current = -1
    println "\nPatching $filename ..."
    if(nameStr) {
        print   "  with \"$nameStr\" "
    }
}

find = { text ->
    current = lines.findIndexOf { it == text }
    if(current == -1) {
        throw new RuntimeException("Cannot locate \"$text\"")
    }
    print "."
}

next = { text ->
    current = lines.findIndexOf(current) { it == text }
    if(current == -1) {
        throw new RuntimeException("Cannot locate \"$text\"")
    }
    print "."
}

delete = { text ->
    next(text)
    lines.remove(current)
    print "-"
}

insert = { text ->
    text = text.stripMargin('+')
    if(signatureStr) {
        text = "$text  $signatureStr"
    }
    lines.add(current, text)
    print "+"
}

add = { text ->
    text = text.stripMargin('+')
    if(signatureStr) {
        text = "$text  $signatureStr"
    }
    lines.add(current + 1, text)
    print "+"
}

save = { name ->
    new File(name).withWriter { out ->
        lines.each {
            String text = it
            if(useTabs) {
                text = it.replaceAll("(?m)(?<=^ *)    ", "\t")
            } else if(useSpaces) {
                text = it.replaceAll("(?m)(?<=^ *)\t", "    ")
            }
            out.writeLine(text)
        }
    }
    println " Done!"
}

append = { where, text ->
    if(signatureStr) {
        text = "$text  $signatureStr"
    }
    lines[current + where] = lines[current + where] + text
    print "+"
}

above = { where ->
    return [append: { text ->
        if(signatureStr) {
            text = "$text  $signatureStr"
        }
        lines[current - where] = lines[current - where] + text
    }]
    print "+"
}

offset = { where ->
    return [append: { text ->
        if(signatureStr) {
            text = "$text  $signatureStr"
        }
        lines[current + where] = lines[current + where] + text
        print "+"
    }, insert: { text ->
        if(signatureStr) {
            text = "$text  $signatureStr"
        }
        lines.add(current + where, text)
        print "+"
    }]
}

check = { text ->
    def found = lines.findIndexOf { it == text } > -1
    if(found) {
        throw new RuntimeException("File \"${filename}\" already patched")
    }
    print "c"
}

signature = { text ->
    signatureStr = "/* $text */"
}


File scriptFile  = new File(args[0])
Script dsl = new GroovyShell(this.class.classLoader, this.binding).parse(scriptFile.text)

dsl.metaClass.getDone = { ->
    save(filename)

    if("git add $filename".execute().waitFor() != 0) {
        return
    }

    if(markCommit) {
        if("git commit -m \"$nameStr\"".execute().waitFor() == 0) {
            def hex = "git log --pretty=\"format:%H\" -1".execute().text
            println "  committed as $hex"
            def diff = nameStr.toLowerCase().replaceAll(" ", "_").replaceAll(/\./, "_") + ".diff"
            new File(diff).write("git --no-pager diff HEAD^1 HEAD".execute().text)
            println "  written to $diff"
        } else {
            println "  failed to commit"
        }

    }
}

dsl.metaClass.getCommit = { ->
    markCommit = true
}

try {
    dsl.run()
} catch(e) {
    println "\n  error: ${e.message}"
}
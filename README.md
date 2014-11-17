Simple Patch Toolkit
====================

SPT or Simple Patch Toolkit is a tool for writing a patch with a domain specific language (DSL).
With the SPT language, we can easily patch a text file with search and replace syntax.
This allows us to write robust patches to support wide-range frequently-evolved source codes.

For example,

    useTabs = true

    name "001 Add new splitter ID to allfilters"
    patch "include/mediastreamer2/allfilters.h"
        check  "\tMS_SPLITTER_ID"

        find   "} MSFilterId;"
        offset (-1) append ","
        insert "\tMS_SPLITTER_ID"

        commit
    done

Patch in the example check to see if there exists MS_SPLITTER_ID defined in `MSFilterId`.
If not, it then locate the line `} MSFilterId;`. Then append `,` to the line prior to that.
Next, the patch inserts a new line containing to "MS_SPLITTER_ID" prefixed with a TAB just before the current line.
Then saves this change as a `Git` commit and automatically generate a `diff` file for us.
The `name` command will be used as the commit's message, and the diff's filename.

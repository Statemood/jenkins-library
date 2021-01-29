package me.rulin.kubernetes

/* 
    Location kind of 'kd', from file 'f', with 'readJSON' or 'readYaml'
*/
def kind(String kd, String t, String f) {
    try {
        def private r = null

        if (t == "json") { def private r = readJSON file: f }
        if (t == "yaml") { def private r = readYaml file: f }

        if (r.size() > 0 && r[0] != null) {
            for (int i=0; i<r.size(); i++) {
                if (r[i].kind == kd) { return i }
            }
        } else { return }
    }
    catch (e) { throw e }
}
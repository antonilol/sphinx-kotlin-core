package chat.sphinx.utils.platform

import okio.Path
import okio.Path.Companion.toPath

actual fun getUserHomeDirectory(): Path {
    // TODO: Checks for systems/platforms without a user home directory...
    return System.getProperty("user.home").toPath()
}

actual fun getSphinxDirectory(): Path {
    var dir = System.getenv("SPHINX_CONFIG_DIR")
    if (dir == null) {
        dir = ".sphinx"
    }
    return getUserHomeDirectory().resolve(dir)
}

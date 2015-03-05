package de.audiobooktool.wrapper.mp4v2

import groovy.util.logging.Log4j
/**
 * Wrapper class for mp4info tool to read tag info from mp4 files.
 * User: andy
 * Date: 20.05.13
 */
@Log4j
class Mp4Info extends AbstractMp4Tool {

    String[] knownInfoTags = [
            " Name:",
            " Artist:",
            " Release Date:",
            " Album:",
            " Genre:",
            " Comments:",
            " Cover Art pieces:",
            " Album Artist:",
            " Copyright:"]

    /**
     * Constructor.
     * @param mp4File mp4 file to operate on.
     */
    Mp4Info(String mp4File) {
        super(mp4File)
    }

    /**
     * List Mp4Tags in file.
     * @return
     */
    List<Mp4Tag> listTags() {
        log.info("getting Metainfo from $mp4File")
        def process = [ tools.mp4InfoExecutable, mp4File ].execute()
        def output=new StringBuilder(1024)
        def error=new StringBuilder(1024)
        process.waitForProcessOutput(output, error)

        if (error.toString().contains("can't open")) {
            log.error(error.toString())
            throw new WrapperException(msg: "error listing tags", execOutput: error.toString())
        }

        List<Mp4Tag> artList = []

        output.eachLine {
            log.info "> $it"

            if (it.startsWith(" ") && it.contains(":")) { // seems to be a tag info
                // check all know tag infos
                def foundTag = knownInfoTags.find { knownTag -> it.startsWith(knownTag) }
                if (foundTag != null) {
                    log.info(" found tag $foundTag")
                    artList << new Mp4Tag(name: foundTag.trim() - ":", value: it.substring(foundTag.length() + 1))
                } else {
                    log.info(" $it seems to be a unknown tag")
                }
            }
        }
        return artList
    }
}

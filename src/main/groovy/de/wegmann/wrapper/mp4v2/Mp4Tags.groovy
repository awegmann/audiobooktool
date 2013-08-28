package de.wegmann.wrapper.mp4v2

import groovy.util.logging.Log

import java.util.logging.Level

/**
 * Set tag information on a given mp4 file.
 *
 * Date: 26.05.13
 * Time: 18:14
 */
@Log
class Mp4Tags extends AbstractMp4Tool {

    /**
     * Mapping between tag name and parameter option for mp4tags
     */
    def parameterMap = [
            "Name": "-song",
            "Artist": "-artist",
            "Release Date": "-year",
            "Album": "-album",
            "Genre": "-genre",
            "Comments": "-comment",
            "Album Artist": "-albumartist",
            "Copyright": "-copyright"]
    /**
     * Constructor.
     * @param mp4File mp4 file to operate on.
     */
    Mp4Tags(String mp4File) {
        super(mp4File)
    }

    /**
     * Set all tags given in the tag list on the mp4 file.
     * @param tagList tag list to set
     */
    void setTags(List<Mp4Tag> tagList) {
        def cmd = [tools.mp4TagsExecutable]

        tagList.each {
            if (parameterMap.containsKey(it.name)) {
                cmd << parameterMap[it.name] << it.value
            } else {
                log.log(Level.WARNING, "tag name <" + it.name + "> unknown!")
            }
        }
        cmd << this.mp4File

        def process = cmd.execute()
        process.waitFor()
        if (process.exitValue() != 0) {
            def errorText = process.err.text
            println errorText
            throw new WrapperException(msg: "error setting tag info", execOutput: errorText)
        }

    }
}

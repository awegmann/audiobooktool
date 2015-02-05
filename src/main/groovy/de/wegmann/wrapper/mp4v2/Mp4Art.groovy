package de.wegmann.wrapper.mp4v2

import groovy.util.logging.*

class Mp4ArtInfo {
	int    bytes
	String crc32
	String type
}

/**
 * Wrapper class of mp4art tools to extract and add images on mp4 files.
 */
@Log
class Mp4Art extends AbstractMp4Tool {

    /**
     * Constructor.
     * @param mp4File mp4 file to operate on.
     */
    Mp4Art(String mp4File) {
        super(mp4File)
    }

    /**
	 * Lists all cover arts.
	 * @return List of {@link Mp4ArtInfo} objects.
	 */

    public List<Mp4ArtInfo> list() {
		def process = [ tools.mp4ArtExecutable, "--list", mp4File ].execute()
        process.waitFor()

		if (process.exitValue()!=0) {
            def errorText = process.err.text
            if (errorText.startsWith("ERROR")) {
                log.error errorText
                throw new WrapperException(msg:"error listing images",execOutput:errorText)
            }
        }

        List<Mp4ArtInfo> artList = []

		process.text.eachLine {
			log.info "> $it"

            if (! it.startsWith("IDX") && !it.startsWith("---")) { // skip headers
				def splitted = it.split()
				artList << new Mp4ArtInfo(bytes:splitted[1].toInteger(), crc32:splitted[2],type:splitted[3])
			}
		}
		return artList
	}

	/**
	 * Add a given image file to the mp4 file.
	 * @param imageFile image to add
	 */
	public void addImage(String imageFile) {
        def process = [ tools.mp4ArtExecutable, "--add", imageFile, mp4File ].execute()
        process.waitFor()
        if (process.exitValue()!=0) {
            def errorText = process.err.text
            if (errorText.startsWith("ERROR")) {
                println errorText
                throw new WrapperException(msg:"error adding image",execOutput:errorText)
            }
        }
	}
	
	/**
	 * Extract an image form mp4 file
	 * @param index index of file to extract
	 * @return name of image file created
	 */
	public String extractImage(int index) {
		def process = [ tools.mp4ArtExecutable, "--extract", "--art-index", index, mp4File ].execute()
        process.waitFor()
        if (process.exitValue()!=0) {
            def errorText = process.err.text
            if (errorText.startsWith("ERROR")) {
                println errorText
                throw new WrapperException(msg:"error extracting image",execOutput:errorText)
            }
        }
		String imageFileName
		process.text.eachLine {
			log.info "> $it"
			if (it.startsWith("extracting")) { 
				def splitted=it.split(" -> ")
				imageFileName=splitted[1]
			}
		}
		return imageFileName
	}

}

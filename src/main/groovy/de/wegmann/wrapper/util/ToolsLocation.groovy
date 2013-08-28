package de.wegmann.wrapper.util

/**
 * Class to supply native tools locations.
 */
class ToolsLocation {
	private static final INSTANCE = new ToolsLocation()
	
	static ToolsLocation getInstance(){
		return INSTANCE
	}

	private ToolsLocation() {
	}

    String mp4ArtExecutable = "mp4art"
    String mp4InfoExecutable = "mp4info"
    String mp4TagsExecutable = "mp4tags"
    String mp4ChapsExecutable = "mp4chaps"

    String ffmpegExecutable = "ffmpeg"
}

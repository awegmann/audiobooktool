package de.audiobooktool.wrapper.mp4v2

import de.audiobooktool.wrapper.util.ToolsLocation

/**
 * Abstract base class for all wrapper implementations.
 * User: andy
 * Date: 20.05.13
 */
class AbstractMp4Tool {

    /**
     * Tools locations / executables to run
     */
    protected ToolsLocation tools;

    /**
     * mp4 file to operate on.
     */
    protected String mp4File

    /**
     * Constructor.
     * @param mp4File mp4 file to operate on.
     */
    public AbstractMp4Tool(String mp4File) {
        super();
        this.mp4File = mp4File;
        this.tools = ToolsLocation.getInstance()
    }
}

package de.audiobooktool.convert.commands

/**
 * User: andy
 * Date: 02.09.13
 */
interface ToolCommandIF {

    /**
     * Set dry run parameter. If dry run is set to TRUE, all commands just print
     * out, what they would do, but don't convert anything.
     */
    public void setDryRun(boolean dryRun)

    /**
     * Get dry run parameter.
     * @return
     */
    public boolean isDryRun()

    /**
     * just execute the convert job
     */
    public void execute()
}

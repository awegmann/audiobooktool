package de.audiobooktool.convert.commands
/**
 * User: andy
 * Date: 03.09.13
 */
abstract class AbstractToolCommand implements ToolCommandIF {

  static long totalExecutionDurationSeconds = 0
  boolean dryRun

    @Override
    void setDryRun(boolean dryRun) {
        this.dryRun = dryRun
    }

    @Override
    boolean isDryRun() {
        return this.dryRun
    }

    /**
     * Init the command with ConversionParameterObj.
     * @param data Parameters for conversion
     */
    abstract public void init(ConversionParameterObj data);

  /**
   * Executes the command, without showing the output at default out and err. The method
   * is waiting to return until the process ended.
   *
   * @param command the command to execute
   * @return the process object
   */
  def static runCommand(command) {
    return runCommand(command, false, null)
  }

  /**
   * Executes the command and the method is waiting to return until the process ended.
   *
   * @param command the command to execute
   * @param outputAtCommandLine true means show output at command line
   * @return the process object
   */
  def static runCommand(command, outputAtCommandLine) {
    return runCommand(command, outputAtCommandLine, null)
  }

  /**
   * Executes the command ...
   *
   * @param command the command to execute
   * @param outputAtCommandLine
   * @param timeoutWaiting
   * @return the process object
   */
  def static runCommand(command, outputAtCommandLine, timeoutWaiting) {
    Date startCmd = new Date()
    Process process = command.execute()
    if (outputAtCommandLine)
      process.consumeProcessOutput(System.out, System.err)
    if (timeoutWaiting == null)
      process.waitFor()
    else
      process.waitForOrKill(timeoutWaiting)
    Date endCmd = new Date()
    // TODO: use logger ...
    long durationSeconds = (endCmd.getTime()-startCmd.getTime())/1000
    totalExecutionDurationSeconds += durationSeconds
    println "********************************************************************************************"
    println "********** Executions duration " + durationSeconds + " seconds!! (total exec seconds: " + totalExecutionDurationSeconds + ")"
    println "********************************************************************************************"
    return process
  }


}

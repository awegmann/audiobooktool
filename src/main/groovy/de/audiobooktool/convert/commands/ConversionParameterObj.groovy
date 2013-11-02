package de.audiobooktool.convert.commands

import de.audiobooktool.config.BookInfo
import de.audiobooktool.config.OutputFormatEnum
import de.audiobooktool.wrapper.util.ToolsLocation
/**
 * This object contains all data to control the workflow.
 *
 * User: WagnerOl
 * Date: 11.09.13
 * Time: 20:38
 */
public class ConversionParameterObj {

    // arguments from command line
    public String fileAudio, fileMetaData, fileOutputBase;
    public boolean dryRun;
    public OutputFormatEnum outputFormat;

    // some other information
    final Date startOfConverting = new Date();

    /**
     * Determine the correct value for the attribute fileOutputBase.
     *
     * The value is calculated by the following rules:
     * <ul>
     * <li>if <code>fileOutputBase</code> is currently <code>null</code>, the value
     * is set to <code>fileMetaData</code> with removed extension.</li>
     * <li>if <code>fileOutputBase</code> points to a directory it is set to this directory plus the
     * fileMetaData filename without extension</li>
     * <li>in any other case the current value is treated as a filename and the extension is cut off.</li>
     * </ul>
     */
    public void determineFileOutputBase() {

        if (this.fileOutputBase == null) {
            fileOutputBase = removeExtension(new File(fileMetaData).getAbsolutePath());
        } else {
            if (new File(fileOutputBase).isDirectory()) {
                fileOutputBase = new File(fileOutputBase, removeExtension(new File(fileMetaData).getName())).getAbsolutePath();
            } else { // so it should be a file reference
                fileOutputBase = removeExtension(fileOutputBase);
            }
        }

    }



    /***********************************************************************************************
     * Getter and setter
     ***********************************************************************************************/

    /**
     * Get a File object which for a chapter file. File name is build as
     * fileOutputBase + no + suffix which results in files like
     * <code>outputdir/000.mp3</code>
     *
     * @param no chapter number for filename
     * @param noOfNumericChars number of digits per chapter number
     * @param suffix suffix of output file
     * @return chapter file output name
     */
    public File getFileOutput(int no, int noOfNumericChars, String suffix) {
        File res = new File(fileOutputBase, fillWithZeros(no, noOfNumericChars) + suffix);
        if (res.exists()) {
            // just to be unique, add a timestamp to basename
            res = new File(fileOutputBase + startOfConverting.toString(), fillWithZeros(no, noOfNumericChars) + suffix);
        }
        return res;
    }

    /**
     *
     * @param suffix
     * @return
     */
    public File getFileOutput(String suffix) {
        File res = new File(fileOutputBase + suffix);
        if (res.exists()) {
            // just to be unique, add a timestamp to basename
            res = new File(fileOutputBase, startOfConverting.toString(), suffix);
        }
        return res;
    }

    /***********************************************************************************************
     * Helper methods
     ***********************************************************************************************/

    /**
     * Removes the file extension from a given file name.
     * @param str file name to remove extension
     * @return file name without extension
     */
    public static String removeExtension(String str) {
        int lastIndexOfDot = str.lastIndexOf('.');
        if (lastIndexOfDot != -1) {
            return str.substring(0, lastIndexOfDot);
        } else {
            return str;
        }
    }

    /**
     * Return given number with padded zeros.
     * @param no number to pad
     * @param noOfNumericChars number of digits
     * @return padded number
     */
    private String fillWithZeros(int no, int noOfNumericChars) {
        // TODO: Info von Andreas
        // assert 'x'.padRight(3,'_') == 'x__'

        return "$no".padLeft(3, '0')
    }

    @Override
    public String toString() {

        ToolsLocation locations = ToolsLocation.getInstance()

        String dumpOut =
                "*********************************************************************************************\n" +
                        "***** Executables                                                                       *****\n" +
                        "*********************************************************************************************\n" +
                        "ffmpeg: $locations.ffmpegExecutable\n" +
                        "mp4tags: $locations.mp4TagsExecutable\n" +
                        "mp4chaps: $locations.mp4ChapsExecutable\n" +
                        "mp4info: $locations.mp4InfoExecutable\n" +
                        "mp4art: $locations.mp4ArtExecutable\n" +

                        "*********************************************************************************************\n" +
                        "***** Command line arguments and configuration                                          *****\n" +
                        "*********************************************************************************************\n" +
                        "fileAudio: $fileAudio\n" +
                        "fileMetaData: $fileMetaData\n" +
                        "fileOutputBase: $fileOutputBase\n" +
                        "dryRun: $dryRun\n" +
                        "OutputFormatEnum: $outputFormat\n" +

                        "*********************************************************************************************\n" +
                        "***** Other values                                                                      *****\n" +
                        "*********************************************************************************************\n" +
                        "startOfConverting: $startOfConverting\n"

        return dumpOut
    }
}

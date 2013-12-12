package de.audiobooktool.convert

import de.audiobooktool.config.OutputFormatEnum
import de.audiobooktool.convert.commands.AbstractToolCommand
import de.audiobooktool.convert.commands.ConversionParameterObj
import de.audiobooktool.convert.commands.MergeToM4BCommand
import de.audiobooktool.convert.commands.SplitToChapterCommand
import groovy.util.logging.Log4j
import org.apache.log4j.BasicConfigurator

/**
 * User: andy
 * Date: 05.07.13
 */
@Log4j
class ConvertTool {
    public static void main(String[] args) {
        // configure log4j
        BasicConfigurator.configure()

        AbstractToolCommand command
        OutputFormatEnum outputFormat

        def cli = new CliBuilder(usage: 'ConvertTool -c joinToM4A -a file -m file [-o file]\nconverts an audio file to a ".m4b" audio' +
                ' book and copies all metadata from another file to it.')
        cli.h(longOpt: 'help', 'usage information')
        cli.c(longOpt: 'cmd', 'command to execute can be splitToMP3, splitToM4A, joinToM4A', args: 1, required: true)
        cli.a(longOpt: 'audioFrom', args: 1, required: true,
                argName: 'audiofile', 'use audio file as audio source to convert from')
        cli.m(longOpt: 'metaFrom', args: 1, required: false,
                argName: 'audiofile', 'use audio file as meta info source to copy from, default use audioFrom value')
        cli.o(longOpt: 'outputTo', args: 1, required: false,
                argName: 'file', 'if file is a directory, put the converted file in here, if it\' a file name use filename ' +
                'for output (default is base name of metaFrom-file plus .m4b extension)')
        cli.d(longOpt: 'dryRun', args: 0, required: false,
                'do not convert, just print what files are affected')

        // parse command line attributes
        def opt = cli.parse(args)

        // if parsing went wrong, stop
        if (!opt) {
            return
        }

        // show usage if help requested
        if (opt.h) {
            cli.usage()
        }

        // check if correct command is given
        String cmdOpt = (String) opt.c
        log.debug("cmd is $cmdOpt")
        switch (cmdOpt) {
            case 'splitToMP3':
                command = new SplitToChapterCommand()
                outputFormat = OutputFormatEnum.MP3
                break
            case 'splitToM4A':
                command = new SplitToChapterCommand()
                outputFormat = OutputFormatEnum.M4A
                break
            case 'joinToM4A':
                command = new MergeToM4BCommand()
                outputFormat = OutputFormatEnum.M4B
                break
            default:
                log.error("No suitable command given. May be one of 'splitToMP3', 'joinToM4A', 'splitToM4A'.")
                return
        }

        ConversionParameterObj data = new ConversionParameterObj(
                fileAudio: (String) opt.a,
                fileMetaData: (String) opt.m ? opt.m : opt.a,
                fileOutputBase: opt.o ? opt.o : null,
                dryRun: opt.d,
                outputFormat: outputFormat
        )

        data.determineFileOutputBase()

        log.info("$data")

        // initialize the command with the preconfigured conversion options
        command.init(data)

        // execute the command
        command.execute()
    }

}

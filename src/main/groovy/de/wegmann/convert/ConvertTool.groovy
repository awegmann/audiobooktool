package de.wegmann.convert

import de.wegmann.config.DataObj
import de.wegmann.config.OutputFormatEnum

/**
 * User: andy
 * Date: 05.07.13
 */
class ConvertTool {
  public void usage() {
    println("""
      Usage:
      Convert Flac encoded file to a .m4a file and copy all metadata from third file to the new one.

      Options:
        -a file, --audioFrom=file
           use file as audio source
        -m file, --metaFrom=file
           use file as metainfo source

""")
  }

  public static void main(String[] args) {
    def cli = new CliBuilder(usage: 'ConvertTool -a file -m file [-o file]\nconverts an audio file to a ".m4b" audio' +
            ' book and copies all metadata from another file to it.')
    cli.h(longOpt: 'help', 'usage information')
    cli.s(longOpt: 'split', 'split one input file to many output files (by chapter info)')
    cli.j(longOpt: 'join', 'join many input file to one output files (and create chapter info)')
    cli.a(longOpt: 'audioFrom', args: 1, required: true,
            argName: 'audiofile', 'use audiofile as audio source to convert from')
    cli.m(longOpt: 'metaFrom', args: 1, required: true,
            argName: 'audiofile', 'use audiofile as meta info source to copy from')
    cli.o(longOpt: 'outputTo', args: 1, required: false,
            argName: 'file', 'if file is a directory, put the converted file in here, if it\' a file name use filename ' +
            'for output (default is base name of metaFrom-file plus .m4b extension)')
    cli.d(longOpt: 'dryRun', args: 0, required: false,
            'do not convert, just print what files are affected')

    def opt = cli.parse(args)

    if (!opt) {
      return
    }

    if (opt.h) {
      cli.usage()
    }

    DataObj data = DataObj.createInstance((String) opt.a, (String) opt.m, opt.o ? opt.o : null,
            new Boolean(opt.d), OutputFormatEnum.MP3)

    println "$data"

    def command = new ConvertAndCopyMeta(destFile: data.getFileOutput(".m4b"), audioFile: data.getFileAudio(),
            metaFile: data.getFileMetaData(), dryRun: data.isDryRun())

    command.execute()

  }

}

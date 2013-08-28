package de.wegmann.wrapper.ffmpeg

import java.security.MessageDigest

/**
 * User: andy
 * Date: 21.06.13
 */
class FfmpegTest extends GroovyTestCase {
    void setUp() {
        def antBuilder = new AntBuilder()
        antBuilder.mkdir(dir: "src/test/resources/temp")
        antBuilder.copy(file: "src/test/resources/Audiobook_Test_plain.m4a",
                tofile: "src/test/resources/temp/Audiobook_Test.m4a")
    }
 /*
    void tearDown() {
        def antBuilder = new AntBuilder()
        antBuilder.delete(dir:"src/test/resources/temp")
    }
   */
    void testConvertAudioM4a() {
        def audioM4a = new Ffmpeg("src/test/resources/temp/Audiobook_Test.m4a")
        audioM4a.convertToFlac("src/test/resources/temp/Audiobook_Test.flac")

        assertTrue("flac file existiert nicht",new File("src/test/resources/temp/Audiobook_Test.flac").exists())

        def audioFlac = new Ffmpeg("src/test/resources/temp/Audiobook_Test.flac")
        audioFlac.convertToM4a("src/test/resources/temp/Audiobook_Test2.m4a")

        assertTrue("m4a file existiert nicht",new File("src/test/resources/temp/Audiobook_Test2.m4a").exists())
    }

    void testConvertAudioM4b() {
        def audioM4a = new Ffmpeg("src/test/resources/temp/Audiobook_Test.m4a")
        audioM4a.convertToFlac("src/test/resources/temp/Audiobook_Test.flac")

        assertTrue("flac file existiert nicht",new File("src/test/resources/temp/Audiobook_Test.flac").exists())

        def audioFlac = new Ffmpeg("src/test/resources/temp/Audiobook_Test.flac")
        audioFlac.convertToM4b("src/test/resources/temp/Audiobook_Test2.m4b")
        audioFlac.convertToM4a("src/test/resources/temp/Audiobook_Test2.m4a")

        def m4afile = new File("src/test/resources/temp/Audiobook_Test2.m4a")
        assertTrue("m4a file existiert nicht", m4afile.exists())
        def m4bfile = new File("src/test/resources/temp/Audiobook_Test2.m4b")
        assertTrue("m4b file existiert nicht", m4bfile.exists())
        assertEquals("md5summen sind nicht gleich", md5sum(m4afile), md5sum(m4bfile))
    }

    private String md5sum(File file) {
        assert file != null
        def md = MessageDigest.getInstance("MD5")
        md.update(file.readBytes())
        new BigInteger(1, md.digest()).toString(16)
    }
}

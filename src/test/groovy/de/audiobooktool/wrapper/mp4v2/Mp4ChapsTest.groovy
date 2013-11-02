package de.audiobooktool.wrapper.mp4v2

/**
 * User: andy
 * Date: 02.06.13
 */
class Mp4ChapsTest extends GroovyTestCase {

    void setUp() {
        def antBuilder = new AntBuilder()
        antBuilder.mkdir(dir: "src/test/resources/temp")
        antBuilder.copy(file: "src/test/resources/Audiobook_Test_with_Metadata.m4a",
                tofile: "src/test/resources/temp/Audiobook_Test_with_Metadata.m4a")
        antBuilder.copy(file: "src/test/resources/Audiobook_Test_plain.m4a",
                tofile: "src/test/resources/temp/Audiobook_Test_plain.m4a")
    }

    void tearDown() {
        def antBuilder = new AntBuilder()
        antBuilder.delete(dir: "src/test/resources/temp")
    }

    /**
     * Test if export of chapters works. exported chapters must contain "Kapitel 1"
     * as a chapter name. Additionally check if the intermediate *.capters.txt file
     * is deleted after export.
     */
    void testExportChapters() {
        def mp4Chaps = new Mp4Chaps("src/test/resources/temp/Audiobook_Test_with_Metadata.m4a")
        def chapters = mp4Chaps.exportChapters()
        assertTrue(chapters.contains("Kapitel 1"))
        assertFalse(new File("src/test/resources/temp/Audiobook_Test_with_Metadata.chapters.txt").exists())
    }

    /**
     * Test if an export on a not existing file throws the expected exception.
     */
    void testExportChaptersWrongFile() {
        def mp4Chaps = new Mp4Chaps("src/test/resources/temp/I do not exist.m4a")
        try {
            def chapters = mp4Chaps.exportChapters()
            fail("should throw exception")
        } catch (WrapperException e) {
            assertTrue(e.msg.contains("error exporting chapters"))
        }
    }

    /**
     * Try to import chapter marks to file. Export the chapter marks and compare input with output.
     * Additionally check if the intermediate chapter.txt-file is cleaned up correctly.
     */
    void testImportChapters() {
        String chaptersToImport = "00:00:00.000 Kapitel 1\n00:00:05.000 Kapitel 2\n00:00:10.000 Kapitel 3\n"

        def testFileName = "src/test/resources/temp/Audiobook_Test_plain.m4a"
        def mp4Chaps = new Mp4Chaps(testFileName)
        try {
            int oldFileSize = new File(testFileName).size()
            mp4Chaps.importChapters(chaptersToImport)
            int newFileSize = new File(testFileName).size()
            assertFalse(new File("src/test/resources/temp/Audiobook_Test_plain.chapters.txt").exists())
            assertFalse(oldFileSize == newFileSize)

            def chaptersExported = mp4Chaps.exportChapters()
            assertEquals(chaptersToImport, chaptersExported)
        } catch (WrapperException e) {
            fail("no exception expected", e)
        }

    }
}

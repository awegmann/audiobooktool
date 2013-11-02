package de.audiobooktool.wrapper.mp4v2

/**
 * Created with IntelliJ IDEA.
 * User: andy
 * Date: 20.05.13
 * Time: 22:52
 * To change this template use File | Settings | File Templates.
 */
class Mp4InfoTest extends GroovyTestCase {

    void setUp() {
        def antBuilder = new AntBuilder()
        antBuilder.mkdir(dir: "src/test/resources/temp")
        antBuilder.copy(file: "src/test/resources/Audiobook_Test_with_Metadata.m4a",
                tofile: "src/test/resources/temp/Audiobook_Test_with_Metadata.m4a")
    }

    void tearDown() {
        def antBuilder = new AntBuilder()
        antBuilder.delete(dir: "src/test/resources/temp")
    }


    void testListTags() {
        def mp4info = new Mp4Info("src/test/resources/temp/Audiobook_Test_with_Metadata.m4a")
        def tags = mp4info.listTags()
        assertEquals(9, tags.size())

        assertEquals("2013", tags.find { it.name == "Release Date" }.value)

    }

    /*
    void testListTagsWithErrors() {
        def mp4info = new Mp4Info("src/test/resources/Ohrdebil.de_ep7_an10nn.aax")
        def tags = mp4info.listTags()
        assertEquals(8,tags.size())

        assertEquals("Johanna Steiner",tags.find{it.name == "Album Artist"}.value)
    }
    */

    void testListTagsForInvalidFile() {

        try {
            def mp4info = new Mp4Info("src/test/resources/this_is_not_here.m4a")
            def tags = mp4info.listTags()
            fail("A WrapperException should be thrown...")
        } catch (WrapperException e) {
            assertTrue("Wrong message in excpetion", e.msg.contains("error listing tags"))
        }

    }
}

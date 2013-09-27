package de.wegmann.wrapper.mp4v2

/**
 * User: andy
 * Date: 01.06.13
 * Time: 17:12
 * To change this template use File | Settings | File Templates.
 */
class Mp4TagsTest extends GroovyTestCase {

    void setUp() {
        def antBuilder = new AntBuilder()
        antBuilder.mkdir(dir: "src/test/resources/temp")
        antBuilder.copy(file: "src/test/resources/Audiobook_Test_plain.m4a",
                tofile: "src/test/resources/temp/Audiobook_Test_plain.m4a")
    }

    void tearDown() {
        def antBuilder = new AntBuilder()
        antBuilder.delete(dir: "src/test/resources/temp")
    }

    /**
     * Testet das Setzen von Tags. Mp4Info liest die gesetzten Infos wieder aus.
     */
    void testSetTags() {
        def theTestFile = "src/test/resources/temp/Audiobook_Test_plain.m4a"
        def mp4tags = new Mp4Tags(theTestFile)
        def mp4info = new Mp4Info(theTestFile)
        def tags = mp4info.listTags()
        assertEquals(0, tags.size())

        mp4tags.setTags(
                ["Name": "testName",
                        "Artist": "testArtist",
                        "Release Date": "2000",
                        "Album": "testAlbum",
                        "Genre": "testGenre",
                        "Comments": "no Comment",
                        "Album Artist": "the Album Artist",
                        "Copyright": "the Copyright"
                ].collect { new Mp4Tag(name: it.key, value: it.value) })

        tags = mp4info.listTags()
        assertEquals(8, tags.size())

        assertEquals("testName", tags.find { it.name == "Name" }.value)
        assertEquals("testArtist", tags.find { it.name == "Artist" }.value)
        assertEquals("2000", tags.find { it.name == "Release Date" }.value)
        assertEquals("testAlbum", tags.find { it.name == "Album" }.value)
        assertEquals("testGenre", tags.find { it.name == "Genre" }.value)
        assertEquals("no Comment", tags.find { it.name == "Comments" }.value)
        assertEquals("the Album Artist", tags.find { it.name == "Album Artist" }.value)
        assertEquals("the Copyright", tags.find { it.name == "Copyright" }.value)
    }

    /**
     * Tests error handling.
     */
    void testSetTagsOnMissingFile() {
        def theTestFile = "src/test/resources/temp/I do not exist.m4a"
        def mp4tags = new Mp4Tags(theTestFile)
        try {
            mp4tags.setTags([new Mp4Tag(name: "Name", value: "a value")])
            fail("should throw exception!")
        } catch (WrapperException e) {
            assertTrue(e.msg.contains("error setting tag info"))
        }
    }
}

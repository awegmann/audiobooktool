package de.wegmann.wrapper.mp4v2

class Mp4ArtTest extends GroovyTestCase {

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

    public void testList() {
        def mp4art = new Mp4Art("src/test/resources/temp/Audiobook_Test_with_Metadata.m4a")
        def artlist = mp4art.list()
        assert artlist[0].bytes == 13663
        assert artlist.size() == 1
    }

    public void testListWithWrongFilename() {

        try {
            def mp4art = new Mp4Art("src/test/resources/temp/this_is_not_here.m4a")
        } catch (WrapperException e) {
            assertEquals(e, "error listing images")
        }
    }


    public void testExtract() {
        def mp4art = new Mp4Art("src/test/resources/temp/Audiobook_Test_with_Metadata.m4a")
        def imageFile = mp4art.extractImage(0)
        assert imageFile == "src/test/resources/temp/Audiobook_Test_with_Metadata.art[0].jpg"
        new File(imageFile).delete()
    }

    public void testExtractInvalidIndex() {
        def mp4art = new Mp4Art("src/test/resources/temp/Audiobook_Test_with_Metadata.m4a")
        try {
            def imageFile = mp4art.extractImage(1)
            fail "must throw exception"
        } catch (WrapperException e) {
            assertEquals(e.msg, "error extracting image")
        }
    }

    public void testAddImage() {
        def mp4art = new Mp4Art("src/test/resources/temp/Audiobook_Test_with_Metadata.m4a")
        try {
            mp4art.addImage("src/test/resources/Cover.jpg")
            def newList = mp4art.list()
            assertEquals(2, newList.size())
        } catch (WrapperException e) {
            assertEquals(e.msg, "error adding image")
        }
    }
}

package cn.odboy.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import static cn.odboy.util.FileUtil.getPrefix;
import static cn.odboy.util.FileUtil.getSize;
import static cn.odboy.util.FileUtil.getSuffix;
import static cn.odboy.util.FileUtil.toFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileUtilTest {

    @Test
    public void testToFile() {
        long retval = toFile(new MockMultipartFile("foo", (byte[]) null)).getTotalSpace();
        assertEquals(500695072768L, retval);
    }

    @Test
    public void testGetExtensionName() {
        assertEquals("foo", getSuffix("foo"));
        assertEquals("exe", getSuffix("bar.exe"));
    }

    @Test
    public void testGetFileNameNoEx() {
        assertEquals("foo", getPrefix("foo"));
        assertEquals("bar", getPrefix("bar.txt"));
    }

    @Test
    public void testGetSize() {
        assertEquals("1000B   ", getSize(1000));
        assertEquals("1.00KB   ", getSize(1024));
        assertEquals("1.00MB   ", getSize(1048576));
        assertEquals("1.00GB   ", getSize(1073741824));
    }
}

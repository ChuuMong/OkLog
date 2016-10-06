package com.github.simonpercic.oklog.core;

import com.github.simonpercic.oklog.shared.data.BodyState;
import com.github.simonpercic.oklog.shared.data.HeaderData;
import com.github.simonpercic.oklog.shared.data.LogData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * LogManager unit test.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CompressionUtils.class, TimberUtils.class})
public class LogManagerUnitTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(CompressionUtils.class);
    }

    @Test
    public void testGetLogUrlIOException() throws Exception {
        when(CompressionUtils.gzipBase64(anyString())).thenThrow(new IOException());

        String baseUrl = "http://example.com";
        LogManager logManager = new LogManager(baseUrl, null, false);

        String logUrl = logManager.getLogUrl("", null);
        String expected = String.format("%s%s0", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH);

        assertEquals(expected, logUrl);
    }

    @Test
    public void testGetLogUrlEmpty() throws Exception {
        when(CompressionUtils.gzipBase64(anyString())).thenReturn("");

        String baseUrl = "http://example.com";
        LogManager logManager = new LogManager(baseUrl, null, false);

        String logUrl = logManager.getLogUrl("", null);
        String expected = String.format("%s%s0", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH);

        assertEquals(expected, logUrl);
    }

    @Test
    public void testGetLogUrl() throws Exception {
        String gzipped = "H4sIAAAAAAAAALXVzY7TMBAH8Ps-RZXzbmKPPZ5Jr7wBV4TQ-ItWtNvQpBfQvjuGVkJa7aoGxYfk\n"
                + "ENvzT_yLxp8eNpuf5dpsumc5pm7bTXJOz8sT9Nir7vE69GM_eTkcvlzOhzJjtyzTvB0Gmfb91_2y\n"
                + "u_g-nI7DOU2neZi_X0qB4fTt96zhtnB4q-gi5_8relv4ZtGy5rhfuu31m8qDeSelejbOp2i1MMHo\n"
                + "IoforPYYlFOQg2enciJlza1MWffPb3VNnofqqD9JL-X-8vg-gu3V08cPen2IV4XXw3hV-B0QDIGi\n"
                + "FyNR40gZg5SbYSWWLUZlGLPYMrACSHVULUgTjCYQdxDKD8o-JUATM8no2WcTsrWGRRSRAKOCoPQK\n"
                + "CNVRVQimBYJpgWDuIgQ7phRT-UHJJuV8YMgoKVJwhMplhzGCZVgBoTqqCgFaIEALBLiL4CSVnfAm\n"
                + "lB0YS7_G0sM5GQFUOmeyKkbRqN0KCNVRVQi61fmgW50Puup8IO-ItPcwmjEKJIoWghgchUIOSlxi\n"
                + "BwR-jdZUG1UL0gSjCcQdBHbRknYeWaJxCAkTOfYBIbMi1qWRl9ahwwoI1VF_ER4-_wL2RqAwvQoA\n"
                + "AA==\n";

        when(CompressionUtils.gzipBase64UrlSafe(eq("response_body"))).thenReturn(gzipped.replaceAll("\n", ""));

        String baseUrl = "http://example.com";
        LogManager logManager = new LogManager(baseUrl, null, false);

        String logUrl = logManager.getLogUrl("response_body", null);

        String gzippedNoNewLine = gzipped.replaceAll("\n", "");
        String expected = String.format("%s%s%s", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH, gzippedNoNewLine);
        assertEquals(expected, logUrl);
    }

    @Test
    public void testGetLogUrlLogData() throws Exception {
        String gzipped = "H4sIAAAAAAAAALXVzY7TMBAH8Ps-RZXzbmKPPZ5Jr7wBV4TQ-ItWtNvQpBfQvjuGVkJa7aoGxYfk\n"
                + "ENvzT_yLxp8eNpuf5dpsumc5pm7bTXJOz8sT9Nir7vE69GM_eTkcvlzOhzJjtyzTvB0Gmfb91_2y\n"
                + "u_g-nI7DOU2neZi_X0qB4fTt96zhtnB4q-gi5_8relv4ZtGy5rhfuu31m8qDeSelejbOp2i1MMHo\n"
                + "IoforPYYlFOQg2enciJlza1MWffPb3VNnofqqD9JL-X-8vg-gu3V08cPen2IV4XXw3hV-B0QDIGi\n"
                + "FyNR40gZg5SbYSWWLUZlGLPYMrACSHVULUgTjCYQdxDKD8o-JUATM8no2WcTsrWGRRSRAKOCoPQK\n"
                + "CNVRVQimBYJpgWDuIgQ7phRT-UHJJuV8YMgoKVJwhMplhzGCZVgBoTqqCgFaIEALBLiL4CSVnfAm\n"
                + "lB0YS7_G0sM5GQFUOmeyKkbRqN0KCNVRVQi61fmgW50Puup8IO-ItPcwmjEKJIoWghgchUIOSlxi\n"
                + "BwR-jdZUG1UL0gSjCcQdBHbRknYeWaJxCAkTOfYBIbMi1qWRl9ahwwoI1VF_ER4-_wL2RqAwvQoA\n"
                + "AA==\n";

        when(CompressionUtils.gzipBase64UrlSafe(eq("response_body"))).thenReturn(gzipped.replaceAll("\n", ""));

        String baseUrl = "http://example.com";
        LogManager logManager = new LogManager(baseUrl, null, false);

        List<HeaderData> requestHeaders = Arrays.asList(new HeaderData("q_n_1", "q_v_1"),
                new HeaderData("q_n_2", "q_v_2"));

        List<HeaderData> responseHeaders = Arrays.asList(new HeaderData("s_n_1", "s_v_1"),
                new HeaderData("s_n_2", "s_v_2"));

        LogData logData = new LogData("request_method", "request_url", "protocol", "request_content_type", 123L,
                requestHeaders, BodyState.PLAIN_BODY, false, 200, "response_message", 456L, 789L, responseHeaders,
                BodyState.ENCODED_BODY, 777L, "response_url");

        byte[] bytes = new byte[]{10, 14, 114, 101, 113, 117, 101, 115, 116, 95, 109, 101, 116, 104, 111, 100, 18, 11,
                114, 101, 113, 117, 101, 115, 116, 95, 117, 114, 108, 26, 8, 112, 114, 111, 116, 111, 99, 111, 108, 34,
                20, 114, 101, 113, 117, 101, 115, 116, 95, 99, 111, 110, 116, 101, 110, 116, 95, 116, 121, 112, 101, 40,
                123, 50, 14, 10, 5, 113, 95, 110, 95, 49, 18, 5, 113, 95, 118, 95, 49, 50, 14, 10, 5, 113, 95, 110, 95,
                50, 18, 5, 113, 95, 118, 95, 50, 56, 1, 64, 0, 72, -56, 1, 82, 16, 114, 101, 115, 112, 111, 110, 115,
                101, 95, 109, 101, 115, 115, 97, 103, 101, 88, -56, 3, 96, -107, 6, 106, 14, 10, 5, 115, 95, 110, 95,
                49, 18, 5, 115, 95, 118, 95, 49, 106, 14, 10, 5, 115, 95, 110, 95, 50, 18, 5, 115, 95, 118, 95, 50,
                112, 3, 120, -119, 6, -126, 1, 12, 114, 101, 115, 112, 111, 110, 115, 101, 95, 117, 114, 108};

        String compressedLogData = "H4sIAAAAAAAAAJ2Rz27TQBDGSYwKXXGo9gARpMISQkLIG69T8qc5YTlWW9HEUbKgHlmvN8lunV3L3"
                + "jrNlbfiDTjlmdgUiQPqicPMZX7zzcw3wLmICeytjSlGvl-UoqaGo3SQsk_I1NVabytaiI4NWu7QRrPbDtMb_6Hw-vmhzQ8"
                + "6wYe9M2x-fnL5qzFvJl9ufj77vnfkCTha8LLmJTyK9DbVO9kCINJKcWaEVhDccl4gmouay7egdYPCP0PmdoNcbIRB14cMn"
                + "aCL5TvQfgSY8w0VSqiVhYJz6YIXVt9wZRDZFRye0KLIBaOHcb6stLLEm5AxXlXoAJY6R2Ge6y1KSrESCjY-yiloP0pMuFn"
                + "rrIKdZEaukunCs755l3E49mbJgnizr8Qbx9cxiT0yD6PYi5LpNI6IbIOX_-hN6D0KVxw2AyzPwKu_Z5GSqoo-eIOuMtjqD"
                + "Za9ZZYGwTDr0nOcYrzsZn2Wyffg6dhaAE_J-s5zcd9NmHG7OOi7eDA6w6Pe0L2YEOu_801QeGz_49Z8dVcWjfu986Pxf8_"
                + "-DXy6JbEpAgAA";

        when(CompressionUtils.gzipBase64UrlSafe(eq(bytes))).thenReturn(compressedLogData);

        String logUrl = logManager.getLogUrl("response_body", logData);

        String gzippedNoNewLine = gzipped.replaceAll("\n", "");
        String expected = String.format("%s%s%s?d=%s", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH, gzippedNoNewLine,
                compressedLogData);
        assertEquals(expected, logUrl);
    }

    @Test
    public void testLogDebugCalled() throws Exception {
        String compressedString = "compressedString";
        when(CompressionUtils.gzipBase64UrlSafe(anyString())).thenReturn(compressedString);
        when(CompressionUtils.gzipBase64UrlSafe((byte[]) anyObject())).thenReturn(compressedString);

        String baseUrl = "http://example.com";
        LogManager logManager = spy(new LogManager(baseUrl, null, false));

        logManager.log(new LogDataBuilder().responseBody("response_body"));

        String expected = String.format("%s%s%s?d=%s", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH, compressedString,
                compressedString);

        verify(logManager).logDebug(eq(expected));
    }

    @Test
    public void testLogInterceptorCalled() throws Exception {
        String compressedString = "compressedString";
        when(CompressionUtils.gzipBase64UrlSafe(anyString())).thenReturn(compressedString);
        when(CompressionUtils.gzipBase64UrlSafe((byte[]) anyObject())).thenReturn(compressedString);

        LogInterceptor logInterceptor = mock(LogInterceptor.class);

        String baseUrl = "http://example.com";
        LogManager logManager = new LogManager(baseUrl, logInterceptor, false);

        logManager.log(new LogDataBuilder().responseBody("response_body"));

        String expected = String.format("%s%s%s?d=%s", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH, compressedString,
                compressedString);

        verify(logInterceptor).onLog(eq(expected));
    }

    @Test
    public void testLogInterceptorHandled() throws Exception {
        String compressedString = "compressedString";
        when(CompressionUtils.gzipBase64UrlSafe(anyString())).thenReturn(compressedString);

        LogInterceptor logInterceptor = mock(LogInterceptor.class);
        when(logInterceptor.onLog(anyString())).thenReturn(true);

        LogManager logManager = spy(new LogManager(null, logInterceptor, false));

        logManager.log(new LogDataBuilder().responseBody("response_body"));

        verify(logManager, never()).logDebug(anyString());
    }

    @Test
    public void testLogInterceptorNotHandled() throws Exception {
        String compressedString = "compressedString";
        when(CompressionUtils.gzipBase64UrlSafe(anyString())).thenReturn(compressedString);
        when(CompressionUtils.gzipBase64UrlSafe((byte[]) anyObject())).thenReturn(compressedString);

        LogInterceptor logInterceptor = mock(LogInterceptor.class);
        when(logInterceptor.onLog(anyString())).thenReturn(false);

        String baseUrl = "http://example.com";
        LogManager logManager = spy(new LogManager(baseUrl, logInterceptor, false));

        logManager.log(new LogDataBuilder().responseBody("response_body"));

        String expected = String.format("%s%s%s?d=%s", baseUrl, Constants.LOG_URL_ECHO_RESPONSE_PATH, compressedString,
                compressedString);

        verify(logManager).logDebug(eq(expected));
    }

    @Test
    public void testUseAndroidLog() throws Exception {
        LogManager logManager = new LogManager(null, null, true);
        assertEquals(true, logManager.useAndroidLog);
    }

    @Test
    public void testUseAndroidLogNoTimber() throws Exception {
        mockStatic(TimberUtils.class);

        when(TimberUtils.hasTimber()).thenReturn(false);

        LogManager logManager = new LogManager(null, null, false);
        assertEquals(true, logManager.useAndroidLog);
    }

    @Test
    public void testUseAndroidLogTimber() throws Exception {
        mockStatic(TimberUtils.class);

        when(TimberUtils.hasTimber()).thenReturn(true);

        LogManager logManager = new LogManager(null, null, false);
        assertEquals(false, logManager.useAndroidLog);
    }
}

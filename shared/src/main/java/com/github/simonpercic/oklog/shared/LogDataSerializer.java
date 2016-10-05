package com.github.simonpercic.oklog.shared;

import com.github.simonpercic.oklog.shared.data.LogData;

import java.io.IOException;

/**
 * Log data serializer.
 *
 * @author Simon Percic <a href="https://github.com/simonpercic">https://github.com/simonpercic</a>
 */
public final class LogDataSerializer {

    private LogDataSerializer() {
        // no instance
    }

    public static String serialize(LogData logData) {
        if (logData == null) {
            return null;
        }

        byte[] bytes = LogData.ADAPTER.encode(logData);
        return new String(bytes, SharedConstants.CHARSET_UTF8);
    }

    public static LogData deserialize(String string) throws IOException {
        if (string == null || string.length() == 0) {
            return null;
        }

        byte[] bytes = string.getBytes(SharedConstants.CHARSET_UTF8);
        return LogData.ADAPTER.decode(bytes);
    }
}

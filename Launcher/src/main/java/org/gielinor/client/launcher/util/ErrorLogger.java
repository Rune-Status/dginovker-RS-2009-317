package org.gielinor.client.launcher.util;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * Created by Corey on 15/08/2017.
 */
public class ErrorLogger extends PrintStream {

    private final PrintStream second;

    private static ErrorLogger logger;

    private static final String LOG_FILE_NAME = "error_log.txt";
    private static final File LOG_FILE = new File(Misc.WRITE_LOCATION.getPath() + "/" + LOG_FILE_NAME);

    public static void initialiseErrorLogger() {
        if (logger == null) {
            try {
                if (!Misc.WRITE_LOCATION.exists()) {
                    Misc.WRITE_LOCATION.mkdirs();
                    LOG_FILE.createNewFile();
                }
                logger = new ErrorLogger(new FileOutputStream(LOG_FILE, true), System.out);
                System.setErr(logger);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ErrorLogger(OutputStream out, PrintStream second) {
        super(out);
        this.second = second;
    }

    @Override
    public void println(String x) {
        String printPrefix = getFriendlyDate() + " error: ";

        if (x.startsWith(System.lineSeparator())) {
            super.println(System.lineSeparator());
            super.println(printPrefix + x.substring(2));
            return;
        }

        super.println(printPrefix + x);
    }

    @Override
    public void flush() {
        super.flush();
        second.flush();
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        second.write(buf, off, len);
    }

    @Override
    public void write(int b) {
        super.write(b);
        second.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
        second.write(b);
    }

    private String getFriendlyDate() {
        String friendlyDate = new SimpleDateFormat("HH:mm:ss z [dd/MM/yyyy]").format(System.currentTimeMillis());
        return friendlyDate;
    }

}
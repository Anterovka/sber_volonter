package com.sbt.mg;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.google.common.hash.Hashing;
import com.sbt.mg.exception.GeneralSdkException;
import com.sbt.mg.exception.checkmodel.DirectoryCreationException;
import com.sbt.mg.exception.checkmodel.FileCreationException;
import com.sbt.mg.exception.checkmodel.UnknownResourceException;
import com.sbt.mg.exception.checkmodel.UnrecognizedPropertyParseJacksonException;
import com.sbt.mg.exception.common.CompleteExecuteException;
import com.sbt.mg.exception.common.ExecuteException;
import com.sbt.mg.exceptional.ExceptionableFunction;
import com.sbt.mg.exceptional.ExceptionableMethod;
import com.sbt.mg.exceptional.ExceptionableSupplier;
import com.sbt.parameters.enums.OperatingSystem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Assistant
 */
public final class Helper {

    private static final OperatingSystem OPERATION_SYSTEM = System.getProperty("os.name")
            .toLowerCase(Locale.ENGLISH).contains("win") ? OperatingSystem.WINDOWS : OperatingSystem.LINUX;

    private static final Logger LOGGER = Logger.getLogger(Helper.class.getName());

    private static Integer wrapLevel = 0;

    public static String replaceIllegalSymbols(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("\\\"", "\"")
                .replace("\\", "\\\\")
                .replace('\"', '\'');
    }

    public static String replaceNullToEmpty(String text) {
        if (text == null) {
            return "";
        }
        return text;
    }

    public static void addToGit(File file) {
        wrap(() -> {
            if (OPERATION_SYSTEM == OperatingSystem.WINDOWS) {
                Runtime.getRuntime().exec("cmd.exe /C git add " + file.getAbsolutePath()).waitFor();
            } else {
                Runtime.getRuntime().exec("/usr/bin/env -- git add " + file.getAbsolutePath()).waitFor();
            }
        });
    }

    /**
     * Wrap in try-catch
     *
     * @param method Method throwing an exception
     */
    public static void wrap(@Nonnull ExceptionableMethod method) {
        wrap(() -> {
            method.execute();

            return null;
        });
    }


    /**
     * Wrap in try-catch
     *
     * @param supplier Data recipient throwing an exception
     * @param <R>      Type of result
     * @return Result
     */
    public static <R> R wrap(@Nonnull ExceptionableSupplier<R> supplier) {
        Throwable ex;
        try {
            ++wrapLevel;
            return supplier.get();
        } catch (CompleteExecuteException e) {
            ex = e;
        } catch (ExecuteException e) {
            if (wrapLevel > 1) {
                throw new ExecuteException(e.getCause());
            } else {
                ex = e.getCause();
            }
        } catch (UnrecognizedPropertyException e) {
            throw new UnrecognizedPropertyParseJacksonException(e);
        } catch (Throwable e) {
            if (e.getCause() != null) {
                if (wrapLevel > 1) {
                    throw new ExecuteException(e.getCause());
                } else {
                    ex = e.getCause();
                }
            } else {
                if (wrapLevel > 1) {
                    throw new ExecuteException(e);
                } else {
                    ex = e;
                }
            }
        } finally {
            --wrapLevel;
        }

        // wrapLevel == 0
        if (ex instanceof GeneralSdkException) {
            LOGGER.warning(ex.toString());
            throw (GeneralSdkException) ex;
        } else if (ex instanceof RuntimeException) {
            LOGGER.warning(ExceptionUtils.getStackTrace(ex));
            throw (RuntimeException) ex;
        } else if (ex instanceof JsonParseException || ex instanceof InvalidFormatException) {
            LOGGER.warning(ExceptionUtils.getStackTrace(ex));
            throw new ExecuteException("Parsing error. " + ex.getMessage(), ex);
        } else {
            LOGGER.warning(ExceptionUtils.getStackTrace(ex));

            throw new ExecuteException("Unhandled exception. See the full error text on the console above", ex);
        }
    }

    /**
     * Обернуть try-with-resources
     *
     * @param autoClosableInitializer Инициализатор автозакрывающегося объекта
     * @param function                 The function that throws an exception
     * @param <C>                      Type of auto-closing object
     * @param <R>                      Type of result
     * @return Result
     */
    public static <R, C extends AutoCloseable> R wrap(@Nonnull ExceptionableSupplier<C> autoClosableInitializer, @Nonnull ExceptionableFunction<C, R> function) {
        return wrap(() -> {
            try (C autoClosable = autoClosableInitializer.get()) {
                return function.apply(autoClosable);
            }
        });
    }

    /**
     * Get text
     *
     * @param inputStream Read stream
     */
    public static String getText(@Nonnull InputStream inputStream) {
        return wrap(() -> new Scanner(inputStream, "UTF8").useDelimiter("\\Z"), Scanner::next);
    }

    /**
     * Get text
     *
     * @param inputStream Read stream
     */
    public static List<String> getAllLines(@Nonnull InputStream inputStream) {
        return wrap(() ->
                new BufferedReader(new InputStreamReader(inputStream,
                        StandardCharsets.UTF_8)).lines().collect(Collectors.toList()));
    }

    /**
     * Creates a directory at the specified File
     */
    public static File createDirectory(File directory) {
        if (!directory.exists() && !directory.mkdir()) {
            throw new DirectoryCreationException(directory.getPath());
        }

        return directory;
    }

    /**
     * Write text to file
     *
     * @param file File
     * @param text Text
     */
    public static void writeText(@Nonnull File file, @Nonnull String text) {
        wrap(() -> new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8),
                outputStreamWriter -> {
                    outputStreamWriter.write(text);

                    return null;
                });
    }

    /**
     * Process text in file
     *
     * @param file     File
     * @param function text processing function
     */
    public static void  fileTextHandler(File file, Function<String, String> function){
        String str = wrap(() -> FileUtils.readFileToString(file, StandardCharsets.UTF_8));
        String result = function.apply(str);
        writeText(file, result);
    }

    protected Helper() {
    }

    /**
     * Get template
     *
     * @param path Path
     */
    public static String getTemplate(@Nonnull String path) {
        return wrap(() -> {
            InputStream stream = Helper.class.getResourceAsStream(path);

            if (stream == null) {
                throw new UnknownResourceException(path);
            }

            return Helper.getText(stream);
        });
    }

    public static File forceCreateDirectory(File rootDir, String goalDir, String splitString) {
        for (String curDirName : goalDir.split(splitString)) {
            rootDir = createDirectory(getFile(rootDir, curDirName));
        }
        return rootDir;
    }

    public static File forceCreateDirectory(File rootDir, String goalDir) {
        wrap(() -> FileUtils.forceMkdir(rootDir));

        String normalizeGoalDir = goalDir
                .replace("\\\\", "\\")
                .replace('\\', '/');
        return forceCreateDirectory(rootDir, normalizeGoalDir, "/");
    }

    /**
     * Creates a directory inside rootDir at the specified goalDir
     */
    public static File createDirectory(File rootDir, String goalDir) {
        return createDirectory(getFile(rootDir, goalDir));
    }

    public static File createFile(File rootDir, String fileName) {
        File goalDirFile = getFile(rootDir, fileName);

        if (!goalDirFile.exists() && !wrap(goalDirFile::createNewFile)) {
            throw new FileCreationException(goalDirFile.getPath());
        }

        return goalDirFile;
    }

    public static File getFile(File rootDir, String fileName) {
        return new File(rootDir, FilenameUtils.getName(fileName));
    }

    public static String readToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, "UTF8");
        scanner.useDelimiter("\\Z");
        return scanner.next();
    }

    public static String beautifierExpression(final String str) {
        if (str == null) {
            return null;
        }

        StringBuilder resultStr = new StringBuilder();
        String tempStr = str.trim();

        boolean strFlag = false;
        int lengthStr = tempStr.length();

        for (int j = 0; j < lengthStr; ++j) {

            char charStr = tempStr.charAt(j);
            if (strFlag) {
                resultStr.append(charStr);
                if (charStr == '\'') {
                    if (j + 1 < lengthStr && tempStr.charAt(j + 1) == '\'') {
                        j++;
                        resultStr.append(tempStr.charAt(j));
                    } else {
                        strFlag = false;
                    }
                }
            } else {
                if (isSplitChar(charStr)) {
                    resultStr.append(' ');
                    while (j < lengthStr && isSplitChar(tempStr.charAt(j + 1))) {
                        j++;
                    }
                    continue;
                }
                if (charStr == '\'') {
                    strFlag = true;
                }
                resultStr.append(Character.toUpperCase(charStr));
            }
        }
        return resultStr.toString().trim();
    }

    private static boolean isSplitChar(char c) {
        return c == '\t' || c == '\n' || c == '\r' || c == ' ';
    }

    /** Returns the number of digits in a number */
    public static int countDigits(int number) {
        int length = 1;
        if (number >= 100000000) {
            length += 8;
            number /= 100000000;
        }
        if (number >= 10000) {
            length += 4;
            number /= 10000;
        }
        if (number >= 100) {
            length += 2;
            number /= 100;
        }
        if (number >= 10) {
            length += 1;
        }
        return length;
    }

    public static boolean isXmlModelFile(File file) {
        try(FileInputStream fis = new FileInputStream(file)) {
            String data = readToString(fis);
            return data.contains("</model>");
        } catch (IOException ex) {
            // The situation where there is no file is hypothetically impossible. Because we are iterating through them. But still log it.
            LOGGER.severe(ex.getMessage());
            return false;
        }
    }

    public static String encodeBase64(byte[] bytes) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes);
    }

    public static byte[] decodeBase64(String str) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(str);
    }

    public static String compress(String str) throws IOException {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        GZIPOutputStream gzipOS = new GZIPOutputStream(byteArrayOS);
        gzipOS.write(str.getBytes(StandardCharsets.UTF_8));
        gzipOS.close();
        return encodeBase64(byteArrayOS.toByteArray());
    }

    public static String decompress(String str) throws IOException{
        GZIPInputStream gzipIS = new GZIPInputStream(new ByteArrayInputStream(decodeBase64(str)));
        return IOUtils.toString(gzipIS, StandardCharsets.UTF_8);
    }

    public static String sha256(String str) {
        return Hashing.sha256().hashString(str, StandardCharsets.UTF_8).toString();
    }

    public static boolean isSnapshotVersion(String version) {
        return version.endsWith("-SNAPSHOT");
    }
}

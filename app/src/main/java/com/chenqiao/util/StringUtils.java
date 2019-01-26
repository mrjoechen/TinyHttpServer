package com.chenqiao.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {
    private static final ThreadLocal<SimpleDateFormat> PLAIN_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        }
    };
    
    private static final ThreadLocal<DecimalFormat> TRIM_DECIMAL_ZERO_NUMBER = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("###.####");
        }
    };

    private StringUtils() {
    }

    /**
     * 转换为字符串,如果小数部分为0则去掉
     *
     * @param d double, decimal number
     * @return
     */
    public static String trimDouble2(double d) {
        return TRIM_DECIMAL_ZERO_NUMBER.get().format(d);
    }

    /**
     * 转换为字符串,如果小数部分为0则去掉
     *
     * @param f float ,decimal number
     * @return
     */
    public static String trimFloat2(float f) {
        return TRIM_DECIMAL_ZERO_NUMBER.get().format(f);
    }

    /**
     * 转换为字符串,如果小数部分为0则去掉
     *
     * @param f
     * @return
     */
    public static String trimFloat(float f) {
        int i = (int) f;
        if (i == f) {
            return i + "";
        } else {
            return f + "";
        }
    }

    public static boolean toBoolean(String str, boolean fallback) {
        if (isEmpty(str)) {
            return fallback;
        }
        try {
            return Boolean.parseBoolean(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static boolean toBoolean(String str) {
        return toBoolean(str, false);
    }

    public static double toDouble(String str, double fallback) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static double toDouble(String str) {
        return toDouble(str, 0);
    }

    /**
     * 将表示整数的字符串转化为int型，如果出现格式错误，返回 {@code fallback}
     *
     * @param str
     * @param radix
     * @param fallback
     * @return
     */
    public static int toInt(String str, int radix, int fallback) {
        if (isEmpty(str)) {
            return fallback;
        }
        try {
            return Integer.parseInt(str, radix);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static boolean judgeContainsChar(String cardNum) {
        String regex = ".*[a-zA-Z].*";
        Matcher m = Pattern.compile(regex).matcher(cardNum);
        return m.matches();
    }

    public static String getPackageTimeDesc(int sec, Context context) {
        if (sec <= 0) {
            return "";
        }
        int hours = sec / 3600;
        int mins = (sec % 3600) / 60;
        int secs = sec % 60;
        boolean append = false;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("时");
            append = true;
        }
        if (mins > 0 || append) {
            sb.append(mins).append("分");
        }
        if (secs > 0) {
            sb.append(sec).append("秒");
        }

        return sb.toString();
    }


    /**
     * 将表示十进制整数字符串转化为int型，如果出现格式错误，返回 {@code fallback}
     *
     * @param str
     * @param fallback
     * @return
     */
    public static int toInt(String str, int fallback) {
        return toInt(str, 10, fallback);
    }

    /**
     * 将表示十进制整数字符串转化为int型，如果出现格式错误，返回 0
     *
     * @param str
     * @return
     */
    public static int toInt(String str) {
        return toInt(str, 0);
    }

    public static long toLong(String str, int radix, long fallback) {
        if (isEmpty(str)) {
            return fallback;
        }
        try {
            return Long.parseLong(str, radix);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static long toLong(String str, long fallback) {
        return toLong(str, 10, fallback);
    }

    public static long toLong(String str) {
        return toLong(str, 0);
    }


    private static final ThreadLocal<SimpleDateFormat> COMPACT_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
        }
    };

    /**
     * 返回以紧凑格式（i.e. yyyyMMddHHmmss）表示的当前系统时间, e.g. 20161221130420
     *
     * @return
     */
    public static String getCompactTimeString() {
        return COMPACT_DATE_FORMAT.get().format(new Date());
    }

    /**
     * 将一个以紧凑格式（i.e. yyyyMMddHHmmss）表示时间的字符串转化为Unix时间戳
     *
     * @param str
     * @return
     */
    public static long parseCompactTimeString(String str) {
        if (str == null) {
            return 0;
        }
        try {
            return COMPACT_DATE_FORMAT.get().parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static final ThreadLocal<SimpleDateFormat> CLOCK_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format;
        }
    };
    private static final ThreadLocal<SimpleDateFormat> CLOCK_DATE_FORMAT_SHORT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("mm:ss", Locale.getDefault());
        }
    } ;
    private static final ThreadLocal<SimpleDateFormat> FULL_MILLI_TIME_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());
        }
    };
    
    private static final ThreadLocal<SimpleDateFormat> COMPACT_CLOCK_TIME_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HHmmss", Locale.getDefault());
        }
    };
    
    private static ThreadLocal<Date> sTmpDate = new ThreadLocal<Date>() {
        @Override
        protected Date initialValue() {
            return new Date();
        }
    };

    /**
     * 返回以计时格式（i.e. HH:mm:ss）表示的给定时间
     *
     * @return
     */
    public static String getClockTimeString(int seconds) {
        Date date = sTmpDate.get();
        date.setTime(seconds * 1000);
        if (seconds < 60 * 60) {
            return CLOCK_DATE_FORMAT_SHORT.get().format(date);
        } else {
            return CLOCK_DATE_FORMAT.get().format(date);
        }
    }


    private static final ThreadLocal<SimpleDateFormat> COMPLETE_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> COMPLETE_DATE_SSS_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> COMPLETE_DATE_ONLY_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        }
    };

    /**
     * 返回当前系统时间的完整表示（yyyy-MM-dd HH:mm:ss）
     *
     * @return
     */
    public static String getCompleteTimeString() {
        return getCompleteTimeString(System.currentTimeMillis());
    }


    public static String getCompleteTimeString(long millis) {
        Date date = sTmpDate.get();
        date.setTime(millis);
        return COMPLETE_DATE_FORMAT.get().format(date);
    }

    public static String getCompleteDateTimeString() {
        return getCompleteDateTimeString(System.currentTimeMillis());
    }

    public static String getCompleteDateTimeString(long millis) {
        Date date = sTmpDate.get();
        date.setTime(millis);
        return COMPLETE_DATE_SSS_FORMAT.get().format(date);
    }

    public static String getDateString(long millis) {
        Date date = sTmpDate.get();
        date.setTime(millis);
        return COMPLETE_DATE_ONLY_FORMAT.get().format(date);
    }

    /**
     * 返回"HHmmss"格式的当前时间
     * @return
     */
    public static String getCompactClockTimeString() {
        Date date = sTmpDate.get();
        date.setTime(System.currentTimeMillis());
        return COMPACT_CLOCK_TIME_FORMAT.get().format(date);
    }

    /**
     * 将时间的完整表示（yyyy-MM-dd HH:mm:ss）转换为Unix时间戳. 若参数为null或格式错误，返回0
     *
     * @param str
     * @return
     */
    public static long parseCompleteTimeString(String str) {
        if (str == null) {
            return 0;
        }
        try {
            return COMPLETE_DATE_FORMAT.get().parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }



    /**
     * 将"HH:mm:ss.SSS"或"HH:mm:ss"格式的字符串转换为毫秒值
     * @param str
     * @return
     */
    public static long parseClockTimeString(String str) {
        if (str == null) {
            return 0;
        }

        try {
            if (str.contains(".")) {
                return FULL_MILLI_TIME_FORMAT.get().parse(str).getTime();
            } else {
                return CLOCK_DATE_FORMAT.get().parse(str).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * check if the given string is null or ""
     *
     * @param str
     * @return true if str is null or ""
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str);
    }

    /**
     * stronger than {@link #isEmpty(String)}, in that it will return true if str.trim is ""
     *
     * @param str
     * @return true if str is null, or str.trim is ""
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * return a String denote the current time in a form like 20161221130420
     *
     * @return
     */
    public static String getPlainTimeString() {
        return PLAIN_DATE_FORMAT.get().format(new Date());
    }

    /**
     * 将当前 日期转换为 格式 yyyyMMdd-HH:mm:ss
     *
     * @return
     */
    public static String parseDateAccurateToSecond() {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd-HH:mm:ss", Locale.CHINA);

        Date date = sTmpDate.get();
        date.setTime(System.currentTimeMillis());
        String yyyyMMddString = yyyyMMdd.format(date);

        return yyyyMMddString;
    }


    /**
     * 获取当前的 月份 0-11，所以要加1
     *
     * @return
     */
    public static int parseMonthString() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 将当前 天  的日期转换为 数字格式 yyyyMMdd
     *
     * @return
     */
    public static int parseDateString() {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

        Date date = sTmpDate.get();
        date.setTime(System.currentTimeMillis());
        String yyyyMMddString = yyyyMMdd.format(date);

        return toInt(yyyyMMddString);
    }

    /**
     * 获取当前的 小时
     *
     * @return
     */
    public static int parseHourString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前的 总分钟 ／当天
     *
     * @return
     */
    public static int parseMinuteString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        return hour * 60 + minute;
    }

    /**
     * 根据 '时间'字符串  获取当前的 总分钟 ／当天
     *
     * @return
     */
    public static int parseMinuteString(String HHmm) {
        SimpleDateFormat yyyyMMdd = new SimpleDateFormat("HH:mm");
        if (!TextUtils.isEmpty(HHmm)) {
            try {
                Date date = yyyyMMdd.parse(HHmm);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                int minute = calendar.get(Calendar.MINUTE);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                return hour * 60 + minute;
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return -1;
    }


    /**
     * 获取今天是周几
     *
     * @return
     */
    public static int parsWeekDayString() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            week = 8;
        }
        return week - 1;
    }

    /**
     * ISO8601 UTC 时间
     *
     * @param date
     * @return
     */
    public static String getISO8601Timestamp(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(date);
        return nowAsISO;
    }

    /**
     * 获取字符串随机数
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        StringBuffer buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        int range = buffer.length();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(random.nextInt(range)));
        }
        return sb.toString();
    }

    /**
     * 获取异常的栈跟踪信息
     *
     * @param e 异常
     * @return
     */
    public static String getStackTraceFromException(Exception e) {
        String exceptionStack = null;
        if (e != null) {
            try (StringWriter writer = new StringWriter()
                 ; PrintWriter stringWriter = new PrintWriter(writer)) {

                e.printStackTrace(stringWriter);
                exceptionStack = writer.toString();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return exceptionStack;
    }

    public static String toString(Exception e) {
        return e == null ? "" : e.toString();
    }

    public static final char[] CHINESE_DIGITS_LOWER_CASE = {'〇', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    public static int sizeOf(String str) {
        return str == null ? 0 : (36 + str.length() * 2);
    }

    public static int string2int(String str) {
        return string2int(str, 0);
    }

    public static int string2int(String str, int def) {
        try {
            return Integer.valueOf(str);
        } catch (Exception e) {
        }
        return def;
    }
    
    private static final ThreadLocal<SimpleDateFormat> COMPACT_DATE_ONLY_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        }
    };

    /**
     * 返回"yyyyMMdd"格式的日期
     * @return
     */
    public static String getCompactDateString() {
        Date date = sTmpDate.get();
        date.setTime(System.currentTimeMillis());
        return COMPACT_DATE_ONLY_FORMAT.get().format(date);
    }

    public String getMacAddress() {

        String MAC = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();

                Log.d("getMacAddress", "NetworkInterface Name:" + intf.getName());
                if (intf.getName().equalsIgnoreCase("eth0")) {
                    byte[] macs = intf.getHardwareAddress();
                    StringBuilder macBuilder = new StringBuilder();
                    for (int i = 0; i < (macs != null ? macs.length : 0); i++) {
                        macBuilder.append(String.format("%02X", macs[i]));
                    }
                    //  return "00E07E00" + "A657";
                    MAC = macBuilder.toString();
                    Log.d("getMacAddress", "MAC:" + MAC);
                    return MAC;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return MAC;
    }

    public static String parseInputStreamToString(InputStream inputStream) throws IOException {

//        StringBuilder sb = new StringBuilder();
//        String line;
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//        while ((line = br.readLine()) != null) {
//            sb.append(line);
//        }
//        String str = sb.toString();
//        return str;
        byte[] bytes = new byte[0];
        bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String str = new String(bytes);
        return str;

    }

}

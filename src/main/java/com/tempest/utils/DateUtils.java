package com.tempest.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import lombok.extern.log4j.Log4j2;

/**
 * DateUtils
 */
@Log4j2
public class DateUtils {
    private static final String FORMAT_JAPANESE = "yyyy/MM/dd HH:mm:ss.SSS";
    private static final String FORMAT_JAPANESE_NO_MILLI = "yyyy/MM/dd HH:mm:ss";
    private static final String UNIQUE_ID = "yyyyMMddHHmmss";
    public static final String FORMAT_DATE = "yyyyMMdd";

    private DateUtils() {
    }

    /**
     * エポックからのミリ秒を指定してLocalDateTimeオブジェクトを作成する。
     *
     * @param epochMilli エポックからのミリ秒
     * @return 日時
     */
    public static LocalDateTime getLocalDateTime(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.of("Asia/Tokyo"));
    }

    /**
     * Date型をLocalDateTime型に変換する。
     *
     * @param date Dateオブジェクト
     * @return LocalDateTimeオブジェクト
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        log.trace(() -> "start getLocalDateTime");
        try {
            Instant instant = date.toInstant();
            ZoneId zone = ZoneId.of("Asia/Tokyo");
            log.trace(() -> "end getLocalDateTime");
            return LocalDateTime.ofInstant(instant, zone);
        } catch (Exception e) {
            log.catching(e);
            throw e;
        }
    }

    /**
     * LocalDateTimeで日本時間の日付を返します。
     *
     * @return 日付
     */
    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Tokyo"));
    }

    public static Date getToday() {
        return getDate(getLocalDateTime());
    }

    /**
     * 文字列型から日付型に変換する。
     *
     * @param str     文字列
     * @param pattarn フォーマット文字列
     * @return 日付
     */
    public static Date getDate(String str, String pattarn) {
        if (pattarn.toLowerCase().indexOf("h") > 0) {
            return getDate(getLocalDateTime(str, pattarn));
        }
        LocalDate ld = getLocalDate(str, pattarn);
        return getDate(ld.atTime(0, 0, 0));
    }

    /**
     * デフォルトのフォーマット文字列を使用して文字列型から日付型に変換する。
     *
     * @param date 文字列
     * @return 日付
     */
    public static Date getFullDate(String date) {
        if (date.indexOf(".") > 0) {
        return getDate(getLocalDateTime(date, FORMAT_JAPANESE));
        }
        return getDate(getLocalDateTime(date, FORMAT_JAPANESE_NO_MILLI));
    }

    /**
     * LocalDateTime型からDate型に変換する。
     *
     * @param ldt LocalDateTimeオブジェクト
     * @return Dateオブジェクト
     */
    public static Date getDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.of("Asia/Tokyo")).toInstant());
    }

    /**
     * エポックからのミリ秒を取得する。
     *
     * @param ldt 日付
     * @return エポックからのミリ秒
     */
    public static long getEpochMilli(LocalDateTime ldt) {
        return ldt.atZone(ZoneId.of("Asia/Tokyo")).toInstant().toEpochMilli();
    }

    /**
     * 文字列型から日付型に変換する。
     *
     * @param date    文字列
     * @param pattarn フォーマット文字列
     * @return 日付
     */
    public static LocalDateTime getLocalDateTime(String date, String pattarn) {
        DateTimeFormatter dtf = null;
        if (pattarn.indexOf("SSS") > 0) {
            String ptn = pattarn.substring(0, pattarn.indexOf("SSS"));
            dtf = new DateTimeFormatterBuilder().appendPattern(ptn).appendValue(ChronoField.MILLI_OF_SECOND, 3)
                    .toFormatter();
        } else {
            dtf = DateTimeFormatter.ofPattern(pattarn);
        }
        return LocalDateTime.parse(date, dtf);
    }

    /**
     * 文字列型から日付型に変換する。
     *
     * @param date    文字列
     * @param pattarn フォーマット文字列
     * @return 日付
     */
    public static LocalDate getLocalDate(String date, String pattarn) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattarn));
    }

    /**
     * デフォルトのフォーマット文字列を使用して文字列型から日付型に変換する。
     *
     * @param date 文字列
     * @return 日付
     */
    public static LocalDateTime getFullDateTime(String date) {
        return getLocalDateTime(date, FORMAT_JAPANESE);
    }

    /**
     * ユニークIDから時刻を作成します。 以下のフォーマットの文字列からLocalDateTime型を作成します。
     * "_"がない場合は、全体を（YYYYMMDDHHMMS)とみなしてLocalDateTime型を作成します。 YYYYMMDDHHMMSS_xxxx
     *
     * @param date
     * @return
     */
    public static LocalDateTime getUniqueIdTime(String date) {
        String tmp = date;
        if (date.indexOf("_") > 0) {
            String[] elem = date.split("_");
            tmp = elem[0];
        }
        return getLocalDateTime(tmp, UNIQUE_ID);
    }

    /**
     * 日付型から文字列型に変換する。
     *
     * 文字列のフォーマットは、"yyyyMMddHHmmss"
     *
     * @param date 日付
     * @return 文字列
     */
    public static String dateToString(Date date) {
        return dateToString(date, UNIQUE_ID);
    }

    /**
     * 日付型から文字列型に変換する。
     *
     * 文字列のフォーマットは、"yyyy/MM/dd HH:mm:ss.SSS"
     *
     * @param date 日付
     * @return 文字列
     */
    public static String dateToStringJapanese(Date date) {
        if (date != null) {
            return dateToString(date, FORMAT_JAPANESE);
        }
        return null;
    }

    /**
     * 日付型から文字列型に変換する。
     *
     *
     * @param date    日付
     * @param pattarn フォーマット文字列
     * @return 文字列
     */
    public static String dateToString(Date date, String pattarn) {
        LocalDateTime time = getLocalDateTime(date);
        return DateTimeFormatter.ofPattern(pattarn).format(time);
    }

    /**
     * 日付型から文字列型に変換する。
     *
     * 文字列のフォーマットは、"yyyyMMddHHmmss"
     *
     * @param date 日付
     * @return 文字列
     */
    public static String dateToString(LocalDateTime date) {
        return dateToString(date, UNIQUE_ID);
    }

    /**
     * 日付型から文字列型に変換する。
     *
     * 文字列のフォーマットは、"yyyy/MM/dd HH:mm:ss.SSS"
     *
     * @param date 日付
     * @return 文字列
     */
    public static String dateToStringJapanese(LocalDateTime date) {
        return dateToString(date, FORMAT_JAPANESE);
    }

    /**
     * 日付型から文字列型に変換する。
     *
     * @param date    日付
     * @param pattarn フォーマット文字列
     * @return 文字列
     */
    public static String dateToString(LocalDateTime date, String pattarn) {
        return DateTimeFormatter.ofPattern(pattarn).format(date);
    }

    /**
     * 日付の秒の1桁目を0にします。
     */
    public static Date roundSec(Date date) {
        LocalDateTime time = getLocalDateTime(date).truncatedTo(ChronoUnit.SECONDS);
        if (log.isDebugEnabled()) {
            log.debug("丸める時間：" + dateToString(time, FORMAT_JAPANESE));
        }
        int sec = time.getSecond();
        String tmp = String.valueOf(sec);
        if (tmp.length() != 1) {
            tmp = tmp.substring(1);
        }
        return getDate(time.minusSeconds(Integer.parseInt(tmp)));
    }

    /**
     * 日付の秒数を現在日付から引く
     *
     * @param date 対象のDate
     * @param s    引く時間
     */
    public static Date roundSec(Date date, int s) {
        LocalDateTime time = getLocalDateTime(date);
        return getDate(time.minusSeconds(s));
    }

    /**
     * ミリ秒で時間計算します。 ミリ秒に与える符号によって、和または差を計算します。
     *
     * @param date 対象のDate
     * @param l    ミリ秒
     * @return 計算結果
     */
    public static Date calcMilliSecound(Date date, long l) {
        return calc(date, l, ChronoUnit.MILLIS);
    }

    /**
     * 秒で時間計算します。 秒に与える符号によって、和または差を計算します。
     *
     * @param date 対象のDate
     * @param l    秒
     * @return 計算結果
     */
    public static Date calcSecound(Date date, long l) {
        return calc(date, l, ChronoUnit.SECONDS);
    }

    /**
     * 分で時間計算します。 分に与える符号によって、和または差を計算します。
     *
     * @param date 対象のDate
     * @param l    分
     * @return 計算結果
     */
    public static Date calcMinntes(Date date, long l) {
        return calc(date, l, ChronoUnit.MINUTES);
    }

    /**
     * 時間計算します。
     *
     * @param date 対象のDate
     * @param l    分
     * @param unit 計算単位
     * @return 計算結果
     */
    public static Date calc(Date date, long l, ChronoUnit unit) {
        LocalDateTime time = getLocalDateTime(date);
        time = time.plus(l, unit);
        return getDate(time);
    }

    /**
     * 2つの時間の差を求めます。 2つの時間の差を分単位で求めます
     *
     * @param date1
     * @param date2
     * @return 2つの時間の差（分）
     */
    public static long diffMinntes(Date date1, Date date2) {
        LocalDateTime dt1 = getLocalDateTime(date1);
        LocalDateTime dt2 = getLocalDateTime(date2);
        return ChronoUnit.MINUTES.between(dt1, dt2);
    }

    /**
     * 2つの時間の差を求めます。 2つの時間の差を秒単位で求めます
     *
     * @param date1
     * @param date2
     * @return 2つの時間の差（秒）
     */
    public static long diffSecounds(Date date1, Date date2) {
        LocalDateTime dt1 = getLocalDateTime(date1);
        LocalDateTime dt2 = getLocalDateTime(date2);
        return diffSecounds(dt1, dt2);
    }

    /**
     * 2つの時間の差を求めます。 2つの時間の差を秒単位で求めます
     *
     * @param date1
     * @param date2
     * @return 2つの時間の差（秒）
     */
    public static long diffSecounds(LocalDateTime date1, LocalDateTime date2) {
        return ChronoUnit.SECONDS.between(date1, date2);
    }

    /**
     * 時間を丸めます
     *
     * @param date 丸める時間
     * @param unit 時間の単位
     * @return 丸めた時間結果
     */
    public static Date round(Date date, ChronoUnit unit) {
        LocalDateTime ldt = getLocalDateTime(date);
        return getDate(ldt.truncatedTo(unit));
    }
}

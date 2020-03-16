package com.mallivora.fabric.service.utils;

import java.util.Arrays;

public class DateUtils {

    private static final Long Nanosecond           = 1L;
    private static final Long Microsecond          = 1000 * Nanosecond;
    private static final Long Millisecond          = 1000 * Microsecond;
    private static final Long Second               = 1000 * Millisecond;
    private static final Long Minute               = 60 * Second;
    private static final Long Hour                 = 60 * Minute;

    public static String dateTo(Long d){
        Long u = d;
        byte[] buf = new byte[32];
        int w = buf.length;

        if (d < 0) {
            u = -u;
        }
        if (u < Second) {
            int prec;
            w--;
            buf[w] = 's';
            w--;
            if (u == 0) {
                return "0s";
            } else if (u < Microsecond) {
                prec = 0;
                buf[w] = 'n';
            } else if (u < Millisecond) {
                prec = 3;
                w--;
                String n = "µ";
                n.getBytes();
                buf[w] = 'p';
            } else {
                prec = 6;
                buf[w] = 'm';
            }
            w = fmtFrac(Arrays.copyOf(buf,w),u,prec);
            for (int i = 0; i < prec; i++) {
                u /= 10;
            }
            //w = fmtInt(Arrays.copyOf(buf,w),u);
            if (u == 0) {
                w--;
                buf[w] = '0';
            } else {
                while ( u > 0) {
                    w--;
                    Long n = u % 10;
                    buf[w] = (byte) (n.byteValue() + '0');
                    u /=10;
                }
            }
        } else {
            w--;
            buf[w] = 's';
            w = fmtFrac(Arrays.copyOf(buf, w), u, 9);
            for (int i = 0; i < 9; i++) {
                u /= 10;
            }
            if (u == 0) {
                w--;
                buf[w] = '0';
            } else {
                while ( u > 0) {
                    w--;
                    Long n = u % 10;
                    buf[w] = (byte) (n.byteValue() + '0');
                    u /=10;
                }
            }
            u /= 60;

            if (u > 0) {
                w--;
                buf[w] = 'm';
                w = fmtInt(Arrays.copyOf(buf, w), u%60);
                u = u%60;
                if (u%60 == 0) {
                    w--;
                    buf[w] = '0';
                } else {
                    while ( u%60 > 0) {
                        w--;
                        Long n = u%60 % 10;
                        buf[w] = (byte) (n.byteValue() + '0');
                        u /=10;
                    }
                }

                u /= 60;
                if (u > 0) {
                    w--;
                    buf[w] = 'h';
                    if (u == 0) {
                        w--;
                        buf[w] = '0';
                    } else {
                        while ( u > 0) {
                            w--;
                            Long n = u % 10;
                            buf[w] = (byte) (n.byteValue() + '0');
                            u /=10;
                        }
                    }
                }
            }

        }

        if (d < 0 ) {
            w--;
            buf[w] = '-';
        }

        return new String(Arrays.copyOfRange(buf,w,buf.length));
    }

    private static int fmtFrac(byte[] bytes, Long v, int prec) {
        int w = bytes.length;
        boolean print = false;
        for (int i = 0; i < prec; i++) {
            Long digit = v % 10;
            print = (print || digit != 0);
            if (print) {
                w--;
                bytes[w] = (byte) (digit.byteValue() + '0');
            }
            v /= 10;
        }

        if (print) {
            w--;
            bytes[w] = '.';
        }
        return w;
    }

    private static int fmtInt(byte[] buf, Long v) {
        int w = buf.length;
        if (v == 0) {
            w--;
            buf[w] = '0';
        } else {
            while ( v > 0) {
                w--;
                Long n = v % 10;
                buf[w] = (byte) (n.byteValue() + '0');
                v /=10;
            }
        }
        return w;
    }

    public static void main(String[] args) {
        byte[] bytes = new byte[32];
        String s = dateTo(2000000000L);
        System.out.println(s);
    }

}

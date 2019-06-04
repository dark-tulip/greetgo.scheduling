package kz.greetgo.scheduling;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *   Examples:
 *
 *      repeat every 10 sec after pause in 20 sec
 *
 *      repeat every 30 sec
 *
 *      11:17
 *
 *      11:21:30
 *
 *      from 11:00 to 17:00 every 30 minutes
 *
 *      (11:17 + 21:19) * (monday + wednesday)
 *
 *      from 11:00 to 17:00 every 30 minutes * (sunday + saturday)
 *
 * Atomic schedulers:
 *
 *    1) Exact time of day
 *
 *             HH:mm[:ss]
 *
 *       HH - hours  : 00 .. 23
 *       mm - minutes: 00 .. 59
 *       ss - seconds: 00 .. 59
 *       [] - content in [] is not mandatory
 *
 *    2) Periodic run task every fixed duration (N1) after pause, if specified, in fixed duration (N2)
 *
 *             repeat every N1 sec|min|hour [after pause in N2 sec|min|hour]
 *
 *       N1, N2 - positive integer numbers
 *       [] - content in [] is not mandatory
 *
 *    3) Period of time with fixed run every fixed time
 *
 *       from HH:mm[:ss] to HH:mm[:ss] every N sec|min|hour
 *
 *    4) Simple period of time in day. It can be used only with cooperation with another schedulers
 *
 *       from HH:mm[:ss] to HH:mm[:ss]
 *
 *    5) Day of week
 *
 *       sunday|monday|tuesday|wednesday|thursday|friday|saturday
 *
 * Scheduler combinations
 *
 *    Some schedulers can be combined with two operator: plus `+` and multiply `*`. Multiply is more harder then plus.
 *    To exactly determine order of operation you can use square brackets, for example:
 *
 *       from 8:00 to 18:00 every 1 hour * (mon + tue + wed + thu + fri)
 * </pre>
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {
  String value();
}

package kz.greetgo.scheduling;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 *
 * Examples:
 *
 * repeat every 10 sec after pause in 20 sec
 *
 * repeat every 30 sec
 *
 * 11:17
 *
 * 11:21:30
 *
 * from 11:00 to 17:00 every 30 minutes
 *
 * (11:17 + 21:19) * (monday + wednesday)
 *
 * from 11:00 to 17:00 every 30 minutes * (sunday + saturday) * 1 5 10..23 mart..june november 2000 2010..2020 years
 *
 * Atomic schedulers:
 *
 * 1) Exact time of day
 *
 * HH:mm[:ss]
 *
 * HH - hours  : 00 .. 23
 * mm - minutes: 00 .. 59
 * ss - seconds: 00 .. 59
 * [] - content in [] is not mandatory
 *
 * 2) Periodic run task every fixed duration (N1) after pause, if specified, in fixed duration (N2)
 *
 * repeat every N1 sec|min|hour [after pause in N2 sec|min|hour]
 *
 * N1, N2 - positive integer numbers
 * [] - content in [] is not mandatory
 *
 * 3) Period of time with fixed run every fixed time
 *
 * from HH:mm[:ss] to HH:mm[:ss] every N sec|min|hour
 *
 * 4) Simple period of time in day. It can be used only with cooperation with another schedulers
 *
 * from HH:mm[:ss] to HH:mm[:ss]
 *
 * 5) Day of week
 *
 * sunday|monday|tuesday|wednesday|thursday|friday|saturday
 *
 * 6) Months, days of month and years
 *
 * Examples:
 *
 * 1..10 mart april
 *
 * It is means: from 1 to 10 mart or april
 *
 * 1 5 7 october
 *
 * It is means: 1st, 5th or 7th october
 *
 * You can combine ranges and simple numbers of months:
 *
 * 1 3 10..20 september
 *
 * It is means: 1st, 3d or from 10th to 20th of the september
 *
 * You can use ranges for months:
 *
 * 17 september..december
 *
 * It is means: 17th of the september or of the october or of the november or of the december
 *
 * If you want to define some number of any month you can do it like:
 *
 * 17 jan..dec
 *
 * It is means: 17th of any month
 *
 * You can also define years, for example:
 *
 * 2000 2005 2010..2017 years
 *
 * It is means: In any year from the: 2000 2005 2010 2011 2012 2013 2014 2015 2016 2017
 *
 * And you can combine years with months, for example:
 *
 * 1 april 2007 2011 year
 *
 * It is means: The first of the april in 2007 or in 2011 years
 *
 * Scheduler combinations
 *
 * Some schedulers can be combined with two operator: plus `+` and multiply `*`. Multiply is more harder then plus.
 * To exactly determine order of operation you can use square brackets, for example:
 *
 * from 8:00 to 18:00 every 1 hour * (mon + tue + wed + thu + fri) * (2011..2020 years)
 * </pre>
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {
  String value();
}

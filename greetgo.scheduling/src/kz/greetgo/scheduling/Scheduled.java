package kz.greetgo.scheduling;

import java.lang.annotation.*;

/**
 * <p>Marks a method that will be executed on the schedule, and also defines this schedule.
 * <b>The method should be public.</b>
 * </p>
 * <p>
 * There are two schedule format: calendar and periodic
 * </p>
 * <h2>CALENDAR FORMAT OF THE SCHEDULE</h2>
 * <p>
 * Schedule format: &lt;hours&gt;<b>:</b>&lt;minutes&gt;&nbsp;&nbsp;&nbsp;<b>(</b>&lt;days of the month&gt;<b>)</b>
 * &nbsp;&nbsp;&nbsp;<b>{</b>&lt;days of the week&gt<b>}</b>&nbsp;&nbsp;&nbsp;<b>[</b>&lt;Months&gt;<b>]</b>
 * </p>
 * <p>
 * Some examples:
 * </p>
 * <table>
 * <thead>
 * <tr>
 * <th>Example</th>
 * <th>When a task will be initiated</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td><b>12:30&nbsp;&nbsp;{monday,wednesday}&nbsp;&nbsp;[March-June,December]</b></td>
 * <td>at 12:30 from Monday to Wednesday from March to June and on December from Monday to Wednesday as well</td>
 * </tr>
 * <tr>
 * <td><b>08-18:00/15&nbsp;&nbsp;&nbsp;{mon-fri}</b></td>
 * <td>every 15 minutes from 8 a.m. till 6 p.m. on weekdays</td>
 * </tr>
 * <tr>
 * <td><b>*:0&nbsp;&nbsp;&nbsp;{mon}</b></td>
 * <td>every one hour in 00 minutes all day and night, but only on Mondays</td>
 * </tr>
 * <tr>
 * <td><b>00:10&nbsp;&nbsp;&nbsp;(3,7,10)</b></td>
 * <td>at 01:10 at night, on the third, seventh and tenth day of each month</td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * A detailed description of the schedule format:
 * </p>
 * <p>
 * Months are in two languages: Russian and English. First three letters of the month can also be used.
 * Any register can be used.
 * </p>
 * <p>
 * Days of the week are in two languages: Russian and English. First three letters can be used as well.
 * Any register can be used. First two letters can also be used: MO, TU, WE, TH, FR, SA, SU.
 * </p>
 * <table>
 * <thead>
 * <tr>
 * <th>Schedule element</th>
 * <th>Possible forms</th>
 * <th>Description and examples</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td rowspan="3">&lt;minutes&gt; or &lt;hours&gt; or &lt;months&gt; </td>
 * <td>DATE</td>
 * <td>Sets a specific minute or an hour, when it is time to start the task</td>
 * </tr>
 * <tr>
 * <td>DATE1-DATE1</td>
 * <td>Defines several values in the range: <br>DATE1 &lt;= x &lt;= DATE2</td>
 * </tr>
 * <tr>
 * <td>DATE1/DATE2</td>
 * <td>defines several values starting with DATE1 with a period DATE2:<br>
 * x = DATE1<br>x = DATE1 + DATE2<br> x = DATE1 + 2*DATE2<br> x = DATE1 + 3*DATE2<br>......
 * </td>
 * </tr>
 * </tbody>
 * </table>
 * <h2>PERIODIC FORMAT OF THE SCHEDULE</h2>
 * <p>
 * Schedule format in Russian: [параллельно] повторять каждые NN1 сек|мин [, начиная с паузы NN2 сек|мин]
 * </p>
 * <p>
 * Schedule format in English: [parallel] repeat every NN1 sec|min [, after pause in NN2 sec|min]
 * </p>
 * <p>
 * where [ ] - means optionality, | - means possibility "one of"
 * <br>If the parameter NN2 is not specified, the value NN2 = 0 is applied
 * <br>After the start of the scheduler, the system waits for time NN2 and initiates the task. And then, depending on the presence of the word
 * "параллель|parallel", which defines the parallel mode of startup (the word is present) or sequential mode
 * of startup (the word is not present):
 * <br><b>In parallel mode</b> the system without waiting for completion of the task, awaiting time NN1  and initiates the task again,
 * and then also without waiting for completion of the task, awaiting time NN1 initiates the task again, and then till the stop of the scheduler 
 * the task will be initiated at regular intervals of NN1.
 * <br><b>In sequential mode</b> the system waits for task completion, and then starts timing.
 * When the time NN1 is over, the task is initiated again. Then waiting for task completion again, after completion -
 * waiting for time NN1 and startup. And then till the stop of the scheduler.
 * </p>
 * <p>
 * Examples: 
 * <br><b>repeat every 13 min after pause in 17 sec</b>
 * <br>
 * <br><b>parallel repeat every 13 sec after pause in 17 min</b>
 * <br>
 * <br><b>repeat every 13 min </b>
 * <br>
 * <br><b>parallel repeat every 13 min</b>
 * <br>
 * <br><b>повторять каждые 13 мин, начиная с паузы 17 сек</b>
 * <br>
 * <br><b>параллельно повторять каждые 13 мин, начиная с паузы 17 сек</b>
 * <br>
 * <br><b>повторять каждые 13 сек</b>
 * <br>
 * <br><b>параллельно повторять каждые 13 сек</b>
 * </p>
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {
  String value();
}

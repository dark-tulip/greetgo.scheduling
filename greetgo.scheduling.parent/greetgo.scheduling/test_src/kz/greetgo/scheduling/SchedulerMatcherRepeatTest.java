package kz.greetgo.scheduling;

import kz.greetgo.scheduling.SchedulerMatcherRepeat.ParseResult;
import org.testng.annotations.Test;

import static kz.greetgo.scheduling.SchedulerMatcherRepeat.parseEng;
import static kz.greetgo.scheduling.SchedulerMatcherRepeat.parseRus;
import static org.fest.assertions.api.Assertions.assertThat;

public class SchedulerMatcherRepeatTest {

  @Test
  public void parseRus_0() throws Exception {
    //
    //
    final ParseResult pr = parseRus(" левая строка ");
    //
    //

    assertThat(pr).isNull();
  }

  @Test
  public void parseRus_1() throws Exception {
    //
    //
    final ParseResult pr = parseRus(" parallel повторять каждые 13 мин, начиная  с паузы 17 сек ");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isTrue();
    assertThat(pr.repeatingBy).isEqualTo(13L * 60L * 1000L);
    assertThat(pr.waitingFor).isEqualTo(17L * 1000L);
  }

  @Test
  public void parseRus_2() throws Exception {
    //
    //
    final ParseResult pr = parseRus(" параллельно повторять каждые 13 минут, начиная  с паузы 17 секунд ");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isTrue();
    assertThat(pr.repeatingBy).isEqualTo(13L * 60L * 1000L);
    assertThat(pr.waitingFor).isEqualTo(17L * 1000L);
  }

  @Test
  public void parseRus_3() throws Exception {
    //
    //
    final ParseResult pr = parseRus(" повторять каждые 13 мин, начиная  с паузы 17 сек ");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isFalse();
    assertThat(pr.repeatingBy).isEqualTo(13L * 60L * 1000L);
    assertThat(pr.waitingFor).isEqualTo(17L * 1000L);
  }


  @Test
  public void parseRus_4() throws Exception {
    //
    //
    final ParseResult pr = parseRus(" повторять каждые 13 мин");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isFalse();
    assertThat(pr.repeatingBy).isEqualTo(13L * 60L * 1000L);
    assertThat(pr.waitingFor).isZero();
  }

  @Test
  public void parseRus_5() throws Exception {
    //
    //
    final ParseResult pr = parseRus("параллельно повторять каждые 13 мин");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isTrue();
    assertThat(pr.repeatingBy).isEqualTo(13L * 60L * 1000L);
    assertThat(pr.waitingFor).isZero();
  }

  @Test
  public void parseEng_0() throws Exception {
    //
    //
    final ParseResult pr = parseEng(" left string ");
    //
    //

    assertThat(pr).isNull();
  }

  @Test
  public void parseEng_1() throws Exception {
    //
    //
    final ParseResult pr = parseEng(" parallel repeat every 13 min after pause in 17 сек ");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isTrue();
    assertThat(pr.repeatingBy).isEqualTo(13L * 60L * 1000L);
    assertThat(pr.waitingFor).isEqualTo(17L * 1000L);
  }

  @Test
  public void parseEng_2() throws Exception {
    //
    //
    final ParseResult pr = parseEng(" параллельно repeat every  13 minutes after pause in 17 s ");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isTrue();
    assertThat(pr.repeatingBy).isEqualTo(13L * 60L * 1000L);
    assertThat(pr.waitingFor).isEqualTo(17L * 1000L);
  }

  @Test
  public void parseEng_3() throws Exception {
    //
    //
    final ParseResult pr = parseEng(" repeat every 13 min after  pause in 17 sec ");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isFalse();
    assertThat(pr.repeatingBy).isEqualTo(13L * 60L * 1000L);
    assertThat(pr.waitingFor).isEqualTo(17L * 1000L);
  }


  @Test
  public void parseEng_4() throws Exception {
    //
    //
    final ParseResult pr = parseEng("repeat every 13 min");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isFalse();
    assertThat(pr.repeatingBy).isEqualTo(13L * 60L * 1000L);
    assertThat(pr.waitingFor).isZero();
  }

  @Test
  public void parseEng_5() throws Exception {
    //
    //
    final ParseResult pr = parseEng("параллельно repeat every 13 min");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isTrue();
    assertThat(pr.repeatingBy).isEqualTo(13L * 60L * 1000L);
    assertThat(pr.waitingFor).isZero();
  }

  @Test
  public void parse_1() throws Exception {
    //
    //
    assertThat(SchedulerMatcherRepeat.parse("repeat every 13 minutes after pause in 17 min", 0L)).isNotNull();
    //
    //
  }

  @Test
  public void parseRus_more() throws Exception {
    //
    //
    final ParseResult pr = parseRus(" повторять каждые 13.6 мин, начиная с паузы 2.3 минуты ");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isFalse();
    assertThat(pr.repeatingBy).isEqualTo(13600L * 60L);
    assertThat(pr.waitingFor).isEqualTo(2300L * 60L);
  }

  @Test
  public void parseEng_more() throws Exception {
    //
    //
    final ParseResult pr = parseEng(" repeat every 13.6 minutes after pause in 2.3 minutes ");
    //
    //

    assertThat(pr).isNotNull();
    assertThat(pr.parallel).isFalse();
    assertThat(pr.repeatingBy).isEqualTo(13600L * 60L);
    assertThat(pr.waitingFor).isEqualTo(2300L * 60L);
  }
}
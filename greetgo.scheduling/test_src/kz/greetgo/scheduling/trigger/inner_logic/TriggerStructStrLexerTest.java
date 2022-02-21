package kz.greetgo.scheduling.trigger.inner_logic;

import kz.greetgo.scheduling.trigger.inner_logic.TriggerStructStrLexer.Lex;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;

import static kz.greetgo.scheduling.trigger.inner_logic.LexType.AT;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.REPEAT;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.TIME_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

public class TriggerStructStrLexerTest {

  @Test
  public void tokenize_01() {

    String source = " 54hb356hb 222 5646:654 44=--0=  111  33 ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.tokenize();
    //
    //

    assertThat(lexer.tokenList).hasSize(6);
    assertThat(lexer.tokenList.get(0).toString()).isEqualTo("{`54hb356hb` Range(1..10)}");
    assertThat(lexer.tokenList.get(1).toString()).isEqualTo("{`222` Range(11..14)}");
    assertThat(lexer.tokenList.get(2).toString()).isEqualTo("{`5646:654` Range(15..23)}");
    assertThat(lexer.tokenList.get(3).toString()).isEqualTo("{`44=--0=` Range(24..31)}");
    assertThat(lexer.tokenList.get(4).toString()).isEqualTo("{`111` Range(33..36)}");
    assertThat(lexer.tokenList.get(5).toString()).isEqualTo("{`33` Range(38..40)}");

  }

  @Test
  public void tokenize_02() {

    String source = " 12..34 ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.tokenize();
    //
    //

    for (final TriggerStructStrLexer.Token token : lexer.tokenList) {
      System.out.println("81Z5qBT2nC :: token = " + token);
    }

    assertThat(lexer.tokenList.get(0).toString()).isEqualTo("{`12` Range(1..3)}");
    assertThat(lexer.tokenList.get(1).toString()).isEqualTo("{`..` Range(3..5)}");
    assertThat(lexer.tokenList.get(2).toString()).isEqualTo("{`34` Range(5..7)}");
    assertThat(lexer.tokenList).hasSize(3);

  }

  @Test
  public void tokenize_03() {

    String source = " 12..  34 ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.tokenize();
    //
    //

    for (final TriggerStructStrLexer.Token token : lexer.tokenList) {
      System.out.println("13cza1EwP0 :: token = " + token);
    }

    assertThat(lexer.tokenList.get(0).toString()).isEqualTo("{`12` Range(1..3)}");
    assertThat(lexer.tokenList.get(1).toString()).isEqualTo("{`..` Range(3..5)}");
    assertThat(lexer.tokenList.get(2).toString()).isEqualTo("{`34` Range(7..9)}");
    assertThat(lexer.tokenList).hasSize(3);

  }

  @Test
  public void tokenize_04() {

    String source = " 12  ..34 ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.tokenize();
    //
    //

    for (final TriggerStructStrLexer.Token token : lexer.tokenList) {
      System.out.println("BZ5Z7j5gg2 :: token = " + token);
    }

    assertThat(lexer.tokenList.get(0).toString()).isEqualTo("{`12` Range(1..3)}");
    assertThat(lexer.tokenList.get(1).toString()).isEqualTo("{`..` Range(5..7)}");
    assertThat(lexer.tokenList.get(2).toString()).isEqualTo("{`34` Range(7..9)}");
    assertThat(lexer.tokenList).hasSize(3);

  }

  @Test
  public void tokenize_05() {

    String source = "   12  ..   347 ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.tokenize();
    //
    //

    for (final TriggerStructStrLexer.Token token : lexer.tokenList) {
      System.out.println("W6gpOQOQiu :: token = " + token);
    }

    assertThat(lexer.tokenList.get(0).toString()).isEqualTo("{`12` Range(3..5)}");
    assertThat(lexer.tokenList.get(1).toString()).isEqualTo("{`..` Range(7..9)}");
    assertThat(lexer.tokenList.get(2).toString()).isEqualTo("{`347` Range(12..15)}");
    assertThat(lexer.tokenList).hasSize(3);

  }

  @Test
  public void parse_001() {

    String source = " повторять каждые ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(REPEAT);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{REPEAT-[{`повторять` Range(1..10)}, {`каждые` Range(11..17)}]}");
  }

  @Test
  public void parse_002() {

    String source = "   повторять ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(REPEAT);
    assertThat(lexer.lexList.get(0).toString()).isEqualTo("LEX{REPEAT-[{`повторять` Range(3..12)}]}");
  }

  @Test
  public void parse_003() {

    String source = " j564v247jc49dx0x ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isNotEmpty();
    assertThat(lexer.errorList.get(0).errorCode).isEqualTo("26kjb43");
  }

  @Test
  public void parse_004() {

    String source = "  repEAt ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(REPEAT);
    assertThat(lexer.lexList.get(0).toString()).isEqualTo("LEX{REPEAT-[{`repEAt` Range(2..8)}]}");

  }

  @Test
  public void parse_005() {

    String source = "  repeat  every ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(REPEAT);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{REPEAT-[{`repeat` Range(2..8)}, {`every` Range(10..15)}]}");

  }

  @Test
  public void parse_006() {

    String source = "  234 секунды ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.TIME_VALUE);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{TIME_VALUE-[{`234` Range(2..5)}, {`секунды` Range(6..13)}]}");

  }

  @Test
  public void parse_007() {

    String source = "  234 seconds ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.TIME_VALUE);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{TIME_VALUE-[{`234` Range(2..5)}, {`seconds` Range(6..13)}]}");

  }

  @DataProvider
  public Object[][] millisInTimeUnitDataProvider() {
    return new Object[][]{

      {"seconds", 1000L},
      {"sec", 1000L},
      {"секунд", 1000L},
      {"сек", 1000L},

      {"минут", 1000L * 60},
      {"мин", 1000L * 60},
      {"minutes", 1000L * 60},
      {"min", 1000L * 60},

      {"часов", 1000L * 60 * 60},
      {"час", 1000L * 60 * 60},
      {"ч", 1000L * 60 * 60},
      {"hour", 1000L * 60 * 60},
      {"h", 1000L * 60 * 60},

      {"tre", null},

    };
  }

  @Test(dataProvider = "millisInTimeUnitDataProvider")
  public void millisInTimeUnit(String lowercaseToken, Long unitValue) {

    //
    //
    Optional<Long> timeUnit = TriggerStructStrLexer.readTimeUnitInMillis(lowercaseToken);
    //
    //

    assertThat(timeUnit).isEqualTo(Optional.ofNullable(unitValue));

  }

  @Test
  public void parse_008() {

    String source = "  начиная  с паУЗЫ ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.AFTER_PAUSE);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{AFTER_PAUSE-[{`начиная` Range(2..9)}, {`с` Range(11..12)}, {`паУЗЫ` Range(13..18)}]}");

  }

  @Test
  public void parse_009() {

    String source = "  after pause in ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.AFTER_PAUSE);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{AFTER_PAUSE-[{`after` Range(2..7)}, {`pause` Range(8..13)}, {`in` Range(14..16)}]}");

  }

  @Test
  public void parse_010() {

    String source = "  after pause ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isNotEmpty();
    assertThat(lexer.errorList.get(0).errorCode).isEqualTo("j5n4367");

  }


  @Test
  public void parse_011() {

    String source = "  after  ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isNotEmpty();
    assertThat(lexer.errorList.get(0).errorCode).isEqualTo("j5n4367");

  }

  @Test
  public void parse_012() {

    String source = "  начиная  с  ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isNotEmpty();
    assertThat(lexer.errorList.get(0).errorCode).isEqualTo("j5n4367");

  }

  @Test
  public void parse_013() {

    String source = "  начиная   ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isNotEmpty();
    assertThat(lexer.errorList.get(0).errorCode).isEqualTo("j5n4367");

  }

  @DataProvider
  public Object[][] fromDataProvider() {
    return new Object[][]{
      {"с"}, {"от"}, {"from"}
    };
  }

  @Test(dataProvider = "fromDataProvider")
  public void parse_015(String from) {

    String source = "  " + from + "   ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.FROM);
    assertThat(lexer.lexList.get(0).tokens.get(0).str()).isEqualTo(from);

  }

  @DataProvider
  public Object[][] toDataProvider() {
    return new Object[][]{
      {"по"}, {"до"}, {"to"}
    };
  }

  @Test(dataProvider = "toDataProvider")
  public void parse_016(String to) {

    String source = "  " + to + "   ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.TO);
    assertThat(lexer.lexList.get(0).tokens.get(0).str()).isEqualTo(to);

  }


  @Test
  public void parse_017() {

    String source = "  каждые ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.EVERY);
    assertThat(lexer.lexList.get(0).toString()).isEqualTo("LEX{EVERY-[{`каждые` Range(2..8)}]}");

  }

  @Test
  public void parse_018() {

    String source = "  every ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.EVERY);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{EVERY-[{`every` Range(2..7)}]}");

  }

  @Test
  public void parse_019() {

    String source = "  11:34 ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.TIME_OF_DAY);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{TIME_OF_DAY-[{`11:34` Range(2..7)}]}");

  }

  @Test
  public void parse_020() {

    String source = "  11:34:17 ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.TIME_OF_DAY);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{TIME_OF_DAY-[{`11:34:17` Range(2..10)}]}");

  }

  @SuppressWarnings("SpellCheckingInspection")
  @DataProvider
  public Object[][] readWeekDayDataProvider() {
    return new Object[][]{

      {"mon", WeekDay.MONDAY},
      {"пон", WeekDay.MONDAY},
      {"пн", WeekDay.MONDAY},

      {"tue", WeekDay.TUESDAY},
      {"вт", WeekDay.TUESDAY},

      {"wed", WeekDay.WEDNESDAY},
      {"ср", WeekDay.WEDNESDAY},

      {"thu", WeekDay.THURSDAY},
      {"чет", WeekDay.THURSDAY},
      {"чт", WeekDay.THURSDAY},

      {"fri", WeekDay.FRIDAY},
      {"пят", WeekDay.FRIDAY},
      {"пт", WeekDay.FRIDAY},

      {"sat", WeekDay.SATURDAY},
      {"суб", WeekDay.SATURDAY},
      {"сб", WeekDay.SATURDAY},

      {"sun", WeekDay.SUNDAY},
      {"вос", WeekDay.SUNDAY},
      {"вс", WeekDay.SUNDAY},

      ////

      {"понедельник", WeekDay.MONDAY},
      {"понедел", WeekDay.MONDAY},
      {"monday", WeekDay.MONDAY},

      {"вторник", WeekDay.TUESDAY},
      {"вторн", WeekDay.TUESDAY},
      {"tuesday", WeekDay.TUESDAY},

      {"среда", WeekDay.WEDNESDAY},
      {"сред", WeekDay.WEDNESDAY},
      {"wednesday", WeekDay.WEDNESDAY},

      ////

      {"wow", null},

    };
  }

  @Test(dataProvider = "readWeekDayDataProvider")
  public void readWeekDay(String lowercaseToken, WeekDay expected) {

    //
    //
    Optional<WeekDay> actual = TriggerStructStrLexer.readWeekDay(lowercaseToken);
    //
    //

    assertThat(actual).isEqualTo(Optional.ofNullable(expected));

  }

  @Test
  public void parse_021() {

    String source = "  понедельника ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.WEEK_DAY);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{WEEK_DAY-[{`понедельника` Range(2..14)}]}");

  }

  @Test
  public void parse_022() {

    String source = "  в ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(AT);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{AT-[{`в` Range(2..3)}]}");

  }

  @Test
  public void parse_023() {

    String source = "  at ";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();
    assertThat(lexer.lexList).hasSize(1);
    assertThat(lexer.lexList.get(0).type).isEqualTo(AT);
    assertThat(lexer.lexList.get(0).toString())
      .isEqualTo("LEX{AT-[{`at` Range(2..4)}]}");

  }


  @Test
  public void parse_024() {

    String source = "повторять каждые 17 секунд";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //


    assertThat(lexer.errorList).isEmpty();

    List<Lex> list = lexer.lexList;
    System.out.println(list);

    assertThat(lexer.lexList).hasSize(2);
    assertThat(lexer.lexList.get(0).type).isEqualTo(REPEAT);
    assertThat(lexer.lexList.get(1).type).isEqualTo(TIME_VALUE);
    assertThat(lexer.lexList.get(1).tokens).hasSize(2);


  }

  @Test
  public void parse_daysWithMonths() {

    String source = "  2 14 21 марта декабря";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();

    for (final Lex lex : lexer.lexList) {
      System.out.println("EEOv02Fkw2 :: lex = " + lex);
    }

    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.DIGIT);
    assertThat(lexer.lexList.get(1).type).isEqualTo(LexType.DIGIT);
    assertThat(lexer.lexList.get(2).type).isEqualTo(LexType.DIGIT);
    assertThat(lexer.lexList.get(3).type).isEqualTo(LexType.MONTH);
    assertThat(lexer.lexList.get(4).type).isEqualTo(LexType.MONTH);

    assertThat(lexer.lexList.get(0).toString()).isEqualTo("LEX{DIGIT-[{`2` Range(2..3)}]}");
    assertThat(lexer.lexList.get(1).toString()).isEqualTo("LEX{DIGIT-[{`14` Range(4..6)}]}");
    assertThat(lexer.lexList.get(2).toString()).isEqualTo("LEX{DIGIT-[{`21` Range(7..9)}]}");
    assertThat(lexer.lexList.get(3).toString()).isEqualTo("LEX{MONTH-[{`марта` Range(10..15)}]}");
    assertThat(lexer.lexList.get(4).toString()).isEqualTo("LEX{MONTH-[{`декабря` Range(16..23)}]}");

    assertThat(lexer.lexList).hasSize(5);

  }

  @Test
  public void parse_daysMonthsRanges() {

    String source = "  10..15 21 апреля сентября..декабрь";
    Range  top    = Range.of(10, 10 + source.length());

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(top, source);

    //
    //
    lexer.parse();
    //
    //

    assertThat(lexer.errorList).isEmpty();

    for (final Lex lex : lexer.lexList) {
      System.out.println("35J761zLGz :: lex = " + lex);
    }

    assertThat(lexer.lexList.get(0).type).isEqualTo(LexType.DIGIT);
    assertThat(lexer.lexList.get(1).type).isEqualTo(LexType.RANGE_DELIMITER);
    assertThat(lexer.lexList.get(2).type).isEqualTo(LexType.DIGIT);
    assertThat(lexer.lexList.get(3).type).isEqualTo(LexType.DIGIT);
    assertThat(lexer.lexList.get(4).type).isEqualTo(LexType.MONTH);
    assertThat(lexer.lexList.get(5).type).isEqualTo(LexType.MONTH);
    assertThat(lexer.lexList.get(6).type).isEqualTo(LexType.RANGE_DELIMITER);
    assertThat(lexer.lexList.get(7).type).isEqualTo(LexType.MONTH);

    assertThat(lexer.lexList.get(0).toString()).isEqualTo("LEX{DIGIT-[{`10` Range(2..4)}]}");
    assertThat(lexer.lexList.get(1).toString()).isEqualTo("LEX{RANGE_DELIMITER-[{`..` Range(4..6)}]}");
    assertThat(lexer.lexList.get(2).toString()).isEqualTo("LEX{DIGIT-[{`15` Range(6..8)}]}");
    assertThat(lexer.lexList.get(3).toString()).isEqualTo("LEX{DIGIT-[{`21` Range(9..11)}]}");
    assertThat(lexer.lexList.get(4).toString()).isEqualTo("LEX{MONTH-[{`апреля` Range(12..18)}]}");
    assertThat(lexer.lexList.get(5).toString()).isEqualTo("LEX{MONTH-[{`сентября` Range(19..27)}]}");
    assertThat(lexer.lexList.get(6).toString()).isEqualTo("LEX{RANGE_DELIMITER-[{`..` Range(27..29)}]}");
    assertThat(lexer.lexList.get(7).toString()).isEqualTo("LEX{MONTH-[{`декабрь` Range(29..36)}]}");

    assertThat(lexer.lexList).hasSize(8);

  }

}

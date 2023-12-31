package kz.greetgo.scheduling.trigger.inner_logic;

import kz.greetgo.scheduling.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;
import static kz.greetgo.scheduling.util.TimeUtil.MILLIS_HOUR;
import static kz.greetgo.scheduling.util.TimeUtil.MILLIS_MINUTE;
import static kz.greetgo.scheduling.util.TimeUtil.MILLIS_SECOND;

public class TriggerStructStrLexer {

  private final Range  topRange;
  private final String source;

  public TriggerStructStrLexer(Range topRange, String source) {
    this.topRange = topRange;
    this.source   = source;
  }

  class Token {
    final Range range;

    public Token(int from, int to) {
      range = Range.of(from, to);
    }

    String str() {
      return range.cut(source);
    }

    String strNormy() {
      return str().toLowerCase();
    }

    @SuppressWarnings("unused")
    Range topRang() {
      return topRange.up(range);
    }

    @Override
    public String toString() {
      return "{`" + str() + "` " + range + "}";
    }
  }

  public void parse() {
    tokenize();
    lexine();
  }

  final List<Token> tokenList = new ArrayList<>();

  void tokenize() {

    boolean isSpace     = true;
    int     startedFrom = 0;

    char prev, ch = ' ';

    for (int i = 0, count = source.length(); i < count; i++) {
      prev = ch;
      ch   = source.charAt(i);
      if (Character.isWhitespace(ch)) {

        if (isSpace) {
          continue;
        }
        isSpace = true;

        if (startedFrom < i) {
          tokenList.add(new Token(startedFrom, i));
        }

      } else {

        if (!isSpace) {

          if (ch == '.' && prev == '.') {
            if (startedFrom != i - 1) {
              tokenList.add(new Token(startedFrom, i - 1));
            }
            tokenList.add(new Token(i - 1, i + 1));
            startedFrom = i + 1;
          }

          continue;
        }

        isSpace     = false;
        startedFrom = i;

      }
    }

    if (!isSpace) {
      tokenList.add(new Token(startedFrom, source.length()));
    }

  }

  class Lex {

    final LexType     type;
    final List<Token> tokens;

    public Lex(LexType type, List<Token> tokens) {
      this.type   = type;
      this.tokens = unmodifiableList(tokens);
    }

    @Override
    public String toString() {
      return "LEX{" + type + "-" + tokens + "}";
    }

    public Range range() {
      return tokens.stream()
                   .map(t -> t.range)
                   .filter(Objects::nonNull)
                   .reduce(Range::union)
                   .orElseThrow(() -> new RuntimeException("5hb436b6 :: no ranges"));
    }

    public long readTimeValueMillis() {
      if (type != LexType.TIME_VALUE) {
        throw new RuntimeException("time value read only from type=TIME_VALUE");
      }

      if (tokens.size() != 2) {
        throw new RuntimeException("time value must have 2 tokens");
      }

      long valueInUnits = Long.parseLong(tokens.get(0).str());


      long unitInMillis = readTimeUnitInMillis(tokens.get(1).strNormy())
        .orElseThrow(() -> new RuntimeException("Empty time unit"));

      return valueInUnits * unitInMillis;
    }

    public long readTimeOfDayInMillis() {
      if (type != LexType.TIME_OF_DAY) {
        throw new RuntimeException("NA93ZKH0uD :: time of day in millis read only from type=" + LexType.TIME_OF_DAY);
      }
      return TimeUtil.hmsToMillis(tokens.get(0).str());
    }

    public WeekDay getWeekDay() {
      if (type != LexType.WEEK_DAY) {
        throw new RuntimeException("l90h2f1J90 :: week day read only from type=" + LexType.WEEK_DAY);
      }

      return readWeekDay(tokens.get(0).str()).orElseThrow(
        () -> new RuntimeException("q6zD8A9IT8 :: cannot read week day"));

    }

    public int getMonth() {
      if (type != LexType.MONTH) {
        throw new RuntimeException("BdWsFzm2yX :: month read only from type=" + LexType.MONTH);
      }

      int month = readMonth(tokens.get(0).str());
      if (month <= 0) {
        throw new RuntimeException("Ze3fw8hS12 :: Cannot ream month from `" + tokens.get(0).str() + "`");
      }
      return month;
    }
  }

  private String token(int... indexes) {

    return Arrays.stream(indexes)
                 .mapToObj(tokenList::get)
                 .map(Token::strNormy)
                 .collect(Collectors.joining(" "))
      ;

  }

  public final List<ParseError> errorList = new ArrayList<>();

  class LexReadResult {
    final int count;
    final Lex lex;

    public LexReadResult(int count, Lex lex) {
      this.count = count;
      this.lex   = lex;
    }
  }

  private Lex lex(int start, int count, LexType lexType) {
    List<Token> tokens = new ArrayList<>();
    for (int i = start; i < start + count; i++) {
      tokens.add(tokenList.get(i));
    }
    return new Lex(lexType, tokens);
  }

  public final List<Lex> lexList = new ArrayList<>();

  private void lexine() {

    int i = 0, len = len();

    while (i < len) {

      LexReadResult result = readLex(i);

      if (result == null) {
        return;
      }

      lexList.add(result.lex);

      i += result.count;

    }

  }

  private int len() {
    return tokenList.size();
  }

  private final static Pattern DIGITS      = Pattern.compile("\\d+");
  private final static Pattern TIME_OF_DAY = Pattern.compile("(\\d+):(\\d+)(:(\\d+))?");

  private final static Optional<Long> oSeconds = Optional.of(MILLIS_SECOND);
  private final static Optional<Long> oMinutes = Optional.of(MILLIS_MINUTE);
  private final static Optional<Long> oHours   = Optional.of(MILLIS_HOUR);

  static Optional<Long> readTimeUnitInMillis(String lowercaseToken) {
    //@formatter:off
    if (lowercaseToken == null) return Optional.empty();

    if (lowercaseToken.    equals("с"   )) return oSeconds;
    if (lowercaseToken.startsWith("сек" )) return oSeconds;
    if (lowercaseToken.    equals("s"   )) return oSeconds;
    if (lowercaseToken.startsWith("sec" )) return oSeconds;

    if (lowercaseToken.    equals("m"   )) return oMinutes;
    if (lowercaseToken.startsWith("min" )) return oMinutes;
    if (lowercaseToken.    equals("м"   )) return oMinutes;
    if (lowercaseToken.startsWith("мин" )) return oMinutes;

    if (lowercaseToken.    equals("ч"   )) return oHours;
    if (lowercaseToken.startsWith("час" )) return oHours;
    if (lowercaseToken.    equals("h"   )) return oHours;
    if (lowercaseToken.startsWith("hour")) return oHours;
    //@formatter:on

    return Optional.empty();
  }

  private static boolean isTimeUnit(String lowercaseToken) {
    return readTimeUnitInMillis(lowercaseToken).isPresent();
  }

  /**
   * преобразует строку в месяц
   *
   * @param lowercaseToken преобразуемая строка
   * @return номер месяца (1-янв, 2-фев, ...) или 0, если преобразовать не получилось
   */
  public static int readMonth(String lowercaseToken) {
    if (lowercaseToken == null) {
      return 0;
    }

    //@formatter:off

    if (lowercaseToken.startsWith("янв")) return  1;
    if (lowercaseToken.startsWith("jan")) return  1;

    if (lowercaseToken.startsWith("фев")) return  2;
    if (lowercaseToken.startsWith("feb")) return  2;

    if (lowercaseToken.startsWith("мар")) return  3;
    if (lowercaseToken.startsWith("mar")) return  3;

    if (lowercaseToken.startsWith("апр")) return  4;
    if (lowercaseToken.startsWith("apr")) return  4;

    if (lowercaseToken.startsWith("май")) return  5;
    if (lowercaseToken.startsWith("may")) return  5;

    if (lowercaseToken.startsWith("июн")) return  6;
    if (lowercaseToken.startsWith("jun")) return  6;

    if (lowercaseToken.startsWith("июл")) return  7;
    if (lowercaseToken.startsWith("jul")) return  7;

    if (lowercaseToken.startsWith("авг")) return  8;
    if (lowercaseToken.startsWith("aug")) return  8;

    if (lowercaseToken.startsWith("сен")) return  9;
    if (lowercaseToken.startsWith("sep")) return  9;

    if (lowercaseToken.startsWith("окт")) return 10;
    if (lowercaseToken.startsWith("oct")) return 10;

    if (lowercaseToken.startsWith("ноя")) return 11;
    if (lowercaseToken.startsWith("nov")) return 11;

    if (lowercaseToken.startsWith("дек")) return 12;
    if (lowercaseToken.startsWith("dec")) return 12;

    //@formatter:on

    return 0;
  }

  public static Optional<WeekDay> readWeekDay(String lowercaseToken) {

    if (lowercaseToken == null) {
      return Optional.empty();
    }

    if (lowercaseToken.startsWith("mon")) {
      return Optional.of(WeekDay.MONDAY);
    }
    if (lowercaseToken.startsWith("пон")) {
      return Optional.of(WeekDay.MONDAY);
    }
    if (lowercaseToken.equals("пн")) {
      return Optional.of(WeekDay.MONDAY);
    }

    if (lowercaseToken.startsWith("tue")) {
      return Optional.of(WeekDay.TUESDAY);
    }
    if (lowercaseToken.startsWith("вто")) {
      return Optional.of(WeekDay.TUESDAY);
    }
    if (lowercaseToken.equals("вт")) {
      return Optional.of(WeekDay.TUESDAY);
    }


    if (lowercaseToken.startsWith("wed")) {
      return Optional.of(WeekDay.WEDNESDAY);
    }
    if (lowercaseToken.startsWith("сре")) {
      return Optional.of(WeekDay.WEDNESDAY);
    }
    if (lowercaseToken.equals("ср")) {
      return Optional.of(WeekDay.WEDNESDAY);
    }


    if (lowercaseToken.startsWith("thu")) {
      return Optional.of(WeekDay.THURSDAY);
    }
    if (lowercaseToken.startsWith("чет")) {
      return Optional.of(WeekDay.THURSDAY);
    }
    if (lowercaseToken.equals("чт")) {
      return Optional.of(WeekDay.THURSDAY);
    }


    if (lowercaseToken.startsWith("fri")) {
      return Optional.of(WeekDay.FRIDAY);
    }
    if (lowercaseToken.startsWith("пят")) {
      return Optional.of(WeekDay.FRIDAY);
    }
    if (lowercaseToken.equals("пт")) {
      return Optional.of(WeekDay.FRIDAY);
    }


    if (lowercaseToken.startsWith("sat")) {
      return Optional.of(WeekDay.SATURDAY);
    }
    if (lowercaseToken.startsWith("суб")) {
      return Optional.of(WeekDay.SATURDAY);
    }
    if (lowercaseToken.equals("сб")) {
      return Optional.of(WeekDay.SATURDAY);
    }


    if (lowercaseToken.startsWith("sun")) {
      return Optional.of(WeekDay.SUNDAY);
    }
    if (lowercaseToken.startsWith("вос")) {
      return Optional.of(WeekDay.SUNDAY);
    }
    if (lowercaseToken.equals("вс")) {
      return Optional.of(WeekDay.SUNDAY);
    }


    return Optional.empty();
  }

  private static boolean isWeekDay(String lowercaseToken) {
    return readWeekDay(lowercaseToken).isPresent();
  }

  private static boolean isMonth(String lowercaseToken) {
    return readMonth(lowercaseToken) > 0;
  }

  private static boolean isYear(String lowercaseToken) {
    if (lowercaseToken.startsWith("год")) {
      return true;
    }
    if (lowercaseToken.startsWith("year")) {
      return true;
    }
    return false;
  }

  private LexReadResult readLex(int i) {
    String current = token(i);
    int    count   = 1;

    if (current.startsWith("пов")) {

      if (i + 1 < len() && token(i + 1).startsWith("каж")) {
        count++;
      }

      return new LexReadResult(count, lex(i, count, LexType.REPEAT));
    }

    if (current.equals("repeat")) {

      if (i + 1 < len() && token(i + 1).equals("every")) {
        count++;
      }

      return new LexReadResult(count, lex(i, count, LexType.REPEAT));
    }

    if (current.equals("начиная")) {

      if (i + 2 < len() && token(i + 1).equals("с") && token(i + 2).startsWith("пауз")) {
        return new LexReadResult(3, lex(i, 3, LexType.AFTER_PAUSE));
      }

      errorList.add(new ParseError(
        tokenList.get(i).range, "j5n4367", "Не полная лексема: должна быть `начиная с паузы`"
      ));

      return null;
    }

    if (current.equals("after")) {

      if (i + 2 < len() && token(i + 1).equals("pause") && token(i + 2).equals("in")) {
        return new LexReadResult(3, lex(i, 3, LexType.AFTER_PAUSE));
      }

      errorList.add(new ParseError(
        tokenList.get(i).range, "j5n4367", "Не полная лексема: должна быть `after pause in`"
      ));

      return null;
    }


    if (current.equals("from") || current.equals("с") || current.equals("c") || current.equals("от")) {
      return new LexReadResult(count, lex(i, count, LexType.FROM));
    }

    if (current.equals("to") || current.equals("по") || current.equals("до")) {
      return new LexReadResult(count, lex(i, count, LexType.TO));
    }

    if (current.equals("в") || current.equals("at")) {
      return new LexReadResult(count, lex(i, count, LexType.AT));
    }

    //noinspection SpellCheckingInspection
    if (current.startsWith("кажд") || current.equals("every")) {
      return new LexReadResult(1, lex(i, 1, LexType.EVERY));
    }

    if (DIGITS.matcher(current).matches()) {

      if (i + 1 < len() && isTimeUnit(token(i + 1))) {
        count++;
        return new LexReadResult(count, lex(i, count, LexType.TIME_VALUE));
      }

      return new LexReadResult(count, lex(i, count, LexType.DIGIT));
    }

    if (TIME_OF_DAY.matcher(current).matches()) {
      return new LexReadResult(1, lex(i, 1, LexType.TIME_OF_DAY));
    }

    if (isWeekDay(current)) {
      return new LexReadResult(1, lex(i, 1, LexType.WEEK_DAY));
    }

    if (isMonth(current)) {
      return new LexReadResult(1, lex(i, 1, LexType.MONTH));
    }

    if ("..".equals(current)) {
      return new LexReadResult(1, lex(i, 1, LexType.RANGE_DELIMITER));
    }

    if (isYear(current)) {
      return new LexReadResult(1, lex(i, 1, LexType.YEAR));
    }

    errorList.add(new ParseError(tokenList.get(i).range, "26kjb43", "Неизвестная лексема"));
    return null;
  }

}

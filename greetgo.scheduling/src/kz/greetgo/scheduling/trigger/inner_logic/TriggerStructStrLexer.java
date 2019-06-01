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

  private final Range topRange;
  private final String source;

  public TriggerStructStrLexer(Range topRange, String source) {
    this.topRange = topRange;
    this.source = source;
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

    boolean isSpace = true;
    int startedFrom = 0;

    for (int i = 0; i < source.length(); i++) {
      if (Character.isWhitespace(source.charAt(i))) {

        if (isSpace) {
          continue;
        }
        isSpace = true;
        tokenList.add(new Token(startedFrom, i));

      } else {

        if (!isSpace) {
          continue;
        }

        isSpace = false;
        startedFrom = i;

      }
    }

    if (!isSpace) {
      tokenList.add(new Token(startedFrom, source.length()));
    }

  }

  enum LexType {
    TIME_VALUE, AFTER_PAUSE, FROM, TO, EVERY, TIME_OF_DAY, WEEK_DAY, AT, REPEAT
  }

  class Lex {

    final LexType type;
    final List<Token> tokens;

    public Lex(LexType type, List<Token> tokens) {
      this.type = type;
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
        throw new RuntimeException("time of day in millis read only from type=TIME_OF_DAY");
      }
      return TimeUtil.hmsToMillis(tokens.get(0).str());
    }

    public WeekDay getWeekDay() {
      if (type != LexType.WEEK_DAY) {
        throw new RuntimeException("week day read only from type=WEEK_DAY");
      }

      return readWeekDay(tokens.get(0).str()).orElseThrow(() -> new RuntimeException("cannot read week day"));

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
      this.lex = lex;
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

  private final static Pattern DIGITS = Pattern.compile("\\d+");
  private final static Pattern TIME_OF_DAY = Pattern.compile("(\\d+):(\\d+)(:(\\d+))?");

  static Optional<Long> readTimeUnitInMillis(String lowercaseToken) {
    if (lowercaseToken.startsWith("с")) {
      return Optional.of(MILLIS_SECOND);
    }
    if (lowercaseToken.startsWith("s")) {
      return Optional.of(MILLIS_SECOND);
    }
    if (lowercaseToken.startsWith("m")) {
      return Optional.of(MILLIS_MINUTE);
    }
    if (lowercaseToken.startsWith("м")) {
      return Optional.of(MILLIS_MINUTE);
    }
    if (lowercaseToken.startsWith("ч")) {
      return Optional.of(MILLIS_HOUR);
    }
    if (lowercaseToken.startsWith("h")) {
      return Optional.of(MILLIS_HOUR);
    }
    return Optional.empty();
  }

  private static boolean isTimeUnit(String lowercaseToken) {
    return readTimeUnitInMillis(lowercaseToken).isPresent();
  }

  public static Optional<WeekDay> readWeekDay(String lowercaseToken) {

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

  private LexReadResult readLex(int i) {
    String current = token(i);
    int count = 1;


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

    if (current.startsWith("кажд") || current.equals("every")) {

      return new LexReadResult(1, lex(i, 1, LexType.EVERY));

    }

    if (DIGITS.matcher(current).matches()) {

      if (i + 1 < len() && isTimeUnit(token(i + 1))) {
        count++;
      }

      return new LexReadResult(count, lex(i, count, LexType.TIME_VALUE));

    }

    if (TIME_OF_DAY.matcher(current).matches()) {

      return new LexReadResult(1, lex(i, 1, LexType.TIME_OF_DAY));

    }

    if (isWeekDay(current)) {

      return new LexReadResult(1, lex(i, 1, LexType.WEEK_DAY));

    }

    errorList.add(new ParseError(tokenList.get(i).range, "26kjb43", "Неизвесная лексема"));
    return null;
  }

}

package kz.greetgo.scheduling.trigger.inner_logic;

import kz.greetgo.scheduling.trigger.atoms.SilentTrigger;
import kz.greetgo.scheduling.trigger.atoms.TriggerDayPoint;
import kz.greetgo.scheduling.trigger.atoms.TriggerMonth;
import kz.greetgo.scheduling.trigger.atoms.TriggerMonthDay;
import kz.greetgo.scheduling.trigger.atoms.TriggerPeriodInDay;
import kz.greetgo.scheduling.trigger.atoms.TriggerPeriodInDayRepeat;
import kz.greetgo.scheduling.trigger.atoms.TriggerRepeat;
import kz.greetgo.scheduling.trigger.atoms.TriggerWeekDay;
import kz.greetgo.scheduling.trigger.atoms.TriggerYear;
import kz.greetgo.scheduling.trigger.inner_logic.TriggerStructStrLexer.Lex;
import kz.greetgo.scheduling.util.TriggerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.AFTER_PAUSE;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.DIGIT;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.EVERY;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.FROM;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.MONTH;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.RANGE_DELIMITER;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.REPEAT;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.TIME_OF_DAY;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.TIME_VALUE;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.TO;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.WEEK_DAY;
import static kz.greetgo.scheduling.trigger.inner_logic.LexType.YEAR;

public class TriggerStructStrParser {

  private final Range  range;
  private final String source;

  private TriggerStructStrParser(Range range, String source) {
    this.range  = range;
    this.source = source;
  }

  public static TriggerStructStrParser of(Range range, String source) {
    return new TriggerStructStrParser(range, source);
  }

  public final List<ParseError> errorList = new ArrayList<>();

  Trigger trigger = null;

  public Trigger parse() {

    TriggerStructStrLexer lexer = new TriggerStructStrLexer(range, source);

    lexer.parse();

    if (lexer.errorList.size() > 0) {
      errorList.addAll(lexer.errorList);
      return new SilentTrigger();
    }

    parseLexList(lexer.lexList);

    if (lexer.errorList.size() > 0) {
      return new SilentTrigger();
    }

    if (trigger == null) {
      errorList.add(new ParseError(Range.of(0, source.length()), "214hY88", "Триггер не определён"));
      return new SilentTrigger();
    }

    return trigger;
  }

  private void parseLexList(List<Lex> lexList) {

    if (lexList.isEmpty()) {
      errorList.add(new ParseError(range, "n1w2mk5", "Нет расписания"));
      return;
    }

    if (lexList.stream().anyMatch(x -> x.type == MONTH || x.type == YEAR)) {
      parseMonthOrYear(lexList);
      return;
    }

    int i = 0, len = lexList.size();

    while (i < len) {

      Lex lex = lexList.get(i);

      if (trigger != null) {

        if (trigger instanceof TriggerPeriodInDay && lex.type == EVERY) {

          if (i + 1 >= len) {
            errorList.add(new ParseError(lex.range(), "2135jh6", "Не указана частота повторений"));
            return;
          }

          Lex next = lexList.get(i + 1);

          if (next.type != TIME_VALUE) {
            errorList.add(new ParseError(next.range(), "1b4ydWQ", "Не та лексема - нужно указать частоту повторений"));
            return;
          }

          trigger = new TriggerPeriodInDayRepeat((TriggerPeriodInDay) trigger, next.readTimeValueMillis());

          i += 2;
          continue;

        }

        errorList.add(new ParseError(lex.range(), "2h4hY88", "Лишняя лексема - триггер уже определён"));
        return;
      }

      if (lex.type == REPEAT) {

        int step = 2;

        if (i + 1 >= len) {
          errorList.add(new ParseError(lex.range(), "2n4b6v7", "Не указано количество повторений"));
          return;
        }

        Lex next = lexList.get(i + 1);

        if (next.type != TIME_VALUE) {
          errorList.add(new ParseError(lex.range(), "2h355h4", "Нужно указать частоту повторений"));
          return;
        }

        long delayMillis = next.readTimeValueMillis();

        long startSilentMillis = 0;

        if (i + 2 < len) {

          Lex next2 = lexList.get(i + 2);

          if (next2.type == AFTER_PAUSE) {

            if (i + 3 >= len) {
              errorList.add(new ParseError(next2.range(), "b54n254", "Не указано время паузы"));
              return;
            }

            Lex next3 = lexList.get(i + 3);

            if (next3.type != TIME_VALUE) {
              errorList.add(new ParseError(lex.range(), "j3u5b6v", "Нужно указать величину паузы"));
              return;
            }

            step              = 4;
            startSilentMillis = next3.readTimeValueMillis();

          }

        }

        trigger = new TriggerRepeat(startSilentMillis, delayMillis);

        i += step;
        continue;

      }

      if (lex.type == FROM) {
        if (i + 1 >= len) {
          errorList.add(new ParseError(lex.range(), "nsy3u8w", "Не указано количество повторений"));
          return;
        }

        Lex fromValue = lexList.get(i + 1);

        if (fromValue.type != TIME_OF_DAY) {
          errorList.add(new ParseError(lex.range(), "n2u63h2", "Несогласованная лексема - ожидается время HH:mm[:ss]"));
          return;
        }

        if (i + 2 >= len) {
          errorList.add(new ParseError(lex.range(), "iqE72WW", "Не указано конечное время"));
          return;
        }

        Lex to = lexList.get(i + 2);

        if (to.type != TO) {
          errorList.add(new ParseError(fromValue.range(), "ws7iq92", "Несогласованная лексема - ожидается" +
            " указатель окончания временного интервала"));
          return;
        }

        if (i + 3 >= len) {
          errorList.add(new ParseError(to.range(), "qii543w", "Незаконченная лексема - ожидается время HH:mm[:ss]"));
          return;
        }

        Lex toValue = lexList.get(i + 3);

        if (toValue.type != TIME_OF_DAY) {
          errorList.add(new ParseError(lex.range(), "qiu2777", "Несогласованная лексема - ожидается время HH:mm[:ss]"));
          return;
        }

        long fromMillis = fromValue.readTimeOfDayInMillis();
        long toMillis   = toValue.readTimeOfDayInMillis();

        trigger = new TriggerPeriodInDay(fromMillis, toMillis);

        i += 4;
        continue;

      }

      if (lex.type == WEEK_DAY) {

        trigger = new TriggerWeekDay(lex.getWeekDay());

        i++;
        continue;
      }

      if (lex.type == TIME_OF_DAY) {

        trigger = new TriggerDayPoint(lex.tokens.get(0).str());

        i++;
        continue;
      }

      errorList.add(new ParseError(lex.range(), "j25bhj4", "Несогласованная лексема"));
      return;
    }

  }

  private boolean validateDoubleYears(List<Lex> lexList) {
    boolean hasYear = false;
    for (final Lex lex : lexList) {
      if (lex.type == YEAR) {
        if (hasYear) {
          errorList.add(new ParseError(lex.range(), "uEIlY6GC5T", "Повторно указан год"));
          return true;
        }
        hasYear = true;
      }
    }
    return false;
  }

  private static <T> List<T> reverseList(List<T> list) {
    List<T> reversedLexList = new ArrayList<>(list);
    Collections.reverse(reversedLexList);
    return reversedLexList;
  }

  private void parseMonthOrYear(List<Lex> lexList) {

    if (validateDoubleYears(lexList)) {
      return;
    }

    List<Lex> reversedLexList = reverseList(lexList);

    List<Lex> reversedYears = new ArrayList<>();
    Lex       yearLex       = null;

    List<Lex> reversedMonthDays = new ArrayList<>();
    List<Lex> reversedMonths    = new ArrayList<>();
    boolean   monthsStopped     = false;

    final int DIGITS_TO_UNKNOWN = 0;
    final int DIGITS_TO_MONTHS  = 1;
    final int DIGITS_TO_YEARS   = 2;

    int digitsTo = DIGITS_TO_UNKNOWN;

    final int RANGE_TO_UNKNOWN    = 0;
    final int RANGE_TO_MONTH_DAYS = 1;
    final int RANGE_TO_MONTHS     = 2;
    final int RANGE_TO_YEARS      = 3;

    int rangeTo = RANGE_TO_UNKNOWN;

    for (final Lex lex : reversedLexList) {
      if (lex.type == MONTH) {
        if (monthsStopped) {
          errorList.add(new ParseError(reversedMonths.get(reversedMonths.size() - 1).range(),
                                       "A6Dy08k7CL", "Месяцы можно определять только один раз"));
          return;
        }
        reversedMonths.add(lex);
        digitsTo = DIGITS_TO_MONTHS;
        rangeTo  = RANGE_TO_MONTHS;
        continue;
      }

      if (lex.type == RANGE_DELIMITER) {
        if (rangeTo == RANGE_TO_MONTH_DAYS) {
          reversedMonthDays.add(lex);
        } else if (rangeTo == RANGE_TO_MONTHS) {
          reversedMonths.add(lex);
        } else if (rangeTo == RANGE_TO_YEARS) {
          reversedYears.add(lex);
        } else {
          errorList.add(new ParseError(lex.range(), "I2MpN0fzDY", "Неуместное положение диапазона"));
          return;
        }
        continue;
      }

      if (reversedMonths.size() > 0) {
        monthsStopped = true;
      }

      if (lex.type == YEAR) {
        if (yearLex != null) {
          errorList.add(new ParseError(lex.range(), "CfD5sdc37e", "Год можно определять только один раз"));
          return;
        }
        yearLex  = lex;
        digitsTo = DIGITS_TO_YEARS;
        rangeTo  = RANGE_TO_YEARS;
        continue;
      }

      if (rangeTo == RANGE_TO_MONTHS) {
        rangeTo = RANGE_TO_MONTH_DAYS;
      }

      if (digitsTo == DIGITS_TO_MONTHS) {
        reversedMonthDays.add(lex);
        continue;
      }
      if (digitsTo == DIGITS_TO_YEARS) {
        reversedYears.add(lex);
        continue;
      }

      {
        errorList.add(new ParseError(lex.range(), "WH3JGs16xh", "Неизвестное назначение лексемы"));
        return;
      }
    }

    List<Lex> years     = reverseList(reversedYears);
    List<Lex> monthDays = reverseList(reversedMonthDays);
    List<Lex> months    = reverseList(reversedMonths);

    Trigger yearTrigger = null;

    if (yearLex != null) {

      if (years.isEmpty()) {
        errorList.add(new ParseError(yearLex.range(), "WH3JGs16xh", "Не указано значение года"));
        return;
      }

      yearTrigger = extractRanges(years, DIGIT).stream()
                                               .map(TriggerYear::new)
                                               .map(Trigger.class::cast)
                                               .reduce(TriggerUtil::or)
                                               .orElse(SilentTrigger.SILENT);
    }

    Trigger monthTrigger = null;

    if (months.size() > 0) {
      monthTrigger = createMonthsTrigger(months, monthDays);
    }

    if (errorList.size() > 0) {
      return;
    }

    if (yearTrigger != null && monthTrigger != null) {
      trigger = TriggerUtil.and(monthTrigger, yearTrigger);
      return;
    }

    if (yearTrigger != null) {
      trigger = yearTrigger;
      return;
    }

    if (monthTrigger != null) {
      trigger = monthTrigger;
      return;
    }

    {
      errorList.add(new ParseError(range, "p4bl3D6LFC", "Ничего нет"));
      return;
    }
  }

  private List<Range> extractRanges(List<Lex> digitRangeLaxList, LexType sourceType) {

    Lex leftLex = digitRangeLaxList.stream()
                                   .filter(x -> x.type != sourceType && x.type != RANGE_DELIMITER)
                                   .findAny()
                                   .orElse(null);

    if (leftLex != null) {
      errorList.add(new ParseError(leftLex.range(),
                                   "RR9FP7bRuO", "Неуместная лексема: возможны только лексемы" +
                                     " типов: " + sourceType + ", " + RANGE_DELIMITER));
      return emptyList();
    }

    List<Range> ret = new ArrayList<>();

    List<Lex> list = new ArrayList<>(digitRangeLaxList);

    while (list.size() > 0) {

      Lex first = list.remove(0);
      if (first.type == RANGE_DELIMITER) {
        errorList.add(new ParseError(first.range(),
                                     "xT5Kl13yda", "Диапазон без значения слева"));
        return emptyList();
      }

      if (list.isEmpty()) {
        ret.add(createRange(first, first, sourceType));
        break;
      }

      if (list.get(0).type != RANGE_DELIMITER) {
        ret.add(createRange(first, first, sourceType));
        continue;
      }

      Lex rangeLex = list.remove(0);

      if (list.isEmpty()) {
        errorList.add(new ParseError(rangeLex.range(),
                                     "S7bGQgQ70z", "Диапазон без значения справа"));
        return emptyList();
      }

      Lex second = list.remove(0);

      if (second.type == RANGE_DELIMITER) {
        errorList.add(new ParseError(second.range(),
                                     "aed7eLG3yd", "Повторный диапазон - уберите один"));
        return emptyList();
      }

      ret.add(createRange(first, second, sourceType));

    }

    return ret;

  }

  private static Range createRange(Lex from, Lex to, LexType sourceType) {
    if (from.type != sourceType || to.type != sourceType) {
      throw new RuntimeException("8B9X6wqGnF :: Both must have type " + sourceType + ": from=" + from + ", to=" + to);
    }

    int intFrom = extractIntFromLex(from);
    int intTo   = extractIntFromLex(to);

    return new Range(intFrom, intTo);
  }

  private static int extractIntFromLex(Lex lex) {
    switch (lex.type) {
      default:
        throw new RuntimeException("KUt77u2COr :: Cannot extract int from lex with type = " + lex.type);

      case DIGIT:
        return Integer.parseInt(lex.tokens.get(0).str());

      case MONTH:
        return TriggerStructStrLexer.readMonth(lex.tokens.get(0).strNormy());
    }
  }


  private Trigger createMonthsTrigger(List<Lex> months, List<Lex> monthDays) {

    Trigger monthTrigger = extractRanges(months, MONTH).stream()
                                                       .map(TriggerMonth::new)
                                                       .map(Trigger.class::cast)
                                                       .reduce(TriggerUtil::or)
                                                       .orElse(SilentTrigger.SILENT);

    if (monthDays.isEmpty()) {
      return monthTrigger;
    }

    Trigger monthDayTrigger = extractRanges(monthDays, DIGIT).stream()
                                                             .map(TriggerMonthDay::new)
                                                             .map(Trigger.class::cast)
                                                             .reduce(TriggerUtil::or)
                                                             .orElse(SilentTrigger.SILENT);


    return TriggerUtil.and(monthDayTrigger, monthTrigger);
  }


}
